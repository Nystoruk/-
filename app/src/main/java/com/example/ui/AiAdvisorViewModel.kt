package com.example.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.LiturgicalDay
import com.google.firebase.Firebase
import com.google.firebase.ai.Chat
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

private const val TAG = "AiAdvisorViewModel"

data class AiChatMessage(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val isStreaming: Boolean = false
)

sealed interface AiAdvisorStatus {
    data object Idle : AiAdvisorStatus
    data object Loading : AiAdvisorStatus
    data object Streaming : AiAdvisorStatus
    data class Error(val message: String) : AiAdvisorStatus
}

data class AiAdvisorUiState(
    val contextDay: LiturgicalDay? = null,
    val messages: List<AiChatMessage> = emptyList(),
    val status: AiAdvisorStatus = AiAdvisorStatus.Idle,
    val lastFailedPrompt: String? = null
)

class AiAdvisorViewModel : ViewModel() {

    private val model by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = content {
                text(
                    """
                    Ти — доброзичливий, грамотний і шанобливий духовний помічник українського православного церковного календаря (УПЦ, традиція юліанського календаря / старого стилю).
                    Відповідай виключно українською мовою. Будь теплим, чітким, структурованим і лаконічним.
                    Допомагай користувачеві з такими темами:
                    1. Пояснення значення православних свят, житій святих, читань Євангелія й Апостола дня.
                    2. Правила та духовний зміст постів, підготовка до сповіді та Причастя, практичні ідеї пісних страв відповідно до статусу посту дня.
                    3. Короткі православні молитви, тропарі та поради щодо церковного етикету.
                    Пам'ятай: у медичних або складних духовних питаннях радь також радитися з духівником або лікарем щодо міри посту. Не вигадуй неіснуючих церковних канонів.
                    """.trimIndent()
                )
            }
        )
    }

    private var activeChat: Chat? = null
    private var activeJob: Job? = null

    private val _uiState = MutableStateFlow(AiAdvisorUiState())
    val uiState: StateFlow<AiAdvisorUiState> = _uiState.asStateFlow()

    fun setContextDay(day: LiturgicalDay) {
        val currentId = _uiState.value.contextDay?.date
        if (currentId != day.date) {
            activeChat = null
            _uiState.update {
                it.copy(
                    contextDay = day,
                    messages = emptyList(),
                    status = AiAdvisorStatus.Idle,
                    lastFailedPrompt = null
                )
            }
        }
    }

    fun clearConversation() {
        activeJob?.cancel()
        activeChat = null
        _uiState.update {
            it.copy(
                messages = emptyList(),
                status = AiAdvisorStatus.Idle,
                lastFailedPrompt = null
            )
        }
    }

    fun retryLastPrompt() {
        val prompt = _uiState.value.lastFailedPrompt ?: return
        sendMessage(prompt)
    }

    fun sendMessage(rawPrompt: String) {
        val userText = rawPrompt.trim()
        if (userText.isEmpty()) return
        if (_uiState.value.status == AiAdvisorStatus.Loading ||
            _uiState.value.status == AiAdvisorStatus.Streaming
        ) {
            return
        }

        val day = _uiState.value.contextDay
        val isFirstTurn = _uiState.value.messages.isEmpty()
        val promptToSend = if (isFirstTurn && day != null) {
            buildString {
                append("[Контекст обраного дня у календарі УПЦ]\n")
                append("Дата: ${day.date.formatUkrainianFull()} (${day.date.weekdayName()}, ${day.date.formatJulianFull()})\n")
                append("Тиждень/глас: ${day.weekDescription}\n")
                append("Свято / подія: ${day.feastTitle} (${day.feastRank.labelUk})\n")
                append("Піст: ${day.fastingType.titleUk}. ${day.fastingNote}\n")
                append("Святі дня: ${day.saints.joinToString("; ")}\n")
                append("Читання: ${day.readingRef}\n\n")
                append("Запитання користувача: $userText")
            }
        } else {
            userText
        }

        val userMsg = AiChatMessage(
            id = "u_${System.currentTimeMillis()}",
            isUser = true,
            text = userText
        )
        val assistantMsgId = "a_${System.currentTimeMillis() + 1}"

        _uiState.update { state ->
            state.copy(
                messages = state.messages + userMsg,
                status = AiAdvisorStatus.Loading,
                lastFailedPrompt = null
            )
        }

        activeJob = viewModelScope.launch {
            val accumulated = StringBuilder()
            try {
                val chat = activeChat ?: model.startChat().also { activeChat = it }

                withTimeout(60_000L) {
                    chat.sendMessageStream(promptToSend).collect { chunk ->
                        val chunkText = chunk.text.orEmpty()
                        if (chunkText.isNotEmpty()) {
                            accumulated.append(chunkText)
                            val currentText = accumulated.toString()
                            _uiState.update { state ->
                                val existingIdx = state.messages.indexOfFirst { it.id == assistantMsgId }
                                val updatedMessages = if (existingIdx >= 0) {
                                    state.messages.toMutableList().apply {
                                        this[existingIdx] = this[existingIdx].copy(
                                            text = currentText,
                                            isStreaming = true
                                        )
                                    }
                                } else {
                                    state.messages + AiChatMessage(
                                        id = assistantMsgId,
                                        isUser = false,
                                        text = currentText,
                                        isStreaming = true
                                    )
                                }
                                state.copy(
                                    messages = updatedMessages,
                                    status = AiAdvisorStatus.Streaming
                                )
                            }
                        }
                    }
                }

                if (accumulated.isNotBlank()) {
                    _uiState.update { state ->
                        val updatedMessages = state.messages.map {
                            if (it.id == assistantMsgId) it.copy(isStreaming = false) else it
                        }
                        state.copy(
                            messages = updatedMessages,
                            status = AiAdvisorStatus.Idle
                        )
                    }
                } else {
                    _uiState.update { state ->
                        state.copy(
                            status = AiAdvisorStatus.Error("Модель повернула порожню відповідь. Спробуйте ще раз."),
                            lastFailedPrompt = userText
                        )
                    }
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Gemini streaming timed out", e)
                _uiState.update { state ->
                    state.copy(
                        messages = state.messages.map {
                            if (it.id == assistantMsgId) it.copy(isStreaming = false) else it
                        },
                        status = AiAdvisorStatus.Error("Час очікування відповіді вичерпано (60 сек). Перевірте з'єднання та спробуйте знову."),
                        lastFailedPrompt = userText
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Firebase AI generation failed", e)
                _uiState.update { state ->
                    state.copy(
                        messages = state.messages.filterNot { it.id == assistantMsgId && it.text.isBlank() },
                        status = AiAdvisorStatus.Error(
                            e.localizedMessage ?: "Не вдалося отримати відповідь від Gemini AI."
                        ),
                        lastFailedPrompt = userText
                    )
                }
            }
        }
    }
}

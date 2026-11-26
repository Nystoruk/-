package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.preferences.ReminderPreferencesRepository
import com.example.domain.calendar.OrthodoxCalendarRepository
import com.example.domain.calendar.UpcomingHighlights
import com.example.domain.model.CalendarDate
import com.example.domain.model.CustomReminder
import com.example.domain.model.FastingPeriodSummary
import com.example.domain.model.LiturgicalDay
import com.example.domain.model.ReminderSettings
import com.example.notifications.NotificationHelper
import com.example.notifications.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CalendarUiState(
    val today: CalendarDate = CalendarDate.today(),
    val todayLiturgical: LiturgicalDay,
    val upcomingHighlights: UpcomingHighlights,
    val displayedYear: Int,
    val displayedMonth: Int,
    val monthDays: List<LiturgicalDay>,
    val selectedDate: CalendarDate,
    val selectedDayLiturgical: LiturgicalDay,
    val sheetDayLiturgical: LiturgicalDay? = null,
    val feastsYear: Int,
    val yearMajorFeasts: List<LiturgicalDay>,
    val yearMemorialDays: List<LiturgicalDay>,
    val yearFastingPeriods: List<FastingPeriodSummary>,
    val searchQuery: String = "",
    val searchResults: List<LiturgicalDay> = emptyList(),
    val reminderSettings: ReminderSettings = ReminderSettings(),
    val customReminders: List<CustomReminder> = emptyList(),
    val hasNotificationPermission: Boolean = true,
    val addReminderTargetDay: LiturgicalDay? = null
)

class CalendarViewModel(application: Application) : AndroidViewModel(application) {

    private val calendarRepo = OrthodoxCalendarRepository()
    private val prefsRepo = ReminderPreferencesRepository(application)

    private val initialToday = CalendarDate.today()

    private val _uiState = MutableStateFlow(
        CalendarUiState(
            today = initialToday,
            todayLiturgical = calendarRepo.getLiturgicalDay(initialToday),
            upcomingHighlights = calendarRepo.getUpcomingHighlights(initialToday),
            displayedYear = initialToday.year,
            displayedMonth = initialToday.month,
            monthDays = calendarRepo.getMonthDays(initialToday.year, initialToday.month),
            selectedDate = initialToday,
            selectedDayLiturgical = calendarRepo.getLiturgicalDay(initialToday),
            feastsYear = initialToday.year,
            yearMajorFeasts = calendarRepo.getYearMajorFeasts(initialToday.year),
            yearMemorialDays = calendarRepo.getYearMemorialDays(initialToday.year),
            yearFastingPeriods = calendarRepo.getYearFastingPeriods(initialToday.year),
            hasNotificationPermission = NotificationHelper.hasNotificationPermission(application)
        )
    )
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        NotificationHelper.ensureChannel(application)
        viewModelScope.launch {
            combine(
                prefsRepo.settingsFlow,
                prefsRepo.customRemindersFlow
            ) { settings, custom ->
                settings to custom
            }.collect { (settings, custom) ->
                _uiState.update { state ->
                    state.copy(
                        reminderSettings = settings,
                        customReminders = custom
                    )
                }
                ReminderScheduler.reschedule(getApplication(), settings)
            }
        }
    }

    fun selectDate(date: CalendarDate) {
        val monthChanged = date.year != _uiState.value.displayedYear ||
            date.month != _uiState.value.displayedMonth
        _uiState.update { state ->
            state.copy(
                selectedDate = date,
                selectedDayLiturgical = calendarRepo.getLiturgicalDay(date),
                displayedYear = if (monthChanged) date.year else state.displayedYear,
                displayedMonth = if (monthChanged) date.month else state.displayedMonth,
                monthDays = if (monthChanged) {
                    calendarRepo.getMonthDays(date.year, date.month)
                } else {
                    state.monthDays
                }
            )
        }
    }

    fun openDaySheet(day: LiturgicalDay) {
        _uiState.update { it.copy(sheetDayLiturgical = day) }
    }

    fun dismissDaySheet() {
        _uiState.update { it.copy(sheetDayLiturgical = null) }
    }

    fun previousMonth() {
        val currentYear = _uiState.value.displayedYear
        val currentMonth = _uiState.value.displayedMonth
        val (newYear, newMonth) = if (currentMonth == 1) {
            (currentYear - 1) to 12
        } else {
            currentYear to (currentMonth - 1)
        }
        val firstOfMonth = CalendarDate(newYear, newMonth, 1)
        _uiState.update { state ->
            state.copy(
                displayedYear = newYear,
                displayedMonth = newMonth,
                monthDays = calendarRepo.getMonthDays(newYear, newMonth),
                selectedDate = firstOfMonth,
                selectedDayLiturgical = calendarRepo.getLiturgicalDay(firstOfMonth)
            )
        }
    }

    fun nextMonth() {
        val currentYear = _uiState.value.displayedYear
        val currentMonth = _uiState.value.displayedMonth
        val (newYear, newMonth) = if (currentMonth == 12) {
            (currentYear + 1) to 1
        } else {
            currentYear to (currentMonth + 1)
        }
        val firstOfMonth = CalendarDate(newYear, newMonth, 1)
        _uiState.update { state ->
            state.copy(
                displayedYear = newYear,
                displayedMonth = newMonth,
                monthDays = calendarRepo.getMonthDays(newYear, newMonth),
                selectedDate = firstOfMonth,
                selectedDayLiturgical = calendarRepo.getLiturgicalDay(firstOfMonth)
            )
        }
    }

    fun jumpToToday() {
        val today = CalendarDate.today()
        _uiState.update { state ->
            state.copy(
                today = today,
                todayLiturgical = calendarRepo.getLiturgicalDay(today),
                upcomingHighlights = calendarRepo.getUpcomingHighlights(today),
                displayedYear = today.year,
                displayedMonth = today.month,
                monthDays = calendarRepo.getMonthDays(today.year, today.month),
                selectedDate = today,
                selectedDayLiturgical = calendarRepo.getLiturgicalDay(today)
            )
        }
    }

    fun changeFeastsYear(delta: Int) {
        val newYear = (_uiState.value.feastsYear + delta).coerceIn(1950, 2099)
        _uiState.update { state ->
            state.copy(
                feastsYear = newYear,
                yearMajorFeasts = calendarRepo.getYearMajorFeasts(newYear),
                yearMemorialDays = calendarRepo.getYearMemorialDays(newYear),
                yearFastingPeriods = calendarRepo.getYearFastingPeriods(newYear)
            )
        }
    }

    fun updateSearchQuery(query: String) {
        val year = _uiState.value.displayedYear
        val results = if (query.isBlank()) {
            emptyList()
        } else {
            calendarRepo.searchEvents(query, year)
        }
        _uiState.update { it.copy(searchQuery = query, searchResults = results) }
    }

    fun openAddReminderDialog(day: LiturgicalDay) {
        _uiState.update { it.copy(addReminderTargetDay = day) }
    }

    fun dismissAddReminderDialog() {
        _uiState.update { it.copy(addReminderTargetDay = null) }
    }

    fun saveCustomReminder(date: CalendarDate, title: String, note: String) {
        val settings = _uiState.value.reminderSettings
        val reminder = CustomReminder(
            id = "${date.toIsoString()}_${System.currentTimeMillis()}",
            isoDate = date.toIsoString(),
            title = title.trim().ifEmpty { calendarRepo.getLiturgicalDay(date).feastTitle },
            note = note.trim(),
            hour = settings.hour,
            minute = settings.minute
        )
        viewModelScope.launch {
            prefsRepo.upsertCustomReminder(reminder)
            _uiState.update { it.copy(addReminderTargetDay = null) }
        }
    }

    fun deleteCustomReminder(id: String) {
        viewModelScope.launch {
            prefsRepo.deleteCustomReminder(id)
        }
    }

    fun toggleQuickReminder(day: LiturgicalDay) {
        val iso = day.date.toIsoString()
        val existing = _uiState.value.customReminders.firstOrNull { it.isoDate == iso }
        if (existing != null) {
            deleteCustomReminder(existing.id)
        } else {
            openAddReminderDialog(day)
        }
    }

    fun updateReminderSettings(transform: (ReminderSettings) -> ReminderSettings) {
        viewModelScope.launch {
            prefsRepo.updateSettings(transform)
        }
    }

    fun refreshNotificationPermission() {
        val granted = NotificationHelper.hasNotificationPermission(getApplication())
        _uiState.update { it.copy(hasNotificationPermission = granted) }
    }

    fun sendTestNotification(): Boolean {
        val app = getApplication<Application>()
        refreshNotificationPermission()
        return NotificationHelper.sendTestNotification(app, _uiState.value.todayLiturgical)
    }

    fun handleDeepLinkDate(isoDate: String?) {
        val parsed = isoDate?.let { CalendarDate.parseIso(it) } ?: return
        selectDate(parsed)
        _uiState.update { it.copy(sheetDayLiturgical = calendarRepo.getLiturgicalDay(parsed)) }
    }
}

package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.domain.model.CustomReminder
import com.example.domain.model.ReminderSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

private val Context.reminderDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "orthodox_reminders_prefs"
)

class ReminderPreferencesRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    private object Keys {
        val ENABLED = booleanPreferencesKey("reminders_enabled")
        val NOTIFY_GREAT_FEASTS = booleanPreferencesKey("notify_great_feasts")
        val NOTIFY_MULTI_DAY_FASTS = booleanPreferencesKey("notify_multi_day_fasts")
        val NOTIFY_ONE_DAY_FASTS = booleanPreferencesKey("notify_one_day_fasts")
        val NOTIFY_MEMORIAL_SATURDAYS = booleanPreferencesKey("notify_memorial_saturdays")
        val NOTIFY_EVENING_BEFORE = booleanPreferencesKey("notify_evening_before")
        val REMINDER_HOUR = intPreferencesKey("reminder_hour")
        val REMINDER_MINUTE = intPreferencesKey("reminder_minute")
        val CUSTOM_REMINDERS_JSON = stringPreferencesKey("custom_reminders_json")
    }

    val settingsFlow: Flow<ReminderSettings> = context.reminderDataStore.data.map { prefs ->
        ReminderSettings(
            enabled = prefs[Keys.ENABLED] ?: true,
            notifyGreatFeasts = prefs[Keys.NOTIFY_GREAT_FEASTS] ?: true,
            notifyMultiDayFasts = prefs[Keys.NOTIFY_MULTI_DAY_FASTS] ?: true,
            notifyOneDayFasts = prefs[Keys.NOTIFY_ONE_DAY_FASTS] ?: false,
            notifyMemorialSaturdays = prefs[Keys.NOTIFY_MEMORIAL_SATURDAYS] ?: true,
            notifyEveningBefore = prefs[Keys.NOTIFY_EVENING_BEFORE] ?: true,
            hour = prefs[Keys.REMINDER_HOUR] ?: 8,
            minute = prefs[Keys.REMINDER_MINUTE] ?: 0
        )
    }

    val customRemindersFlow: Flow<List<CustomReminder>> = context.reminderDataStore.data.map { prefs ->
        decodeCustomReminders(prefs[Keys.CUSTOM_REMINDERS_JSON])
    }

    suspend fun getSettingsSnapshot(): ReminderSettings = settingsFlow.first()

    suspend fun getCustomRemindersSnapshot(): List<CustomReminder> = customRemindersFlow.first()

    suspend fun updateSettings(transform: (ReminderSettings) -> ReminderSettings) {
        context.reminderDataStore.edit { prefs ->
            val current = ReminderSettings(
                enabled = prefs[Keys.ENABLED] ?: true,
                notifyGreatFeasts = prefs[Keys.NOTIFY_GREAT_FEASTS] ?: true,
                notifyMultiDayFasts = prefs[Keys.NOTIFY_MULTI_DAY_FASTS] ?: true,
                notifyOneDayFasts = prefs[Keys.NOTIFY_ONE_DAY_FASTS] ?: false,
                notifyMemorialSaturdays = prefs[Keys.NOTIFY_MEMORIAL_SATURDAYS] ?: true,
                notifyEveningBefore = prefs[Keys.NOTIFY_EVENING_BEFORE] ?: true,
                hour = prefs[Keys.REMINDER_HOUR] ?: 8,
                minute = prefs[Keys.REMINDER_MINUTE] ?: 0
            )
            val updated = transform(current)
            prefs[Keys.ENABLED] = updated.enabled
            prefs[Keys.NOTIFY_GREAT_FEASTS] = updated.notifyGreatFeasts
            prefs[Keys.NOTIFY_MULTI_DAY_FASTS] = updated.notifyMultiDayFasts
            prefs[Keys.NOTIFY_ONE_DAY_FASTS] = updated.notifyOneDayFasts
            prefs[Keys.NOTIFY_MEMORIAL_SATURDAYS] = updated.notifyMemorialSaturdays
            prefs[Keys.NOTIFY_EVENING_BEFORE] = updated.notifyEveningBefore
            prefs[Keys.REMINDER_HOUR] = updated.hour
            prefs[Keys.REMINDER_MINUTE] = updated.minute
        }
    }

    suspend fun upsertCustomReminder(reminder: CustomReminder) {
        context.reminderDataStore.edit { prefs ->
            val current = decodeCustomReminders(prefs[Keys.CUSTOM_REMINDERS_JSON]).toMutableList()
            val index = current.indexOfFirst { it.id == reminder.id }
            if (index >= 0) {
                current[index] = reminder
            } else {
                current.add(reminder)
            }
            current.sortBy { it.isoDate }
            prefs[Keys.CUSTOM_REMINDERS_JSON] = json.encodeToString(current)
        }
    }

    suspend fun deleteCustomReminder(id: String) {
        context.reminderDataStore.edit { prefs ->
            val current = decodeCustomReminders(prefs[Keys.CUSTOM_REMINDERS_JSON])
                .filterNot { it.id == id }
            prefs[Keys.CUSTOM_REMINDERS_JSON] = json.encodeToString(current)
        }
    }

    private fun decodeCustomReminders(raw: String?): List<CustomReminder> {
        if (raw.isNullOrBlank()) return emptyList()
        return runCatching {
            json.decodeFromString<List<CustomReminder>>(raw)
        }.getOrDefault(emptyList())
    }
}

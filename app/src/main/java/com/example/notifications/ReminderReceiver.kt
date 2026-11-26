package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.preferences.ReminderPreferencesRepository
import com.example.domain.calendar.OrthodoxCalendarRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        val isEveningCheck = intent.getBooleanExtra(ReminderScheduler.EXTRA_IS_EVENING_CHECK, false)
        val appContext = context.applicationContext

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prefsRepo = ReminderPreferencesRepository(appContext)
                val calendarRepo = OrthodoxCalendarRepository()
                val settings = prefsRepo.getSettingsSnapshot()
                val customReminders = prefsRepo.getCustomRemindersSnapshot()

                NotificationHelper.evaluateAndNotify(
                    context = appContext,
                    repository = calendarRepo,
                    settings = settings,
                    customReminders = customReminders,
                    isEveningBeforeCheck = isEveningCheck
                )

                // Reschedule for tomorrow
                ReminderScheduler.reschedule(appContext, settings)
            } finally {
                pendingResult.finish()
            }
        }
    }
}

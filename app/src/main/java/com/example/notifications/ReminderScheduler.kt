package com.example.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.domain.model.ReminderSettings
import java.util.Calendar

object ReminderScheduler {

    private const val REQUEST_CODE_MORNING = 1001
    private const val REQUEST_CODE_EVENING = 1002
    const val EXTRA_IS_EVENING_CHECK = "extra_is_evening_check"

    fun reschedule(context: Context, settings: ReminderSettings) {
        cancelAll(context)
        if (!settings.enabled) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        scheduleNextOccurrence(
            context = context,
            alarmManager = alarmManager,
            requestCode = REQUEST_CODE_MORNING,
            hour = settings.hour,
            minute = settings.minute,
            isEveningCheck = false
        )

        if (settings.notifyEveningBefore) {
            scheduleNextOccurrence(
                context = context,
                alarmManager = alarmManager,
                requestCode = REQUEST_CODE_EVENING,
                hour = 19,
                minute = 0,
                isEveningCheck = true
            )
        }
    }

    fun cancelAll(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        listOf(REQUEST_CODE_MORNING to false, REQUEST_CODE_EVENING to true).forEach { (code, evening) ->
            val pi = buildPendingIntent(context, code, evening)
            alarmManager.cancel(pi)
        }
    }

    private fun scheduleNextOccurrence(
        context: Context,
        alarmManager: AlarmManager,
        requestCode: Int,
        hour: Int,
        minute: Int,
        isEveningCheck: Boolean
    ) {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= now.timeInMillis + 15_000L) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val pi = buildPendingIntent(context, requestCode, isEveningCheck)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                target.timeInMillis,
                pi
            )
        } else {
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                target.timeInMillis,
                pi
            )
        }
    }

    private fun buildPendingIntent(
        context: Context,
        requestCode: Int,
        isEveningCheck: Boolean
    ): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(EXTRA_IS_EVENING_CHECK, isEveningCheck)
        }
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}

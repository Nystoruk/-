package com.example.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.domain.calendar.OrthodoxCalendarRepository
import com.example.domain.model.CalendarDate
import com.example.domain.model.CustomReminder
import com.example.domain.model.FastingPeriod
import com.example.domain.model.LiturgicalDay
import com.example.domain.model.ReminderSettings

object NotificationHelper {

    const val CHANNEL_ID = "orthodox_reminders"
    const val EXTRA_DATE_ISO = "extra_date_iso"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val existing = manager.getNotificationChannel(CHANNEL_ID)
            if (existing == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = context.getString(R.string.notification_channel_desc)
                }
                manager.createNotificationChannel(channel)
            }
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun sendTestNotification(
        context: Context,
        todayLit: LiturgicalDay
    ): Boolean {
        if (!hasNotificationPermission(context)) return false
        ensureChannel(context)

        val title = "Календар УПЦ · ${todayLit.date.formatUkrainianShort()}"
        val body = buildString {
            append(todayLit.feastTitle)
            append(" — ")
            append(todayLit.fastingType.shortUk)
            append(".")
        }
        postNotification(
            context = context,
            notificationId = 9001,
            title = title,
            body = body,
            dateIso = todayLit.date.toIsoString()
        )
        return true
    }

    fun evaluateAndNotify(
        context: Context,
        repository: OrthodoxCalendarRepository,
        settings: ReminderSettings,
        customReminders: List<CustomReminder>,
        isEveningBeforeCheck: Boolean
    ) {
        if (!settings.enabled || !hasNotificationPermission(context)) return
        ensureChannel(context)

        val today = CalendarDate.today()
        val targetDate = if (isEveningBeforeCheck) today.plusDays(1) else today
        val day = repository.getLiturgicalDay(targetDate)
        val prevDay = repository.getLiturgicalDay(targetDate.minusDays(1))
        val targetIso = targetDate.toIsoString()

        val matchingCustom = customReminders.filter { it.isoDate == targetIso }

        val shouldNotifyFeast = settings.notifyGreatFeasts && day.isMajorFeast
        val isMultiDayFastStart = day.isFastDay &&
            day.fastingPeriod != FastingPeriod.ONE_DAY_FAST &&
            (!prevDay.isFastDay || prevDay.fastingPeriod != day.fastingPeriod)
        val shouldNotifyMultiDayFast = settings.notifyMultiDayFasts && isMultiDayFastStart
        val shouldNotifyOneDayFast = settings.notifyOneDayFasts &&
            day.fastingPeriod == FastingPeriod.ONE_DAY_FAST
        val shouldNotifyMemorial = settings.notifyMemorialSaturdays && day.isMemorialDay

        if (isEveningBeforeCheck && !settings.notifyEveningBefore) {
            return
        }

        val prefix = if (isEveningBeforeCheck) "Завтра (${targetDate.formatUkrainianShort()}): " else "Сьогодні: "

        when {
            matchingCustom.isNotEmpty() -> {
                val first = matchingCustom.first()
                val title = "$prefix${first.title}"
                val body = buildString {
                    if (first.note.isNotBlank()) append(first.note).append(" · ")
                    append(day.feastTitle)
                    append(" (${day.fastingType.shortUk})")
                }
                postNotification(context, targetDate.hashCode(), title, body, targetIso)
            }
            shouldNotifyFeast -> {
                val title = "$prefix${day.feastTitle}"
                val body = "${day.feastRank.labelUk} · ${day.fastingType.shortUk}. ${day.reflection}"
                postNotification(context, targetDate.hashCode(), title, body, targetIso)
            }
            shouldNotifyMemorial -> {
                val title = "$prefix${day.feastTitle}"
                val body = "Поминальний день. ${day.fastingType.shortUk}. Помоліться за спочилих близьких."
                postNotification(context, targetDate.hashCode(), title, body, targetIso)
            }
            shouldNotifyMultiDayFast -> {
                val title = "${prefix}Починається ${day.fastingPeriod.titleUk}"
                val body = day.fastingNote
                postNotification(context, targetDate.hashCode(), title, body, targetIso)
            }
            shouldNotifyOneDayFast && !isEveningBeforeCheck -> {
                val title = "Пісний день · ${targetDate.formatUkrainianShort()}"
                val body = "${day.fastingType.titleUk}. ${day.feastTitle}"
                postNotification(context, targetDate.hashCode(), title, body, targetIso)
            }
        }
    }

    private fun postNotification(
        context: Context,
        notificationId: Int,
        title: String,
        body: String,
        dateIso: String
    ) {
        if (!hasNotificationPermission(context)) return

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_DATE_ISO, dateIso)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        runCatching {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        }
    }
}

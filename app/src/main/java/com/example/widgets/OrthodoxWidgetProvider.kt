package com.example.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.domain.calendar.OrthodoxCalendarRepository
import com.example.domain.model.CalendarDate

class OrthodoxWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val repository = OrthodoxCalendarRepository()
            val today = CalendarDate.today()
            val litDay = repository.getLiturgicalDay(today)

            val views = RemoteViews(context.packageName, R.layout.orthodox_widget_layout).apply {
                setTextViewText(
                    R.id.widget_date_text,
                    "${today.formatUkrainianShort()} · ${today.formatJulianShort()}"
                )
                setTextViewText(
                    R.id.widget_feast_title,
                    litDay.feastTitle
                )
                setTextViewText(
                    R.id.widget_fasting_badge,
                    litDay.fastingType.shortUk
                )
                val primarySaint = litDay.saints.firstOrNull() ?: "Пам'ять дня"
                setTextViewText(
                    R.id.widget_saints_text,
                    primarySaint
                )

                val intent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    appWidgetId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                setOnClickPendingIntent(R.id.widget_root, pendingIntent)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ai.configureAppCheck
import com.example.notifications.NotificationHelper
import com.example.ui.AiAdvisorViewModel
import com.example.ui.CalendarViewModel
import com.example.ui.navigation.OrthodoxCalendarApp
import com.example.ui.theme.OrthodoxCalendarTheme

class MainActivity : ComponentActivity() {

    private val calendarViewModel: CalendarViewModel by viewModels()
    private val aiAdvisorViewModel: AiAdvisorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure App Check with launch intent before any UI, ViewModels, or Firebase calls
        configureAppCheck(this, intent)

        enableEdgeToEdge()
        handleIntentDate(intent)

        setContent {
            OrthodoxCalendarTheme {
                OrthodoxCalendarApp(
                    viewModel = calendarViewModel,
                    aiViewModel = aiAdvisorViewModel
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntentDate(intent)
    }

    override fun onResume() {
        super.onResume()
        calendarViewModel.refreshNotificationPermission()
    }

    private fun handleIntentDate(intent: Intent?) {
        val dateIso = intent?.getStringExtra(NotificationHelper.EXTRA_DATE_ISO)
        if (!dateIso.isNullOrBlank()) {
            calendarViewModel.handleDeepLinkDate(dateIso)
        }
    }
}

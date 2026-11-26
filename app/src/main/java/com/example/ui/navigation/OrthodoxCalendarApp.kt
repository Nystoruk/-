package com.example.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Church
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Church
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.domain.calendar.OrthodoxCalendarRepository
import com.example.domain.model.LiturgicalDay
import com.example.ui.AiAdvisorViewModel
import com.example.ui.CalendarViewModel
import com.example.ui.components.AddCustomReminderDialog
import com.example.ui.components.LiturgicalDayDetailCard
import com.example.ui.screens.AiAdvisorScreen
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.FeastsAndFastsScreen
import com.example.ui.screens.RemindersScreen
import com.example.ui.screens.TodayScreen
import com.example.ui.theme.LocalLiturgicalColors
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object TodayRoute

@Serializable
object CalendarRoute

@Serializable
object FeastsFastsRoute

@Serializable
object RemindersRoute

@Serializable
object AiAdvisorRoute

private data class NavDestinationItem(
    val route: Any,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

private val navItems = listOf(
    NavDestinationItem(
        route = TodayRoute,
        label = "Сьогодні",
        selectedIcon = Icons.Filled.Today,
        unselectedIcon = Icons.Outlined.Today,
        testTag = "nav_today"
    ),
    NavDestinationItem(
        route = CalendarRoute,
        label = "Календар",
        selectedIcon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth,
        testTag = "nav_calendar"
    ),
    NavDestinationItem(
        route = FeastsFastsRoute,
        label = "Свята і пости",
        selectedIcon = Icons.Filled.Church,
        unselectedIcon = Icons.Outlined.Church,
        testTag = "nav_feasts"
    ),
    NavDestinationItem(
        route = RemindersRoute,
        label = "Нагадування",
        selectedIcon = Icons.Filled.Notifications,
        unselectedIcon = Icons.Outlined.Notifications,
        testTag = "nav_reminders"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrthodoxCalendarApp(
    viewModel: CalendarViewModel,
    aiViewModel: AiAdvisorViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val aiUiState by aiViewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val isAiScreen = currentDestination?.hasRoute(AiAdvisorRoute::class) == true
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val litColors = LocalLiturgicalColors.current

    fun navigateTo(route: Any) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun openAiAdvisor(day: LiturgicalDay) {
        aiViewModel.setContextDay(day)
        navController.navigate(AiAdvisorRoute) {
            launchSingleTop = true
        }
    }

    fun showMessage(msg: String) {
        coroutineScope.launch {
            snackbarHostState.showSnackbar(msg)
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useRail = maxWidth >= 700.dp

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                if (!useRail && !isAiScreen) {
                    NavigationBar(
                        containerColor = litColors.cardElevated,
                        tonalElevation = 4.dp
                    ) {
                        navItems.forEach { item ->
                            val selected = currentDestination?.hasRoute(item.route::class) == true
                            NavigationBarItem(
                                selected = selected,
                                onClick = { navigateTo(item.route) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.label
                                    )
                                },
                                label = { Text(item.label) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = litColors.feastCrimsonContainer
                                ),
                                modifier = Modifier.testTag(item.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (useRail && !isAiScreen) {
                    NavigationRail(
                        containerColor = litColors.cardElevated,
                        modifier = Modifier
                            .fillMaxHeight()
                            .windowInsetsPadding(WindowInsets.safeDrawing)
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        navItems.forEach { item ->
                            val selected = currentDestination?.hasRoute(item.route::class) == true
                            NavigationRailItem(
                                selected = selected,
                                onClick = { navigateTo(item.route) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.label
                                    )
                                },
                                label = { Text(item.label) },
                                modifier = Modifier.testTag(item.testTag)
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    NavHost(
                        navController = navController,
                        startDestination = TodayRoute
                    ) {
                        composable<TodayRoute> {
                            TodayScreen(
                                uiState = uiState,
                                onOpenDayDetail = { viewModel.openDaySheet(it) },
                                onToggleReminder = { viewModel.toggleQuickReminder(it) },
                                onOpenCalendarTab = { navigateTo(CalendarRoute) },
                                onOpenFeastsTab = { navigateTo(FeastsFastsRoute) },
                                onOpenAiAdvisor = ::openAiAdvisor
                            )
                        }
                        composable<CalendarRoute> {
                            CalendarScreen(
                                uiState = uiState,
                                onSelectDate = { viewModel.selectDate(it) },
                                onPreviousMonth = { viewModel.previousMonth() },
                                onNextMonth = { viewModel.nextMonth() },
                                onJumpToToday = { viewModel.jumpToToday() },
                                onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                                onToggleReminder = { viewModel.toggleQuickReminder(it) },
                                onOpenAiAdvisor = ::openAiAdvisor
                            )
                        }
                        composable<FeastsFastsRoute> {
                            FeastsAndFastsScreen(
                                uiState = uiState,
                                onChangeYear = { viewModel.changeFeastsYear(it) },
                                onOpenDayDetail = { viewModel.openDaySheet(it) },
                                onToggleReminder = { viewModel.toggleQuickReminder(it) }
                            )
                        }
                        composable<RemindersRoute> {
                            val tempRepo = remember { OrthodoxCalendarRepository() }
                            RemindersScreen(
                                uiState = uiState,
                                onUpdateSettings = { viewModel.updateReminderSettings(it) },
                                onRefreshPermission = { viewModel.refreshNotificationPermission() },
                                onSendTestNotification = { viewModel.sendTestNotification() },
                                onPickDateForReminder = { pickedDate ->
                                    viewModel.openAddReminderDialog(tempRepo.getLiturgicalDay(pickedDate))
                                },
                                onDeleteCustomReminder = { id ->
                                    viewModel.deleteCustomReminder(id)
                                    showMessage("Нагадування видалено")
                                },
                                onOpenDateInCalendar = { date ->
                                    viewModel.selectDate(date)
                                    navigateTo(CalendarRoute)
                                },
                                onShowMessage = ::showMessage
                            )
                        }
                        composable<AiAdvisorRoute> {
                            AiAdvisorScreen(
                                uiState = aiUiState,
                                fallbackToday = uiState.todayLiturgical,
                                onBack = { navController.popBackStack() },
                                onSendMessage = { aiViewModel.sendMessage(it) },
                                onRetry = { aiViewModel.retryLastPrompt() },
                                onClearChat = { aiViewModel.clearConversation() }
                            )
                        }
                    }
                }
            }
        }
    }

    // Detail Modal Sheet for inspecting any feast/memorial from Today or Feasts tabs
    val sheetDay = uiState.sheetDayLiturgical
    if (sheetDay != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { viewModel.dismissDaySheet() },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp)
            ) {
                LiturgicalDayDetailCard(
                    day = sheetDay,
                    hasCustomReminder = uiState.customReminders.any {
                        it.isoDate == sheetDay.date.toIsoString()
                    },
                    onToggleReminder = { viewModel.toggleQuickReminder(sheetDay) },
                    onAskAi = {
                        viewModel.dismissDaySheet()
                        openAiAdvisor(sheetDay)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = {
                        viewModel.selectDate(sheetDay.date)
                        viewModel.dismissDaySheet()
                        navigateTo(CalendarRoute)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Показати цей день у календарі")
                }
            }
        }
    }

    // Add Custom Reminder Dialog
    val targetReminderDay = uiState.addReminderTargetDay
    if (targetReminderDay != null) {
        AddCustomReminderDialog(
            day = targetReminderDay,
            onDismiss = { viewModel.dismissAddReminderDialog() },
            onSave = { title, note ->
                viewModel.saveCustomReminder(targetReminderDay.date, title, note)
                showMessage("Нагадування збережено на ${targetReminderDay.date.formatUkrainianShort()}")
            }
        )
    }
}

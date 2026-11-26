package com.example.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.CalendarDate
import com.example.domain.model.CustomReminder
import com.example.domain.model.ReminderSettings
import com.example.ui.CalendarUiState
import com.example.ui.theme.LocalLiturgicalColors
import java.util.Calendar
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    uiState: CalendarUiState,
    onUpdateSettings: ((ReminderSettings) -> ReminderSettings) -> Unit,
    onRefreshPermission: () -> Unit,
    onSendTestNotification: () -> Boolean,
    onPickDateForReminder: (CalendarDate) -> Unit,
    onDeleteCustomReminder: (String) -> Unit,
    onOpenDateInCalendar: (CalendarDate) -> Unit,
    onShowMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val litColors = LocalLiturgicalColors.current
    val settings = uiState.reminderSettings
    var showTimePicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        onRefreshPermission()
        if (granted) {
            onShowMessage("Дозвіл на сповіщення надано")
        } else {
            onShowMessage("Без дозволу сповіщення не зможуть відображатися")
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 760.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "Нагадування",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Автоматичні сповіщення про свята, початок постів та ваші пам'ятні дати",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Permission Banner (Android 13+)
            if (!uiState.hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                item {
                    Card(
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(containerColor = litColors.feastCrimsonContainer),
                        border = BorderStroke(1.dp, litColors.feastCrimson.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.NotificationsOff,
                                    contentDescription = "Потрібен дозвіл на сповіщення",
                                    tint = litColors.feastCrimson
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Сповіщення вимкнені системою",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = litColors.feastCrimson
                                )
                            }
                            Text(
                                text = "Надайте дозвіл на сповіщення, щоб вчасно отримувати нагадування про церковні свята та пости.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Button(
                                onClick = {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                },
                                modifier = Modifier.testTag("grant_notifications_button")
                            ) {
                                Text("Дозволити сповіщення")
                            }
                        }
                    }
                }
            }

            // Master Settings Card
            item {
                Card(
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = litColors.cardElevated),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ReminderToggleRow(
                            title = "Щоденні нагадування",
                            subtitle = "Головний перемикач усіх запланованих сповіщень",
                            checked = settings.enabled,
                            onCheckedChange = { enabled ->
                                if (enabled && !uiState.hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                                onUpdateSettings { it.copy(enabled = enabled) }
                            },
                            testTag = "switch_master_reminders"
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .clickable(enabled = settings.enabled) { showTimePicker = true }
                                .padding(vertical = 10.dp)
                                .testTag("reminder_time_row"),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Schedule,
                                    contentDescription = "Час сповіщення",
                                    tint = litColors.goldAccent
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Час ранкового нагадування",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Щоденна перевірка календаря",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Text(
                                text = "%02d:%02d".format(settings.hour, settings.minute),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                        ReminderToggleRow(
                            title = "Напередодні ввечері (о 19:00)",
                            subtitle = "Нагадувати за 1 день до свята чи посту перед вечірнім богослужінням",
                            checked = settings.notifyEveningBefore,
                            enabled = settings.enabled,
                            onCheckedChange = { checked ->
                                onUpdateSettings { it.copy(notifyEveningBefore = checked) }
                            },
                            testTag = "switch_evening_before"
                        )
                    }
                }
            }

            // Categories Card
            item {
                Card(
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = litColors.cardElevated),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Категорії подій",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        ReminderToggleRow(
                            title = "Дванадесяті та Великі свята",
                            subtitle = "Пасха, Різдво, Богоявлення, Покрова, свт. Миколай та інші",
                            checked = settings.notifyGreatFeasts,
                            enabled = settings.enabled,
                            onCheckedChange = { checked ->
                                onUpdateSettings { it.copy(notifyGreatFeasts = checked) }
                            },
                            testTag = "switch_great_feasts"
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                        ReminderToggleRow(
                            title = "Початок багатоденних постів",
                            subtitle = "Великий, Петрів, Успенський та Різдвяний пости",
                            checked = settings.notifyMultiDayFasts,
                            enabled = settings.enabled,
                            onCheckedChange = { checked ->
                                onUpdateSettings { it.copy(notifyMultiDayFasts = checked) }
                            },
                            testTag = "switch_multiday_fasts"
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                        ReminderToggleRow(
                            title = "Одноденні пости",
                            subtitle = "Кожна середа і п'ятниця, Святвечори, Усікновення, Воздвиження",
                            checked = settings.notifyOneDayFasts,
                            enabled = settings.enabled,
                            onCheckedChange = { checked ->
                                onUpdateSettings { it.copy(notifyOneDayFasts = checked) }
                            },
                            testTag = "switch_oneday_fasts"
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                        ReminderToggleRow(
                            title = "Батьківські поминальні суботи",
                            subtitle = "М'ясопусна, Великопосні суботи, Радониця, Троїцька, Димитрівська",
                            checked = settings.notifyMemorialSaturdays,
                            enabled = settings.enabled,
                            onCheckedChange = { checked ->
                                onUpdateSettings { it.copy(notifyMemorialSaturdays = checked) }
                            },
                            testTag = "switch_memorial_saturdays"
                        )
                    }
                }
            }

            // Test notification button
            item {
                OutlinedButton(
                    onClick = {
                        if (!uiState.hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            val sent = onSendTestNotification()
                            onShowMessage(
                                if (sent) "Тестове сповіщення надіслано" else "Надайте дозвіл на сповіщення"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("test_notification_button"),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(
                        imageVector = Icons.Filled.Send,
                        contentDescription = "Надіслати тестове сповіщення",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Надіслати тестове сповіщення зараз")
                }
            }

            // Personal Reminders Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Мої нагадування на дати",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Button(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.testTag("add_custom_reminder_button"),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Додати нагадування",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Додати")
                    }
                }
            }

            if (uiState.customReminders.isEmpty()) {
                item {
                    Card(
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.NotificationsActive,
                                contentDescription = null,
                                tint = litColors.goldAccent,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = "Поки немає власних нагадувань",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Натисніть «Додати» або значок дзвіночка на будь-якому дні календаря, щоб зберегти особисте нагадування чи нотатку.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(uiState.customReminders, key = { it.id }) { reminder ->
                    CustomReminderRow(
                        reminder = reminder,
                        onOpenDate = {
                            CalendarDate.parseIso(reminder.isoDate)?.let(onOpenDateInCalendar)
                        },
                        onDelete = { onDeleteCustomReminder(reminder.id) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }
        }
    }

    if (showTimePicker) {
        val timeState = rememberTimePickerState(
            initialHour = settings.hour,
            initialMinute = settings.minute,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Оберіть час сповіщення") },
            text = {
                TimePicker(state = timeState)
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateSettings {
                            it.copy(hour = timeState.hour, minute = timeState.minute)
                        }
                        showTimePicker = false
                    },
                    modifier = Modifier.testTag("confirm_time_picker_button")
                ) {
                    Text("Зберегти")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Скасувати")
                }
            }
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                                timeInMillis = millis
                            }
                            val picked = CalendarDate(
                                year = cal.get(Calendar.YEAR),
                                month = cal.get(Calendar.MONTH) + 1,
                                day = cal.get(Calendar.DAY_OF_MONTH)
                            )
                            showDatePicker = false
                            onPickDateForReminder(picked)
                        }
                    },
                    enabled = datePickerState.selectedDateMillis != null
                ) {
                    Text("Далі")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Скасувати")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun ReminderToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = if (enabled) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                }
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            modifier = Modifier.testTag(testTag)
        )
    }
}

@Composable
private fun CustomReminderRow(
    reminder: CustomReminder,
    onOpenDate: () -> Unit,
    onDelete: () -> Unit
) {
    val litColors = LocalLiturgicalColors.current
    val parsedDate = CalendarDate.parseIso(reminder.isoDate)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onOpenDate)
            .testTag("custom_reminder_${reminder.id}"),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = litColors.cardElevated),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = parsedDate?.formatUkrainianFull() ?: reminder.isoDate,
                    style = MaterialTheme.typography.labelMedium,
                    color = litColors.feastCrimson,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = reminder.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (reminder.note.isNotBlank()) {
                    Text(
                        text = reminder.note,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier.testTag("delete_reminder_${reminder.id}")
            ) {
                Icon(
                    imageVector = Icons.Filled.DeleteOutline,
                    contentDescription = "Видалити нагадування",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SetMeal
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.domain.model.FastingType
import com.example.domain.model.FeastRank
import com.example.domain.model.LiturgicalDay
import com.example.ui.theme.LocalLiturgicalColors

@Composable
fun FeastRankBadge(
    rank: FeastRank,
    modifier: Modifier = Modifier
) {
    val litColors = LocalLiturgicalColors.current
    val (bg, fg) = when (rank) {
        FeastRank.PASCHA, FeastRank.TWELVE_GREAT ->
            litColors.feastCrimsonContainer to litColors.feastCrimson
        FeastRank.GREAT ->
            litColors.goldContainer to litColors.goldAccent
        FeastRank.MEMORIAL ->
            litColors.lentPurpleContainer to litColors.lentPurple
        FeastRank.MAJOR_SAINT ->
            MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        FeastRank.SUNDAY ->
            litColors.feastCrimsonContainer.copy(alpha = 0.7f) to litColors.feastCrimson
        FeastRank.ORDINARY ->
            MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = bg
    ) {
        Text(
            text = rank.labelUk,
            style = MaterialTheme.typography.labelMedium,
            color = fg,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
        )
    }
}

@Composable
fun FastingStatusBadge(
    fastingType: FastingType,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val litColors = LocalLiturgicalColors.current
    val (bg, fg, icon) = fastingVisuals(fastingType, litColors)

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = bg
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = fastingType.titleUk,
                tint = fg,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (compact) fastingType.shortUk else fastingType.titleUk,
                style = MaterialTheme.typography.labelMedium,
                color = fg,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun fastingVisuals(
    fastingType: FastingType,
    litColors: com.example.ui.theme.LiturgicalColors
): Triple<Color, Color, ImageVector> {
    return when (fastingType) {
        FastingType.NONE, FastingType.FAST_FREE -> Triple(
            litColors.fastFreeGreenContainer,
            litColors.fastFreeGreen,
            Icons.Filled.AutoAwesome
        )
        FastingType.DAIRY_ALLOWED -> Triple(
            litColors.goldContainer,
            litColors.goldAccent,
            Icons.Filled.Restaurant
        )
        FastingType.FISH_ALLOWED -> Triple(
            litColors.fastingAmberContainer,
            litColors.fastingAmber,
            Icons.Filled.SetMeal
        )
        FastingType.WITH_OIL, FastingType.NO_OIL -> Triple(
            litColors.lentPurpleContainer,
            litColors.lentPurple,
            Icons.Filled.Spa
        )
        FastingType.STRICT_DRY -> Triple(
            litColors.feastCrimsonContainer,
            litColors.feastCrimson,
            Icons.Filled.Spa
        )
    }
}

@Composable
fun LiturgicalDayDetailCard(
    day: LiturgicalDay,
    hasCustomReminder: Boolean,
    onToggleReminder: () -> Unit,
    modifier: Modifier = Modifier,
    onAskAi: (() -> Unit)? = null
) {
    val litColors = LocalLiturgicalColors.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = litColors.cardElevated),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top Date & Reminder Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${day.date.weekdayName()}, ${day.date.formatUkrainianFull()}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${day.date.formatJulianFull()} · ${day.weekDescription}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                FeastRankBadge(rank = day.feastRank)
            }

            // Title & Secondary Title
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = day.feastTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!day.secondaryTitle.isNullOrBlank()) {
                    Text(
                        text = day.secondaryTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Fasting Section
            val (fastBg, fastFg, fastIcon) = fastingVisuals(day.fastingType, litColors)
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = fastBg.copy(alpha = 0.65f),
                border = BorderStroke(1.dp, fastFg.copy(alpha = 0.25f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(fastFg.copy(alpha = 0.14f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = fastIcon,
                            contentDescription = "Статус посту",
                            tint = fastFg,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = day.fastingType.titleUk,
                            style = MaterialTheme.typography.titleSmall,
                            color = fastFg,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = day.fastingNote,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Saints of the day
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Пам'ять святих та події дня",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                day.saints.forEach { saint ->
                    Row(verticalAlignment = Alignment.Top) {
                        Text(
                            text = "✦",
                            style = MaterialTheme.typography.bodySmall,
                            color = litColors.goldAccent,
                            modifier = Modifier.padding(top = 2.dp, end = 8.dp)
                        )
                        Text(
                            text = saint,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Readings & Reflection
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.MenuBook,
                            contentDescription = "Читання Писання",
                            tint = litColors.goldAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = day.readingRef,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = day.reflection,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Reminder Action Button
            if (hasCustomReminder) {
                OutlinedButton(
                    onClick = onToggleReminder,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("day_reminder_toggle_button"),
                    shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(1.dp, litColors.feastCrimson)
                ) {
                    Icon(
                        imageVector = Icons.Filled.NotificationsActive,
                        contentDescription = "Нагадування встановлено",
                        tint = litColors.feastCrimson,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Нагадування увімкнено (натисніть, щоб прибрати)",
                        color = litColors.feastCrimson
                    )
                }
                } else {
                Button(
                    onClick = onToggleReminder,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("day_reminder_toggle_button"),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.NotificationsNone,
                        contentDescription = "Нагадати про цей день",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Нагадати про цей день")
                }
            }

            if (onAskAi != null) {
                OutlinedButton(
                    onClick = onAskAi,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("day_ask_ai_button"),
                    shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(1.dp, litColors.goldAccent.copy(alpha = 0.55f))
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = "Запитати духовного помічника",
                        tint = litColors.goldAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Запитати духовного помічника",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun AddCustomReminderDialog(
    day: LiturgicalDay,
    onDismiss: () -> Unit,
    onSave: (title: String, note: String) -> Unit
) {
    var title by remember(day.date) { mutableStateOf(day.feastTitle) }
    var note by remember(day.date) { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Нагадування на ${day.date.formatUkrainianShort()}",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "${day.date.formatJulianFull()} · ${day.fastingType.shortUk}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Назва нагадування") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reminder_title_input")
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Нотатка (необов'язково)") },
                    placeholder = { Text("Напр.: Піти на Всенічну, взяти просфору") },
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reminder_note_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(title, note) },
                modifier = Modifier.testTag("confirm_save_reminder_button")
            ) {
                Text("Зберегти")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_reminder_dialog_button")
            ) {
                Text("Скасувати")
            }
        }
    )
}

fun formatDaysCountdown(days: Int): String {
    return when {
        days < 0 -> "Минуло"
        days == 0 -> "Сьогодні"
        days == 1 -> "Завтра"
        else -> {
            val mod100 = days % 100
            val mod10 = days % 10
            val word = when {
                mod100 in 11..14 -> "днів"
                mod10 == 1 -> "день"
                mod10 in 2..4 -> "дні"
                else -> "днів"
            }
            "Через $days $word"
        }
    }
}

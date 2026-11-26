package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Church
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.domain.model.CalendarDate
import com.example.domain.model.LiturgicalDay
import com.example.ui.CalendarUiState
import com.example.ui.components.FastingStatusBadge
import com.example.ui.components.FeastRankBadge
import com.example.ui.components.formatDaysCountdown
import com.example.ui.theme.LocalLiturgicalColors

@Composable
fun TodayScreen(
    uiState: CalendarUiState,
    onOpenDayDetail: (LiturgicalDay) -> Unit,
    onToggleReminder: (LiturgicalDay) -> Unit,
    onOpenCalendarTab: () -> Unit,
    onOpenFeastsTab: () -> Unit,
    onOpenAiAdvisor: (LiturgicalDay) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = uiState.todayLiturgical
    val litColors = LocalLiturgicalColors.current
    val hasTodayReminder = uiState.customReminders.any { it.isoDate == today.date.toIsoString() }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 760.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Title Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Церковний календар УПЦ",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "За традиційним юліанським (старим) стилем",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Surface(
                        shape = CircleShape,
                        color = litColors.goldContainer,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable(onClick = onOpenCalendarTab)
                            .testTag("today_open_calendar_chip")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CalendarMonth,
                                contentDescription = "Відкрити календар",
                                tint = litColors.goldAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = today.date.formatJulianShort(),
                                style = MaterialTheme.typography.labelMedium,
                                color = litColors.goldAccent,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Hero Card with Liturgical Artwork
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("today_hero_card"),
                    shape = MaterialTheme.shapes.extraLarge,
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Image(
                            painter = painterResource(id = R.drawable.img_hero_liturgical),
                            contentDescription = "Візантійське світло храму",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xD62A1315),
                                            Color(0xE83D1117),
                                            Color(0xF5280D10)
                                        )
                                    )
                                )
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "СЬОГОДНІ · ${today.date.weekdayName().uppercase()}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color(0xFFEBD29A)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = today.date.formatUkrainianFull(),
                                        style = MaterialTheme.typography.displayMedium,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${today.date.formatJulianFull()} · ${today.weekDescription}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFEADFD8)
                                    )
                                }

                                IconButton(
                                    onClick = { onToggleReminder(today) },
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = Color.White.copy(alpha = 0.16f),
                                        contentColor = Color(0xFFFFE7AE)
                                    ),
                                    modifier = Modifier.testTag("today_hero_reminder_button")
                                ) {
                                    Icon(
                                        imageVector = if (hasTodayReminder) {
                                            Icons.Filled.NotificationsActive
                                        } else {
                                            Icons.Outlined.NotificationsNone
                                        },
                                        contentDescription = "Нагадати про сьогоднішній день"
                                    )
                                }
                            }

                            Text(
                                text = today.feastTitle,
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White
                            )

                            if (!today.secondaryTitle.isNullOrBlank()) {
                                Text(
                                    text = today.secondaryTitle,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFFEEDFD7)
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                FeastRankBadge(rank = today.feastRank)
                                FastingStatusBadge(fastingType = today.fastingType, compact = true)
                            }
                        }
                    }
                }
            }

            // Spiritual AI Advisor Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.large)
                        .clickable { onOpenAiAdvisor(today) }
                        .testTag("today_ai_advisor_card"),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = litColors.goldContainer),
                    border = BorderStroke(1.dp, litColors.goldAccent.copy(alpha = 0.45f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(litColors.goldAccent.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = "Духовний помічник",
                                tint = litColors.goldAccent,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Духовний помічник (AI)",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Пояснення свята, молитва на день або ідеї пісних страв",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Відкрити помічника",
                            tint = litColors.goldAccent
                        )
                    }
                }
            }

            // Fasting Guidance Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.large)
                        .clickable(onClick = onOpenFeastsTab)
                        .testTag("today_fasting_card"),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = litColors.cardElevated),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Піст та устав трапези",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            FastingStatusBadge(fastingType = today.fastingType, compact = true)
                        }
                        Text(
                            text = today.fastingNote,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Saints & Scripture Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("today_saints_card"),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = litColors.cardElevated),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Пам'ять святих дня",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        today.saints.forEach { saint ->
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

                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
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
                                        text = today.readingRef,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = today.reflection,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Upcoming Highlights Section
            item {
                Text(
                    text = "Найближчі події",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            val highlights = uiState.upcomingHighlights
            highlights.nextMajorFeast?.let { feast ->
                item {
                    UpcomingHighlightCard(
                        categoryLabel = "Найближче велике свято",
                        day = feast,
                        today = uiState.today,
                        icon = Icons.Filled.Church,
                        accentColor = litColors.feastCrimson,
                        containerColor = litColors.feastCrimsonContainer,
                        onClick = { onOpenDayDetail(feast) },
                        testTag = "upcoming_feast_card"
                    )
                }
            }

            highlights.nextFastDay?.let { fast ->
                item {
                    UpcomingHighlightCard(
                        categoryLabel = "Початок посту",
                        day = fast,
                        today = uiState.today,
                        icon = Icons.Filled.Spa,
                        accentColor = litColors.lentPurple,
                        containerColor = litColors.lentPurpleContainer,
                        onClick = { onOpenDayDetail(fast) },
                        testTag = "upcoming_fast_card"
                    )
                }
            }

            highlights.nextMemorialDay?.let { memorial ->
                item {
                    UpcomingHighlightCard(
                        categoryLabel = "Поминальна субота",
                        day = memorial,
                        today = uiState.today,
                        icon = Icons.Filled.NotificationsActive,
                        accentColor = litColors.goldAccent,
                        containerColor = litColors.goldContainer,
                        onClick = { onOpenDayDetail(memorial) },
                        testTag = "upcoming_memorial_card"
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }
        }
    }
}

@Composable
private fun UpcomingHighlightCard(
    categoryLabel: String,
    day: LiturgicalDay,
    today: CalendarDate,
    icon: ImageVector,
    accentColor: Color,
    containerColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    val litColors = LocalLiturgicalColors.current
    val daysDiff = today.daysUntil(day.date)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = litColors.cardElevated),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(containerColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = categoryLabel,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = categoryLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = accentColor,
                        fontWeight = FontWeight.SemiBold
                    )
                    Surface(
                        shape = CircleShape,
                        color = containerColor.copy(alpha = 0.7f)
                    ) {
                        Text(
                            text = formatDaysCountdown(daysDiff),
                            style = MaterialTheme.typography.labelSmall,
                            color = accentColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = day.feastTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${day.date.formatUkrainianFull()} · ${day.date.formatJulianShort()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Детальніше",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

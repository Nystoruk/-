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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.domain.model.FastingPeriodSummary
import com.example.domain.model.FeastRank
import com.example.domain.model.LiturgicalDay
import com.example.ui.CalendarUiState
import com.example.ui.components.FeastRankBadge
import com.example.ui.components.formatDaysCountdown
import com.example.ui.theme.LocalLiturgicalColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeastsAndFastsScreen(
    uiState: CalendarUiState,
    onChangeYear: (Int) -> Unit,
    onOpenDayDetail: (LiturgicalDay) -> Unit,
    onToggleReminder: (LiturgicalDay) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val customDates = uiState.customReminders.map { it.isoDate }.toSet()

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
            // Top Year Switcher & Segmented Tab
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Свята та пости",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { onChangeYear(-1) },
                                modifier = Modifier.testTag("feasts_prev_year_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                    contentDescription = "Попередній рік"
                                )
                            }
                            Text(
                                text = "${uiState.feastsYear} р.",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(
                                onClick = { onChangeYear(1) },
                                modifier = Modifier.testTag("feasts_next_year_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "Наступний рік"
                                )
                            }
                        }
                    }

                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        SegmentedButton(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                            modifier = Modifier.testTag("segment_feasts")
                        ) {
                            Text("Головні свята")
                        }
                        SegmentedButton(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                            modifier = Modifier.testTag("segment_fasts")
                        ) {
                            Text("Пости і суботи")
                        }
                    }
                }
            }

            if (selectedTab == 0) {
                // Pascha Banner
                val pascha = uiState.yearMajorFeasts.firstOrNull { it.feastRank == FeastRank.PASCHA }
                item {
                    Card(
                        shape = MaterialTheme.shapes.extraLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.extraLarge)
                            .clickable(enabled = pascha != null) { pascha?.let(onOpenDayDetail) }
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Image(
                                painter = painterResource(id = R.drawable.img_feast_dome),
                                contentDescription = "Золоті бані православного храму",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color(0x331B0E0F),
                                                Color(0xDD2C0E12)
                                            )
                                        )
                                    )
                            )
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "ПАСХАЛІЯ ${uiState.feastsYear} РОКУ",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFFFFE09A)
                                )
                                Text(
                                    text = if (pascha != null) {
                                        "Великдень — ${pascha.date.formatUkrainianFull()}"
                                    } else {
                                        "Головні православні свята"
                                    },
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color.White
                                )
                                if (pascha != null) {
                                    Text(
                                        text = "${pascha.date.formatJulianFull()} · Дванадесяті та Великі свята УПЦ",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFEFE4DC)
                                    )
                                }
                            }
                        }
                    }
                }

                items(uiState.yearMajorFeasts, key = { it.date.toIsoString() }) { feast ->
                    MajorFeastRowCard(
                        feast = feast,
                        today = uiState.today,
                        hasReminder = customDates.contains(feast.date.toIsoString()),
                        onClick = { onOpenDayDetail(feast) },
                        onToggleReminder = { onToggleReminder(feast) }
                    )
                }
            } else {
                // Fasts Banner
                item {
                    Card(
                        shape = MaterialTheme.shapes.extraLarge,
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = LocalLiturgicalColors.current.cardElevated
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column {
                            Image(
                                painter = painterResource(id = R.drawable.img_fasting_bread),
                                contentDescription = "Просфора, оливкова гілка та свічка",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(155.dp)
                            )
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Духовний зміст посту",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Піст у Православній Церкві — це час оновлення душі через стриманість, посилену молитву, покаяння та справи милосердя до ближніх.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Багатоденні пости ${uiState.feastsYear} року",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                items(uiState.yearFastingPeriods, key = { it.id }) { period ->
                    FastingPeriodCard(period = period)
                }

                item {
                    OneDayAndFastFreeSummaryCard()
                }

                item {
                    Text(
                        text = "Батьківські суботи та Радониця",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                items(uiState.yearMemorialDays, key = { it.date.toIsoString() }) { memorial ->
                    MajorFeastRowCard(
                        feast = memorial,
                        today = uiState.today,
                        hasReminder = customDates.contains(memorial.date.toIsoString()),
                        onClick = { onOpenDayDetail(memorial) },
                        onToggleReminder = { onToggleReminder(memorial) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(10.dp)) }
        }
    }
}

@Composable
private fun MajorFeastRowCard(
    feast: LiturgicalDay,
    today: com.example.domain.model.CalendarDate,
    hasReminder: Boolean,
    onClick: () -> Unit,
    onToggleReminder: () -> Unit
) {
    val litColors = LocalLiturgicalColors.current
    val diff = today.daysUntil(feast.date)
    val isUpcoming = diff >= 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onClick)
            .testTag("feast_item_${feast.date.toIsoString()}"),
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
            // Date box
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = if (feast.feastRank == FeastRank.MEMORIAL) {
                    litColors.lentPurpleContainer
                } else {
                    litColors.feastCrimsonContainer
                },
                modifier = Modifier.width(62.dp)
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val accent = if (feast.feastRank == FeastRank.MEMORIAL) {
                        litColors.lentPurple
                    } else {
                        litColors.feastCrimson
                    }
                    Text(
                        text = feast.date.day.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        color = accent,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = com.example.domain.model.CalendarDate.monthGenitiveShort(feast.date.month),
                        style = MaterialTheme.typography.labelSmall,
                        color = accent
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FeastRankBadge(rank = feast.feastRank)
                    if (feast.date.year == today.year && isUpcoming) {
                        Surface(
                            shape = CircleShape,
                            color = litColors.goldContainer
                        ) {
                            Text(
                                text = formatDaysCountdown(diff),
                                style = MaterialTheme.typography.labelSmall,
                                color = litColors.goldAccent,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
                Text(
                    text = feast.feastTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${feast.date.weekdayName()} · ${feast.date.formatJulianShort()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = onToggleReminder,
                modifier = Modifier.testTag("feast_bell_${feast.date.toIsoString()}")
            ) {
                Icon(
                    imageVector = if (hasReminder) {
                        Icons.Filled.NotificationsActive
                    } else {
                        Icons.Outlined.NotificationsNone
                    },
                    contentDescription = "Нагадати про свято",
                    tint = if (hasReminder) litColors.feastCrimson else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun FastingPeriodCard(period: FastingPeriodSummary) {
    val litColors = LocalLiturgicalColors.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = litColors.cardElevated),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = litColors.lentPurpleContainer
            ) {
                Text(
                    text = "${period.startDate.formatUkrainianFull()} — ${period.endDate.formatUkrainianFull()}",
                    style = MaterialTheme.typography.labelMedium,
                    color = litColors.lentPurple,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                )
            }
            Text(
                text = period.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = period.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface
            ) {
                Text(
                    text = "Правила трапези: ${period.dietarySummary}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Composable
private fun OneDayAndFastFreeSummaryCard() {
    val litColors = LocalLiturgicalColors.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = litColors.cardElevated),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Одноденні пости та суцільні седмиці",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "• Середа і п'ятниця протягом усього року (крім суцільних седмиць) — на спомин зради і Хресних страждань Спасителя.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "• Суворі одноденні пости: Хрещенський святвечір (18 січня), Усікновення глави Іоанна Предтечі (11 вересня), Воздвиження Хреста Господнього (27 вересня).",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "• Суцільні седмиці (посту немає навіть у середу та п'ятницю): Різдвяні святки (7–17 січня), седмиця про митаря і фарисея, Світла Пасхальна седмиця, Троїцька седмиця. У Сирну седмицю (Масляну) м'ясо не вживається, але дозволені молочні продукти та риба.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

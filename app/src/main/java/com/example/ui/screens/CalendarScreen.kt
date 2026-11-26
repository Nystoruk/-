package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.domain.model.CalendarDate
import com.example.domain.model.FastingType
import com.example.domain.model.LiturgicalDay
import com.example.ui.CalendarUiState
import com.example.ui.components.FastingStatusBadge
import com.example.ui.components.LiturgicalDayDetailCard
import com.example.ui.theme.LocalLiturgicalColors

@Composable
fun CalendarScreen(
    uiState: CalendarUiState,
    onSelectDate: (CalendarDate) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onJumpToToday: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onToggleReminder: (LiturgicalDay) -> Unit,
    onOpenAiAdvisor: (LiturgicalDay) -> Unit,
    modifier: Modifier = Modifier
) {
    var isSearchOpen by rememberSaveable { mutableStateOf(false) }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 720.dp

        if (isWideScreen) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1.05f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CalendarTopBar(
                        uiState = uiState,
                        isSearchOpen = isSearchOpen,
                        onToggleSearch = {
                            isSearchOpen = !isSearchOpen
                            if (!isSearchOpen) onSearchQueryChange("")
                        },
                        onJumpToToday = onJumpToToday,
                        onPreviousMonth = onPreviousMonth,
                        onNextMonth = onNextMonth,
                        onSearchQueryChange = onSearchQueryChange
                    )
                    if (isSearchOpen && uiState.searchQuery.isNotBlank()) {
                        SearchResultsList(
                            results = uiState.searchResults,
                            onSelectResult = {
                                onSelectDate(it.date)
                                isSearchOpen = false
                                onSearchQueryChange("")
                            }
                        )
                    } else {
                        MonthGridCard(
                            uiState = uiState,
                            onSelectDate = onSelectDate
                        )
                        CalendarLegendRow()
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(0.95f)
                        .verticalScroll(rememberScrollState())
                ) {
                    LiturgicalDayDetailCard(
                        day = uiState.selectedDayLiturgical,
                        hasCustomReminder = uiState.customReminders.any {
                            it.isoDate == uiState.selectedDate.toIsoString()
                        },
                        onToggleReminder = { onToggleReminder(uiState.selectedDayLiturgical) },
                        onAskAi = { onOpenAiAdvisor(uiState.selectedDayLiturgical) }
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    CalendarTopBar(
                        uiState = uiState,
                        isSearchOpen = isSearchOpen,
                        onToggleSearch = {
                            isSearchOpen = !isSearchOpen
                            if (!isSearchOpen) onSearchQueryChange("")
                        },
                        onJumpToToday = onJumpToToday,
                        onPreviousMonth = onPreviousMonth,
                        onNextMonth = onNextMonth,
                        onSearchQueryChange = onSearchQueryChange
                    )
                }

                if (isSearchOpen && uiState.searchQuery.isNotBlank()) {
                    if (uiState.searchResults.isEmpty()) {
                        item {
                            Card(
                                shape = MaterialTheme.shapes.large,
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Text(
                                    text = "За запитом «${uiState.searchQuery}» нічого не знайдено у ${uiState.displayedYear} році.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(20.dp)
                                )
                            }
                        }
                    } else {
                        items(uiState.searchResults, key = { it.date.toIsoString() }) { day ->
                            SearchResultItem(
                                day = day,
                                onClick = {
                                    onSelectDate(day.date)
                                    isSearchOpen = false
                                    onSearchQueryChange("")
                                }
                            )
                        }
                    }
                } else {
                    item {
                        MonthGridCard(
                            uiState = uiState,
                            onSelectDate = onSelectDate
                        )
                    }

                    item {
                        CalendarLegendRow()
                    }

                    item {
                        LiturgicalDayDetailCard(
                            day = uiState.selectedDayLiturgical,
                            hasCustomReminder = uiState.customReminders.any {
                                it.isoDate == uiState.selectedDate.toIsoString()
                            },
                            onToggleReminder = { onToggleReminder(uiState.selectedDayLiturgical) },
                            onAskAi = { onOpenAiAdvisor(uiState.selectedDayLiturgical) }
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
private fun CalendarTopBar(
    uiState: CalendarUiState,
    isSearchOpen: Boolean,
    onToggleSearch: () -> Unit,
    onJumpToToday: () -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onSearchQueryChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onPreviousMonth,
                    modifier = Modifier.testTag("calendar_prev_month_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Попередній місяць"
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${CalendarDate.monthNominative(uiState.displayedMonth)} ${uiState.displayedYear}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                IconButton(
                    onClick = onNextMonth,
                    modifier = Modifier.testTag("calendar_next_month_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Наступний місяць"
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(
                    onClick = onJumpToToday,
                    modifier = Modifier.testTag("calendar_today_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Today,
                        contentDescription = "Сьогодні",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Сьогодні")
                }
                IconButton(
                    onClick = onToggleSearch,
                    modifier = Modifier.testTag("calendar_search_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isSearchOpen) Icons.Filled.Close else Icons.Filled.Search,
                        contentDescription = if (isSearchOpen) "Закрити пошук" else "Пошук свят і святих"
                    )
                }
            }
        }

        AnimatedVisibility(visible = isSearchOpen) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Пошук свята, святого чи посту (напр. Миколай)") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Очистити пошук"
                            )
                        }
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("calendar_search_input")
            )
        }
    }
}

@Composable
private fun MonthGridCard(
    uiState: CalendarUiState,
    onSelectDate: (CalendarDate) -> Unit
) {
    val litColors = LocalLiturgicalColors.current
    val firstDayOfMonth = CalendarDate(uiState.displayedYear, uiState.displayedMonth, 1)
    val leadingEmptySlots = firstDayOfMonth.dayOfWeek - 1 // 0 for Mon .. 6 for Sun
    val customReminderDates = uiState.customReminders.map { it.isoDate }.toSet()

    val cells = buildList<LiturgicalDay?> {
        repeat(leadingEmptySlots) { add(null) }
        addAll(uiState.monthDays)
        while (size % 7 != 0) add(null)
    }

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = litColors.cardElevated),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Weekday headers
            Row(modifier = Modifier.fillMaxWidth()) {
                CalendarDate.weekdayShortNames.forEachIndexed { index, label ->
                    val isWeekend = index == 6
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isWeekend) litColors.feastCrimson else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (isWeekend) FontWeight.Bold else FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Weeks
            cells.chunked(7).forEach { week ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    week.forEach { day ->
                        if (day == null) {
                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(0.95f)
                            )
                        } else {
                            CalendarDayCell(
                                day = day,
                                isSelected = day.date == uiState.selectedDate,
                                isToday = day.date == uiState.today,
                                hasReminder = customReminderDates.contains(day.date.toIsoString()),
                                onClick = { onSelectDate(day.date) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    day: LiturgicalDay,
    isSelected: Boolean,
    isToday: Boolean,
    hasReminder: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val litColors = LocalLiturgicalColors.current

    val cellBg = when {
        isSelected -> MaterialTheme.colorScheme.primary
        day.isMajorFeast -> litColors.feastCrimsonContainer.copy(alpha = 0.65f)
        day.isFastDay -> litColors.lentPurpleContainer.copy(alpha = 0.45f)
        day.fastingType == FastingType.FAST_FREE -> litColors.fastFreeGreenContainer.copy(alpha = 0.45f)
        else -> Color.Transparent
    }

    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        day.isMajorFeast || day.date.isSunday -> litColors.feastCrimson
        else -> MaterialTheme.colorScheme.onSurface
    }

    val borderStroke = when {
        isToday && !isSelected -> BorderStroke(1.5.dp, litColors.goldAccent)
        else -> null
    }

    Box(
        modifier = modifier
            .aspectRatio(0.95f)
            .clip(RoundedCornerShape(12.dp))
            .background(cellBg)
            .then(
                if (borderStroke != null) {
                    Modifier.border(borderStroke, RoundedCornerShape(12.dp))
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onClick)
            .testTag("calendar_day_${day.date.day}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = day.date.day.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected || day.isMajorFeast || isToday) FontWeight.Bold else FontWeight.Medium,
                color = textColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (day.isMajorFeast) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Color.White else litColors.feastCrimson)
                    )
                } else if (day.isMemorialDay) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Color.White else litColors.goldAccent)
                    )
                } else if (day.isFastDay) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Color.White else litColors.lentPurple)
                    )
                }
                if (hasReminder) {
                    Icon(
                        imageVector = Icons.Filled.NotificationsActive,
                        contentDescription = "Є нагадування",
                        tint = if (isSelected) Color.White else litColors.goldAccent,
                        modifier = Modifier.size(9.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarLegendRow() {
    val litColors = LocalLiturgicalColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendDot(color = litColors.feastCrimson, label = "Свято")
        LegendDot(color = litColors.lentPurple, label = "Піст")
        LegendDot(color = litColors.goldAccent, label = "Сьогодні / Поминання")
        LegendDot(color = litColors.fastFreeGreen, label = "Суцільна")
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SearchResultsList(
    results: List<LiturgicalDay>,
    onSelectResult: (LiturgicalDay) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        results.forEach { day ->
            SearchResultItem(day = day, onClick = { onSelectResult(day) })
        }
    }
}

@Composable
private fun SearchResultItem(
    day: LiturgicalDay,
    onClick: () -> Unit
) {
    val litColors = LocalLiturgicalColors.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = litColors.cardElevated),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = day.feastTitle,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${day.date.formatUkrainianFull()} (${day.date.formatJulianShort()})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            FastingStatusBadge(fastingType = day.fastingType, compact = true)
        }
    }
}

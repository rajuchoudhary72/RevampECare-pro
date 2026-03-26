package com.app.ecarepro.feature.message.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
import com.app.ecarepro.core.ui.component.EcareProPullToRefresh
import com.app.ecarepro.core.ui.component.LazyListLoadMoreHandler
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.message.common.InboxMessageListItem
import com.app.ecarepro.feature.message.common.MessageEmptyState
import com.app.ecarepro.feature.message.navigation.MessageNavigationGraph
import kotlinx.coroutines.launch
import java.util.Calendar

// ============== ENTRY POINT ==============

@Composable
internal fun TimelineScreen(
    viewModel: TimelineViewModel = hiltViewModel(),
    onChatClick: (MessageNavigationGraph.ChatDetail) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    TimelineScreenContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        onChatClick = onChatClick,
    )
}

// ============== CONTENT ==============

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimelineScreenContent(
    uiState: UiState<TimelineUiState>,
    handleIntent: (TimelineIntent) -> Unit,
    onChatClick: (MessageNavigationGraph.ChatDetail) -> Unit = {},
) {
    var showCalendar by remember { mutableStateOf(false) }

    UiStateHandler(
        state = uiState,
        onRetry = { handleIntent(TimelineIntent.OnRetry) },
    ) { data ->

        Column(modifier = Modifier.fillMaxSize()) {

            // Date-grouped message list with pull-to-refresh
            Box(modifier = Modifier.weight(1f)) {
                EcareProPullToRefresh(
                    isRefreshing = data.isRefreshing,
                    onRefresh = { handleIntent(TimelineIntent.OnRefresh) },
                    modifier = Modifier.fillMaxSize(),
                ) {
                    if (data.messageGroups.isEmpty()) {
                        MessageEmptyState(modifier = Modifier.fillMaxSize())
                    } else {
                        val listState = rememberLazyListState()

                        LazyListLoadMoreHandler(
                            listState = listState,
                            enabled = data.showLoadMoreView,
                            onLoadMore = { handleIntent(TimelineIntent.OnLoadMore) },
                        )

                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = 12.dp, end = 12.dp, top = 10.dp, bottom = 4.dp
                            ),
                        ) {
                            data.messageGroups.forEach { group ->
                                item(key = "header_${group.dateLabel}") {
                                    DateGroupHeader(label = group.dateLabel)
                                }
                                items(group.messages, key = { it.id }) { message ->
                                    InboxMessageListItem(
                                        message = message,
                                        onClick = {
                                            onChatClick(
                                                MessageNavigationGraph.ChatDetail(
                                                    messageId = message.id,
                                                    title = message.title,
                                                    participantName = message.senderName,
                                                    participantRole = message.preview,
                                                    participantPhotoUrl = message.senderPhoto,
                                                )
                                            )
                                        },
                                    )
                                }
                                item(key = "spacer_${group.dateLabel}") {
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Scrollable week strip at bottom — swipe up opens full calendar
            ScrollableWeekStrip(
                selectedDateMillis = data.selectedDateMillis,
                onDaySelected = { handleIntent(TimelineIntent.OnDateSelected(it)) },
                onSwipeUp = { showCalendar = true },
            )
        }

        // Full calendar bottom sheet
        if (showCalendar) {
            CalendarBottomSheet(
                selectedDateMillis = data.selectedDateMillis,
                onDateSelected = { millis ->
                    handleIntent(TimelineIntent.OnDateSelected(millis))
                    showCalendar = false
                },
                onDismiss = { showCalendar = false },
            )
        }
    }
}

// ============== DATE GROUP HEADER ==============

@Composable
private fun DateGroupHeader(label: String) {
    Text(
        text = label,
        style = MaterialTheme.appTypography.interMedium12px.copy(
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp,
        ),
        color = MaterialTheme.appColors.textSecondary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, top = 12.dp, bottom = 6.dp),
    )
}

// ============== SCROLLABLE WEEK STRIP ==============

private data class DayItem(
    val dayShort: String,   // "Mo", "Tu", "Wed" …
    val dayNumber: Int,     // 1-31
    val dateMillis: Long,
)

@Composable
private fun ScrollableWeekStrip(
    selectedDateMillis: Long,
    onDaySelected: (Long) -> Unit,
    onSwipeUp: () -> Unit,
) {
    // All days from 90 days ago → today (past only)
    val allDays = remember { generatePastDays(daysBack = 90) }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Auto-scroll so the selected date is visible (centred in view) on first load & on change
    LaunchedEffect(selectedDateMillis) {
        val index = allDays.indexOfFirst { isSameDay(it.dateMillis, selectedDateMillis) }
        if (index >= 0) {
            scope.launch {
                // Scroll so selected item sits roughly in the middle (offset by ~3 cells)
                listState.animateScrollToItem(maxOf(0, index - 3))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount < -10f) onSwipeUp()
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Drag handle pill — visual hint to swipe up
        Box(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 4.dp)
                .width(36.dp)
                .height(4.dp)
                .background(Color(0xFFDDDDDD), RoundedCornerShape(2.dp)),
        )

        // Horizontally scrollable day list
        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            itemsIndexed(allDays, key = { _, day -> day.dateMillis }) { _, day ->
                val isSelected = isSameDay(day.dateMillis, selectedDateMillis)
                DayCell(
                    day = day,
                    isSelected = isSelected,
                    onClick = { onDaySelected(day.dateMillis) },
                )
            }
        }
    }
}

@Composable
private fun DayCell(
    day: DayItem,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Day label: "Mo", "Tu" …
        Text(
            text = day.dayShort,
            style = MaterialTheme.appTypography.interRegular12px,
            color = if (isSelected) MaterialTheme.appColors.primary
            else MaterialTheme.appColors.textSecondary,
        )
        Spacer(modifier = Modifier.height(4.dp))
        // Date number inside green circle when selected
        Box(
            modifier = Modifier
                .size(34.dp)
                .then(
                    if (isSelected)
                        Modifier.background(MaterialTheme.appColors.primary, CircleShape)
                    else Modifier
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = day.dayNumber.toString(),
                style = MaterialTheme.appTypography.interSemiBold14px.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                ),
                color = if (isSelected) White else MaterialTheme.appColors.textPrimary,
            )
        }
    }
}

// ============== CALENDAR BOTTOM SHEET ==============

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarBottomSheet(
    selectedDateMillis: Long,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Future dates are not selectable — only up to end of today
    val todayEndMillis = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }
    val currentYear = remember { Calendar.getInstance().get(Calendar.YEAR) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDateMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis <= todayEndMillis
            override fun isSelectableYear(year: Int) = year <= currentYear
        },
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = White,
    ) {
        // Swipe left/right on month header to navigate previous months (built-in to DatePicker)
        DatePicker(
            state = datePickerState,
            showModeToggle = false,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = MaterialTheme.appColors.primary,
                    style = MaterialTheme.appTypography.interMedium12px,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { onDateSelected(it) }
                },
            ) {
                Text(
                    text = "OK",
                    color = MaterialTheme.appColors.primary,
                    style = MaterialTheme.appTypography.interMedium12px.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }
        }
    }
}

// ============== HELPERS ==============

/** Generates every day from [daysBack] days ago up to and including today. */
private fun generatePastDays(daysBack: Int): List<DayItem> {
    val today = Calendar.getInstance()
    val cursor = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -daysBack) }
    val days = mutableListOf<DayItem>()
    while (!cursor.after(today)) {
        days.add(
            DayItem(
                dayShort = dayShortName(cursor.get(Calendar.DAY_OF_WEEK)),
                dayNumber = cursor.get(Calendar.DAY_OF_MONTH),
                dateMillis = cursor.timeInMillis,
            )
        )
        cursor.add(Calendar.DAY_OF_MONTH, 1)
    }
    return days
}

private fun dayShortName(dayOfWeek: Int): String = when (dayOfWeek) {
    Calendar.MONDAY -> "Mo"
    Calendar.TUESDAY -> "Tu"
    Calendar.WEDNESDAY -> "Wed"
    Calendar.THURSDAY -> "Th"
    Calendar.FRIDAY -> "Fr"
    Calendar.SATURDAY -> "Sa"
    Calendar.SUNDAY -> "Su"
    else -> ""
}

private fun isSameDay(millis1: Long, millis2: Long): Boolean {
    val c1 = Calendar.getInstance().apply { timeInMillis = millis1 }
    val c2 = Calendar.getInstance().apply { timeInMillis = millis2 }
    return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
            c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
}

// ============== PREVIEW ==============

@Preview(showBackground = true)
@Composable
private fun TimelineScreenPreview() {
    EcareProTheme {
        TimelineScreenContent(
            uiState = UiState.Success(
                TimelineUiState(
                    selectedDateMillis = buildDateMillis(2025, 1, 21),
                    messageGroups = emptyList(),
                )
            ),
            handleIntent = {},
        )
    }
}

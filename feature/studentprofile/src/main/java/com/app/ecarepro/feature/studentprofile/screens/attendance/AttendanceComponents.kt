package com.app.ecarepro.feature.studentprofile.screens.attendance

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.core.domain.model.AcademicYear
import com.app.ecarepro.core.domain.model.AttendanceDetail
import com.app.ecarepro.core.domain.model.DailyAttendance
import com.app.ecarepro.core.domain.model.MonthlyAttendance
import com.app.ecarepro.core.domain.model.MonthlyAttendanceDetailResponse
import com.app.ecarepro.designsystem.core.component.EcareProDropdownField
import com.app.ecarepro.designsystem.core.component.EcareProEmptyState
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AttendanceTab(
    attendanceDTL: AttendanceDetail?,
    academicYears: List<AcademicYear>,
    selectedYearId: Int,
    expandedMonthIds: Set<Int>,
    monthlyDetailCache: Map<Int, MonthlyAttendanceDetailResponse>,
    loadingMonthIds: Set<Int>,
    isLoadingYear: Boolean,
    onYearSelected: (Int) -> Unit,
    onToggleMonth: (Int) -> Unit,
) {
    if (attendanceDTL == null) {
        EcareProEmptyState(message = "No attendance data available", icon = Icons.Default.CalendarMonth)
        return
    }

    var showYearSheet by remember { mutableStateOf(false) }
    val yearSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val selectedYearLabel = academicYears.firstOrNull { it.yrID == selectedYearId }?.session ?: ""

    Column(modifier = Modifier.fillMaxSize()) {
        // Year dropdown
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            EcareProDropdownField(
                value = selectedYearLabel,
                placeholder = "Select year",
                onClick = { showYearSheet = true }
            )
        }

        if (isLoadingYear) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.appColors.primary)
            }
            return@Column
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                AttendanceStatsSection(attendanceDTL = attendanceDTL)
            }

            val months = attendanceDTL.summaryAttendance ?: emptyList()
            items(months, key = { it.monthID }) { month ->
                MonthlyAttendanceCard(
                    month = month,
                    isExpanded = month.monthID in expandedMonthIds,
                    isLoading = month.monthID in loadingMonthIds,
                    detail = monthlyDetailCache[month.monthID],
                    onToggle = { onToggleMonth(month.monthID) }
                )
            }
        }
    }

    if (showYearSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                scope.launch { yearSheetState.hide() }.invokeOnCompletion { showYearSheet = false }
            },
            sheetState = yearSheetState,
            containerColor = White,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Select Academic Year",
                    style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 18.sp),
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                academicYears.forEach { year ->
                    val isSelected = year.yrID == selectedYearId
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onYearSelected(year.yrID)
                                scope.launch { yearSheetState.hide() }
                                    .invokeOnCompletion { showYearSheet = false }
                            }
                            .padding(vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = year.session,
                            style = MaterialTheme.appTypography.interMedium16px,
                            color = if (isSelected) MaterialTheme.appColors.primary else MaterialTheme.appColors.textPrimary
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.appColors.primary
                            )
                        }
                    }
                    HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFEEEEEE))
                }
            }
        }
    }
}

@Composable
internal fun AttendanceStatsSection(attendanceDTL: AttendanceDetail) {
    val present = attendanceDTL.present ?: 0
    val absent = attendanceDTL.absent ?: 0
    val late = attendanceDTL.late ?: 0
    val leave = attendanceDTL.leave ?: 0
    val wh = attendanceDTL.wh ?: 0
    val total = present + absent + late + leave + wh

    fun pct(value: Int): String {
        if (total == 0) return "0.0%"
        return String.format("%.1f%%", (value.toDouble() / total.toDouble()) * 100)
    }

    val badges = listOf(
        Triple("Present ${pct(present)}", Color(0xFF81C784), present),
        Triple("Absent ${pct(absent)}", Color(0xFFF48FB1), absent),
        Triple("Late ${pct(late)}", Color(0xFFFFD54F), late),
        Triple("Leave ${pct(leave)}", Color(0xFF64B5F6), leave),
        Triple("Working Holiday ${pct(wh)}", Color(0xFFBA68C8), wh),
    ).filter { it.third > 0 }

    if (badges.isEmpty()) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                badges.take(3).forEach { (label, color, _) ->
                    AttendanceBadge(label = label, color = color, modifier = Modifier.weight(1f))
                }
                repeat(maxOf(0, 3 - badges.take(3).size)) { Spacer(modifier = Modifier.weight(1f)) }
            }
            if (badges.size > 3) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    badges.drop(3).forEach { (label, color, _) ->
                        AttendanceBadge(label = label, color = color, modifier = Modifier.weight(1f))
                    }
                    repeat(maxOf(0, 3 - (badges.size - 3))) { Spacer(modifier = Modifier.weight(1f)) }
                }
            }
        }
    }
}

@Composable
internal fun AttendanceBadge(label: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(color = color, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 12.sp),
            color = White,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
internal fun MonthlyAttendanceCard(
    month: MonthlyAttendance,
    isExpanded: Boolean,
    isLoading: Boolean,
    detail: MonthlyAttendanceDetailResponse?,
    onToggle: () -> Unit,
) {
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(200),
        label = "chevron"
    )

    val presentCountText = if (detail?.presentDays != null) {
        "${detail.presentDays} / ${month.working} days present"
    } else {
        "${month.totalPresent} / ${month.working} days present"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = MaterialTheme.appColors.primary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = month.month,
                        style = MaterialTheme.appTypography.interSemiBold14px,
                        color = MaterialTheme.appColors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = presentCountText,
                        style = MaterialTheme.appTypography.interRegular12px,
                        color = MaterialTheme.appColors.textSecondary
                    )
                }

                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(rotation),
                    tint = MaterialTheme.appColors.textSecondary
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFEEEEEE))
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.appColors.primary,
                                strokeWidth = 2.dp
                            )
                        }
                    } else if (detail != null) {
                        AttendanceCalendarGrid(
                            month = month,
                            dailyAttendance = detail.attendance ?: emptyList(),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                        )
                    }
                }
            }
        }
    }
}

internal enum class AttendanceStatus(val color: Color, val useBorder: Boolean = false) {
    PRESENT(Color(0xFF81C784)),
    ABSENT(Color(0xFFF48FB1)),
    LATE(Color(0xFFFFD54F)),
    LEAVE(Color(0xFF64B5F6)),
    WORKING_HOLIDAY(Color(0xFFBA68C8)),
    HOLIDAY(Color(0xFFE0E0E0), useBorder = true),
    EMPTY(Color.Transparent);

    companion object {
        fun from(day: DailyAttendance): AttendanceStatus {
            if (day.isLate == true) return LATE
            return when (day.status) {
                1 -> PRESENT
                2 -> ABSENT
                3 -> LEAVE
                4 -> PRESENT
                5 -> HOLIDAY
                6 -> WORKING_HOLIDAY
                7 -> LATE
                else -> HOLIDAY
            }
        }
    }
}

internal data class CalendarDay(val dayNumber: Int, val status: AttendanceStatus)

@Composable
internal fun AttendanceCalendarGrid(
    month: MonthlyAttendance,
    dailyAttendance: List<DailyAttendance>,
    modifier: Modifier = Modifier,
) {
    val weeks = remember(month, dailyAttendance) {
        buildCalendarWeeks(month, dailyAttendance)
    }

    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach { label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.appTypography.interRegular12px,
                    color = MaterialTheme.appColors.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        weeks.forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { day ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp)
                            .size(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (day.status != AttendanceStatus.EMPTY && day.dayNumber > 0) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(day.status.color, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.dayNumber.toString(),
                                    style = if (day.status.useBorder)
                                        MaterialTheme.appTypography.interRegular12px
                                    else
                                        MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 12.sp),
                                    color = if (day.status.useBorder)
                                        MaterialTheme.appColors.textSecondary
                                    else
                                        White
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

internal fun buildCalendarWeeks(
    month: MonthlyAttendance,
    dailyAttendance: List<DailyAttendance>
): List<List<CalendarDay>> {
    val calendar = java.util.Calendar.getInstance()
    calendar.set(month.year, month.monthID - 1, 1)
    val daysInMonth = calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
    val firstWeekday = calendar.get(java.util.Calendar.DAY_OF_WEEK)

    val sdf = java.text.SimpleDateFormat("dd-MMM-yyyy", java.util.Locale.ENGLISH)
    val statusByDay = dailyAttendance.associate { att ->
        val date = runCatching { sdf.parse(att.attDate) }.getOrNull()
        val dayNum = if (date != null) {
            java.util.Calendar.getInstance().also { c -> c.time = date }
                .get(java.util.Calendar.DAY_OF_MONTH)
        } else -1
        dayNum to AttendanceStatus.from(att)
    }

    val weeks = mutableListOf<List<CalendarDay>>()
    var currentWeek = mutableListOf<CalendarDay>()

    repeat(firstWeekday - 1) { currentWeek.add(CalendarDay(0, AttendanceStatus.EMPTY)) }

    for (day in 1..daysInMonth) {
        currentWeek.add(CalendarDay(day, statusByDay[day] ?: AttendanceStatus.HOLIDAY))
        if (currentWeek.size == 7) {
            weeks.add(currentWeek.toList())
            currentWeek = mutableListOf()
        }
    }

    while (currentWeek.isNotEmpty() && currentWeek.size < 7) {
        currentWeek.add(CalendarDay(0, AttendanceStatus.EMPTY))
    }
    if (currentWeek.isNotEmpty()) weeks.add(currentWeek)

    return weeks
}

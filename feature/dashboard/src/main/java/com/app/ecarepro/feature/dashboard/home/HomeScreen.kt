package com.app.ecarepro.feature.dashboard.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Cake
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.CurrencyRupee
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Female
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.Male
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.QuestionAnswer
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.dashboard.AdmissionComparison
import com.app.ecarepro.core.domain.model.dashboard.BankBalance
import com.app.ecarepro.core.domain.model.dashboard.BirthdaySummary
import com.app.ecarepro.core.domain.model.dashboard.ClassSummary
import com.app.ecarepro.core.domain.model.dashboard.DashFeedItem
import com.app.ecarepro.core.domain.model.dashboard.DashboardActivity
import com.app.ecarepro.core.domain.model.dashboard.DashboardCard
import com.app.ecarepro.core.domain.model.dashboard.DashboardQuestionnaire
import com.app.ecarepro.core.domain.model.dashboard.FeeCollectionData
import com.app.ecarepro.core.domain.model.dashboard.FeeDefaulterSummary
import com.app.ecarepro.core.domain.model.dashboard.LibraryData
import com.app.ecarepro.core.domain.model.dashboard.ModeWiseData
import com.app.ecarepro.core.domain.model.dashboard.StaffAttendance
import com.app.ecarepro.core.domain.model.dashboard.StatItem
import com.app.ecarepro.core.domain.model.dashboard.StudentBirthday
import com.app.ecarepro.core.domain.model.dashboard.TimetablePeriod
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.dashboard.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val BG = Color(0xFFF5F5F5)
private val DIVIDER = Color(0xFFEEEEEE)

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navigateToQuestionnaire: () -> Unit = {},
    navigateToNotifications: () -> Unit = {},
    navigateToSettings: () -> Unit = {},
    navigateToHomeSelection: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is HomeEvent.NavigateToModule -> {}
                HomeEvent.NavigateToSettings -> navigateToSettings()
                HomeEvent.NavigateToNotifications -> navigateToNotifications()
                HomeEvent.NavigateToQuestionnaire -> navigateToQuestionnaire()
                HomeEvent.NavigateToHomeSelection -> navigateToHomeSelection()
            }
        }
    }
    HomeContent(uiState = uiState, handleIntent = viewModel::handleIntent)
}

@Composable
private fun HomeContent(uiState: HomeUiState, handleIntent: (HomeIntent) -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.appColors.primary)
                }
                uiState.isError -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(stringResource(R.string.feature_dashboard_error_loading), style = MaterialTheme.appTypography.interRegular14px)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { handleIntent(HomeIntent.OnRetry) }) {
                            Text(stringResource(R.string.feature_dashboard_retry))
                        }
                    }
                }
                else -> LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .background(BG),
                    contentPadding = PaddingValues(bottom = 16.dp),
                ) {
                    // Overview
                    val showOverview = uiState.visibility.showProCards || uiState.visibility.showCards
                    if (showOverview) {
                        item {
                            SectionHeader(
                                title = stringResource(R.string.feature_dashboard_overview),
                                icon = Icons.Outlined.GridView,
                                iconTint = Color(0xFF9C27B0),
                                iconBg = Color(0xFFF3E5F5),
                                isExpanded = uiState.isSectionExpanded(HomeSectionType.OVERVIEW),
                                onClick = { handleIntent(HomeIntent.ToggleSection(HomeSectionType.OVERVIEW)) },
                            )
                        }
                        item {
                            Column {
                            AnimatedVisibility(
                                visible = uiState.isSectionExpanded(HomeSectionType.OVERVIEW),
                                enter = expandVertically(tween(300)),
                                exit = shrinkVertically(tween(300)),
                            ) {
                                Column(Modifier.background(BG)) {
                                    uiState.totalStudentsCard?.let { card ->
                                        FullWidthStatCard(card) {
                                            handleIntent(HomeIntent.OnCardClicked(card.menuID, card.chMenuID, card.sbChMenuID))
                                        }
                                    }
                                    if (uiState.gridProCards.isNotEmpty()) {
                                        ProCardsGrid(uiState.gridProCards) { c ->
                                            handleIntent(HomeIntent.OnCardClicked(c.menuID, c.chMenuID, c.sbChMenuID))
                                        }
                                    }
                                    if (uiState.cards.isNotEmpty()) {
                                        ProCardsGrid(uiState.cards) { c ->
                                            handleIntent(HomeIntent.OnCardClicked(c.menuID, c.chMenuID, c.sbChMenuID))
                                        }
                                    }
                                    uiState.totalStaffCard?.let { card ->
                                        FullWidthStatCard(card, isStaff = true) {
                                            handleIntent(HomeIntent.OnCardClicked(card.menuID, card.chMenuID, card.sbChMenuID))
                                        }
                                    }
                                }
                            }
                            }
                        }
                    }
                    // Financial
                    val showFinancial = uiState.visibility.showFeeCollection || uiState.visibility.showBankBalance ||
                        uiState.visibility.showFeeDafaulter || uiState.visibility.showCollectionModeWise
                    if (showFinancial) {
                        item {
                            SectionHeader(
                                title = stringResource(R.string.feature_dashboard_financial_overview),
                                icon = Icons.Outlined.CurrencyRupee,
                                iconTint = Color(0xFF4CAF50),
                                iconBg = Color(0xFFE8F5E9),
                                isExpanded = uiState.isSectionExpanded(HomeSectionType.FINANCIAL),
                                onClick = { handleIntent(HomeIntent.ToggleSection(HomeSectionType.FINANCIAL)) },
                            )
                        }
                        item {
                            Column {
                            AnimatedVisibility(
                                visible = uiState.isSectionExpanded(HomeSectionType.FINANCIAL),
                                enter = expandVertically(tween(300)),
                                exit = shrinkVertically(tween(300)),
                            ) {
                                Column(Modifier.background(BG)) {
                                    if (uiState.visibility.showFeeCollection) {
                                        EstimatedCollectionCard(uiState.feeCollection) { handleIntent(HomeIntent.RefreshFeeCollection) }
                                    }
                                    if (uiState.visibility.showBankBalance && uiState.bankBalances.isNotEmpty()) {
                                        BankBalanceCard(uiState.bankBalances)
                                    }
                                    if (uiState.visibility.showFeeDafaulter) {
                                        FeeDefaulterCard(uiState.feeDefaulter)
                                    }
                                    if (uiState.visibility.showCollectionModeWise) {
                                        ModeWiseCard(uiState.modeWiseCollection) { handleIntent(HomeIntent.RefreshModeWise) }
                                    }
                                }
                            }
                            }
                        }
                    }
                    // Calendar
                    if (uiState.visibility.showActivities) {
                        item {
                            SectionHeader(
                                title = stringResource(R.string.feature_dashboard_calendar),
                                icon = Icons.Outlined.CalendarMonth,
                                iconTint = Color(0xFFFF9800),
                                iconBg = Color(0xFFFFF3E0),
                                isExpanded = uiState.isSectionExpanded(HomeSectionType.CALENDAR),
                                onClick = { handleIntent(HomeIntent.ToggleSection(HomeSectionType.CALENDAR)) },
                            )
                        }
                        item {
                            Column {
                            AnimatedVisibility(
                                visible = uiState.isSectionExpanded(HomeSectionType.CALENDAR),
                                enter = expandVertically(tween(300)),
                                exit = shrinkVertically(tween(300)),
                            ) {
                                CalendarCard(uiState.activities)
                            }
                            }
                        }
                    }
                    // Attendance
                    val showAttendance = uiState.visibility.showAttendanceSummary || uiState.visibility.showStaffAttendanceSummary
                    if (showAttendance) {
                        item {
                            SectionHeader(
                                title = stringResource(R.string.feature_dashboard_attendance),
                                icon = Icons.Outlined.CheckCircle,
                                iconTint = Color(0xFF2196F3),
                                iconBg = Color(0xFFE3F2FD),
                                isExpanded = uiState.isSectionExpanded(HomeSectionType.ATTENDANCE),
                                onClick = { handleIntent(HomeIntent.ToggleSection(HomeSectionType.ATTENDANCE)) },
                            )
                        }
                        item {
                            Column {
                            AnimatedVisibility(
                                visible = uiState.isSectionExpanded(HomeSectionType.ATTENDANCE),
                                enter = expandVertically(tween(300)),
                                exit = shrinkVertically(tween(300)),
                            ) {
                                Column(Modifier.background(BG)) {
                                    if (uiState.visibility.showStaffAttendanceSummary) {
                                        StaffAttendanceCard(uiState.staffAttendance)
                                    }
                                    if (uiState.visibility.showAttendanceSummary && uiState.classSummaries.isNotEmpty()) {
                                        ClassAttendanceCard(
                                            classes = uiState.classSummaries,
                                            showAll = uiState.showAllClasses,
                                            onToggle = { handleIntent(HomeIntent.ToggleShowAllClasses) },
                                        )
                                    }
                                }
                            }
                            }
                        }
                    }
                    // Birthdays
                    val showBirthday = uiState.visibility.showBDayCards || uiState.visibility.showStudentBDayCards
                    if (showBirthday) {
                        item {
                            SectionHeader(
                                title = stringResource(R.string.feature_dashboard_birthdays),
                                icon = Icons.Outlined.Cake,
                                iconTint = Color(0xFFE91E63),
                                iconBg = Color(0xFFFCE4EC),
                                isExpanded = uiState.isSectionExpanded(HomeSectionType.BIRTHDAYS),
                                onClick = { handleIntent(HomeIntent.ToggleSection(HomeSectionType.BIRTHDAYS)) },
                            )
                        }
                        item {
                            Column {
                            AnimatedVisibility(
                                visible = uiState.isSectionExpanded(HomeSectionType.BIRTHDAYS),
                                enter = expandVertically(tween(300)),
                                exit = shrinkVertically(tween(300)),
                            ) {
                                Column(Modifier.background(BG)) {
                                    if (uiState.birthdaySummaries.isNotEmpty()) {
                                        BirthdaySummaryCard(uiState.birthdaySummaries)
                                    }
                                    if (uiState.studentBirthdays.isNotEmpty()) {
                                        StudentBirthdayCarousel(uiState.studentBirthdays)
                                    }
                                }
                            }
                            }
                        }
                    }
                    // Questionnaire
                    if (uiState.visibility.showQuestionnaire && uiState.questionnaires.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = stringResource(R.string.feature_dashboard_questionnaire),
                                icon = Icons.Outlined.QuestionAnswer,
                                iconTint = Color(0xFF00BCD4),
                                iconBg = Color(0xFFE0F7FA),
                                isExpanded = uiState.isSectionExpanded(HomeSectionType.QUESTIONNAIRE),
                                onClick = { handleIntent(HomeIntent.ToggleSection(HomeSectionType.QUESTIONNAIRE)) },
                            )
                        }
                        item {
                            Column {
                            AnimatedVisibility(
                                visible = uiState.isSectionExpanded(HomeSectionType.QUESTIONNAIRE),
                                enter = expandVertically(tween(300)),
                                exit = shrinkVertically(tween(300)),
                            ) {
                                QuestionnaireCarousel(uiState.questionnaires) { handleIntent(HomeIntent.OnQuestionnaireClicked) }
                            }
                            }
                        }
                    }
                    // Timetable
                    val showTimetable = uiState.visibility.showClassTimetable || uiState.visibility.showTeacherTimetable
                    if (showTimetable && uiState.timetablePeriods.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = stringResource(R.string.feature_dashboard_timetable),
                                icon = Icons.Outlined.Schedule,
                                iconTint = Color(0xFF607D8B),
                                iconBg = Color(0xFFECEFF1),
                                isExpanded = uiState.isSectionExpanded(HomeSectionType.TIMETABLE),
                                onClick = { handleIntent(HomeIntent.ToggleSection(HomeSectionType.TIMETABLE)) },
                            )
                        }
                        item {
                            Column {
                            AnimatedVisibility(
                                visible = uiState.isSectionExpanded(HomeSectionType.TIMETABLE),
                                enter = expandVertically(tween(300)),
                                exit = shrinkVertically(tween(300)),
                            ) {
                                TimetableCard(uiState.timetablePeriods)
                            }
                            }
                        }
                    }
                    // Library
                    if (uiState.visibility.showLibraryDTL) {
                        item {
                            SectionHeader(
                                title = stringResource(R.string.feature_dashboard_library),
                                icon = Icons.Outlined.LibraryBooks,
                                iconTint = Color(0xFF795548),
                                iconBg = Color(0xFFEFEBE9),
                                isExpanded = uiState.isSectionExpanded(HomeSectionType.LIBRARY),
                                onClick = { handleIntent(HomeIntent.ToggleSection(HomeSectionType.LIBRARY)) },
                            )
                        }
                        item {
                            Column {
                            AnimatedVisibility(
                                visible = uiState.isSectionExpanded(HomeSectionType.LIBRARY),
                                enter = expandVertically(tween(300)),
                                exit = shrinkVertically(tween(300)),
                            ) {
                                LibraryCard(uiState.library)
                            }
                            }
                        }
                    }
                    // Admissions
                    val showAdmissions = uiState.visibility.showAdmissionComparison ||
                        uiState.visibility.showStuStatusWiseStatistics ||
                        uiState.visibility.showStuCategoryStatistics ||
                        uiState.visibility.showStuReligionWiseStatistics ||
                        uiState.visibility.showAdmissionModeComparison
                    if (showAdmissions) {
                        item {
                            SectionHeader(
                                title = stringResource(R.string.feature_dashboard_admissions),
                                icon = Icons.Outlined.PersonAdd,
                                iconTint = Color(0xFF3F51B5),
                                iconBg = Color(0xFFE8EAF6),
                                isExpanded = uiState.isSectionExpanded(HomeSectionType.ADMISSIONS),
                                onClick = { handleIntent(HomeIntent.ToggleSection(HomeSectionType.ADMISSIONS)) },
                            )
                        }
                        item {
                            Column {
                            AnimatedVisibility(
                                visible = uiState.isSectionExpanded(HomeSectionType.ADMISSIONS),
                                enter = expandVertically(tween(300)),
                                exit = shrinkVertically(tween(300)),
                            ) {
                                AdmissionsCard(
                                    comparison = uiState.admissionComparison,
                                    statusStats = uiState.stuStatusStats,
                                    categoryStats = uiState.stuCategoryStats,
                                    religionStats = uiState.stuReligionStats,
                                    modeStats = uiState.admissionModeStats,
                                )
                            }
                            }
                        }
                    }
                    // Feed
                    if (uiState.visibility.showFeed && uiState.feedItems.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = stringResource(R.string.feature_dashboard_feed),
                                icon = Icons.Outlined.NotificationsActive,
                                iconTint = Color(0xFFFF9800),
                                iconBg = Color(0xFFFFF3E0),
                                isExpanded = uiState.isSectionExpanded(HomeSectionType.FEED),
                                onClick = { handleIntent(HomeIntent.ToggleSection(HomeSectionType.FEED)) },
                            )
                        }
                        item {
                            Column {
                            AnimatedVisibility(
                                visible = uiState.isSectionExpanded(HomeSectionType.FEED),
                                enter = expandVertically(tween(300)),
                                exit = shrinkVertically(tween(300)),
                            ) {
                                FeedCarousel(uiState.feedItems)
                            }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── Header ──────────────────────────────────────────────────────────────────

@Composable
internal fun DashboardHeader(
    userName: String,
    userPhotoUrl: String,
    onNotificationClick: () -> Unit,
    onHomeSelectionClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.appColors.primary,
                        MaterialTheme.appColors.primary.copy(alpha = 0.85f),
                    )
                )
            ),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(White.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center,
            ) {
                EcareProAsyncImage(
                    imageUrl = userPhotoUrl,
                    contentDescription = userName,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape),
                    error = painterResource(R.drawable.ic_profile),
                    placeholder = painterResource(R.drawable.ic_profile),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.feature_dashboard_hi),
                    style = MaterialTheme.appTypography.interRegular14px.copy(color = White.copy(alpha = 0.8f)),
                )
                Text(
                    text = "${userName.uppercase()}!",
                    style = MaterialTheme.appTypography.interSemiBold14px.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = White,
                    ),
                )
            }
            IconButton(onClick = onNotificationClick) {
                Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = White)
            }
            IconButton(onClick = onHomeSelectionClick) {
                Icon(Icons.Outlined.Tune, contentDescription = "Home Selection", tint = White)
            }
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Outlined.Settings, contentDescription = "Settings", tint = White)
            }
        }
    }
}

// ─── Section Header ───────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(
    title: String,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    isExpanded: Boolean,
    onClick: () -> Unit,
) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .size(44.dp)
                    .background(iconBg, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.appColors.textPrimary,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = if (isExpanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                contentDescription = null,
                tint = MaterialTheme.appColors.textSecondary,
            )
        }
    }
}

// ─── Overview Cards ───────────────────────────────────────────────────────────

@Composable
private fun FullWidthStatCard(card: DashboardCard, isStaff: Boolean = false, onClick: () -> Unit) {
    val iconBg = if (isStaff) Color(0xFFE0F7FA) else Color(0xFFF3E5F5)
    val iconTint = if (isStaff) Color(0xFF00BCD4) else Color(0xFF9C27B0)
    Card(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .size(48.dp)
                    .background(iconBg, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (isStaff) Icons.Outlined.Groups else Icons.Outlined.People,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(26.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = card.heading,
                    style = MaterialTheme.appTypography.interRegular12px,
                    color = MaterialTheme.appColors.textSecondary,
                )
                Text(
                    text = card.data,
                    style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.appColors.textPrimary,
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Female, contentDescription = null, tint = Color(0xFFE91E63), modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(2.dp))
                    Text(
                        text = "${card.value1 ?: 0} ${card.data1 ?: if (isStaff) "Female" else "Girls"}",
                        style = MaterialTheme.appTypography.interRegular12px,
                        color = MaterialTheme.appColors.textSecondary,
                    )
                    Spacer(Modifier.width(12.dp))
                    Icon(Icons.Outlined.Male, contentDescription = null, tint = Color(0xFF2196F3), modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(2.dp))
                    Text(
                        text = "${card.value2 ?: 0} ${card.data2 ?: if (isStaff) "Male" else "Boys"}",
                        style = MaterialTheme.appTypography.interRegular12px,
                        color = MaterialTheme.appColors.textSecondary,
                    )
                }
            }
        }
    }
}

@Composable
private fun ProCardsGrid(cards: List<DashboardCard>, onClick: (DashboardCard) -> Unit) {
    val rows = cards.chunked(2)
    Column(Modifier.padding(horizontal = 16.dp)) {
        rows.forEach { row ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { card ->
                    Card(
                        Modifier
                            .weight(1f)
                            .clickable { onClick(card) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        elevation = CardDefaults.cardElevation(2.dp),
                    ) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier
                                    .size(40.dp)
                                    .background(Color(0xFFF5F5F5), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center,
                            ) {
                                EcareProAsyncImage(
                                    imageUrl = card.tinyIcon,
                                    contentDescription = card.heading,
                                    modifier = Modifier.size(24.dp),
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = card.heading,
                                    style = MaterialTheme.appTypography.interRegular12px.copy(fontSize = 11.sp),
                                    color = MaterialTheme.appColors.textSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = card.data,
                                    style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.appColors.textPrimary,
                                )
                            }
                        }
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

// ─── Financial Cards ──────────────────────────────────────────────────────────

@Composable
private fun EstimatedCollectionCard(data: FeeCollectionData?, onRefresh: () -> Unit) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.CurrencyRupee, contentDescription = null, tint = Color(0xFFE57373), modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.feature_dashboard_estimated_collection),
                        style = MaterialTheme.appTypography.interRegular12px,
                        color = MaterialTheme.appColors.textSecondary,
                    )
                    Text(
                        text = "\u20B9${data?.estimate ?: "0"}",
                        style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                        color = MaterialTheme.appColors.textPrimary,
                    )
                }
                IconButton(onClick = onRefresh, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Outlined.Refresh, contentDescription = null, tint = MaterialTheme.appColors.textSecondary, modifier = Modifier.size(18.dp))
                }
            }
            HorizontalDivider(Modifier.padding(vertical = 8.dp), color = DIVIDER, thickness = 0.5.dp)
            Row(Modifier.fillMaxWidth()) {
                FinancialStatCell(
                    label = stringResource(R.string.feature_dashboard_due),
                    value = "\u20B9${data?.due ?: "0"}",
                    color = Color(0xFFE53935),
                    modifier = Modifier.weight(1f),
                )
                FinancialStatCell(
                    label = stringResource(R.string.feature_dashboard_received),
                    value = "\u20B9${data?.received ?: "0"}",
                    color = Color(0xFF43A047),
                    modifier = Modifier.weight(1f),
                )
                FinancialStatCell(
                    label = stringResource(R.string.feature_dashboard_concession),
                    value = "\u20B9${data?.concession ?: "0"}",
                    color = MaterialTheme.appColors.textSecondary,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun FinancialStatCell(label: String, value: String, color: Color, modifier: Modifier) {
    Column(modifier) {
        Text(label, style = MaterialTheme.appTypography.interRegular12px, color = MaterialTheme.appColors.textSecondary)
        Text(value, style = MaterialTheme.appTypography.interSemiBold14px.copy(fontWeight = FontWeight.SemiBold), color = color)
    }
}

@Composable
private fun BankBalanceCard(bankBalances: List<BankBalance>) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.AccountBalance, contentDescription = null, tint = Color(0xFF1565C0), modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.feature_dashboard_bank_balance),
                    style = MaterialTheme.appTypography.interSemiBold14px.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.appColors.textPrimary,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(8.dp))
            bankBalances.forEachIndexed { index, bank ->
                if (index > 0) HorizontalDivider(Modifier.padding(vertical = 6.dp), color = DIVIDER, thickness = 0.5.dp)
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(8.dp).background(MaterialTheme.appColors.primary, CircleShape))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = bank.accountName,
                        style = MaterialTheme.appTypography.interRegular14px,
                        color = MaterialTheme.appColors.textPrimary,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = "\u20B9${bank.balance}",
                        style = MaterialTheme.appTypography.interSemiBold14px.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.appColors.textPrimary,
                    )
                }
            }
        }
    }
}

@Composable
private fun FeeDefaulterCard(data: FeeDefaulterSummary?) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.CurrencyRupee, contentDescription = null, tint = Color(0xFFE57373), modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.feature_dashboard_fee_defaulter),
                        style = MaterialTheme.appTypography.interRegular12px,
                        color = MaterialTheme.appColors.textSecondary,
                    )
                    Text(
                        text = "\u20B9${data?.amount ?: "0"}",
                        style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                        color = MaterialTheme.appColors.textPrimary,
                    )
                }
            }
            HorizontalDivider(Modifier.padding(vertical = 8.dp), color = DIVIDER, thickness = 0.5.dp)
            Row(Modifier.fillMaxWidth()) {
                FinancialStatCell(
                    label = stringResource(R.string.feature_dashboard_count),
                    value = "${data?.defaulterCount ?: 0}",
                    color = MaterialTheme.appColors.textPrimary,
                    modifier = Modifier.weight(1f),
                )
                FinancialStatCell(
                    label = stringResource(R.string.feature_dashboard_total_students_label),
                    value = "${data?.totalStudent ?: 0}",
                    color = MaterialTheme.appColors.textPrimary,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun ModeWiseCard(data: ModeWiseData?, onRefresh: () -> Unit) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.AccountBalanceWallet, contentDescription = null, tint = Color(0xFF43A047), modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.feature_dashboard_mode_of_collection),
                        style = MaterialTheme.appTypography.interRegular12px,
                        color = MaterialTheme.appColors.textSecondary,
                    )
                    Text(
                        text = "\u20B9${"%.0f".format(data?.totalCollection ?: 0.0)}",
                        style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                        color = MaterialTheme.appColors.textPrimary,
                    )
                }
                IconButton(onClick = onRefresh, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Outlined.Refresh, contentDescription = null, tint = MaterialTheme.appColors.textSecondary, modifier = Modifier.size(18.dp))
                }
            }
            HorizontalDivider(Modifier.padding(vertical = 8.dp), color = DIVIDER, thickness = 0.5.dp)
            val modes = data?.modes?.filter { it.amount > 0 }?.take(4) ?: emptyList()
            if (modes.isNotEmpty()) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    modes.forEach { mode ->
                        Column(Modifier.weight(1f)) {
                            Text(mode.mode, style = MaterialTheme.appTypography.interRegular12px, color = MaterialTheme.appColors.textSecondary, maxLines = 1)
                            Text(
                                text = "\u20B9${"%.0f".format(mode.amount)}",
                                style = MaterialTheme.appTypography.interSemiBold14px.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.appColors.textPrimary,
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── Calendar ────────────────────────────────────────────────────────────────

@Composable
private fun CalendarCard(activities: List<DashboardActivity>) {
    var displayedMonth by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.MONTH)) }
    var displayedYear by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var selectedDay by remember { mutableStateOf<Int?>(null) }
    val today = Calendar.getInstance()

    // Map of day-of-month → list of activity titles for the displayed month
    val activityDayMap = remember(activities, displayedMonth, displayedYear) {
        val fmt = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        val map = mutableMapOf<Int, MutableList<String>>()
        activities.forEach { act ->
            runCatching {
                val startCal = Calendar.getInstance().also { c ->
                    c.time = fmt.parse(act.fromDate) ?: return@forEach
                }
                if (startCal.get(Calendar.MONTH) == displayedMonth && startCal.get(Calendar.YEAR) == displayedYear) {
                    repeat(act.duration) { d ->
                        val dayNum = startCal.get(Calendar.DAY_OF_MONTH) + d
                        map.getOrPut(dayNum) { mutableListOf() }.add(act.title)
                    }
                }
            }
        }
        map as Map<Int, List<String>>
    }

    // Reset selection when navigating months
    LaunchedEffect(displayedMonth, displayedYear) { selectedDay = null }

    val activityCount = activityDayMap.size
    Card(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(36.dp)
                        .background(Color(0xFFFFF3E0), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.CalendarMonth, contentDescription = null, tint = Color(0xFFFF9800), modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        text = stringResource(R.string.feature_dashboard_activities),
                        style = MaterialTheme.appTypography.interRegular12px,
                        color = MaterialTheme.appColors.textSecondary,
                    )
                    Text(
                        text = "$activityCount",
                        style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                val monthName = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(
                    Calendar.getInstance().also { it.set(displayedYear, displayedMonth, 1) }.time
                )
                Text(
                    text = monthName,
                    style = MaterialTheme.appTypography.interSemiBold14px.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.weight(1f),
                )
                IconButton(
                    onClick = {
                        val c = Calendar.getInstance().also {
                            it.set(displayedYear, displayedMonth, 1)
                            it.add(Calendar.MONTH, -1)
                        }
                        displayedMonth = c.get(Calendar.MONTH)
                        displayedYear = c.get(Calendar.YEAR)
                    },
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(Icons.Outlined.ChevronLeft, contentDescription = null, tint = MaterialTheme.appColors.textPrimary)
                }
                IconButton(
                    onClick = {
                        val c = Calendar.getInstance().also {
                            it.set(displayedYear, displayedMonth, 1)
                            it.add(Calendar.MONTH, 1)
                        }
                        displayedMonth = c.get(Calendar.MONTH)
                        displayedYear = c.get(Calendar.YEAR)
                    },
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = MaterialTheme.appColors.textPrimary)
                }
            }
            Spacer(Modifier.height(8.dp))
            val dayHeaders = listOf("S", "M", "T", "W", "T", "F", "S")
            Row(Modifier.fillMaxWidth()) {
                dayHeaders.forEach { d ->
                    Text(
                        text = d,
                        style = MaterialTheme.appTypography.interRegular12px,
                        color = MaterialTheme.appColors.textSecondary,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            val cal = Calendar.getInstance().also { it.set(displayedYear, displayedMonth, 1) }
            val firstDow = cal.get(Calendar.DAY_OF_WEEK) - 1
            val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            val isCurrentMonth = displayedMonth == today.get(Calendar.MONTH) && displayedYear == today.get(Calendar.YEAR)
            val todayDay = if (isCurrentMonth) today.get(Calendar.DAY_OF_MONTH) else -1
            val cells = List(firstDow) { 0 } + (1..daysInMonth).toList()
            cells.chunked(7).forEach { row ->
                Row(Modifier.fillMaxWidth()) {
                    row.forEach { day ->
                        val hasActivity = day > 0 && day in activityDayMap
                        val isSelected = day == selectedDay
                        Box(
                            Modifier
                                .weight(1f)
                                .padding(vertical = 2.dp)
                                .then(
                                    if (hasActivity) Modifier.clickable {
                                        selectedDay = if (isSelected) null else day
                                    } else Modifier
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (day > 0) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        Modifier
                                            .size(28.dp)
                                            .then(
                                                when {
                                                    day == todayDay -> Modifier.background(MaterialTheme.appColors.primary, CircleShape)
                                                    isSelected -> Modifier.background(MaterialTheme.appColors.primary.copy(alpha = 0.15f), CircleShape)
                                                    else -> Modifier
                                                }
                                            ),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = "$day",
                                            style = MaterialTheme.appTypography.interRegular12px.copy(
                                                color = if (day == todayDay) White else MaterialTheme.appColors.textPrimary,
                                            ),
                                        )
                                    }
                                    if (hasActivity) {
                                        Box(
                                            Modifier
                                                .size(4.dp)
                                                .background(MaterialTheme.appColors.primary, CircleShape)
                                        )
                                    } else {
                                        Spacer(Modifier.size(4.dp))
                                    }
                                }
                            }
                        }
                    }
                    repeat(7 - row.size) { Box(Modifier.weight(1f)) }
                }
            }
            // Show activity names for the selected day
            val selectedTitles = selectedDay?.let { activityDayMap[it] } ?: emptyList()
            if (selectedTitles.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)
                Spacer(Modifier.height(8.dp))
                selectedTitles.forEach { title ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            Modifier
                                .size(8.dp)
                                .background(MaterialTheme.appColors.primary, CircleShape)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = title,
                            style = MaterialTheme.appTypography.interRegular14px,
                            color = MaterialTheme.appColors.textPrimary,
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                }
            }
        }
    }
}

// ─── Attendance ───────────────────────────────────────────────────────────────

@Composable
private fun StaffAttendanceCard(data: StaffAttendance?) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            AttStat(stringResource(R.string.feature_dashboard_present), "${data?.present ?: 0}", Color(0xFF43A047))
            AttStat(stringResource(R.string.feature_dashboard_absent), "${data?.absent ?: 0}", Color(0xFFE53935))
            AttStat(stringResource(R.string.feature_dashboard_on_leave), "${data?.onLeave ?: 0}", Color(0xFFFF9800))
            AttStat(stringResource(R.string.feature_dashboard_total), "${data?.total ?: 0}", Color(0xFF1E88E5))
        }
    }
}

@Composable
private fun AttStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
            color = color,
        )
        Text(text = label, style = MaterialTheme.appTypography.interRegular12px, color = MaterialTheme.appColors.textSecondary)
    }
}

@Composable
private fun ClassAttendanceCard(classes: List<ClassSummary>, showAll: Boolean, onToggle: () -> Unit) {
    val displayed = if (showAll) classes else classes.take(5)
    Card(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            displayed.forEachIndexed { i, cls ->
                if (i > 0) HorizontalDivider(Modifier.padding(vertical = 4.dp), color = DIVIDER, thickness = 0.5.dp)
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(cls.className, style = MaterialTheme.appTypography.interRegular14px, modifier = Modifier.weight(1f))
                    AttendanceDot("P", "${cls.present}", Color(0xFF43A047))
                    Spacer(Modifier.width(8.dp))
                    AttendanceDot("L", "${cls.late}", Color(0xFFFF9800))
                    Spacer(Modifier.width(8.dp))
                    AttendanceDot("A", "${cls.absent}", Color(0xFFE53935))
                    Spacer(Modifier.width(8.dp))
                    AttendanceDot("Lv", "${cls.leave}", Color(0xFF9C27B0))
                }
            }
            if (classes.size > 5) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = if (showAll) stringResource(R.string.feature_dashboard_show_less) else stringResource(R.string.feature_dashboard_view_all),
                    style = MaterialTheme.appTypography.interSemiBold14px.copy(color = MaterialTheme.appColors.primary),
                    modifier = Modifier
                        .clickable { onToggle() }
                        .align(Alignment.CenterHorizontally),
                )
            }
        }
    }
}

@Composable
private fun AttendanceDot(label: String, value: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(6.dp).background(color, CircleShape))
        Spacer(Modifier.width(3.dp))
        Text("$label:$value", style = MaterialTheme.appTypography.interRegular12px, color = MaterialTheme.appColors.textSecondary)
    }
}

// ─── Birthdays ────────────────────────────────────────────────────────────────

@Composable
private fun BirthdaySummaryCard(summaries: List<BirthdaySummary>) {
    val rows = summaries.chunked(2)
    Card(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            rows.forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    row.forEach { s ->
                        Column(Modifier.weight(1f)) {
                            Text(s.heading, style = MaterialTheme.appTypography.interRegular12px, color = MaterialTheme.appColors.textSecondary)
                            Text(
                                text = s.data,
                                style = MaterialTheme.appTypography.interSemiBold14px.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                                color = Color(0xFFE91E63),
                            )
                        }
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun StudentBirthdayCarousel(students: List<StudentBirthday>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(students) { s ->
            Card(
                Modifier.width(110.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(2.dp),
            ) {
                Column(Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box {
                        EcareProAsyncImage(
                            imageUrl = s.photo,
                            contentDescription = s.studentName,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape),
                        )
                        if (s.isToday) {
                            Box(
                                Modifier
                                    .size(16.dp)
                                    .background(Color(0xFFFFEB3B), CircleShape)
                                    .align(Alignment.BottomEnd),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text("\uD83C\uDF82", fontSize = 10.sp)
                            }
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = s.studentName,
                        style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 11.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = s.birthdayOn,
                        style = MaterialTheme.appTypography.interRegular12px.copy(fontSize = 10.sp),
                        color = MaterialTheme.appColors.textSecondary,
                    )
                }
            }
        }
    }
}

// ─── Questionnaire ────────────────────────────────────────────────────────────

@Composable
private fun QuestionnaireCarousel(items: List<DashboardQuestionnaire>, onClick: () -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(items) { q ->
            Card(
                Modifier
                    .width(220.dp)
                    .height(140.dp)
                    .clickable { onClick() },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(2.dp),
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                ) {
                    Text(
                        text = q.question,
                        style = MaterialTheme.appTypography.interRegular14px.copy(fontSize = 13.sp),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = q.updatedBy,
                        style = MaterialTheme.appTypography.interRegular12px.copy(fontSize = 11.sp),
                        color = MaterialTheme.appColors.textSecondary,
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("\u2764\uFE0F ${q.likes}", style = MaterialTheme.appTypography.interRegular12px.copy(fontSize = 11.sp))
                        Spacer(Modifier.width(8.dp))
                        Text("\uD83D\uDCAC ${q.totalAnswer}", style = MaterialTheme.appTypography.interRegular12px.copy(fontSize = 11.sp))
                        if (q.isVerified) {
                            Spacer(Modifier.width(8.dp))
                            Text("\u2713", color = Color(0xFF43A047), style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 12.sp))
                        }
                    }
                }
            }
        }
    }
}

// ─── Timetable ────────────────────────────────────────────────────────────────

@Composable
private fun TimetableCard(periods: List<TimetablePeriod>) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(Modifier.padding(vertical = 8.dp)) {
            periods.forEachIndexed { i, p ->
                if (i > 0) HorizontalDivider(color = DIVIDER, thickness = 0.5.dp)
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        Modifier
                            .size(28.dp)
                            .background(MaterialTheme.appColors.primary.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "${p.period}",
                            style = MaterialTheme.appTypography.interSemiBold14px.copy(color = MaterialTheme.appColors.primary, fontSize = 12.sp),
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = p.subject,
                            style = MaterialTheme.appTypography.interSemiBold14px.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.appColors.textPrimary,
                        )
                        if (p.teachBy.isNotEmpty()) {
                            Text(p.teachBy, style = MaterialTheme.appTypography.interRegular12px, color = MaterialTheme.appColors.textSecondary)
                        }
                    }
                    Text(p.time, style = MaterialTheme.appTypography.interRegular12px, color = MaterialTheme.appColors.textSecondary)
                }
            }
        }
    }
}

// ─── Library ──────────────────────────────────────────────────────────────────

@Composable
private fun LibraryCard(lib: LibraryData?) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth()) {
                LibStat(
                    value = "\u20B9${lib?.dueFine ?: 0}",
                    label = stringResource(R.string.feature_dashboard_fine_pending),
                    color = Color(0xFFE53935),
                    modifier = Modifier.weight(1f),
                )
                LibStat(
                    value = "\u20B9${lib?.fineCollected ?: 0}",
                    label = stringResource(R.string.feature_dashboard_fine_collected),
                    color = Color(0xFF43A047),
                    modifier = Modifier.weight(1f),
                    align = TextAlign.End,
                )
            }
            HorizontalDivider(Modifier.padding(vertical = 10.dp), color = DIVIDER, thickness = 0.5.dp)
            Row(Modifier.fillMaxWidth()) {
                LibStat(
                    value = "${lib?.totalBooks ?: 0}",
                    label = stringResource(R.string.feature_dashboard_total_books),
                    color = Color(0xFF1E88E5),
                    modifier = Modifier.weight(1f),
                )
                LibStat(
                    value = "${lib?.circulatedBooks ?: 0}",
                    label = stringResource(R.string.feature_dashboard_circulated),
                    color = Color(0xFFFF9800),
                    modifier = Modifier.weight(1f),
                    align = TextAlign.Center,
                )
                LibStat(
                    value = "${lib?.discardedBooks ?: 0}",
                    label = stringResource(R.string.feature_dashboard_discarded),
                    color = MaterialTheme.appColors.textSecondary,
                    modifier = Modifier.weight(1f),
                    align = TextAlign.End,
                )
            }
            HorizontalDivider(Modifier.padding(vertical = 10.dp), color = DIVIDER, thickness = 0.5.dp)
            Row(Modifier.fillMaxWidth()) {
                LibStat(
                    value = "${lib?.newsSubscribed ?: 0}",
                    label = stringResource(R.string.feature_dashboard_news_subscribed),
                    color = Color(0xFF7B1FA2),
                    modifier = Modifier.weight(1f),
                )
                LibStat(
                    value = "${lib?.magazineSubscribed ?: 0}",
                    label = stringResource(R.string.feature_dashboard_magazine_subscribed),
                    color = Color(0xFF0097A7),
                    modifier = Modifier.weight(1f),
                    align = TextAlign.End,
                )
            }
        }
    }
}

@Composable
private fun LibStat(
    value: String,
    label: String,
    color: Color,
    modifier: Modifier,
    align: TextAlign = TextAlign.Start,
) {
    Column(modifier) {
        Text(
            text = value,
            style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
            color = color,
            textAlign = align,
        )
        Text(
            text = label,
            style = MaterialTheme.appTypography.interRegular12px,
            color = MaterialTheme.appColors.textSecondary,
            textAlign = align,
        )
    }
}

// ─── Admissions ───────────────────────────────────────────────────────────────

@Composable
private fun AdmissionsCard(
    comparison: AdmissionComparison?,
    statusStats: List<StatItem>,
    categoryStats: List<StatItem>,
    religionStats: List<StatItem>,
    modeStats: List<StatItem>,
) {
    Column {
        comparison?.let {
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 5.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(2.dp),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.feature_dashboard_admission_trends),
                        style = MaterialTheme.appTypography.interSemiBold14px.copy(fontWeight = FontWeight.SemiBold),
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        val prevTotal = it.standards.sumOf { s -> s.previousSession }
                        val currTotal = it.standards.sumOf { s -> s.currentSession }
                        val nextTotal = it.standards.sumOf { s -> s.nextSession }
                        SessionStat(it.previousSession, "$prevTotal", Color(0xFFFF9800))
                        SessionStat(it.currentSession, "$currTotal", Color(0xFF43A047))
                        if (it.isNextSessionActive) {
                            SessionStat(it.nextSession, "$nextTotal", Color(0xFF1E88E5))
                        }
                    }
                }
            }
        }
        if (statusStats.isNotEmpty()) {
            StatGridCard(stringResource(R.string.feature_dashboard_student_status), statusStats)
        }
        if (categoryStats.isNotEmpty()) {
            StatGridCard(stringResource(R.string.feature_dashboard_student_category), categoryStats)
        }
        if (religionStats.isNotEmpty()) {
            StatGridCard(stringResource(R.string.feature_dashboard_student_religion), religionStats)
        }
        if (modeStats.isNotEmpty()) {
            StatGridCard(stringResource(R.string.feature_dashboard_admission_mode), modeStats)
        }
    }
}

@Composable
private fun SessionStat(session: String, count: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
            color = color,
        )
        Text(
            text = session,
            style = MaterialTheme.appTypography.interRegular12px.copy(fontSize = 10.sp),
            color = MaterialTheme.appColors.textSecondary,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun StatGridCard(title: String, items: List<StatItem>) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.appTypography.interSemiBold14px.copy(fontWeight = FontWeight.SemiBold))
            Spacer(Modifier.height(8.dp))
            items.chunked(2).forEach { row ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                ) {
                    row.forEach { item ->
                        Row(
                            Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(Modifier.size(8.dp).background(MaterialTheme.appColors.primary, CircleShape))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = item.label,
                                style = MaterialTheme.appTypography.interRegular12px,
                                color = MaterialTheme.appColors.textSecondary,
                                modifier = Modifier.weight(1f),
                            )
                            Text(
                                text = "${item.value}",
                                style = MaterialTheme.appTypography.interSemiBold14px.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.appColors.textPrimary,
                            )
                        }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

// ─── Feed ─────────────────────────────────────────────────────────────────────

@Composable
private fun FeedCarousel(items: List<DashFeedItem>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(items) { item ->
            Card(
                Modifier
                    .width(220.dp)
                    .height(140.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(2.dp),
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(28.dp)
                                .background(Color(0xFFE3F2FD), RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Outlined.Campaign, contentDescription = null, tint = Color(0xFF1E88E5), modifier = Modifier.size(16.dp))
                        }
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = item.module,
                            style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 12.sp),
                            color = Color(0xFF1E88E5),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = item.caption,
                        style = MaterialTheme.appTypography.interRegular14px.copy(fontSize = 13.sp),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Schedule, contentDescription = null, tint = MaterialTheme.appColors.textSecondary, modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = item.updatedOn,
                            style = MaterialTheme.appTypography.interRegular12px.copy(fontSize = 11.sp),
                            color = MaterialTheme.appColors.textSecondary,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    EcareProTheme { HomeContent(uiState = HomeUiState(isLoading = false), handleIntent = {}) }
}

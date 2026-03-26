package com.app.ecarepro.feature.staffprofile.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.SalaryStructure
import com.app.ecarepro.core.domain.model.StaffAttendanceDetail
import com.app.ecarepro.core.domain.model.StaffPersonalDetailSection
import com.app.ecarepro.core.domain.model.StaffProfileDetail
import com.app.ecarepro.core.domain.model.StaffProfileSection
import com.app.ecarepro.core.domain.model.TimetableSummary
import com.app.ecarepro.core.domain.util.ProfileUtils.orDefault
import com.app.ecarepro.core.domain.util.ProfileUtils.toRupees
import com.app.ecarepro.core.domain.util.ProfileUtils.toWordsRupees
import com.app.ecarepro.designsystem.core.component.ExpandableCardSection
import com.app.ecarepro.designsystem.core.component.ExpandableStatCard
import com.app.ecarepro.designsystem.core.component.StatItem
import com.app.ecarepro.designsystem.core.component.InfoGridItem
import com.app.ecarepro.designsystem.core.component.InfoGridView
import com.app.ecarepro.designsystem.core.component.InfoItemType
import com.app.ecarepro.core.ui.component.PlaceholderTab
import com.app.ecarepro.core.ui.component.ProfileDetailsScaffold
import com.app.ecarepro.core.ui.component.ProfileDetailsHeaderData
import com.app.ecarepro.core.ui.component.ProfileTab
import com.app.ecarepro.core.ui.component.ProfileDetailsPager
import com.app.ecarepro.designsystem.core.component.EcareProEmptyState
import com.app.ecarepro.designsystem.core.component.SnackbarMessage

@Composable
fun StaffProfileDetailsScreen(
    staffId: Int,
    viewModel: StaffProfileDetailsViewModel = hiltViewModel(),
    navigateToBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(staffId) {
        viewModel.loadStaffProfile(staffId)
    }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                StaffProfileDetailsEvent.NavigateBack -> navigateToBack()
                is StaffProfileDetailsEvent.ShowMessage -> {
                    snackbarMessage = event.message
                    snackbarHostState.showSnackbar(event.message.text)
                }
            }
        }
    }

    StaffProfileDetailsScreenContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun StaffProfileDetailsScreenContent(
    uiState: com.app.ecarepro.core.ui.UiState<StaffProfileDetailsUiState>,
    handleIntent: (StaffProfileDetailsIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit
) {
    ProfileDetailsScaffold(
        uiState = uiState,
        headerData = { data ->
            data.profileData?.let { profile ->
                ProfileDetailsHeaderData(
                    photo = profile.photo,
                    displayName = "${profile.name}${profile.designation?.let { ", $it" } ?: ""}",
                    subtitle = "DOJ - ${profile.doj.orDefault()}"
                )
            }
        },
        visibleTabs = { data ->
            data.visibleTabs.map { ProfileTab(it.displayName, it) }
        },
        selectedTabIndex = { it.selectedTabIndex },
        onCloseClicked = { handleIntent(StaffProfileDetailsIntent.OnBackClicked) },
        onTabSelected = { index -> handleIntent(StaffProfileDetailsIntent.OnTabSelected(index)) },
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = onSnackbarDismissed
    ) { pagerState, data ->
        ProfileDetailsPager(
            pagerState = pagerState
        ) { page ->
            val currentTab = data.visibleTabs.getOrNull(page)
            when (currentTab) {
                StaffProfileSection.PERSONAL_DETAILS -> {
                    PersonalDetailsTab(
                        profileData = data.profileData,
                        expandedSections = data.expandedSections,
                        onToggleSection = { section ->
                            handleIntent(StaffProfileDetailsIntent.OnToggleSection(section))
                        }
                    )
                }
                StaffProfileSection.ATTENDANCE -> {
                    AttendanceTab(
                        attendanceDetail = data.attendanceDetail,
                        isExpanded = data.isAttendanceExpanded,
                        onToggle = { handleIntent(StaffProfileDetailsIntent.OnToggleAttendance) }
                    )
                }
                StaffProfileSection.TIMETABLE -> {
                    TimetableTab(
                        timetableSummary = data.timetableSummary,
                        isExpanded = data.isTimetableExpanded,
                        onToggle = { handleIntent(StaffProfileDetailsIntent.OnToggleTimetable) }
                    )
                }
                StaffProfileSection.SALARY -> {
                    SalaryTab(
                        salaryStructure = data.salaryStructure,
                        expandedCards = data.expandedSalaryCards,
                        onToggleCard = { section ->
                            handleIntent(StaffProfileDetailsIntent.OnToggleSalaryCard(section))
                        }
                    )
                }
                else -> {
                    PlaceholderTab(
                        title = currentTab?.displayName ?: "Section",
                        icon = getIconForSection(currentTab ?: StaffProfileSection.PERSONAL_DETAILS)
                    )
                }
            }
        }
    }
}

@Composable
private fun PersonalDetailsTab(
    profileData: StaffProfileDetail?,
    expandedSections: Set<StaffPersonalDetailSection>,
    onToggleSection: (StaffPersonalDetailSection) -> Unit
) {
    if (profileData == null) {
        EcareProEmptyState(
            message = "No profile data available",
            icon = Icons.Default.Person
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Personal Info Section
        item(key = "personal_info") {
            ExpandableCardSection(
                title = StaffPersonalDetailSection.PERSONAL_INFO.displayName,
                icon = Icons.Default.Person,
                iconColor = Color(StaffPersonalDetailSection.PERSONAL_INFO.iconColor),
                iconBackgroundColor = Color(StaffPersonalDetailSection.PERSONAL_INFO.iconBackgroundColor),
                isExpanded = expandedSections.contains(StaffPersonalDetailSection.PERSONAL_INFO),
                onToggle = { onToggleSection(StaffPersonalDetailSection.PERSONAL_INFO) }
            ) {
                InfoGridView(
                    items = listOf(
                        InfoGridItem("Date of birth", profileData.dob.orDefault()),
                        InfoGridItem("Gender", profileData.gender.orDefault()),
                        InfoGridItem("Father/Spouse name", profileData.fatherHusbandName.orDefault()),
                        InfoGridItem("Qualification", profileData.qualification.orDefault()),
                        InfoGridItem("Religion", profileData.religion.orDefault()),
                        InfoGridItem("Nationality", profileData.nationality.orDefault()),
                        InfoGridItem("Pan No", profileData.panNumber.orDefault()),
                        InfoGridItem("Aadhar Number", profileData.aadharCardNo.orDefault()),
                        InfoGridItem("CBSE Id", profileData.cbseid.orDefault()),
                        InfoGridItem("Blood Group", profileData.bloodGroup.orDefault()),
                        InfoGridItem("Marital Status", profileData.maritalStatus.orDefault()),
                        InfoGridItem("Date of Joining", profileData.doj.orDefault()),
                        InfoGridItem("Designation", profileData.designation.orDefault()),
                        InfoGridItem("Address", profileData.address.orDefault())
                    )
                )
            }
        }

        // Contact Details Section
        item(key = "contact_details") {
            ExpandableCardSection(
                title = StaffPersonalDetailSection.CONTACT_DETAILS.displayName,
                icon = Icons.Default.Person,
                iconColor = Color(StaffPersonalDetailSection.CONTACT_DETAILS.iconColor),
                iconBackgroundColor = Color(StaffPersonalDetailSection.CONTACT_DETAILS.iconBackgroundColor),
                isExpanded = expandedSections.contains(StaffPersonalDetailSection.CONTACT_DETAILS),
                onToggle = { onToggleSection(StaffPersonalDetailSection.CONTACT_DETAILS) }
            ) {
                InfoGridView(
                    items = listOf(
                        InfoGridItem("Contact no", profileData.mobile.orDefault(), InfoItemType.PHONE),
                        InfoGridItem("Email Id", profileData.emailID.orDefault(), InfoItemType.EMAIL),
                        InfoGridItem("Alternate Mobile", profileData.alternateMobile.orDefault(), InfoItemType.PHONE),
                        InfoGridItem("Alternate Email", profileData.alternateEmailID.orDefault(), InfoItemType.EMAIL),
                        InfoGridItem("Emergency Contact", profileData.emergencyContactNo.orDefault(), InfoItemType.PHONE)
                    )
                )
            }
        }

        // Family Details Section
        item(key = "family_details") {
            ExpandableCardSection(
                title = StaffPersonalDetailSection.FAMILY_DETAILS.displayName,
                icon = Icons.Default.Person,
                iconColor = Color(StaffPersonalDetailSection.FAMILY_DETAILS.iconColor),
                iconBackgroundColor = Color(StaffPersonalDetailSection.FAMILY_DETAILS.iconBackgroundColor),
                isExpanded = expandedSections.contains(StaffPersonalDetailSection.FAMILY_DETAILS),
                onToggle = { onToggleSection(StaffPersonalDetailSection.FAMILY_DETAILS) }
            ) {
                InfoGridView(
                    items = listOf(
                        InfoGridItem("Father/Spouse Name", profileData.fatherHusbandName.orDefault()),
                        InfoGridItem("Father/Spouse Mobile", profileData.fatherHusbandMob.orDefault(), InfoItemType.PHONE),
                        InfoGridItem("Anniversary Date", profileData.doAnniversary.orDefault())
                    )
                )
            }
        }

        // Other Details Section
        item(key = "other_details") {
            ExpandableCardSection(
                title = StaffPersonalDetailSection.OTHER_DETAILS.displayName,
                icon = Icons.Default.Person,
                iconColor = Color(StaffPersonalDetailSection.OTHER_DETAILS.iconColor),
                iconBackgroundColor = Color(StaffPersonalDetailSection.OTHER_DETAILS.iconBackgroundColor),
                isExpanded = expandedSections.contains(StaffPersonalDetailSection.OTHER_DETAILS),
                onToggle = { onToggleSection(StaffPersonalDetailSection.OTHER_DETAILS) }
            ) {
                InfoGridView(
                    items = listOf(
                        InfoGridItem("UAN Number", profileData.uanNumber.orDefault()),
                        InfoGridItem("EPF Joining Date", profileData.dojEPF.orDefault()),
                        InfoGridItem("State Code", profileData.stateCode.orDefault()),
                        InfoGridItem("National Code", profileData.nationalCode.orDefault()),
                        InfoGridItem("Permanent Address", profileData.permanentAddress.orDefault())
                    )
                )
            }
        }
    }
}

@Composable
private fun AttendanceTab(
    attendanceDetail: StaffAttendanceDetail?,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    if (attendanceDetail == null) {
        EcareProEmptyState(
            message = "No attendance data available",
            icon = Icons.Default.CalendarMonth
        )
        return
    }

    val presentColor = Color(0xFF4CAF50)
    val absentColor = Color(0xFFF44336)
    val lateInColor = Color(0xFFFF9800)
    val earlyOutColor = Color(0xFF757575)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(key = "attendance_card") {
            ExpandableStatCard(
                title = "${attendanceDetail.fromDate.orDefault()} - Present",
                icon = Icons.Default.CalendarMonth,
                iconColor = Color(0xFF29B6F6),
                iconBackgroundColor = Color(0xFFE1F5FE),
                isExpanded = isExpanded,
                onToggle = onToggle,
                stats = listOf(
                    StatItem(
                        label = "Present",
                        value = (attendanceDetail.totalPresent ?: 0).toString(),
                        labelColor = presentColor,
                        valueColor = presentColor
                    ),
                    StatItem(
                        label = "Total Absent",
                        value = (attendanceDetail.totalAbsent ?: 0).toString(),
                        labelColor = absentColor,
                        valueColor = absentColor
                    ),
                    StatItem(
                        label = "Late in",
                        value = (attendanceDetail.latInCount ?: 0).toString(),
                        labelColor = lateInColor,
                        valueColor = lateInColor
                    ),
                    StatItem(
                        label = "Early Out",
                        value = (attendanceDetail.earlyOutsCount ?: 0).toString(),
                        labelColor = earlyOutColor,
                        valueColor = earlyOutColor
                    )
                )
            )
        }
    }
}

@Composable
private fun TimetableTab(
    timetableSummary: TimetableSummary?,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    if (timetableSummary == null) {
        EcareProEmptyState(
            message = "No timetable data available",
            icon = Icons.Default.Schedule
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(key = "timetable_card") {
            ExpandableStatCard(
                title = "2025-26",
                icon = Icons.Default.Schedule,
                iconColor = Color(0xFF4CAF50),
                iconBackgroundColor = Color(0xFFE8F5E9),
                isExpanded = isExpanded,
                onToggle = onToggle,
                stats = listOf(
                    StatItem(
                        label = "Total lecture in a week",
                        value = (timetableSummary.totalLecture ?: 0).toString()
                    ),
                    StatItem(
                        label = "No of period taken",
                        value = String.format("%02d", timetableSummary.periodTaken ?: 0)
                    ),
                    StatItem(
                        label = "No of free period",
                        value = (timetableSummary.freePeriod ?: 0).toString()
                    )
                )
            )
        }
    }
}

// ─── Salary Tab ────────────────────────────────────────────────────────────

@Composable
private fun SalaryTab(
    salaryStructure: SalaryStructure?,
    expandedCards: Set<SalaryCardSection>,
    onToggleCard: (SalaryCardSection) -> Unit
) {
    if (salaryStructure == null) {
        EcareProEmptyState(
            message = "No salary data available",
            icon = Icons.Default.AttachMoney
        )
        return
    }

    val daAmount = salaryStructure.salaryHeads
        ?.find { it.shortName == "DA" }?.amount?.toDoubleOrNull() ?: 0.0
    val pfAmount = salaryStructure.salaryHeads
        ?.find { it.shortName == "PF" }?.amount?.toDoubleOrNull() ?: 0.0
    val tdsAmount = salaryStructure.salaryHeads
        ?.find { it.shortName == "TDS" }?.amount?.toDoubleOrNull() ?: 0.0

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Card 1 – Payslip Summary: custom layout for bold net pay + amount in words
        item(key = "salary_payslip") {
            ExpandableCardSection(
                title = "Payslip for month of ${salaryStructure.salryOf.orEmpty()}",
                icon = Icons.Default.Description,
                iconColor = Color(0xFF5C6BC0),
                iconBackgroundColor = Color(0xFFE8EAF6),
                isExpanded = expandedCards.contains(SalaryCardSection.PAYSLIP),
                onToggle = { onToggleCard(SalaryCardSection.PAYSLIP) }
            ) {
                PayslipCardContent(
                    allowances = salaryStructure.allowances ?: 0.0,
                    deduction = salaryStructure.deduction ?: 0.0,
                    netSalary = salaryStructure.netSalary ?: 0.0
                )
            }
        }

        // Card 2 – Earnings (basic + DA shortName filter from salaryHeads + allowances total)
        item(key = "salary_earnings") {
            ExpandableStatCard(
                title = "Earnings",
                icon = Icons.Default.TrendingUp,
                iconColor = Color(0xFF4CAF50),
                iconBackgroundColor = Color(0xFFE8F5E9),
                isExpanded = expandedCards.contains(SalaryCardSection.EARNINGS),
                onToggle = { onToggleCard(SalaryCardSection.EARNINGS) },
                stats = listOf(
                    StatItem("Basic", (salaryStructure.basic ?: 0.0).toRupees()),
                    StatItem("Dearness Allowances", daAmount.toRupees()),
                    StatItem("Total Earnings", (salaryStructure.allowances ?: 0.0).toRupees())
                )
            )
        }

        // Card 3 – Deductions (PF + TDS shortName filter from salaryHeads)
        item(key = "salary_deductions") {
            ExpandableStatCard(
                title = "Deductions",
                icon = Icons.Default.TrendingDown,
                iconColor = Color(0xFFFF5722),
                iconBackgroundColor = Color(0xFFFBE9E7),
                isExpanded = expandedCards.contains(SalaryCardSection.DEDUCTIONS),
                onToggle = { onToggleCard(SalaryCardSection.DEDUCTIONS) },
                stats = listOf(
                    StatItem("Provident Fund", pfAmount.toRupees()),
                    StatItem("TDS", tdsAmount.toRupees())
                )
            )
        }
    }
}

// Custom content for Card 1 only – needed because StatItem cannot render bold or sub-text
@Composable
private fun PayslipCardContent(
    allowances: Double,
    deduction: Double,
    netSalary: Double
) {
    Column {
        SalaryRowItem("Total earnings", allowances.toRupees())
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
        SalaryRowItem("Total deductions", deduction.toRupees())
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
        SalaryRowItem("Net pay for the month", netSalary.toRupees(), isBold = true)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = netSalary.toWordsRupees(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SalaryRowItem(label: String, amount: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = if (isBold) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = amount,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// ───────────────────────────────────────────────────────────────────────────

private fun getIconForSection(section: StaffProfileSection): ImageVector {
    return when (section) {
        StaffProfileSection.PERSONAL_DETAILS -> Icons.Default.Person
        StaffProfileSection.ATTENDANCE -> Icons.Default.CalendarMonth
        StaffProfileSection.SALARY -> Icons.Default.AttachMoney
        StaffProfileSection.TIMETABLE -> Icons.Default.Schedule
        StaffProfileSection.LEAVE -> Icons.Default.CalendarMonth
        StaffProfileSection.SESSION_LOG -> Icons.Default.CalendarMonth
    }
}

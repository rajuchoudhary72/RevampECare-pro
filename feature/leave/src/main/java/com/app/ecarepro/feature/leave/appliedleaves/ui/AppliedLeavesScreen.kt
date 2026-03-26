package com.app.ecarepro.feature.leave.appliedleaves.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.Loader
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.feature.leave.R
import com.app.ecarepro.feature.leave.appliedleaves.data.AppliedLeavesScreenType
import com.app.ecarepro.feature.leave.appliedleaves.data.Leave
import com.app.ecarepro.feature.leave.appliedleaves.data.LeaveCardPresentation
import com.app.ecarepro.feature.leave.appliedleaves.data.LeaveStatus
import com.app.ecarepro.feature.leave.appliedleaves.domain.AppliedLeavesViewModel
import com.app.ecarepro.feature.leave.appliedleaves.domain.AppliedLeavesViewPresentation
import com.app.ecarepro.feature.leave.appliedleaves.domain.UIState
import com.app.ecarepro.feature.leave.appliedleaves.ui.components.BottomActionBar
import com.app.ecarepro.feature.leave.appliedleaves.ui.components.LeaveCardView
import com.app.ecarepro.feature.leave.appliedleaves.ui.components.LeaveStatusTabs
import kotlinx.coroutines.launch

@Composable
fun AppliedLeavesScreen(
    screenType: AppliedLeavesScreenType,
    viewModel: AppliedLeavesViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
    navigateToApplyLeave: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val presentation by viewModel.presentation.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val selectedLeaveIds by viewModel.selectedLeaveIds.collectAsStateWithLifecycle()
    val selectAll by viewModel.selectAll.collectAsStateWithLifecycle()
    val showAttendance by viewModel.showAttendance.collectAsStateWithLifecycle()
    val successMessage by viewModel.successMessage.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.initialize(screenType)
    }

    AppliedLeavesScreenContent(
        screenType = screenType,
        uiState = uiState,
        presentation = presentation,
        selectedTab = selectedTab,
        selectedLeaveIds = selectedLeaveIds,
        selectAll = selectAll,
        showAttendance = showAttendance,
        successMessage = successMessage,
        errorMessage = errorMessage,
        onBackClick = navigateToBack,
        onApplyLeaveClick = navigateToApplyLeave,
        onTabChange = { viewModel.onTabChange(it) },
        onToggleSelectAll = { viewModel.toggleSelectAll() },
        onToggleAttendance = { viewModel.toggleAttendance() },
        onLeaveSelect = { viewModel.toggleLeaveSelection(it) },
        onApprove = { viewModel.approveLeave(it) },
        onReject = { viewModel.rejectLeave(it) },
        onApproveSelected = { viewModel.approveSelectedLeaves() },
        onRejectSelected = { viewModel.rejectSelectedLeaves() },
        onRefresh = { viewModel.refreshLeaves() },
        onClearSuccess = { viewModel.clearSuccessMessage() },
        onClearError = { viewModel.clearErrorMessage() }
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun AppliedLeavesScreenContent(
    screenType: AppliedLeavesScreenType,
    uiState: UIState,
    presentation: com.app.ecarepro.feature.leave.appliedleaves.domain.AppliedLeavesViewPresentation?,
    selectedTab: LeaveStatus,
    selectedLeaveIds: Set<String>,
    selectAll: Boolean,
    showAttendance: Boolean,
    successMessage: String?,
    errorMessage: String?,
    onBackClick: () -> Unit,
    onApplyLeaveClick: () -> Unit,
    onTabChange: (LeaveStatus) -> Unit,
    onToggleSelectAll: () -> Unit,
    onToggleAttendance: () -> Unit,
    onLeaveSelect: (String) -> Unit,
    onApprove: (Int) -> Unit,
    onReject: (Int) -> Unit,
    onApproveSelected: () -> Unit,
    onRejectSelected: () -> Unit,
    onRefresh: () -> Unit,
    onClearSuccess: () -> Unit,
    onClearError: () -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = if (presentation != null) screenType.availableTabs.indexOf(selectedTab).coerceAtLeast(0) else 0,
        pageCount = { if (screenType.showTabs) screenType.availableTabs.size else 1 }
    )
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress && screenType.showTabs) {
            val newTab = screenType.availableTabs.getOrNull(pagerState.currentPage)
            if (newTab != null && newTab != selectedTab) {
                onTabChange(newTab)
            }
        }
    }

    LaunchedEffect(selectedTab) {
        if (screenType.showTabs) {
            val tabIndex = screenType.availableTabs.indexOf(selectedTab)
            if (tabIndex >= 0 && tabIndex != pagerState.currentPage) {
                pagerState.animateScrollToPage(tabIndex)
            }
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            onClearSuccess()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Long
            )
            onClearError()
        }
    }

    EcareProScaffold(
        topBar = {
            Column(
                modifier = Modifier.shadow(elevation = 1.dp)
            ) {
                EcareProTopAppBar(
                    title = screenType.title,
                    onNavigationClicked = onBackClick,
                    actions = {
                        if (screenType.showCheckbox && selectedTab == LeaveStatus.PENDING) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Checkbox(
                                    checked = selectAll,
                                    onCheckedChange = { onToggleSelectAll() },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MaterialTheme.appColors.primary
                                    )
                                )
                                Text(
                                    text = "Select All",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.appColors.textPrimary
                                )
                            }
                        }
                    }
                )

                // Tab Bar
                if (screenType.showTabs) {
                    LeaveStatusTabs(
                        tabs = screenType.availableTabs,
                        selectedTab = selectedTab,
                        onTabSelected = {
                            coroutineScope.launch {
                                onTabChange(it)
                            }
                        }
                    )
                }

                // Attendance Toggle for Student Leaves
                if (screenType.showAttendanceToggle && selectedTab == LeaveStatus.PENDING) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Show Attendance",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.appColors.textPrimary
                        )
                        Switch(
                            checked = showAttendance,
                            onCheckedChange = { onToggleAttendance() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.appColors.primary,
                                checkedTrackColor = MaterialTheme.appColors.primary.copy(alpha = 0.5f)
                            )
                        )
                    }
                    HorizontalDivider(
                        color = MaterialTheme.appColors.divider,
                        thickness = 0.5.dp
                    )
                }
            }
        },
        floatingActionButton = {
            if (screenType.showApplyButton) {
                FloatingActionButton(
                    onClick = onApplyLeaveClick,
                    containerColor = MaterialTheme.appColors.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Apply Leave",
                        tint = White
                    )
                }
            }
        },
        bottomBar = {
            if (selectedLeaveIds.isNotEmpty()) {
                BottomActionBar(
                    selectedCount = selectedLeaveIds.size,
                    onApprove = onApproveSelected,
                    onReject = onRejectSelected
                )
            }
        },
        //snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                UIState.Loading, UIState.NotInitialized -> {
                    Loader()
                }
                UIState.Error -> {
                    EmptyStateView(
                        message = errorMessage ?: "Failed to load leaves",
                        onRetry = onRefresh
                    )
                }
                UIState.NoResult -> {
                    EmptyStateView(
                        message = "No leaves found",
                        showRetry = false
                    )
                }
                else -> {
                    presentation?.let { pres ->
                        if (screenType.showTabs) {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxSize()
                            ) { page ->
                                val tab = screenType.availableTabs[page]
                                val leaves = pres.leavesForTab(tab)
                                LeaveListContent(
                                    leaves = leaves,
                                    screenType = screenType,
                                    onLeaveSelect = onLeaveSelect,
                                    onApprove = onApprove,
                                    onReject = onReject,
                                    onRefresh = onRefresh
                                )
                            }
                        } else {
                            LeaveListContent(
                                leaves = pres.allLeaves,
                                screenType = screenType,
                                onLeaveSelect = onLeaveSelect,
                                onApprove = onApprove,
                                onReject = onReject,
                                onRefresh = onRefresh
                            )
                        }
                    }
                }
            }

            if (uiState is UIState.SilentLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                    color = MaterialTheme.appColors.primary
                )
            }

            // Snackbar Host
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun LeaveListContent(
    leaves: List<com.app.ecarepro.feature.leave.appliedleaves.data.LeaveCardPresentation>,
    screenType: AppliedLeavesScreenType,
    onLeaveSelect: (String) -> Unit,
    onApprove: (Int) -> Unit,
    onReject: (Int) -> Unit,
    onRefresh: () -> Unit
) {
    if (leaves.isEmpty()) {
        EmptyStateView(
            message = "No leaves in this category",
            showRetry = false
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(
                items = leaves,
                key = { it.id }
            ) { leave ->
                LeaveCardView(
                    leave = leave,
                    onSelect = if (leave.showCheckbox) {
                        { onLeaveSelect(leave.id) }
                    } else null,
                    onApprove = if (leave.showActionButtons) {
                        { onApprove(leave.lvID) }
                    } else null,
                    onReject = if (leave.showActionButtons) {
                        { onReject(leave.lvID) }
                    } else null
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.appColors.divider,
                    thickness = 0.5.dp
                )
            }
        }
    }
}

@Composable
private fun EmptyStateView(
    message: String,
    showRetry: Boolean = true,
    onRetry: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.appColors.textSecondary
        )
        if (showRetry) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.appColors.primary
                )
            ) {
                Text("Retry")
            }
        }
    }
}

// Preview Functions
@Preview(showBackground = true, name = "Applied Leaves - Self (Pending)")
@Composable
private fun AppliedLeavesScreenPreview() {
    val mockLeaves = getMockLeavesPending()
    val presentation = AppliedLeavesViewPresentation(
        allLeaves = mockLeaves,
        pendingLeaves = mockLeaves,
        approvedLeaves = emptyList(),
        rejectedLeaves = emptyList(),
        cancelledLeaves = emptyList(),
        selectedTab = LeaveStatus.PENDING,
        screenType = AppliedLeavesScreenType.SELF_LEAVES
    )

    EcareProTheme {
        AppliedLeavesScreenContent(
            screenType = AppliedLeavesScreenType.SELF_LEAVES,
            uiState = UIState.Loaded,
            presentation = presentation,
            selectedTab = LeaveStatus.PENDING,
            selectedLeaveIds = emptySet(),
            selectAll = false,
            showAttendance = false,
            successMessage = null,
            errorMessage = null,
            onBackClick = {},
            onApplyLeaveClick = {},
            onTabChange = {},
            onToggleSelectAll = {},
            onToggleAttendance = {},
            onLeaveSelect = {},
            onApprove = {},
            onReject = {},
            onApproveSelected = {},
            onRejectSelected = {},
            onRefresh = {},
            onClearSuccess = {},
            onClearError = {}
        )
    }
}

@Preview(showBackground = true, name = "Applied Leaves - Student with Tabs")
@Composable
private fun AppliedLeavesScreenStudentPreview() {
    val mockLeaves = getMockLeavesPending()
    val presentation = AppliedLeavesViewPresentation(
        allLeaves = mockLeaves,
        pendingLeaves = mockLeaves,
        approvedLeaves = getMockLeavesApproved(),
        rejectedLeaves = emptyList(),
        cancelledLeaves = emptyList(),
        selectedTab = LeaveStatus.PENDING,
        screenType = AppliedLeavesScreenType.STUDENT_LEAVES
    )

    EcareProTheme {
        AppliedLeavesScreenContent(
            screenType = AppliedLeavesScreenType.STUDENT_LEAVES,
            uiState = UIState.Loaded,
            presentation = presentation,
            selectedTab = LeaveStatus.PENDING,
            selectedLeaveIds = setOf("1"),
            selectAll = false,
            showAttendance = true,
            successMessage = null,
            errorMessage = null,
            onBackClick = {},
            onApplyLeaveClick = {},
            onTabChange = {},
            onToggleSelectAll = {},
            onToggleAttendance = {},
            onLeaveSelect = {},
            onApprove = {},
            onReject = {},
            onApproveSelected = {},
            onRejectSelected = {},
            onRefresh = {},
            onClearSuccess = {},
            onClearError = {}
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
private fun AppliedLeavesScreenLoadingPreview() {
    EcareProTheme {
        AppliedLeavesScreenContent(
            screenType = AppliedLeavesScreenType.SELF_LEAVES,
            uiState = UIState.Loading,
            presentation = null,
            selectedTab = LeaveStatus.PENDING,
            selectedLeaveIds = emptySet(),
            selectAll = false,
            showAttendance = false,
            successMessage = null,
            errorMessage = null,
            onBackClick = {},
            onApplyLeaveClick = {},
            onTabChange = {},
            onToggleSelectAll = {},
            onToggleAttendance = {},
            onLeaveSelect = {},
            onApprove = {},
            onReject = {},
            onApproveSelected = {},
            onRejectSelected = {},
            onRefresh = {},
            onClearSuccess = {},
            onClearError = {}
        )
    }
}

@Preview(showBackground = true, name = "Empty State")
@Composable
private fun AppliedLeavesScreenEmptyPreview() {
    EcareProTheme {
        AppliedLeavesScreenContent(
            screenType = AppliedLeavesScreenType.SELF_LEAVES,
            uiState = UIState.NoResult,
            presentation = null,
            selectedTab = LeaveStatus.PENDING,
            selectedLeaveIds = emptySet(),
            selectAll = false,
            showAttendance = false,
            successMessage = null,
            errorMessage = null,
            onBackClick = {},
            onApplyLeaveClick = {},
            onTabChange = {},
            onToggleSelectAll = {},
            onToggleAttendance = {},
            onLeaveSelect = {},
            onApprove = {},
            onReject = {},
            onApproveSelected = {},
            onRejectSelected = {},
            onRefresh = {},
            onClearSuccess = {},
            onClearError = {}
        )
    }
}

// Mock Data for Previews
private fun getMockLeavesPending(): List<LeaveCardPresentation> {
    val mockLeave1 = Leave(
        lvID = 1,
        fromDate = "15 Dec 2024",
        tillDate = "17 Dec 2024",
        submittedOn = "10 Dec 2024",
        duration = kotlinx.serialization.json.JsonPrimitive(3),
        reason = "Family function to attend",
        studentName = "John Doe",
        studentClass = "10-A",
        status = "Pending",
        leaveType = "Casual Leave",
        attPer = "95.5"
    )

    val mockLeave2 = Leave(
        lvID = 2,
        fromDate = "20 Dec 2024",
        tillDate = "22 Dec 2024",
        submittedOn = "12 Dec 2024",
        duration = kotlinx.serialization.json.JsonPrimitive(3),
        reason = "Medical checkup appointment",
        studentName = "Jane Smith",
        studentClass = "10-B",
        status = "Pending",
        leaveType = "Medical Leave",
        attPer = "92.0",
        attachment = "medical_certificate.pdf"
    )

    return listOf(
        LeaveCardPresentation.fromLeave(
            mockLeave1,
            screenType = AppliedLeavesScreenType.STUDENT_LEAVES,
            showAttendance = true
        ),
        LeaveCardPresentation.fromLeave(
            mockLeave2,
            screenType = AppliedLeavesScreenType.STUDENT_LEAVES,
            showAttendance = true,
            isSelected = true
        )
    )
}

private fun getMockLeavesApproved(): List<LeaveCardPresentation> {
    val mockLeave = Leave(
        lvID = 3,
        fromDate = "01 Dec 2024",
        tillDate = "03 Dec 2024",
        submittedOn = "25 Nov 2024",
        duration = kotlinx.serialization.json.JsonPrimitive(3),
        reason = "Wedding ceremony",
        studentName = "Alice Johnson",
        studentClass = "10-C",
        status = "Approved",
        leaveType = "Personal Leave",
        actionOn = "27 Nov 2024",
        teacherName = "Ms. Sarah Williams"
    )

    return listOf(
        LeaveCardPresentation.fromLeave(
            mockLeave,
            screenType = AppliedLeavesScreenType.STUDENT_LEAVES
        )
    )
}

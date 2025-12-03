package com.app.ecarepro.feature.leave.leave_report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.ApplType
import com.app.ecarepro.core.domain.model.LeaveReportItem
import com.app.ecarepro.core.domain.model.LeaveReportTab
import com.app.ecarepro.designsystem.core.component.EcareProBackground
import com.app.ecarepro.designsystem.core.component.Loader
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.feature.leave.leave_report.component.ApproveBottomSheet
import com.app.ecarepro.feature.leave.leave_report.component.CancelBottomSheet
import com.app.ecarepro.feature.leave.leave_report.component.ForwardBottomSheet
import com.app.ecarepro.feature.leave.leave_report.component.LeaveReportBottomBar
import com.app.ecarepro.feature.leave.leave_report.component.LeaveReportCard
import com.app.ecarepro.feature.leave.leave_report.component.RejectBottomSheet

@Composable
fun LeaveReportScreen(
    viewModel: LeaveReportViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LeaveReportScreenContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onTabChanged = { tab ->
            viewModel.handleIntent(LeaveReportIntent.OnTabChanged(tab))
        },
        onApplTypeChanged = { applType ->
            viewModel.handleIntent(LeaveReportIntent.OnApplTypeChanged(applType))
        },
        onLeaveSelected = { leaveId, isSelected ->
            viewModel.handleIntent(LeaveReportIntent.OnLeaveSelected(leaveId, isSelected))
        },
        onSelectAllToggled = {
            viewModel.handleIntent(LeaveReportIntent.OnSelectAllToggled)
        },
        onAttendancePercentageToggled = {
            viewModel.handleIntent(LeaveReportIntent.OnAttendancePercentageToggled)
        },
        onApproveClicked = { leave ->
            viewModel.handleIntent(LeaveReportIntent.OnApproveClicked(leave))
        },
        onRejectClicked = { leave ->
            viewModel.handleIntent(LeaveReportIntent.OnRejectClicked(leave))
        },
        onForwardClicked = { leave ->
            viewModel.handleIntent(LeaveReportIntent.OnForwardClicked(leave))
        },
        onCancelClicked = { leave ->
            viewModel.handleIntent(LeaveReportIntent.OnCancelClicked(leave))
        },
        onApproveAllClicked = {
            viewModel.handleIntent(LeaveReportIntent.OnApproveAllClicked)
        },
        onRejectAllClicked = {
            viewModel.handleIntent(LeaveReportIntent.OnRejectAllClicked)
        },
        onBottomSheetDismissed = {
            viewModel.handleIntent(LeaveReportIntent.OnBottomSheetDismissed)
        },
        onApproveConfirmed = { startDate, endDate ->
            viewModel.handleIntent(LeaveReportIntent.OnApproveConfirmed(startDate, endDate))
        },
        onRejectConfirmed = { reason ->
            viewModel.handleIntent(LeaveReportIntent.OnRejectConfirmed(reason))
        },
        onForwardConfirmed = { managerId ->
            viewModel.handleIntent(LeaveReportIntent.OnForwardConfirmed(managerId))
        },
        onCancelConfirmed = {
            viewModel.handleIntent(LeaveReportIntent.OnCancelConfirmed)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LeaveReportScreenContent(
    uiState: LeaveReportUiState,
    onBackClick: () -> Unit,
    onTabChanged: (LeaveReportTab) -> Unit,
    onApplTypeChanged: (ApplType) -> Unit,
    onLeaveSelected: (Int, Boolean) -> Unit,
    onSelectAllToggled: () -> Unit,
    onAttendancePercentageToggled: () -> Unit,
    onApproveClicked: (LeaveReportItem) -> Unit,
    onRejectClicked: (LeaveReportItem) -> Unit,
    onForwardClicked: (LeaveReportItem) -> Unit,
    onCancelClicked: (LeaveReportItem) -> Unit,
    onApproveAllClicked: () -> Unit,
    onRejectAllClicked: () -> Unit,
    onBottomSheetDismissed: () -> Unit,
    onApproveConfirmed: (String?, String?) -> Unit,
    onRejectConfirmed: (String?) -> Unit,
    onForwardConfirmed: (Int) -> Unit,
    onCancelConfirmed: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        EcareProBackground(
            overlayColor = MaterialTheme.appColors.background
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                contentWindowInsets = WindowInsets.systemBars,
                topBar = {
                    Column {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "Leave Report",
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = onBackClick) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.appColors.surface,
                                titleContentColor = MaterialTheme.appColors.textPrimary
                            )
                        )

                        // Application Type Tabs (Staff/Student)
                        TabRow(
                            selectedTabIndex = if (uiState.applType == ApplType.STAFF) 0 else 1,
                            containerColor = MaterialTheme.appColors.surface,
                            contentColor = MaterialTheme.appColors.primary
                        ) {
                            Tab(
                                selected = uiState.applType == ApplType.STAFF,
                                onClick = { onApplTypeChanged(ApplType.STAFF) },
                                text = {
                                    Text(
                                        text = "Staff",
                                        fontWeight = if (uiState.applType == ApplType.STAFF) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            )
                            Tab(
                                selected = uiState.applType == ApplType.STUDENT,
                                onClick = { onApplTypeChanged(ApplType.STUDENT) },
                                text = {
                                    Text(
                                        text = "Student",
                                        fontWeight = if (uiState.applType == ApplType.STUDENT) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            )
                        }

                        // Status Tabs (Pending/Approved/Rejected/Cancelled)
                        ScrollableTabRow(
                            selectedTabIndex = uiState.selectedTab.ordinal,
                            containerColor = MaterialTheme.appColors.surface,
                            contentColor = MaterialTheme.appColors.primary,
                            edgePadding = 16.dp
                        ) {
                            LeaveReportTab.entries.forEach { tab ->
                                Tab(
                                    selected = uiState.selectedTab == tab,
                                    onClick = { onTabChanged(tab) },
                                    text = {
                                        Text(
                                            text = tab.displayName,
                                            fontWeight = if (uiState.selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                )
                            }
                        }

                        // Show Attendance Percentage checkbox (Student only)
                        if (uiState.applType == ApplType.STUDENT) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.appColors.surface)
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = uiState.showAttendancePercentage,
                                    onCheckedChange = { onAttendancePercentageToggled() }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Show Attendance(%)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.appColors.textPrimary
                                )
                            }
                        }

                        // Select All checkbox (Student + Pending tab only)
                        if (uiState.applType == ApplType.STUDENT &&
                            uiState.selectedTab == LeaveReportTab.PENDING &&
                            uiState.leaveItems.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.appColors.surface)
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = uiState.selectedLeaveIds.size == uiState.leaveItems.size,
                                    onCheckedChange = { onSelectAllToggled() }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Select All",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.appColors.textPrimary
                                )
                            }
                        }
                    }
                },
                bottomBar = {
                    if (uiState.applType == ApplType.STUDENT &&
                        uiState.selectedTab == LeaveReportTab.PENDING &&
                        uiState.selectedLeaveIds.isNotEmpty()) {
                        LeaveReportBottomBar(
                            selectedCount = uiState.selectedLeaveIds.size,
                            onApproveAllClicked = onApproveAllClicked,
                            onRejectAllClicked = onRejectAllClicked
                        )
                    }
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    if (uiState.errorMessage != null) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = uiState.errorMessage,
                                color = MaterialTheme.appColors.error
                            )
                        }
                    } else if (uiState.leaveItems.isEmpty() && !uiState.isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No leave applications",
                                color = MaterialTheme.appColors.textSecondary
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.appColors.background)
                        ) {
                            items(
                                items = uiState.leaveItems,
                                key = { it.lvID }
                            ) { leave ->
                                LeaveReportCard(
                                    leave = leave,
                                    applType = uiState.applType,
                                    selectedTab = uiState.selectedTab,
                                    isSelected = leave.lvID in uiState.selectedLeaveIds,
                                    showCheckbox = uiState.applType == ApplType.STUDENT &&
                                                   uiState.selectedTab == LeaveReportTab.PENDING,
                                    showAttendancePercentage = uiState.showAttendancePercentage,
                                    onCheckboxChanged = { isSelected ->
                                        onLeaveSelected(leave.lvID, isSelected)
                                    },
                                    onApproveClicked = { onApproveClicked(leave) },
                                    onRejectClicked = { onRejectClicked(leave) },
                                    onForwardClicked = { onForwardClicked(leave) },
                                    onCancelClicked = { onCancelClicked(leave) }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (uiState.isLoading) {
            Loader()
        }

        // Bottom Sheets
        if (uiState.showApproveBottomSheet && uiState.selectedLeaveForAction != null) {
            ApproveBottomSheet(
                leave = uiState.selectedLeaveForAction,
                isSubmitting = uiState.isSubmitting,
                onDismiss = onBottomSheetDismissed,
                onConfirm = onApproveConfirmed
            )
        }

        if (uiState.showRejectBottomSheet && uiState.selectedLeaveForAction != null) {
            RejectBottomSheet(
                leave = uiState.selectedLeaveForAction,
                isRejectionReasonRequired = uiState.isRejectionReasonRequired,
                isSubmitting = uiState.isSubmitting,
                onDismiss = onBottomSheetDismissed,
                onConfirm = onRejectConfirmed
            )
        }

        if (uiState.showForwardBottomSheet && uiState.selectedLeaveForAction != null) {
            ForwardBottomSheet(
                leave = uiState.selectedLeaveForAction,
                reportingManagers = uiState.reportingManagers,
                isSubmitting = uiState.isSubmitting,
                onDismiss = onBottomSheetDismissed,
                onConfirm = onForwardConfirmed
            )
        }

        if (uiState.showCancelBottomSheet && uiState.selectedLeaveForAction != null) {
            CancelBottomSheet(
                leave = uiState.selectedLeaveForAction,
                isSubmitting = uiState.isSubmitting,
                onDismiss = onBottomSheetDismissed,
                onConfirm = onCancelConfirmed
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LeaveReportScreenPreview() {
    EcareProTheme {
        LeaveReportScreenContent(
            uiState = LeaveReportUiState(
                applType = ApplType.STAFF,
                selectedTab = LeaveReportTab.PENDING,
                leaveItems = emptyList()
            ),
            onBackClick = {},
            onTabChanged = {},
            onApplTypeChanged = {},
            onLeaveSelected = { _, _ -> },
            onSelectAllToggled = {},
            onAttendancePercentageToggled = {},
            onApproveClicked = {},
            onRejectClicked = {},
            onForwardClicked = {},
            onCancelClicked = {},
            onApproveAllClicked = {},
            onRejectAllClicked = {},
            onBottomSheetDismissed = {},
            onApproveConfirmed = { _, _ -> },
            onRejectConfirmed = {},
            onForwardConfirmed = {},
            onCancelConfirmed = {}
        )
    }
}

package com.app.ecarepro.feature.leave

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.LeaveApplication
import com.app.ecarepro.designsystem.core.component.EcareProBackground
import com.app.ecarepro.designsystem.core.component.Loader
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.feature.leave.component.LeaveCard
import com.app.ecarepro.feature.leave.component.LeaveHeader

@Composable
fun LeaveListScreen(
    viewModel: LeaveListViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onApplyLeaveClick: () -> Unit = {},
    onLeaveClick: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LeaveListScreenContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onTabSelected = { tab ->
            viewModel.handleIntent(LeaveListIntent.OnTabChanged(tab))
        },
        onApplyLeaveClick = {
            viewModel.handleIntent(LeaveListIntent.OnApplyLeaveClicked)
            onApplyLeaveClick()
        },
        onLeaveClick = { leaveId ->
            viewModel.handleIntent(LeaveListIntent.OnLeaveClicked(leaveId))
            onLeaveClick(leaveId)
        },
        onDeleteClick = { leaveId ->
            viewModel.handleIntent(LeaveListIntent.OnDeleteLeave(leaveId))
        }
    )
}

@Composable
internal fun LeaveListScreenContent(
    uiState: LeaveListUiState,
    onBackClick: () -> Unit,
    onTabSelected: (LeaveTab) -> Unit,
    onApplyLeaveClick: () -> Unit,
    onLeaveClick: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit
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
                    LeaveHeader(
                        selectedTab = uiState.selectedTab,
                        onTabSelected = onTabSelected,
                        onBackClick = onBackClick
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = onApplyLeaveClick,
                        containerColor = MaterialTheme.appColors.primary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Apply Leave",
                            tint = Color.White
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
                    } else if (filteredLeaveList(uiState).isEmpty() && !uiState.isLoading) {
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
                                items = filteredLeaveList(uiState),
                                key = { it.lvID }
                            ) { leave ->
                                LeaveCard(
                                    leave = leave,
                                    onClick = { onLeaveClick(leave.lvID) },
                                    onDeleteClick = { onDeleteClick(leave.lvID) }
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
    }
}

private fun filteredLeaveList(uiState: LeaveListUiState): List<LeaveApplication> {
    return when (uiState.selectedTab) {
        LeaveTab.ALL -> uiState.leaveApplications
        LeaveTab.PENDING -> uiState.leaveApplications.filter { it.status.equals("Pending", true) }
        LeaveTab.APPROVED -> uiState.leaveApplications.filter { it.status.equals("Approved", true) }
        LeaveTab.REJECTED -> uiState.leaveApplications.filter { it.status.equals("Rejected", true) }
        LeaveTab.CANCELLED -> uiState.leaveApplications.filter { it.status.equals("Cancelled", true) }
    }
}

@Preview(showBackground = true)
@Composable
fun LeaveListScreenPreview() {
    EcareProTheme {
        LeaveListScreenContent(
            uiState = LeaveListUiState(
                selectedTab = LeaveTab.ALL,
                leaveApplications = emptyList(),
                isLoading = false
            ),
            onBackClick = {},
            onTabSelected = {},
            onApplyLeaveClick = {},
            onLeaveClick = {},
            onDeleteClick = {}
        )
    }
}

package com.app.ecarepro.feature.leave.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.feature.leave.ApplyLeaveScreen
import com.app.ecarepro.feature.leave.LeaveListScreen
import com.app.ecarepro.feature.leave.leave_report.LeaveReportScreen
import kotlinx.serialization.Serializable

const val LEAVE_LIST_ROUTE = "leave_list"
const val APPLY_LEAVE_ROUTE = "apply_leave"
const val LEAVE_REPORT_ROUTE = "leave_report"

fun NavController.navigateToLeaveList(navOptions: NavOptions? = null) {
    navigate(LEAVE_LIST_ROUTE, navOptions)
}

fun NavController.navigateToApplyLeave(navOptions: NavOptions? = null) {
    navigate(APPLY_LEAVE_ROUTE, navOptions)
}

fun NavController.navigateToLeaveReport(navOptions: NavOptions? = null) {
    navigate(LEAVE_REPORT_ROUTE, navOptions)
}

fun NavGraphBuilder.leaveListScreen(
    onBackClick: () -> Unit,
    onApplyLeaveClick: () -> Unit,
    onLeaveClick: (Int) -> Unit
) {
    composable(route = LEAVE_LIST_ROUTE) {
        LeaveListScreen(
            onBackClick = onBackClick,
            onApplyLeaveClick = onApplyLeaveClick,
            onLeaveClick = onLeaveClick
        )
    }
}

fun NavGraphBuilder.applyLeaveScreen(
    onBackClick: () -> Unit,
    onSuccess: () -> Unit
) {
    composable(route = APPLY_LEAVE_ROUTE) {
        ApplyLeaveScreen(
            onBackClick = onBackClick,
            onSuccess = onSuccess
        )
    }
}

fun NavGraphBuilder.leaveReportScreen(
    onBackClick: () -> Unit
) {
    composable(route = LEAVE_REPORT_ROUTE) {
        LeaveReportScreen(
            onBackClick = onBackClick
        )
    }
}

// Navigation3 support for TestActivity
@Serializable
sealed interface LeaveNavigationGraph : NavKey {
    @Serializable
    data object LeaveReport : LeaveNavigationGraph

    @Serializable
    data object LeaveList : LeaveNavigationGraph

    @Serializable
    data object ApplyLeave : LeaveNavigationGraph
}

@Composable
fun EntryProviderBuilder<NavKey>.EntryLeaveNavigation(
    onBackClick: () -> Unit
) {
    entry<LeaveNavigationGraph.LeaveReport> {
        LeaveReportScreen(
            onBackClick = onBackClick
        )
    }

    entry<LeaveNavigationGraph.LeaveList> {
        LeaveListScreen(
            onBackClick = onBackClick,
            onApplyLeaveClick = {},
            onLeaveClick = {}
        )
    }

    entry<LeaveNavigationGraph.ApplyLeave> {
        ApplyLeaveScreen(
            onBackClick = onBackClick,
            onSuccess = {}
        )
    }
}

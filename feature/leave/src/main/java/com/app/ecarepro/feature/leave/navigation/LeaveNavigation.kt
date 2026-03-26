package com.app.ecarepro.feature.leave.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.feature.leave.appliedleaves.data.AppliedLeavesScreenType
import com.app.ecarepro.feature.leave.appliedleaves.ui.AppliedLeavesScreen
import com.app.ecarepro.feature.leave.applyleave.data.ApplyLeaveScreenType
import com.app.ecarepro.feature.leave.applyleave.ui.ApplyLeaveScreen
import kotlinx.serialization.Serializable

// Route constants
const val APPLIED_LEAVES_SELF_ROUTE = "applied_leaves_self"
const val APPLIED_LEAVES_STUDENT_ROUTE = "applied_leaves_student"
const val APPLIED_LEAVES_STAFF_ROUTE = "applied_leaves_staff"
const val APPLY_LEAVE_STUDENT_ROUTE = "apply_leave_student"
const val APPLY_LEAVE_STAFF_ROUTE = "apply_leave_staff"

// Navigation functions
fun NavController.navigateToAppliedLeavesSelf(navOptions: NavOptions? = null) {
    navigate(APPLIED_LEAVES_SELF_ROUTE, navOptions)
}

fun NavController.navigateToAppliedLeavesStudent(navOptions: NavOptions? = null) {
    navigate(APPLIED_LEAVES_STUDENT_ROUTE, navOptions)
}

fun NavController.navigateToAppliedLeavesStaff(navOptions: NavOptions? = null) {
    navigate(APPLIED_LEAVES_STAFF_ROUTE, navOptions)
}

fun NavController.navigateToApplyLeaveStudent(navOptions: NavOptions? = null) {
    navigate(APPLY_LEAVE_STUDENT_ROUTE, navOptions)
}

fun NavController.navigateToApplyLeaveStaff(navOptions: NavOptions? = null) {
    navigate(APPLY_LEAVE_STAFF_ROUTE, navOptions)
}

// Applied Leaves Screens
fun NavGraphBuilder.appliedLeavesSelfScreen(
    onBackClick: () -> Unit,
    onApplyLeaveClick: () -> Unit
) {
    composable(route = APPLIED_LEAVES_SELF_ROUTE) {
        AppliedLeavesScreen(
            screenType = AppliedLeavesScreenType.SELF_LEAVES,
            navigateToBack = onBackClick,
            navigateToApplyLeave = onApplyLeaveClick
        )
    }
}

fun NavGraphBuilder.appliedLeavesStudentScreen(
    onBackClick: () -> Unit
) {
    composable(route = APPLIED_LEAVES_STUDENT_ROUTE) {
        AppliedLeavesScreen(
            screenType = AppliedLeavesScreenType.STUDENT_LEAVES,
            navigateToBack = onBackClick
        )
    }
}

fun NavGraphBuilder.appliedLeavesStaffScreen(
    onBackClick: () -> Unit
) {
    composable(route = APPLIED_LEAVES_STAFF_ROUTE) {
        AppliedLeavesScreen(
            screenType = AppliedLeavesScreenType.STAFF_LEAVES,
            navigateToBack = onBackClick
        )
    }
}

// Apply Leave Screens
fun NavGraphBuilder.applyLeaveStudentScreen(
    onBackClick: () -> Unit
) {
    composable(route = APPLY_LEAVE_STUDENT_ROUTE) {
        ApplyLeaveScreen(
            screenType = ApplyLeaveScreenType.STUDENT,
            navigateToBack = onBackClick
        )
    }
}

fun NavGraphBuilder.applyLeaveStaffScreen(
    onBackClick: () -> Unit
) {
    composable(route = APPLY_LEAVE_STAFF_ROUTE) {
        ApplyLeaveScreen(
            screenType = ApplyLeaveScreenType.STAFF,
            navigateToBack = onBackClick
        )
    }
}

// Navigation3 support
@Serializable
sealed interface LeaveNavigationGraph : NavKey {
    @Serializable
    data object AppliedLeavesSelf : LeaveNavigationGraph

    @Serializable
    data object AppliedLeavesStudent : LeaveNavigationGraph

    @Serializable
    data object AppliedLeavesStaff : LeaveNavigationGraph

    @Serializable
    data object ApplyLeaveStudent : LeaveNavigationGraph

    @Serializable
    data object ApplyLeaveStaff : LeaveNavigationGraph
}

@Composable
fun EntryProviderBuilder<NavKey>.EntryLeaveNavigation(
    onBackClick: () -> Unit,
    onApplyLeaveClick: () -> Unit = {}
) {
    entry<LeaveNavigationGraph.AppliedLeavesSelf> {
        AppliedLeavesScreen(
            screenType = AppliedLeavesScreenType.SELF_LEAVES,
            navigateToBack = onBackClick,
            navigateToApplyLeave = onApplyLeaveClick
        )
    }

    entry<LeaveNavigationGraph.AppliedLeavesStudent> {
        AppliedLeavesScreen(
            screenType = AppliedLeavesScreenType.STUDENT_LEAVES,
            navigateToBack = onBackClick
        )
    }

    entry<LeaveNavigationGraph.AppliedLeavesStaff> {
        AppliedLeavesScreen(
            screenType = AppliedLeavesScreenType.STAFF_LEAVES,
            navigateToBack = onBackClick
        )
    }

    entry<LeaveNavigationGraph.ApplyLeaveStudent> {
        ApplyLeaveScreen(
            screenType = ApplyLeaveScreenType.STUDENT,
            navigateToBack = onBackClick
        )
    }

    entry<LeaveNavigationGraph.ApplyLeaveStaff> {
        ApplyLeaveScreen(
            screenType = ApplyLeaveScreenType.STAFF,
            navigateToBack = onBackClick
        )
    }
}

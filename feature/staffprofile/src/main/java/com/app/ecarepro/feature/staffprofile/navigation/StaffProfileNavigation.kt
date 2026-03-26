package com.app.ecarepro.feature.staffprofile.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.core.domain.model.StaffProfile
import com.app.ecarepro.feature.staffprofile.StaffProfileScreen
import com.app.ecarepro.feature.staffprofile.screens.StaffProfileDetailsScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface StaffProfileNavigationGraph : NavKey {
    @Serializable
    data object StaffProfileList : StaffProfileNavigationGraph

    @Serializable
    data class StaffProfileDetail(val staffId: Int) : StaffProfileNavigationGraph
}

@Composable
fun EntryProviderBuilder<NavKey>.EntryStaffProfileNavigation(
    backStack: SnapshotStateList<NavKey>,
    navigateToBack: () -> Unit,
    navigateToProfileDetail: (StaffProfile) -> Unit,
) {
    entry<StaffProfileNavigationGraph.StaffProfileList> {
        StaffProfileScreen(
            navigateToBack = navigateToBack,
            navigateToProfileDetail = { profile ->
                // Navigate to staff profile details
                backStack.add(StaffProfileNavigationGraph.StaffProfileDetail(profile.sid))
            }
        )
    }

    entry<StaffProfileNavigationGraph.StaffProfileDetail> { StaffProfileDetail ->
        StaffProfileDetailsScreen(
            staffId = StaffProfileDetail.staffId,
            navigateToBack = {
                backStack.remove(StaffProfileDetail)
            }
        )
    }
}

package com.app.ecarepro.feature.assignment.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.feature.assignment.AssignmentScreen
import com.app.ecarepro.feature.assignment.screens.AssignmentDetailsScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface AssignmentNavigationGraph : NavKey {
    @Serializable
    data object Assignment : AssignmentNavigationGraph

    @Serializable
    data object AssignmentDetails : AssignmentNavigationGraph

    @Serializable
    data object AddAssignment : AssignmentNavigationGraph
}


@Composable
fun EntryProviderBuilder<NavKey>.EntryAssignmentNavigation(
    backStack: SnapshotStateList<NavKey>,
    navigateToBack: () -> Unit,
    openDocVier: (title: String, url: String) -> Unit,
) {
    entry<AssignmentNavigationGraph.Assignment> {
        AssignmentScreen(
            navigateToBack = navigateToBack,
            navigateToAddAssignment = {
               // backStack.add(AssignmentNavigationGraph.AddAssignment)
            },
            openDocViewer = openDocVier,
            navigateToDetails = {
                backStack.add(AssignmentNavigationGraph.AssignmentDetails)
            }
        )
    }

    entry<AssignmentNavigationGraph.AssignmentDetails> {
        AssignmentDetailsScreen(
            navigateToBack = navigateToBack,
        )
    }

}

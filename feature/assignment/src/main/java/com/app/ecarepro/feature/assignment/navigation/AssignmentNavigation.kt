package com.app.ecarepro.feature.assignment.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.core.ui.viewmodel.navKeyViewModel
import com.app.ecarepro.feature.assignment.AssignmentScreen
import com.app.ecarepro.feature.assignment.screens.AddAssignmentScreen
import com.app.ecarepro.feature.assignment.screens.AssignmentDetailsScreen
import com.app.ecarepro.feature.assignment.screens.AssignmentDetailsViewModel
import kotlinx.serialization.Serializable

@Serializable
sealed interface AssignmentNavigationGraph : NavKey {
    @Serializable
    data object Assignment : AssignmentNavigationGraph

    @Serializable
    data class AssignmentDetails(val assignment: com.app.ecarepro.core.domain.model.Assignment) :
        AssignmentNavigationGraph

    @Serializable
    data class AddAssignment(val assignment: com.app.ecarepro.core.domain.model.Assignment? = null) :
        AssignmentNavigationGraph
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
                backStack.add(AssignmentNavigationGraph.AddAssignment())
            },
            openDocViewer = openDocVier,
            navigateToDetails = { assignment ->
                backStack.add(AssignmentNavigationGraph.AssignmentDetails(assignment))
            }
        )
    }

    entry<AssignmentNavigationGraph.AssignmentDetails> {
        val assignmentDetailsViewModel: AssignmentDetailsViewModel = navKeyViewModel(it)
        AssignmentDetailsScreen(
            navigateToBack = navigateToBack,
        )
    }

    entry<AssignmentNavigationGraph.AddAssignment> {
        AddAssignmentScreen(
            viewModel = navKeyViewModel(it),
            navigateToBack = navigateToBack,
        )
    }

}

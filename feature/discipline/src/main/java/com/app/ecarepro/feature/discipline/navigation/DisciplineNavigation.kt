package com.app.ecarepro.feature.discipline.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.core.ui.viewmodel.navKeyViewModel
import com.app.ecarepro.feature.discipline.DisciplineUserType
import com.app.ecarepro.feature.discipline.screens.addinfractionscreen.AddInfractionScreen
import com.app.ecarepro.feature.discipline.screens.addinfractionscreen.AddInfractionViewModel
import com.app.ecarepro.feature.discipline.screens.addappreciationscreen.AddAppreciationScreen
import com.app.ecarepro.feature.discipline.screens.addappreciationscreen.AddAppreciationViewModel
import com.app.ecarepro.feature.discipline.screens.addcompliancescreen.AddComplianceScreen
import com.app.ecarepro.feature.discipline.screens.addcompliancescreen.AddComplianceViewModel
import com.app.ecarepro.feature.discipline.screens.infractionlistscreen.InfractionListScreen
import com.app.ecarepro.feature.discipline.screens.infractionlistscreen.InfractionListViewModel
import com.app.ecarepro.feature.discipline.screens.viewallinfractionscreen.ViewAllInfractionsScreen
import com.app.ecarepro.feature.discipline.screens.viewallinfractionscreen.ViewAllInfractionsViewModel
import com.app.ecarepro.feature.discipline.screens.appreciationlistscreen.AppreciationListScreen
import com.app.ecarepro.feature.discipline.screens.appreciationlistscreen.AppreciationListViewModel
import com.app.ecarepro.feature.discipline.screens.viewallappreciationscreen.ViewAllAppreciationsScreen
import com.app.ecarepro.feature.discipline.screens.viewallappreciationscreen.ViewAllAppreciationsViewModel
import kotlinx.serialization.Serializable

@Serializable
sealed interface DisciplineNavigationGraph : NavKey {

    @Serializable
    data class InfractionList(val userType: DisciplineUserType) : DisciplineNavigationGraph

    @Serializable
    data class AddInfraction(val userType: DisciplineUserType, val userId: Int) : DisciplineNavigationGraph

    @Serializable
    data class ViewAllInfractions(val userType: DisciplineUserType, val userId: Int) : DisciplineNavigationGraph

    @Serializable
    data class AddCompliance(
        val infractionID: String,
        val userType: DisciplineUserType,
        val userId: Int,
    ) : DisciplineNavigationGraph

    @Serializable
    data class AppreciationList(val dummy: Int = 0) : DisciplineNavigationGraph

    @Serializable
    data class AddAppreciation(val studentId: Int) : DisciplineNavigationGraph

    @Serializable
    data class ViewAllAppreciations(val studentId: Int) : DisciplineNavigationGraph
}

@Composable
fun EntryProviderBuilder<NavKey>.EntryDisciplineNavigation(
    backStack: SnapshotStateList<NavKey>,
    navigateToBack: () -> Unit,
) {
    entry<DisciplineNavigationGraph.InfractionList> {
        val viewModel: InfractionListViewModel = navKeyViewModel(it)
        InfractionListScreen(
            viewModel = viewModel,
            navigateToBack = navigateToBack,
            navigateToAddInfraction = { userType, userId ->
                backStack.add(DisciplineNavigationGraph.AddInfraction(userType, userId))
            },
            navigateToViewAllInfractions = { userType, userId ->
                backStack.add(DisciplineNavigationGraph.ViewAllInfractions(userType, userId))
            },
        )
    }

    entry<DisciplineNavigationGraph.AddInfraction> {
        val viewModel: AddInfractionViewModel = navKeyViewModel(it)
        AddInfractionScreen(
            viewModel = viewModel,
            navigateToBack = navigateToBack,
        )
    }

    entry<DisciplineNavigationGraph.ViewAllInfractions> {
        val viewModel: ViewAllInfractionsViewModel = navKeyViewModel(it)
        ViewAllInfractionsScreen(
            viewModel = viewModel,
            navigateToBack = navigateToBack,
            navigateToAddCompliance = { infractionID, userType, userId ->
                backStack.add(DisciplineNavigationGraph.AddCompliance(infractionID, userType, userId))
            },
        )
    }

    entry<DisciplineNavigationGraph.AddCompliance> {
        val viewModel: AddComplianceViewModel = navKeyViewModel(it)
        AddComplianceScreen(
            viewModel = viewModel,
            navigateToBack = navigateToBack,
        )
    }

    entry<DisciplineNavigationGraph.AppreciationList> {
        val viewModel: AppreciationListViewModel = navKeyViewModel(it)
        AppreciationListScreen(
            viewModel = viewModel,
            navigateToBack = navigateToBack,
            navigateToAddAppreciation = { studentId ->
                backStack.add(DisciplineNavigationGraph.AddAppreciation(studentId))
            },
            navigateToViewAllAppreciations = { studentId ->
                backStack.add(DisciplineNavigationGraph.ViewAllAppreciations(studentId))
            },
        )
    }

    entry<DisciplineNavigationGraph.AddAppreciation> {
        val viewModel: AddAppreciationViewModel = navKeyViewModel(it)
        AddAppreciationScreen(
            viewModel = viewModel,
            navigateToBack = navigateToBack,
        )
    }

    entry<DisciplineNavigationGraph.ViewAllAppreciations> {
        val viewModel: ViewAllAppreciationsViewModel = navKeyViewModel(it)
        ViewAllAppreciationsScreen(
            viewModel = viewModel,
            navigateToBack = navigateToBack,
        )
    }
}

package com.app.ecarepro.feature.schoolcode.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.core.ui.viewmodel.navKeyViewModel
import com.app.ecarepro.feature.schoolcode.SchoolCodeIntent
import com.app.ecarepro.feature.schoolcode.SchoolCodeView
import com.app.ecarepro.feature.schoolcode.SchoolCodeViewModel
import com.app.ecarepro.feature.schoolcode.search.FindYourSchoolView
import kotlinx.serialization.Serializable

@Serializable
sealed interface SchoolCodeNavigationGraph : NavKey {
    @Serializable
    data object SchoolCode : SchoolCodeNavigationGraph

    @Serializable
    data object SearchSchoolCode : SchoolCodeNavigationGraph
}


@Composable
fun EntryProviderBuilder<NavKey>.EntrySchoolCodeNavigation(
    backStack: SnapshotStateList<NavKey>,
    navigateToLogin: (schoolCode: String) -> Unit,
) {
    val schoolCodeViewModel: SchoolCodeViewModel = hiltViewModel()
    entry<SchoolCodeNavigationGraph.SchoolCode> {
        SchoolCodeView(
            viewModel = schoolCodeViewModel,
            navigateToNextScreen = navigateToLogin,
            navigateToFindCodeScreen = {
                backStack.add(SchoolCodeNavigationGraph.SearchSchoolCode)
            }
        )
    }
    entry<SchoolCodeNavigationGraph.SearchSchoolCode> {
        FindYourSchoolView(
            onSchoolCodeSelect = { schoolCode ->
                backStack.removeLastOrNull()
                schoolCodeViewModel.handleIntent(SchoolCodeIntent.OnCodeChanged(schoolCode))
            }
        )
    }
}

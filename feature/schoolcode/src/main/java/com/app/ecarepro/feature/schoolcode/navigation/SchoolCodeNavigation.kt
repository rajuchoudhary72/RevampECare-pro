package com.app.ecarepro.feature.schoolcode.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.core.ui.viewmodel.navKeyViewModel
import com.app.ecarepro.feature.schoolcode.SchoolCodeIntent
import com.app.ecarepro.feature.schoolcode.SchoolCodeScreen
import com.app.ecarepro.feature.schoolcode.SchoolCodeViewModel
import com.app.ecarepro.feature.schoolcode.search.SchoolSearchScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface SchoolCodeNavigationGraph : NavKey {
    @Serializable
    data class SchoolCode(val isAddAccount: Boolean = false) : SchoolCodeNavigationGraph

    @Serializable
    data object SearchSchoolCode : SchoolCodeNavigationGraph
}


@Composable
fun EntryProviderBuilder<NavKey>.EntrySchoolCodeNavigation(
    backStack: SnapshotStateList<NavKey>,
    navigateToLogin: (schoolCode: String, isAddAccount: Boolean) -> Unit,
) {
    val schoolCodeViewModel: SchoolCodeViewModel = hiltViewModel()
    entry<SchoolCodeNavigationGraph.SchoolCode> { key ->
        SchoolCodeScreen(
            viewModel = schoolCodeViewModel,
            navigateToNextScreen = { schoolCode ->
                navigateToLogin(schoolCode, key.isAddAccount)
            },
            navigateToFindCodeScreen = {
                backStack.add(SchoolCodeNavigationGraph.SearchSchoolCode)
            }
        )
    }
    entry<SchoolCodeNavigationGraph.SearchSchoolCode> {
        SchoolSearchScreen(
            onSchoolCodeSelect = { schoolCode ->
                backStack.removeLastOrNull()
                schoolCodeViewModel.handleIntent(SchoolCodeIntent.OnCodeChanged(schoolCode))
            }
        )
    }
}

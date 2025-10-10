package com.app.ecarepro.feature.schoolcode.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.feature.schoolcode.SchoolCodeScreen
import com.app.ecarepro.feature.schoolcode.search.SchoolSearchScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface SchoolCodeNavigationGraph : NavKey {
    @Serializable
    data object SchoolCode : SchoolCodeNavigationGraph

    @Serializable
    data object SearchSchoolCode : SchoolCodeNavigationGraph
}


@Composable
fun EntryProviderBuilder<NavKey>.SchoolCode(
    backStack: SnapshotStateList<NavKey>,
    navigateToLogin: (schoolCode: String) -> Unit,
) {
    entry<SchoolCodeNavigationGraph.SchoolCode> {
        SchoolCodeScreen(
            navigateToNextScreen = navigateToLogin,
            navigateToFindCodeScreen = {
                backStack.add(SchoolCodeNavigationGraph.SearchSchoolCode)
            }
        )
    }
    entry<SchoolCodeNavigationGraph.SearchSchoolCode> {
        SchoolSearchScreen {}
    }
}

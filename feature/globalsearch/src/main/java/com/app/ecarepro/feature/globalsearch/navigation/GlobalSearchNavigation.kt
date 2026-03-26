package com.app.ecarepro.feature.globalsearch.navigation

import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.feature.globalsearch.search.GlobalSearchScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface GlobalSearchNavGraph : NavKey {
    @Serializable
    data object GlobalSearch : GlobalSearchNavGraph
}

fun EntryProviderBuilder<NavKey>.entryGlobalSearchNavigation(
    navigateBack: () -> Unit,
    navigateToStudentProfile: (Int) -> Unit = {},
    navigateToModule: (Int) -> Unit = {},
) {
    entry<GlobalSearchNavGraph.GlobalSearch> {
        GlobalSearchScreen(
            navigateBack = navigateBack,
            navigateToStudentProfile = navigateToStudentProfile,
            navigateToModule = navigateToModule,
        )
    }
}

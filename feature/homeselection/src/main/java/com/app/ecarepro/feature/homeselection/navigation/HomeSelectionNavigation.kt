package com.app.ecarepro.feature.homeselection.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.core.domain.model.HomeScreenType
import com.app.ecarepro.feature.homeselection.HomeSelectionScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface HomeSelectionNavigationGraph : NavKey {
    @Serializable
    data object HomeSelection : HomeSelectionNavigationGraph
}


@Composable
fun EntryProviderBuilder<NavKey>.EntryHomeSelectionNavigation(
    onComplete: (HomeScreenType) -> Unit,
) {
    entry<HomeSelectionNavigationGraph.HomeSelection> {
        HomeSelectionScreen(
            onComplete = onComplete
        )
    }
}

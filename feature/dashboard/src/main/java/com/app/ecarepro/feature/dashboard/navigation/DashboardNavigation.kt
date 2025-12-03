package com.app.ecarepro.feature.dashboard.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.feature.dashboard.DashboardScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface DashboardNavigationGraph : NavKey {
    @Serializable
    data object Dashboard : DashboardNavigationGraph
}


@Composable
fun EntryProviderBuilder<NavKey>.EntryDashboardNavigation(
    navigateToTestingMenu:() -> Unit = {}
) {
    entry<DashboardNavigationGraph.Dashboard> {
        DashboardScreen(
            navigateToTestingMenu
        )
    }
}

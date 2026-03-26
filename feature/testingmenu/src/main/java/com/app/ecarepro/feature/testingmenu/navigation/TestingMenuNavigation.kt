package com.app.ecarepro.feature.testingmenu.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.feature.testingmenu.TestingMenuScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface TestingMenuNavigationGraph : NavKey {
    @Serializable
    data object TestingMenu : TestingMenuNavigationGraph
}


@Composable
fun EntryProviderBuilder<NavKey>.EntryTestingMenuNavigation(
    navigateToBack: () -> Unit,
    navigateToModule: (NavKey) -> Unit,
) {
    entry<TestingMenuNavigationGraph.TestingMenu> {
        TestingMenuScreen(
            navigateToBack = navigateToBack,
            navigateToModule = navigateToModule
        )
    }
}
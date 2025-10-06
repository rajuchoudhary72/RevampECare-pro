package com.app.ecarepro.feature.schoolcode.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
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
    navigateToLogin: () -> Unit
) {
    entry<SchoolCodeNavigationGraph.SchoolCode> {

    }
    entry<SchoolCodeNavigationGraph.SearchSchoolCode> {

    }
}

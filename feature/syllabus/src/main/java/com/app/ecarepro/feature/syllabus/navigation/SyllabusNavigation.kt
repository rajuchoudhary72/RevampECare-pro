package com.app.ecarepro.feature.syllabus.navigation


import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.feature.syllabus.SyllabusScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface SyllabusNavigationGraph : NavKey {
    @Serializable
    data object Syllabus : SyllabusNavigationGraph
}


@Composable
fun EntryProviderBuilder<NavKey>.EntrySyllabusNavigation(
    navigateToBack: () -> Unit,
    openDocVier: (title: String, url: String) -> Unit,
) {
    entry<SyllabusNavigationGraph.Syllabus> {
        SyllabusScreen(
            navigateToBack = navigateToBack,
            openDocVier = openDocVier
        )
    }
}

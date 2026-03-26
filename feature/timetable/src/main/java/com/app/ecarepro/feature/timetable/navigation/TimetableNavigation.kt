package com.app.ecarepro.feature.timetable.navigation


import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.feature.timetable.TimetableScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface TimetableNavigationGraph : NavKey {
    @Serializable
    data object Timetable : TimetableNavigationGraph
}


@Composable
fun EntryProviderBuilder<NavKey>.EntryTimetableNavigation(
    navigateToBack: () -> Unit,
) {
    entry<TimetableNavigationGraph.Timetable> {
        TimetableScreen(
            navigateToBack = navigateToBack
        )
    }
}

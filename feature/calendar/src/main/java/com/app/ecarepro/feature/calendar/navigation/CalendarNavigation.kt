package com.app.ecarepro.feature.calendar.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.feature.calendar.CalendarScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface CalendarNavGraph : NavKey {
    @Serializable
    data object ActivityCalendarMain : CalendarNavGraph
}

@Composable
fun EntryProviderBuilder<NavKey>.EntryCalendarNavigation(
    navigateToBack: () -> Unit,
) {
    entry<CalendarNavGraph.ActivityCalendarMain> {
        CalendarScreen(navigateToBack = navigateToBack)
    }
}

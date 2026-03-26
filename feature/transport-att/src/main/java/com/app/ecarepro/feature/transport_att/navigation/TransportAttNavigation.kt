package com.app.ecarepro.feature.transport_att.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.feature.transport_att.TransportAttScreen
import com.app.ecarepro.feature.transport_att.TransportAttViewModel
import com.app.ecarepro.feature.transport_att.screens.OutPassScreen
import com.app.ecarepro.feature.transport_att.screens.ViewAttendanceScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface TransportAttNavigationGraph : NavKey {
    @Serializable
    data object TransportAttendance : TransportAttNavigationGraph

    @Serializable
    data object ViewAttendance : TransportAttNavigationGraph

    @Serializable
    data object OutPass : TransportAttNavigationGraph
}

@Composable
fun EntryProviderBuilder<NavKey>.EntryTransportAttNavigation(
    backStack: SnapshotStateList<NavKey>,
    navigateToBack: () -> Unit,
) {
    entry<TransportAttNavigationGraph.TransportAttendance> {
        val viewModel: TransportAttViewModel = hiltViewModel()
        TransportAttScreen(
            viewModel = viewModel,
            navigateToBack = navigateToBack,
            navigateToViewAttendance = {
                backStack.add(TransportAttNavigationGraph.ViewAttendance)
            },
            navigateToOutPass = {
                backStack.add(TransportAttNavigationGraph.OutPass)
            },
        )
    }

    entry<TransportAttNavigationGraph.ViewAttendance> {
        ViewAttendanceScreen(
            navigateToBack = { backStack.removeLastOrNull() },
        )
    }

    entry<TransportAttNavigationGraph.OutPass> {
        OutPassScreen(
            navigateToBack = { backStack.removeLastOrNull() },
        )
    }
}

package com.app.ecarepro.feature.marksmanager.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.core.ui.viewmodel.navKeyViewModel
import com.app.ecarepro.feature.marksmanager.MarkManagerScreen
import com.app.ecarepro.feature.marksmanager.MarkManagerViewModel
import kotlinx.serialization.Serializable

/**
 * Distinguishes how the WebView screen should obtain its URL.
 *
 * - [MARKS_ENTRY]    — calls GenerateToken API, reads `marksEntryURL` from the schools DB.
 * - [SCHOOL_WEBSITE] — reads `webSite` from the schools DB directly (no API call).
 */
@Serializable
enum class MarkManagerType { MARKS_ENTRY, SCHOOL_WEBSITE }

@Serializable
sealed interface MarkManagerNavGraph : NavKey {
    /** Entry point for both Mark Manager and School Website WebView screens. */
    @Serializable
    data class MarkManager(val type: MarkManagerType) : MarkManagerNavGraph
}

@Composable
fun EntryProviderBuilder<NavKey>.entryMarkManagerNavigation(
    navigateBack: () -> Unit,
) {
    entry<MarkManagerNavGraph.MarkManager> { key ->
        val viewModel: MarkManagerViewModel = navKeyViewModel(key)
        MarkManagerScreen(
            viewModel = viewModel,
            onBackClick = navigateBack,
        )
    }
}

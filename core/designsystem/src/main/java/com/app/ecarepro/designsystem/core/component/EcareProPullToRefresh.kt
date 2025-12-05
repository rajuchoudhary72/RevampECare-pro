package com.app.ecarepro.designsystem.core.component

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * A reusable pull-to-refresh container that can be used throughout the app.
 * Wraps any content with pull-to-refresh functionality.
 *
 * @param isRefreshing Whether the refresh is currently in progress
 * @param onRefresh Callback invoked when user pulls to refresh
 * @param modifier Modifier for the container
 * @param content The content to display inside the pull-to-refresh container
 *
 * Usage example:
 * ```
 * EcareProPullToRefresh(
 *     isRefreshing = viewModel.isRefreshing,
 *     onRefresh = { viewModel.handleIntent(YourIntent.OnRefresh) }
 * ) {
 *     LazyColumn { ... }
 * }
 * ```
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcareProPullToRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier
    ) {
        content()
    }
}

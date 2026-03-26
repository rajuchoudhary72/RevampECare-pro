package com.app.ecarepro.core.ui.component

import androidx.compose.ui.Alignment
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.ecarepro.designsystem.core.theme.appColors

/**
 * Project-standard pull-to-refresh wrapper.
 *
 * Uses Material3 [PullToRefreshBox] with the app's primary colour on the indicator
 * so every screen looks consistent without repeating the setup.
 *
 * Usage:
 * ```
 * EcareProPullToRefresh(
 *     isRefreshing = uiState.isRefreshing,
 *     onRefresh    = { viewModel.handleIntent(MyIntent.OnRefresh) },
 * ) {
 *     LazyColumn { ... }
 * }
 * ```
 *
 * @param isRefreshing true while the refresh is in progress (drives the indicator)
 * @param onRefresh    called when the user completes a pull gesture
 * @param modifier     applied to the outer [PullToRefreshBox]
 * @param content      the scrollable content (LazyColumn, Column, etc.)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcareProPullToRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val state = rememberPullToRefreshState()
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = state,
        modifier = modifier,
        indicator = {
            PullToRefreshDefaults.Indicator(
                state = state,
                isRefreshing = isRefreshing,
                color = MaterialTheme.appColors.primary,
                modifier = Modifier.align(Alignment.TopCenter),
            )
        },
    ) {
        content()
    }
}

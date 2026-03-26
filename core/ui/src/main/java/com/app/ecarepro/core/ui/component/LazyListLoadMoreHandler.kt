package com.app.ecarepro.core.ui.component

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember

/**
 * Fires [onLoadMore] when the LazyColumn/LazyRow is scrolled near the end.
 *
 * Uses [derivedStateOf] so it only triggers recomposition when the threshold
 * actually changes — not on every scroll frame.
 *
 * Usage:
 * ```
 * val listState = rememberLazyListState()
 *
 * LazyListLoadMoreHandler(
 *     listState = listState,
 *     enabled   = uiState.showLoadMoreView,
 *     onLoadMore = { viewModel.handleIntent(MyIntent.OnLoadMore) },
 * )
 *
 * LazyColumn(state = listState) { ... }
 * ```
 *
 * @param listState  the [LazyListState] from your LazyColumn / LazyRow
 * @param enabled    set false when on the last page to stop triggering
 * @param buffer     items from the end that activate the trigger (default 3)
 * @param onLoadMore called once when the user scrolls within [buffer] items of the end
 */
@Composable
fun LazyListLoadMoreHandler(
    listState: LazyListState,
    enabled: Boolean,
    buffer: Int = 3,
    onLoadMore: () -> Unit,
) {
    val shouldLoadMore = remember(listState) {
        derivedStateOf {
            if (!enabled) return@derivedStateOf false
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                ?: return@derivedStateOf false
            val totalItems = listState.layoutInfo.totalItemsCount
            totalItems > 0 && lastVisible >= totalItems - buffer
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) onLoadMore()
    }
}

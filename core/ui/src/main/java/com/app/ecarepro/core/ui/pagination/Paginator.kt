package com.app.ecarepro.core.ui.pagination

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Generic paginator following the project MD pattern:
 *   - page starts at 1
 *   - isLastPage = (total - page * itemsPerPage) <= 0
 *   - loadNextPage increments page then fetches; reverts on failure
 *
 * Usage in a ViewModel:
 * ```
 * private val paginator = Paginator<MyItem>(
 *     scope        = viewModelScope,
 *     onRequest    = { page -> repository.getItems(page) },
 *     onSuccess    = { items, isLastPage, isReset -> /* update uiState */ },
 *     onError      = { error, isReset -> /* update uiState */ },
 * )
 *
 * init { paginator.refresh() }
 * ```
 *
 * @param T          the item type returned per page
 * @param scope      CoroutineScope to launch fetch jobs (typically viewModelScope)
 * @param itemsPerPage items per page — default 50 as per MD
 * @param onRequest  returns Flow<Result<Pair<List<T>, Int>>> where Int is the total count
 * @param onSuccess  called with (newItems, isLastPage, isReset)
 * @param onError    called with (throwable, isReset)
 */
class Paginator<T>(
    private val scope: CoroutineScope,
    private val itemsPerPage: Int = DEFAULT_ITEMS_PER_PAGE,
    private val onRequest: (page: Int) -> Flow<Result<Pair<List<T>, Int>>>,
    private val onSuccess: (newItems: List<T>, isLastPage: Boolean, isReset: Boolean) -> Unit,
    private val onError: (error: Throwable, isReset: Boolean) -> Unit,
) {
    // MD pattern state: page, isLoading, isLastPage, totalPageCount
    private var page = DEFAULT_PAGE
    private var isLoading = false
    private var isLastPage = false
    var totalPageCount = DEFAULT_PAGE
        private set

    /**
     * Reset to page 1 and reload from scratch.
     * Call this on init, pull-to-refresh, or retry after an error.
     */
    fun refresh() {
        page = DEFAULT_PAGE
        isLastPage = false
        isLoading = false
        fetch(isReset = true)
    }

    /**
     * Load the next page.
     * Safe to call multiple times — ignored if already loading or on the last page.
     *
     * @param retry pass true to re-fetch the previously failed page (page is not incremented)
     */
    fun loadNextPage(retry: Boolean = false) {
        if (isLastPage || isLoading) return
        if (!retry) page++
        fetch(isReset = false)
    }

    private fun fetch(isReset: Boolean) {
        isLoading = true
        scope.launch {
            onRequest(page).collectLatest { result ->
                isLoading = false
                result.fold(
                    onSuccess = { (items, total) ->
                        // MD: isLastPage = (total - page * 50) <= 0
                        isLastPage = (total - page * itemsPerPage) <= 0
                        // MD: totalPages = total / 50 (or +1 if remainder)
                        totalPageCount = if (total % itemsPerPage == 0) {
                            total / itemsPerPage
                        } else {
                            total / itemsPerPage + 1
                        }
                        onSuccess(items, isLastPage, isReset)
                    },
                    onFailure = { error ->
                        if (!isReset) page-- // revert so the next loadNextPage retries same page
                        onError(error, isReset)
                    },
                )
            }
        }
    }

    companion object {
        const val DEFAULT_PAGE = 1
        const val DEFAULT_ITEMS_PER_PAGE = 50
    }
}

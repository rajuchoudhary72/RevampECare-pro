package com.app.ecarepro.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.model.Feed
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsConstants
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsManager
import com.app.ecarepro.ui.message.sent.DEFAULT_PAGE
import com.app.ecarepro.ui.message.sent.UNKNOWN_ERROR_MESSAGE
import com.app.ecarepro.ui.message.sent.calculateTotalPages
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedsViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository,
    private val analyticsManager: AnalyticsManager
) : ViewModel() {
    private var page = DEFAULT_PAGE
    private var isLoading: Boolean = false
    private var isLastPage: Boolean = true
    private var totalPageCount: Int = DEFAULT_PAGE

    val uiState = MutableStateFlow<FeedsUiState>(FeedsUiState.Loading)

    init {
        fetchInboxMessages()
    }

    fun isLoading() = isLoading

    fun isLastPage() = isLastPage

    fun totalPageCount() = totalPageCount

    private fun fetchInboxMessages(isRefresh: Boolean = false) {
        viewModelScope.launch {
            schoolRepository
                .getFeeds(page)
                .map { result ->
                    isLoading = false
                    if (result.isSuccess) {
                        result.getOrNull()!!.let { response ->
                            isLastPage =
                                com.app.ecarepro.ui.message.sent.isLastPage(response.total, page)
                            totalPageCount = calculateTotalPages(response.total)
                            val feeds = mutableListOf<Feed>()
                            val currentUiState = uiState.value
                            if (isRefresh.not() && currentUiState is FeedsUiState.Success) {
                                feeds.addAll(currentUiState.feeds)
                            }
                            feeds.addAll(response.updates ?: emptyList())

                            if (feeds.isEmpty()) {
                                FeedsUiState.EmptyInbox

                            } else {
                                FeedsUiState.Success(feeds)
                            }
                        }

                    } else {
                        val error = result.exceptionOrNull() ?: IllegalArgumentException(
                            UNKNOWN_ERROR_MESSAGE
                        )
                        val currentUiState = uiState.value
                        if (currentUiState is FeedsUiState.Success) {
                            currentUiState.copy(
                                showLoadMoreView = false,
                                loadMoreError = error
                            )
                        } else {
                            FeedsUiState.Error(
                                error
                            )
                        }
                    }
                }
                .collectLatest { uiState ->
                    this@FeedsViewModel.uiState.update {
                        uiState
                    }
                }
        }
    }

    fun loadNextPage(retry: Boolean = false) {
        viewModelScope.launch {
            isLoading = true
            val currentUiState = uiState.value
            if (currentUiState is FeedsUiState.Success) {
                uiState.update {
                    currentUiState.copy(showLoadMoreView = true)
                }
            }
            if (retry.not())
                page += 1

            fetchInboxMessages()
        }
    }


    fun refresh() {
        page = DEFAULT_PAGE
        isLoading = false
        isLastPage = false
        totalPageCount = DEFAULT_PAGE
        fetchInboxMessages(true)
    }

    fun sendScreenEvent() {
        analyticsManager.trackScreen(AnalyticsConstants.Screens.ATTENDANCE_TAB)
    }

}

sealed interface FeedsUiState {
    object Loading : FeedsUiState

    object EmptyInbox : FeedsUiState

    data class Success(
        val feeds: List<Feed>,
        val showLoadMoreView: Boolean = false,
        val loadMoreError: Throwable? = null
    ) : FeedsUiState

    data class Error(
        val error: Throwable
    ) : FeedsUiState

    fun isLoading() = this == Loading

    fun getErrorOrNull() = if (this is Error) this.error else null
}

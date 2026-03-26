package com.app.ecarepro.feature.feed

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.FeedModule
import com.app.ecarepro.core.domain.model.FeedUpdate
import com.app.ecarepro.core.domain.repository.FeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    init {
        loadFeed()
    }

    fun handleIntent(intent: FeedIntent) {
        when (intent) {
            is FeedIntent.LoadFeed -> loadFeed(refresh = false)
            is FeedIntent.RefreshFeed -> loadFeed(refresh = true)
            is FeedIntent.LoadMoreFeed -> loadMoreFeed()
            is FeedIntent.OnFeedItemClick -> Unit
            is FeedIntent.ChangeTab -> {
                _uiState.update { it.copy(selectedTab = intent.tab) }
                loadFeed(refresh = true)
            }
            is FeedIntent.ShowFilter -> {
                _uiState.update { it.copy(showFilterSheet = true) }
            }
            is FeedIntent.DismissFilter -> {
                _uiState.update { it.copy(showFilterSheet = false) }
            }
            is FeedIntent.ApplyFilter -> {
                val filtered = applyFilter(
                    all = _uiState.value.allFeeds,
                    selectedMenuIDs = intent.selectedMenuIDs,
                )
                _uiState.update {
                    it.copy(
                        showFilterSheet = false,
                        activeFilterMenuIDs = intent.selectedMenuIDs,
                        feeds = filtered,
                    )
                }
            }
        }
    }

    private fun loadFeed(refresh: Boolean = false) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            feedRepository.getFeeds(isDashboard = true, page = 1).collect { result ->
                result.fold(
                    onSuccess = { response ->
                        val feeds = applyFilter(response.updates, _uiState.value.activeFilterMenuIDs)
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                allFeeds = response.updates,
                                feeds = feeds,
                                modules = response.modules,
                                currentPage = 1,
                                totalCount = response.total,
                                hasMorePages = response.updates.size < response.total,
                                errorMessage = null,
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = error.message ?: "Failed to load feed",
                            )
                        }
                    }
                )
            }
        }
    }

    private fun loadMoreFeed() {
        val state = _uiState.value
        if (state.isLoading || state.isLoadingMore || !state.hasMorePages) return

        val nextPage = state.currentPage + 1

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }

            feedRepository.getFeeds(isDashboard = true, page = nextPage).collect { result ->
                result.fold(
                    onSuccess = { response ->
                        val newAll = state.allFeeds + response.updates
                        val feeds = applyFilter(newAll, state.activeFilterMenuIDs)
                        _uiState.update {
                            it.copy(
                                isLoadingMore = false,
                                allFeeds = newAll,
                                feeds = feeds,
                                currentPage = nextPage,
                                hasMorePages = newAll.size < response.total,
                                errorMessage = null,
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isLoadingMore = false,
                                errorMessage = error.message ?: "Failed to load more feed",
                            )
                        }
                    }
                )
            }
        }
    }

    private fun applyFilter(all: List<FeedUpdate>, selectedMenuIDs: Set<Int>): List<FeedUpdate> {
        if (selectedMenuIDs.isEmpty()) return all
        return all.filter { it.menuID in selectedMenuIDs }
    }
}

sealed interface FeedIntent {
    data object LoadFeed : FeedIntent
    data object RefreshFeed : FeedIntent
    data object LoadMoreFeed : FeedIntent
    data class OnFeedItemClick(val feedUpdate: FeedUpdate) : FeedIntent
    data class ChangeTab(val tab: FeedTab) : FeedIntent
    data object ShowFilter : FeedIntent
    data object DismissFilter : FeedIntent
    data class ApplyFilter(val selectedMenuIDs: Set<Int>) : FeedIntent
}

enum class FeedTab {
    LATEST,
    PINNED,
}

@Immutable
data class FeedUiState(
    val feeds: List<FeedUpdate> = emptyList(),
    val allFeeds: List<FeedUpdate> = emptyList(),
    val modules: List<FeedModule> = emptyList(),
    val activeFilterMenuIDs: Set<Int> = emptySet(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val currentPage: Int = 1,
    val totalCount: Int = 0,
    val hasMorePages: Boolean = true,
    val errorMessage: String? = null,
    val selectedTab: FeedTab = FeedTab.LATEST,
    val showFilterSheet: Boolean = false,
)

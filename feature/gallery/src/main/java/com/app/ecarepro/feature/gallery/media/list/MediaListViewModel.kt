package com.app.ecarepro.feature.gallery.media.list

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.MediaItem
import com.app.ecarepro.core.domain.repository.GalleryRepository
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaListViewModel @Inject constructor(
    private val galleryRepository: GalleryRepository,
) : BaseViewModel<MediaListIntent, MediaListEvent>() {

    private val _uiState = MutableStateFlow<UiState<MediaListUiState>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    // All items loaded from API (unfiltered)
    private var allItems: List<MediaItem> = emptyList()
    private var allYears: List<String> = emptyList()

    init {
        fetchMediaGallery()
    }

    private fun fetchMediaGallery(queryType: Int = 0, year: Int = 0) {
        _uiState.update { UiState.Loading }
        viewModelScope.launch {
            galleryRepository.getMediaGallery(queryType = queryType, year = year).collect { result ->
                result
                    .onSuccess { data ->
                        allItems = data.items
                        if (year == 0 && queryType == 0) allYears = data.years
                        val current = (_uiState.value as? UiState.Success)?.data
                        _uiState.update {
                            UiState.Success(
                                MediaListUiState(
                                    items = applyLocalFilter(data.items, current?.searchText.orEmpty(), current?.selectedQueryType ?: 0),
                                    filteredItems = applyLocalFilter(data.items, current?.searchText.orEmpty(), current?.selectedQueryType ?: 0),
                                    years = if (year == 0 && queryType == 0) data.years else allYears,
                                    selectedYear = year,
                                    selectedQueryType = current?.selectedQueryType ?: queryType,
                                    searchText = current?.searchText.orEmpty(),
                                )
                            )
                        }
                    }
                    .onFailure { error ->
                        _uiState.update { UiState.Error(error.errorMessage()) }
                    }
            }
        }
    }

    override fun handleIntent(intent: MediaListIntent) {
        when (intent) {
            is MediaListIntent.OnBackClicked -> sendEvent(MediaListEvent.NavigateBack)
            is MediaListIntent.OnRefresh -> {
                val current = (_uiState.value as? UiState.Success)?.data
                fetchMediaGallery(queryType = current?.selectedQueryType ?: 0, year = current?.selectedYear ?: 0)
            }
            is MediaListIntent.OnItemClicked -> sendEvent(MediaListEvent.NavigateToDetail(intent.item))
            is MediaListIntent.OnSearchTextChanged -> onSearchTextChanged(intent.text)
            is MediaListIntent.OnQueryTypeSelected -> onQueryTypeSelected(intent.queryType)
            is MediaListIntent.OnYearSelected -> onYearSelected(intent.year)
        }
    }

    private fun onSearchTextChanged(text: String) {
        _uiState.update { currentState ->
            (currentState as? UiState.Success)?.let { state ->
                val filtered = applyLocalFilter(allItems, text, state.data.selectedQueryType)
                UiState.Success(state.data.copy(searchText = text, filteredItems = filtered))
            } ?: currentState
        }
    }

    private fun onQueryTypeSelected(queryType: Int) {
        _uiState.update { currentState ->
            (currentState as? UiState.Success)?.let { state ->
                val filtered = applyLocalFilter(allItems, state.data.searchText, queryType)
                UiState.Success(state.data.copy(selectedQueryType = queryType, filteredItems = filtered))
            } ?: currentState
        }
    }

    private fun onYearSelected(year: Int) {
        _uiState.update { currentState ->
            (currentState as? UiState.Success)?.let { state ->
                UiState.Success(state.data.copy(selectedYear = year))
            } ?: currentState
        }
        fetchMediaGallery(year = year)
    }

    private fun applyLocalFilter(items: List<MediaItem>, searchText: String, queryType: Int): List<MediaItem> {
        if (searchText.isBlank()) return items
        return when (queryType) {
            1 -> items.filter { it.newsName.contains(searchText, ignoreCase = true) }
            2 -> items.filter { it.headline.contains(searchText, ignoreCase = true) }
            3 -> items.filter { it.publishedOn.contains(searchText, ignoreCase = true) }
            else -> items.filter {
                it.newsName.contains(searchText, ignoreCase = true) ||
                    it.headline.contains(searchText, ignoreCase = true)
            }
        }
    }
}

@Immutable
data class MediaListUiState(
    val items: List<MediaItem> = emptyList(),
    val filteredItems: List<MediaItem> = emptyList(),
    val years: List<String> = emptyList(),
    val selectedYear: Int = 0,
    val selectedQueryType: Int = 0,   // 0=All, 1=Newspaper, 2=Headline, 3=Publish date
    val searchText: String = "",
)

sealed interface MediaListIntent {
    data object OnBackClicked : MediaListIntent
    data object OnRefresh : MediaListIntent
    data class OnItemClicked(val item: MediaItem) : MediaListIntent
    data class OnSearchTextChanged(val text: String) : MediaListIntent
    data class OnQueryTypeSelected(val queryType: Int) : MediaListIntent
    data class OnYearSelected(val year: Int) : MediaListIntent
}

sealed interface MediaListEvent {
    data object NavigateBack : MediaListEvent
    data class NavigateToDetail(val item: MediaItem) : MediaListEvent
}

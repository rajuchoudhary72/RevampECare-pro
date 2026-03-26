package com.app.ecarepro.feature.gallery.kids.list

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.KidsAcademicYear
import com.app.ecarepro.core.domain.model.KidsAlbum
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
class KidsListViewModel @Inject constructor(
    private val galleryRepository: GalleryRepository,
) : BaseViewModel<KidsListIntent, KidsListEvent>() {

    private val _uiState = MutableStateFlow<UiState<KidsListUiState>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var allAlbums: List<KidsAlbum> = emptyList()

    init {
        fetchAlbums()
    }

    private fun fetchAlbums(yrID: Int = 0) {
        _uiState.update { current ->
            if (current is UiState.Success) UiState.Success(current.data.copy(isRefreshing = true))
            else UiState.Loading
        }
        viewModelScope.launch {
            galleryRepository.getKidsAlbums(yrID = yrID).collect { result ->
                result
                    .onSuccess { data ->
                        allAlbums = data.albums
                        val currentState = (_uiState.value as? UiState.Success)?.data
                        _uiState.update {
                            UiState.Success(
                                KidsListUiState(
                                    albums = filterAlbums(data.albums, currentState?.searchQuery.orEmpty()),
                                    academicYears = if (yrID == 0) data.academicYears else currentState?.academicYears ?: data.academicYears,
                                    selectedYear = currentState?.selectedYear ?: data.academicYears.firstOrNull { it.isCur } ?: data.academicYears.firstOrNull(),
                                    searchQuery = currentState?.searchQuery.orEmpty(),
                                    isRefreshing = false,
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

    override fun handleIntent(intent: KidsListIntent) {
        when (intent) {
            is KidsListIntent.OnBackClicked -> sendEvent(KidsListEvent.NavigateBack)
            is KidsListIntent.OnAlbumClicked -> sendEvent(KidsListEvent.NavigateToDetail(intent.kid, intent.title))
            is KidsListIntent.OnSearchQueryChanged -> onSearchQueryChanged(intent.query)
            is KidsListIntent.OnYearSelected -> onYearSelected(intent.year)
            is KidsListIntent.OnRefresh -> fetchAlbums((_uiState.value as? UiState.Success)?.data?.selectedYear?.yrID ?: 0)
        }
    }

    private fun onSearchQueryChanged(query: String) {
        _uiState.update { current ->
            (current as? UiState.Success)?.let { state ->
                UiState.Success(state.data.copy(searchQuery = query, albums = filterAlbums(allAlbums, query)))
            } ?: current
        }
    }

    private fun onYearSelected(year: KidsAcademicYear) {
        _uiState.update { current ->
            (current as? UiState.Success)?.let { state ->
                UiState.Success(state.data.copy(selectedYear = year))
            } ?: current
        }
        fetchAlbums(yrID = year.yrID)
    }

    private fun filterAlbums(albums: List<KidsAlbum>, query: String): List<KidsAlbum> {
        if (query.isBlank()) return albums
        return albums.filter { it.title.contains(query, ignoreCase = true) }
    }
}

@Immutable
data class KidsListUiState(
    val albums: List<KidsAlbum> = emptyList(),
    val academicYears: List<KidsAcademicYear> = emptyList(),
    val selectedYear: KidsAcademicYear? = null,
    val searchQuery: String = "",
    val isRefreshing: Boolean = false,
)

sealed interface KidsListIntent {
    data object OnBackClicked : KidsListIntent
    data object OnRefresh : KidsListIntent
    data class OnAlbumClicked(val kid: String, val title: String) : KidsListIntent
    data class OnSearchQueryChanged(val query: String) : KidsListIntent
    data class OnYearSelected(val year: KidsAcademicYear) : KidsListIntent
}

sealed interface KidsListEvent {
    data object NavigateBack : KidsListEvent
    data class NavigateToDetail(val kid: String, val title: String) : KidsListEvent
}

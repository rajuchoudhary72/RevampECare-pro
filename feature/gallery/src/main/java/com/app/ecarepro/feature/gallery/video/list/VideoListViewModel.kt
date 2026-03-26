package com.app.ecarepro.feature.gallery.video.list

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.VideoAlbum
import com.app.ecarepro.core.domain.repository.GalleryRepository
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoListViewModel @Inject constructor(
    private val galleryRepository: GalleryRepository,
) : BaseViewModel<VideoListIntent, VideoListEvent>() {

    private val _uiState = MutableStateFlow<UiState<VideoListUiState>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchVideoAlbums()
    }

    private fun fetchVideoAlbums(isRefreshing: Boolean = false) {
        viewModelScope.launch {
            galleryRepository.getVideoAlbums().collect { result ->
                result
                    .onSuccess { albums ->
                        _uiState.update { currentState ->
                            val current = (currentState as? UiState.Success)?.data ?: VideoListUiState()
                            UiState.Success(
                                current.copy(
                                    allAlbums = albums,
                                    filteredAlbums = filterAlbums(albums, current.searchQuery),
                                    isRefreshing = false,
                                )
                            )
                        }
                    }
                    .onFailure { error ->
                        if (isRefreshing) {
                            _uiState.update { currentState ->
                                (currentState as? UiState.Success)?.let {
                                    UiState.Success(it.data.copy(isRefreshing = false))
                                } ?: currentState
                            }
                            sendEvent(VideoListEvent.ShowMessage(SnackbarMessage(error.errorMessage(), MessageType.ERROR)))
                        } else {
                            _uiState.update { UiState.Error(error.errorMessage()) }
                        }
                    }
            }
        }
    }

    override fun handleIntent(intent: VideoListIntent) {
        when (intent) {
            is VideoListIntent.OnBackClicked -> sendEvent(VideoListEvent.NavigateBack)
            is VideoListIntent.OnAlbumClicked -> sendEvent(VideoListEvent.NavigateToDetail(intent.albumId, intent.albumTitle))
            is VideoListIntent.OnSearchQueryChanged -> onSearchQueryChanged(intent.query)
            is VideoListIntent.OnRefresh -> {
                val current = (_uiState.value as? UiState.Success)?.data ?: return
                _uiState.update { UiState.Success(current.copy(isRefreshing = true)) }
                fetchVideoAlbums(isRefreshing = true)
            }
        }
    }

    private fun onSearchQueryChanged(query: String) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.update { UiState.Success(current.copy(searchQuery = query, filteredAlbums = filterAlbums(current.allAlbums, query))) }
    }

    private fun filterAlbums(albums: List<VideoAlbum>, query: String): List<VideoAlbum> {
        if (query.isBlank()) return albums
        return albums.filter { it.title.contains(query, ignoreCase = true) }
    }
}

@Immutable
data class VideoListUiState(
    val isRefreshing: Boolean = false,
    val allAlbums: List<VideoAlbum> = emptyList(),
    val filteredAlbums: List<VideoAlbum> = emptyList(),
    val searchQuery: String = "",
)

sealed interface VideoListIntent {
    data object OnBackClicked : VideoListIntent
    data class OnAlbumClicked(val albumId: String, val albumTitle: String) : VideoListIntent
    data class OnSearchQueryChanged(val query: String) : VideoListIntent
    data object OnRefresh : VideoListIntent
}

sealed interface VideoListEvent {
    data object NavigateBack : VideoListEvent
    data class NavigateToDetail(val albumId: String, val albumTitle: String) : VideoListEvent
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : VideoListEvent
}

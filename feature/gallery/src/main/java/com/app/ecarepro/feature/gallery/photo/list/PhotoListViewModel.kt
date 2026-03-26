package com.app.ecarepro.feature.gallery.photo.list

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.Album
import com.app.ecarepro.core.domain.model.AlbumType
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
class PhotoListViewModel @Inject constructor(
    private val galleryRepository: GalleryRepository,
) : BaseViewModel<PhotoListIntent, PhotoListEvent>() {

    private val _uiState = MutableStateFlow<UiState<PhotoListUiState>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchAlbumTypes()
    }

    private fun fetchAlbumTypes() {
        viewModelScope.launch {
            galleryRepository.getAlbumTypes().collect { result ->
                result
                    .onSuccess { types ->
                        val allType = AlbumType(typeID = 0, typeName = "All")
                        val allTypes = listOf(allType) + types
                        _uiState.update { currentState ->
                            val current = (currentState as? UiState.Success)?.data ?: PhotoListUiState()
                            UiState.Success(current.copy(albumTypes = allTypes))
                        }
                        fetchAlbums(typeId = 0)
                    }
                    .onFailure { error ->
                        _uiState.update { UiState.Error(error.errorMessage()) }
                    }
            }
        }
    }

    private fun fetchAlbums(typeId: Int, isRefreshing: Boolean = false) {
        viewModelScope.launch {
            galleryRepository.getAlbums(typeId = typeId).collect { result ->
                result
                    .onSuccess { albums ->
                        _uiState.update { currentState ->
                            val current = (currentState as? UiState.Success)?.data ?: PhotoListUiState()
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
                            sendEvent(PhotoListEvent.ShowMessage(SnackbarMessage(error.errorMessage(), MessageType.ERROR)))
                        } else {
                            _uiState.update { currentState ->
                                if (currentState is UiState.Success) {
                                    UiState.Success(currentState.data.copy(allAlbums = emptyList(), filteredAlbums = emptyList()))
                                } else {
                                    UiState.Error(error.errorMessage())
                                }
                            }
                        }
                    }
            }
        }
    }

    override fun handleIntent(intent: PhotoListIntent) {
        when (intent) {
            is PhotoListIntent.OnBackClicked -> sendEvent(PhotoListEvent.NavigateBack)
            is PhotoListIntent.OnAlbumClicked -> sendEvent(PhotoListEvent.NavigateToDetail(intent.albumId, intent.albumTitle))
            is PhotoListIntent.OnAlbumTypeSelected -> onAlbumTypeSelected(intent.albumType)
            is PhotoListIntent.OnSearchQueryChanged -> onSearchQueryChanged(intent.query)
            is PhotoListIntent.OnRefresh -> onRefresh()
        }
    }

    private fun onAlbumTypeSelected(albumType: AlbumType) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.update { UiState.Success(current.copy(selectedAlbumType = albumType, allAlbums = emptyList(), filteredAlbums = emptyList())) }
        fetchAlbums(typeId = albumType.typeID)
    }

    private fun onSearchQueryChanged(query: String) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.update { UiState.Success(current.copy(searchQuery = query, filteredAlbums = filterAlbums(current.allAlbums, query))) }
    }

    private fun onRefresh() {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.update { UiState.Success(current.copy(isRefreshing = true)) }
        fetchAlbums(typeId = current.selectedAlbumType.typeID, isRefreshing = true)
    }

    private fun filterAlbums(albums: List<Album>, query: String): List<Album> {
        if (query.isBlank()) return albums
        return albums.filter { it.title.contains(query, ignoreCase = true) }
    }
}

@Immutable
data class PhotoListUiState(
    val isRefreshing: Boolean = false,
    val albumTypes: List<AlbumType> = emptyList(),
    val selectedAlbumType: AlbumType = AlbumType(typeID = 0, typeName = "All"),
    val allAlbums: List<Album> = emptyList(),
    val filteredAlbums: List<Album> = emptyList(),
    val searchQuery: String = "",
)

sealed interface PhotoListIntent {
    data object OnBackClicked : PhotoListIntent
    data class OnAlbumClicked(val albumId: String, val albumTitle: String) : PhotoListIntent
    data class OnAlbumTypeSelected(val albumType: AlbumType) : PhotoListIntent
    data class OnSearchQueryChanged(val query: String) : PhotoListIntent
    data object OnRefresh : PhotoListIntent
}

sealed interface PhotoListEvent {
    data object NavigateBack : PhotoListEvent
    data class NavigateToDetail(val albumId: String, val albumTitle: String) : PhotoListEvent
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : PhotoListEvent
}

package com.app.ecarepro.feature.gallery.photo.slider

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.Photo
import com.app.ecarepro.core.domain.model.PhotoSetting
import com.app.ecarepro.core.domain.repository.GalleryRepository
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.AssistedViewModelFactory
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.feature.gallery.navigation.GalleryNavGraph
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = PhotoSliderViewModel.Factory::class)
class PhotoSliderViewModel @AssistedInject constructor(
    @Assisted val navKey: GalleryNavGraph.PhotoSlider,
    private val galleryRepository: GalleryRepository,
) : BaseViewModel<PhotoSliderIntent, PhotoSliderEvent>() {

    private val _uiState = MutableStateFlow<UiState<PhotoSliderUiState>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchPhotos()
    }

    private fun fetchPhotos() {
        viewModelScope.launch {
            galleryRepository.getAlbumDetail(navKey.albumId).collect { result ->
                result
                    .onSuccess { detail ->
                        _uiState.update {
                            UiState.Success(
                                PhotoSliderUiState(
                                    photos = detail.photos,
                                    setting = detail.setting,
                                    currentIndex = navKey.initialPhotoIndex.coerceIn(0, (detail.photos.size - 1).coerceAtLeast(0)),
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

    override fun handleIntent(intent: PhotoSliderIntent) {
        when (intent) {
            is PhotoSliderIntent.OnBackClicked -> sendEvent(PhotoSliderEvent.NavigateBack)
            is PhotoSliderIntent.OnPageChanged -> {
                val current = (_uiState.value as? UiState.Success)?.data ?: return
                _uiState.update { UiState.Success(current.copy(currentIndex = intent.index)) }
            }
            is PhotoSliderIntent.OnToggleLayout -> {
                val current = (_uiState.value as? UiState.Success)?.data ?: return
                _uiState.update { UiState.Success(current.copy(isVerticalLayout = !current.isVerticalLayout)) }
            }
            is PhotoSliderIntent.OnLikeClicked -> onLikeClicked(intent.photoId, intent.currentLike)
            is PhotoSliderIntent.OnBookmarkClicked -> onBookmarkClicked(intent.photoId, intent.currentBookmark)
            is PhotoSliderIntent.OnShareClicked -> sendEvent(PhotoSliderEvent.ShareImage(intent.photoUrl))
        }
    }

    private fun onLikeClicked(photoId: String, currentLike: Boolean) {
        val newLike = !currentLike
        viewModelScope.launch {
            galleryRepository.toggleLike(photoId, newLike).collect { result ->
                result
                    .onSuccess { totalLikes ->
                        _uiState.update { currentState ->
                            (currentState as? UiState.Success)?.let { state ->
                                val updatedPhotos = state.data.photos.map { photo ->
                                    if (photo.id == photoId) photo.copy(isLike = newLike, likes = totalLikes) else photo
                                }
                                UiState.Success(state.data.copy(photos = updatedPhotos))
                            } ?: currentState
                        }
                    }
                    .onFailure { error ->
                        sendEvent(PhotoSliderEvent.ShowMessage(SnackbarMessage(error.errorMessage(), MessageType.ERROR)))
                    }
            }
        }
    }

    private fun onBookmarkClicked(photoId: String, currentBookmark: Boolean) {
        val action = if (currentBookmark) "remove" else "add"
        val newBookmark = !currentBookmark
        viewModelScope.launch {
            galleryRepository.manageFavorite(photoId, action).collect { result ->
                result
                    .onSuccess {
                        _uiState.update { currentState ->
                            (currentState as? UiState.Success)?.let { state ->
                                val updatedPhotos = state.data.photos.map { photo ->
                                    if (photo.id == photoId) photo.copy(isFavourite = newBookmark) else photo
                                }
                                UiState.Success(state.data.copy(photos = updatedPhotos))
                            } ?: currentState
                        }
                    }
                    .onFailure { error ->
                        sendEvent(PhotoSliderEvent.ShowMessage(SnackbarMessage(error.errorMessage(), MessageType.ERROR)))
                    }
            }
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<GalleryNavGraph.PhotoSlider, PhotoSliderViewModel> {
        override fun create(param: GalleryNavGraph.PhotoSlider): PhotoSliderViewModel
    }
}

@Immutable
data class PhotoSliderUiState(
    val photos: List<Photo> = emptyList(),
    val setting: PhotoSetting = PhotoSetting(isLikeEnabled = false, isShareEnabled = false, isAddFavouriteEnabled = false),
    val currentIndex: Int = 0,
    val isVerticalLayout: Boolean = false,
)

sealed interface PhotoSliderIntent {
    data object OnBackClicked : PhotoSliderIntent
    data class OnPageChanged(val index: Int) : PhotoSliderIntent
    data object OnToggleLayout : PhotoSliderIntent
    data class OnLikeClicked(val photoId: String, val currentLike: Boolean) : PhotoSliderIntent
    data class OnBookmarkClicked(val photoId: String, val currentBookmark: Boolean) : PhotoSliderIntent
    data class OnShareClicked(val photoUrl: String) : PhotoSliderIntent
}

sealed interface PhotoSliderEvent {
    data object NavigateBack : PhotoSliderEvent
    data class ShareImage(val photoUrl: String) : PhotoSliderEvent
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : PhotoSliderEvent
}

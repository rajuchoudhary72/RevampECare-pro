package com.app.ecarepro.feature.gallery.photo.detail

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.AlbumDetail
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

@HiltViewModel(assistedFactory = PhotoDetailViewModel.Factory::class)
class PhotoDetailViewModel @AssistedInject constructor(
    @Assisted val navKey: GalleryNavGraph.PhotoAlbumDetail,
    private val galleryRepository: GalleryRepository,
) : BaseViewModel<PhotoDetailIntent, PhotoDetailEvent>() {

    private val _uiState = MutableStateFlow<UiState<PhotoDetailUiState>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchAlbumDetail()
    }

    private fun fetchAlbumDetail(isRefreshing: Boolean = false) {
        viewModelScope.launch {
            galleryRepository.getAlbumDetail(navKey.albumId).collect { result ->
                result
                    .onSuccess { detail ->
                        _uiState.update {
                            UiState.Success(
                                PhotoDetailUiState(
                                    albumDetail = detail,
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
                            sendEvent(PhotoDetailEvent.ShowMessage(SnackbarMessage(error.errorMessage(), MessageType.ERROR)))
                        } else {
                            _uiState.update { UiState.Error(error.errorMessage()) }
                        }
                    }
            }
        }
    }

    override fun handleIntent(intent: PhotoDetailIntent) {
        when (intent) {
            is PhotoDetailIntent.OnBackClicked -> sendEvent(PhotoDetailEvent.NavigateBack)
            is PhotoDetailIntent.OnPhotoClicked -> sendEvent(PhotoDetailEvent.NavigateToSlider(intent.photoIndex))
            is PhotoDetailIntent.OnToggleDescription -> {
                val current = (_uiState.value as? UiState.Success)?.data ?: return
                _uiState.update { UiState.Success(current.copy(isDescriptionExpanded = !current.isDescriptionExpanded)) }
            }
            is PhotoDetailIntent.OnRefresh -> {
                val current = (_uiState.value as? UiState.Success)?.data ?: return
                _uiState.update { UiState.Success(current.copy(isRefreshing = true)) }
                fetchAlbumDetail(isRefreshing = true)
            }
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<GalleryNavGraph.PhotoAlbumDetail, PhotoDetailViewModel> {
        override fun create(param: GalleryNavGraph.PhotoAlbumDetail): PhotoDetailViewModel
    }
}

@Immutable
data class PhotoDetailUiState(
    val albumDetail: AlbumDetail = AlbumDetail(
        title = "",
        description = "",
        eventDate = "",
        totalPhotos = 0,
        setting = PhotoSetting(isLikeEnabled = false, isShareEnabled = false, isAddFavouriteEnabled = false),
        photos = emptyList(),
    ),
    val isDescriptionExpanded: Boolean = false,
    val isRefreshing: Boolean = false,
)

sealed interface PhotoDetailIntent {
    data object OnBackClicked : PhotoDetailIntent
    data class OnPhotoClicked(val photoIndex: Int) : PhotoDetailIntent
    data object OnToggleDescription : PhotoDetailIntent
    data object OnRefresh : PhotoDetailIntent
}

sealed interface PhotoDetailEvent {
    data object NavigateBack : PhotoDetailEvent
    data class NavigateToSlider(val initialPhotoIndex: Int) : PhotoDetailEvent
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : PhotoDetailEvent
}

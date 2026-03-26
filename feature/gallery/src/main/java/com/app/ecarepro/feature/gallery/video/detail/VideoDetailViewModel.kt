package com.app.ecarepro.feature.gallery.video.detail

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.PhotoSetting
import com.app.ecarepro.core.domain.model.VideoAlbumDetail
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

@HiltViewModel(assistedFactory = VideoDetailViewModel.Factory::class)
class VideoDetailViewModel @AssistedInject constructor(
    @Assisted val navKey: GalleryNavGraph.VideoAlbumDetail,
    private val galleryRepository: GalleryRepository,
) : BaseViewModel<VideoDetailIntent, VideoDetailEvent>() {

    private val _uiState = MutableStateFlow<UiState<VideoDetailUiState>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchVideoAlbumDetail()
    }

    private fun fetchVideoAlbumDetail(isRefreshing: Boolean = false) {
        viewModelScope.launch {
            galleryRepository.getVideoAlbumDetail(navKey.albumId).collect { result ->
                result
                    .onSuccess { detail ->
                        _uiState.update {
                            UiState.Success(
                                VideoDetailUiState(
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
                            sendEvent(VideoDetailEvent.ShowMessage(SnackbarMessage(error.errorMessage(), MessageType.ERROR)))
                        } else {
                            _uiState.update { UiState.Error(error.errorMessage()) }
                        }
                    }
            }
        }
    }

    override fun handleIntent(intent: VideoDetailIntent) {
        when (intent) {
            is VideoDetailIntent.OnBackClicked -> sendEvent(VideoDetailEvent.NavigateBack)
            is VideoDetailIntent.OnVideoClicked -> sendEvent(VideoDetailEvent.NavigateToPlayer(intent.videoIndex))
            is VideoDetailIntent.OnToggleDescription -> {
                val current = (_uiState.value as? UiState.Success)?.data ?: return
                _uiState.update { UiState.Success(current.copy(isDescriptionExpanded = !current.isDescriptionExpanded)) }
            }
            is VideoDetailIntent.OnRefresh -> {
                val current = (_uiState.value as? UiState.Success)?.data ?: return
                _uiState.update { UiState.Success(current.copy(isRefreshing = true)) }
                fetchVideoAlbumDetail(isRefreshing = true)
            }
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<GalleryNavGraph.VideoAlbumDetail, VideoDetailViewModel> {
        override fun create(param: GalleryNavGraph.VideoAlbumDetail): VideoDetailViewModel
    }
}

@Immutable
data class VideoDetailUiState(
    val albumDetail: VideoAlbumDetail = VideoAlbumDetail(
        title = "",
        description = "",
        eventDate = "",
        totalVideos = 0,
        setting = PhotoSetting(isLikeEnabled = false, isShareEnabled = false, isAddFavouriteEnabled = false),
        videos = emptyList(),
    ),
    val isDescriptionExpanded: Boolean = false,
    val isRefreshing: Boolean = false,
)

sealed interface VideoDetailIntent {
    data object OnBackClicked : VideoDetailIntent
    data class OnVideoClicked(val videoIndex: Int) : VideoDetailIntent
    data object OnToggleDescription : VideoDetailIntent
    data object OnRefresh : VideoDetailIntent
}

sealed interface VideoDetailEvent {
    data object NavigateBack : VideoDetailEvent
    data class NavigateToPlayer(val initialVideoIndex: Int) : VideoDetailEvent
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : VideoDetailEvent
}

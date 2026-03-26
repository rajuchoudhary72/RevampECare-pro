package com.app.ecarepro.feature.gallery.video.player

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.PhotoSetting
import com.app.ecarepro.core.domain.model.Video
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

@HiltViewModel(assistedFactory = VideoPlayerViewModel.Factory::class)
class VideoPlayerViewModel @AssistedInject constructor(
    @Assisted val navKey: GalleryNavGraph.VideoPlayer,
    private val galleryRepository: GalleryRepository,
) : BaseViewModel<VideoPlayerIntent, VideoPlayerEvent>() {

    private val _uiState = MutableStateFlow<UiState<VideoPlayerUiState>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchVideos()
    }

    private fun fetchVideos() {
        viewModelScope.launch {
            galleryRepository.getVideoAlbumDetail(navKey.albumId).collect { result ->
                result
                    .onSuccess { detail ->
                        _uiState.update {
                            UiState.Success(
                                VideoPlayerUiState(
                                    videos = detail.videos,
                                    setting = detail.setting,
                                    currentIndex = navKey.initialVideoIndex.coerceIn(0, (detail.videos.size - 1).coerceAtLeast(0)),
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

    override fun handleIntent(intent: VideoPlayerIntent) {
        when (intent) {
            is VideoPlayerIntent.OnBackClicked -> sendEvent(VideoPlayerEvent.NavigateBack)
            is VideoPlayerIntent.OnVideoSelected -> {
                val current = (_uiState.value as? UiState.Success)?.data ?: return
                _uiState.update { UiState.Success(current.copy(currentIndex = intent.index)) }
            }
            is VideoPlayerIntent.OnLikeClicked -> onLikeClicked(intent.videoId, intent.currentLike)
            is VideoPlayerIntent.OnBookmarkClicked -> onBookmarkClicked(intent.videoId, intent.currentBookmark)
            is VideoPlayerIntent.OnShareClicked -> sendEvent(VideoPlayerEvent.ShareVideo(intent.videoUrl))
        }
    }

    private fun onLikeClicked(videoId: String, currentLike: Boolean) {
        val newLike = !currentLike
        viewModelScope.launch {
            galleryRepository.toggleVideoLike(videoId, newLike).collect { result ->
                result
                    .onSuccess { totalLikes ->
                        _uiState.update { currentState ->
                            (currentState as? UiState.Success)?.let { state ->
                                val updatedVideos = state.data.videos.map { video ->
                                    if (video.id == videoId) video.copy(isLike = newLike, likes = totalLikes) else video
                                }
                                UiState.Success(state.data.copy(videos = updatedVideos))
                            } ?: currentState
                        }
                    }
                    .onFailure { error ->
                        sendEvent(VideoPlayerEvent.ShowMessage(SnackbarMessage(error.errorMessage(), MessageType.ERROR)))
                    }
            }
        }
    }

    private fun onBookmarkClicked(videoId: String, currentBookmark: Boolean) {
        val action = if (currentBookmark) "remove" else "add"
        val newBookmark = !currentBookmark
        viewModelScope.launch {
            galleryRepository.manageVideoFavorite(videoId, action).collect { result ->
                result
                    .onSuccess {
                        _uiState.update { currentState ->
                            (currentState as? UiState.Success)?.let { state ->
                                val updatedVideos = state.data.videos.map { video ->
                                    if (video.id == videoId) video.copy(isFavourite = newBookmark) else video
                                }
                                UiState.Success(state.data.copy(videos = updatedVideos))
                            } ?: currentState
                        }
                    }
                    .onFailure { error ->
                        sendEvent(VideoPlayerEvent.ShowMessage(SnackbarMessage(error.errorMessage(), MessageType.ERROR)))
                    }
            }
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<GalleryNavGraph.VideoPlayer, VideoPlayerViewModel> {
        override fun create(param: GalleryNavGraph.VideoPlayer): VideoPlayerViewModel
    }
}

@Immutable
data class VideoPlayerUiState(
    val videos: List<Video> = emptyList(),
    val setting: PhotoSetting = PhotoSetting(isLikeEnabled = false, isShareEnabled = false, isAddFavouriteEnabled = false),
    val currentIndex: Int = 0,
)

sealed interface VideoPlayerIntent {
    data object OnBackClicked : VideoPlayerIntent
    data class OnVideoSelected(val index: Int) : VideoPlayerIntent
    data class OnLikeClicked(val videoId: String, val currentLike: Boolean) : VideoPlayerIntent
    data class OnBookmarkClicked(val videoId: String, val currentBookmark: Boolean) : VideoPlayerIntent
    data class OnShareClicked(val videoUrl: String) : VideoPlayerIntent
}

sealed interface VideoPlayerEvent {
    data object NavigateBack : VideoPlayerEvent
    data class ShareVideo(val videoUrl: String) : VideoPlayerEvent
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : VideoPlayerEvent
}

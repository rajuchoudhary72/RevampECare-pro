package com.app.ecarepro.feature.gallery.favorites.viewer

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
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

@HiltViewModel(assistedFactory = FavoriteViewerViewModel.Factory::class)
class FavoriteViewerViewModel @AssistedInject constructor(
    @Assisted val navKey: GalleryNavGraph.FavoriteViewer,
    private val galleryRepository: GalleryRepository,
) : BaseViewModel<FavoriteViewerIntent, FavoriteViewerEvent>() {

    private val _uiState = MutableStateFlow(
        UiState.Success(
            FavoriteViewerUiState(
                id = navKey.id,
                galleryType = navKey.galleryType,
                fileName = navKey.fileName,
                totalLike = navKey.totalLike,
                isLike = navKey.isLike,
                isFavourite = navKey.isFavourite,
                setting = PhotoSetting(
                    isLikeEnabled = navKey.isLikeEnabled,
                    isShareEnabled = navKey.isShareEnabled,
                    isAddFavouriteEnabled = navKey.isAddFavouriteEnabled,
                ),
            )
        ) as UiState<FavoriteViewerUiState>
    )
    val uiState = _uiState.asStateFlow()

    override fun handleIntent(intent: FavoriteViewerIntent) {
        when (intent) {
            is FavoriteViewerIntent.OnBackClicked -> sendEvent(FavoriteViewerEvent.NavigateBack)
            is FavoriteViewerIntent.OnLikeClicked -> onLikeClicked()
            is FavoriteViewerIntent.OnUnbookmarkClicked -> onUnbookmarkClicked()
            is FavoriteViewerIntent.OnShareClicked -> {
                val current = (_uiState.value as? UiState.Success)?.data ?: return
                sendEvent(FavoriteViewerEvent.ShareContent(current.fileName))
            }
        }
    }

    private fun onLikeClicked() {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        val newLike = !current.isLike
        viewModelScope.launch {
            val flow = if (current.galleryType == 2) {
                galleryRepository.toggleVideoLike(current.id, newLike)
            } else {
                galleryRepository.toggleLike(current.id, newLike)
            }
            flow.collect { result ->
                result
                    .onSuccess { totalLikes ->
                        _uiState.update { state ->
                            (state as? UiState.Success)?.let {
                                UiState.Success(it.data.copy(isLike = newLike, totalLike = totalLikes))
                            } ?: state
                        }
                    }
                    .onFailure { error ->
                        sendEvent(FavoriteViewerEvent.ShowMessage(SnackbarMessage(error.errorMessage(), MessageType.ERROR)))
                    }
            }
        }
    }

    private fun onUnbookmarkClicked() {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        viewModelScope.launch {
            val flow = if (current.galleryType == 2) {
                galleryRepository.manageVideoFavorite(current.id, "remove")
            } else {
                galleryRepository.manageFavorite(current.id, "remove")
            }
            flow.collect { result ->
                result
                    .onSuccess {
                        sendEvent(FavoriteViewerEvent.NavigateBack)
                    }
                    .onFailure { error ->
                        sendEvent(FavoriteViewerEvent.ShowMessage(SnackbarMessage(error.errorMessage(), MessageType.ERROR)))
                    }
            }
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<GalleryNavGraph.FavoriteViewer, FavoriteViewerViewModel> {
        override fun create(param: GalleryNavGraph.FavoriteViewer): FavoriteViewerViewModel
    }
}

@Immutable
data class FavoriteViewerUiState(
    val id: String,
    val galleryType: Int,
    val fileName: String,
    val totalLike: Int,
    val isLike: Boolean,
    val isFavourite: Boolean,
    val setting: PhotoSetting,
)

sealed interface FavoriteViewerIntent {
    data object OnBackClicked : FavoriteViewerIntent
    data object OnLikeClicked : FavoriteViewerIntent
    data object OnUnbookmarkClicked : FavoriteViewerIntent
    data object OnShareClicked : FavoriteViewerIntent
}

sealed interface FavoriteViewerEvent {
    data object NavigateBack : FavoriteViewerEvent
    data class ShareContent(val url: String) : FavoriteViewerEvent
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : FavoriteViewerEvent
}

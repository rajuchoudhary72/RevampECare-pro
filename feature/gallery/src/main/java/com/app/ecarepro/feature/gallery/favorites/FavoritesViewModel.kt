package com.app.ecarepro.feature.gallery.favorites

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.FavoriteItem
import com.app.ecarepro.core.domain.model.FavoritesData
import com.app.ecarepro.core.domain.model.PhotoSetting
import com.app.ecarepro.core.domain.repository.GalleryRepository
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.feature.gallery.navigation.GalleryNavGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val galleryRepository: GalleryRepository,
) : BaseViewModel<FavoritesIntent, FavoritesEvent>() {

    private val _uiState = MutableStateFlow<UiState<FavoritesUiState>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchFavorites()
    }

    private fun fetchFavorites() {
        _uiState.update { UiState.Loading }
        viewModelScope.launch {
            galleryRepository.getFavorites().collect { result ->
                result
                    .onSuccess { data ->
                        _uiState.update {
                            UiState.Success(
                                FavoritesUiState(
                                    items = data.items,
                                    setting = data.setting,
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

    override fun handleIntent(intent: FavoritesIntent) {
        when (intent) {
            is FavoritesIntent.OnBackClicked -> sendEvent(FavoritesEvent.NavigateBack)
            is FavoritesIntent.OnRefresh -> fetchFavorites()
            is FavoritesIntent.OnItemClicked -> onItemClicked(intent.item)
            is FavoritesIntent.OnLikeClicked -> onLikeClicked(intent.item)
            is FavoritesIntent.OnUnbookmarkClicked -> onUnbookmarkClicked(intent.item)
        }
    }

    private fun onItemClicked(item: FavoriteItem) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        sendEvent(
            FavoritesEvent.NavigateToViewer(
                GalleryNavGraph.FavoriteViewer(
                    id = item.id,
                    galleryType = item.galleryType,
                    fileName = item.fileName,
                    totalLike = item.totalLike,
                    isLike = item.isLike,
                    isFavourite = item.isFavourite,
                    isLikeEnabled = current.setting.isLikeEnabled,
                    isShareEnabled = current.setting.isShareEnabled,
                    isAddFavouriteEnabled = current.setting.isAddFavouriteEnabled,
                )
            )
        )
    }

    private fun onLikeClicked(item: FavoriteItem) {
        val newLike = !item.isLike
        viewModelScope.launch {
            val flow = if (item.galleryType == 2) {
                galleryRepository.toggleVideoLike(item.id, newLike)
            } else {
                galleryRepository.toggleLike(item.id, newLike)
            }
            flow.collect { result ->
                result
                    .onSuccess { totalLikes ->
                        _uiState.update { currentState ->
                            (currentState as? UiState.Success)?.let { state ->
                                val updatedItems = state.data.items.map { i ->
                                    if (i.id == item.id && i.galleryType == item.galleryType) {
                                        i.copy(isLike = newLike, totalLike = totalLikes)
                                    } else i
                                }
                                UiState.Success(state.data.copy(items = updatedItems))
                            } ?: currentState
                        }
                    }
                    .onFailure { error ->
                        sendEvent(FavoritesEvent.ShowMessage(SnackbarMessage(error.errorMessage(), MessageType.ERROR)))
                    }
            }
        }
    }

    private fun onUnbookmarkClicked(item: FavoriteItem) {
        viewModelScope.launch {
            val flow = if (item.galleryType == 2) {
                galleryRepository.manageVideoFavorite(item.id, "remove")
            } else {
                galleryRepository.manageFavorite(item.id, "remove")
            }
            flow.collect { result ->
                result
                    .onSuccess {
                        _uiState.update { currentState ->
                            (currentState as? UiState.Success)?.let { state ->
                                val updatedItems = state.data.items.filter { i ->
                                    !(i.id == item.id && i.galleryType == item.galleryType)
                                }
                                UiState.Success(state.data.copy(items = updatedItems))
                            } ?: currentState
                        }
                    }
                    .onFailure { error ->
                        sendEvent(FavoritesEvent.ShowMessage(SnackbarMessage(error.errorMessage(), MessageType.ERROR)))
                    }
            }
        }
    }
}

@Immutable
data class FavoritesUiState(
    val items: List<FavoriteItem> = emptyList(),
    val setting: PhotoSetting = PhotoSetting(isLikeEnabled = false, isShareEnabled = false, isAddFavouriteEnabled = false),
)

sealed interface FavoritesIntent {
    data object OnBackClicked : FavoritesIntent
    data object OnRefresh : FavoritesIntent
    data class OnItemClicked(val item: FavoriteItem) : FavoritesIntent
    data class OnLikeClicked(val item: FavoriteItem) : FavoritesIntent
    data class OnUnbookmarkClicked(val item: FavoriteItem) : FavoritesIntent
}

sealed interface FavoritesEvent {
    data object NavigateBack : FavoritesEvent
    data class NavigateToViewer(val navKey: GalleryNavGraph.FavoriteViewer) : FavoritesEvent
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : FavoritesEvent
}

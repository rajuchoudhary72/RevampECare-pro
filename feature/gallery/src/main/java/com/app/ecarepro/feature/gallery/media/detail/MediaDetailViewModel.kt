package com.app.ecarepro.feature.gallery.media.detail

import androidx.compose.runtime.Immutable
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.AssistedViewModelFactory
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.feature.gallery.navigation.GalleryNavGraph
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel(assistedFactory = MediaDetailViewModel.Factory::class)
class MediaDetailViewModel @AssistedInject constructor(
    @Assisted val navKey: GalleryNavGraph.MediaGalleryDetail,
) : BaseViewModel<MediaDetailIntent, MediaDetailEvent>() {

    private val _uiState = MutableStateFlow(
        UiState.Success(
            MediaDetailUiState(
                id = navKey.id,
                newsName = navKey.newsName,
                headline = navKey.headline,
                publishedOn = navKey.publishedOn,
                updatedOn = navKey.updatedOn,
                thumbnailUrl = navKey.thumbnailUrl,
                fullSizeUrl = navKey.fullSizeUrl,
                description = navKey.description,
            )
        ) as UiState<MediaDetailUiState>
    )
    val uiState = _uiState.asStateFlow()

    override fun handleIntent(intent: MediaDetailIntent) {
        when (intent) {
            is MediaDetailIntent.OnBackClicked -> sendEvent(MediaDetailEvent.NavigateBack)
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<GalleryNavGraph.MediaGalleryDetail, MediaDetailViewModel> {
        override fun create(param: GalleryNavGraph.MediaGalleryDetail): MediaDetailViewModel
    }
}

@Immutable
data class MediaDetailUiState(
    val id: Int,
    val newsName: String,
    val headline: String,
    val publishedOn: String,
    val updatedOn: String,
    val thumbnailUrl: String,
    val fullSizeUrl: String,
    val description: String,
)

sealed interface MediaDetailIntent {
    data object OnBackClicked : MediaDetailIntent
}

sealed interface MediaDetailEvent {
    data object NavigateBack : MediaDetailEvent
}

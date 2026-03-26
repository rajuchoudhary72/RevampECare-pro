package com.app.ecarepro.feature.gallery.kids.detail

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.KidsAlbumDetailData
import com.app.ecarepro.core.domain.model.KidsPhoto
import com.app.ecarepro.core.domain.repository.GalleryRepository
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = KidsDetailViewModel.Factory::class)
class KidsDetailViewModel @AssistedInject constructor(
    @Assisted val navKey: GalleryNavGraph.KidsCornerDetail,
    private val galleryRepository: GalleryRepository,
) : BaseViewModel<KidsDetailIntent, KidsDetailEvent>() {

    private val _uiState = MutableStateFlow<UiState<KidsDetailUiState>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchDetail()
    }

    private fun fetchDetail() {
        _uiState.update { UiState.Loading }
        viewModelScope.launch {
            galleryRepository.getKidsAlbumDetail(navKey.kid).collect { result ->
                result
                    .onSuccess { data ->
                        _uiState.update { UiState.Success(data.toUiState()) }
                    }
                    .onFailure { error ->
                        _uiState.update { UiState.Error(error.errorMessage()) }
                    }
            }
        }
    }

    override fun handleIntent(intent: KidsDetailIntent) {
        when (intent) {
            is KidsDetailIntent.OnBackClicked -> sendEvent(KidsDetailEvent.NavigateBack)
            is KidsDetailIntent.OnPhotoClicked -> sendEvent(KidsDetailEvent.OpenPhoto(intent.photoUrl))
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<GalleryNavGraph.KidsCornerDetail, KidsDetailViewModel> {
        override fun create(param: GalleryNavGraph.KidsCornerDetail): KidsDetailViewModel
    }
}

private fun KidsAlbumDetailData.toUiState() = KidsDetailUiState(
    kid = kid,
    title = title,
    description = description,
    albumIcon = albumIcon,
    yearName = yearName,
    totalPhoto = totalPhoto,
    createdOn = createdOn,
    updatedOn = updatedOn,
    photos = photos,
)

@Immutable
data class KidsDetailUiState(
    val kid: String,
    val title: String,
    val description: String,
    val albumIcon: String,
    val yearName: String,
    val totalPhoto: Int,
    val createdOn: String,
    val updatedOn: String,
    val photos: List<KidsPhoto> = emptyList(),
)

sealed interface KidsDetailIntent {
    data object OnBackClicked : KidsDetailIntent
    data class OnPhotoClicked(val photoUrl: String) : KidsDetailIntent
}

sealed interface KidsDetailEvent {
    data object NavigateBack : KidsDetailEvent
    data class OpenPhoto(val photoUrl: String) : KidsDetailEvent
}

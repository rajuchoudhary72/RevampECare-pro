package com.app.ecarepro.feature.announcement.circular

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.Circular
import com.app.ecarepro.core.domain.repository.AnnouncementRepository
import com.app.ecarepro.core.download.FileDownloader
import com.app.ecarepro.core.download.model.DownloadRequest
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.AssistedViewModelFactory
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.ECAttachment
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.feature.announcement.navigation.AnnouncementNavGraph
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel(assistedFactory = CircularDetailViewModel.Factory::class)
class CircularDetailViewModel @AssistedInject constructor(
    @Assisted val navKey: AnnouncementNavGraph.CircularDetail,
    private val announcementRepository: AnnouncementRepository,
    private val fileDownloader: FileDownloader,
) : BaseViewModel<CircularDetailIntent, CircularDetailEvent>() {

    private val circularId: String = navKey.circularId

    private val _uiState = MutableStateFlow<UiState<CircularDetailUiState>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchDetail()
    }

    private fun fetchDetail() {
        viewModelScope.launch {
            announcementRepository.getCircularDetail(circularId)
                .onStart { _uiState.update { UiState.Loading } }
                .collect { result ->
                    result
                        .onSuccess { circular ->
                            _uiState.update { UiState.Success(CircularDetailUiState(circular = circular)) }
                        }
                        .onFailure { error ->
                            _uiState.update { UiState.Error(error.errorMessage()) }
                        }
                }
        }
    }

    override fun handleIntent(intent: CircularDetailIntent) {
        when (intent) {
            is CircularDetailIntent.OnBackClicked -> sendEvent(CircularDetailEvent.NavigateBack)
            is CircularDetailIntent.OnViewAttachment -> viewAttachment()
            is CircularDetailIntent.OnDownloadAttachment -> downloadAttachment()
        }
    }

    private fun viewAttachment() {
        val circular = (_uiState.value as? UiState.Success)?.data?.circular ?: return
        val filePath = circular.filePath ?: return
        val attachment = ECAttachment(id = circular.id, name = circular.title, url = filePath)
        sendEvent(CircularDetailEvent.ViewAttachment(listOf(attachment)))
    }

    private fun downloadAttachment() {
        val circular = (_uiState.value as? UiState.Success)?.data?.circular ?: return
        val filePath = circular.filePath ?: run {
            sendEvent(CircularDetailEvent.ShowMessage(SnackbarMessage("No attachment available.", MessageType.ERROR)))
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                fileDownloader.download(DownloadRequest(url = filePath, fileName = circular.title)).collect {}
                withContext(Dispatchers.Main) {
                    sendEvent(CircularDetailEvent.ShowMessage(SnackbarMessage("Download started", MessageType.INFO)))
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    sendEvent(CircularDetailEvent.ShowMessage(SnackbarMessage("Download failed. Please try again.", MessageType.ERROR)))
                }
            }
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<AnnouncementNavGraph.CircularDetail, CircularDetailViewModel> {
        override fun create(param: AnnouncementNavGraph.CircularDetail): CircularDetailViewModel
    }
}

@Immutable
data class CircularDetailUiState(val circular: Circular)

sealed interface CircularDetailIntent {
    data object OnBackClicked : CircularDetailIntent
    data object OnViewAttachment : CircularDetailIntent
    data object OnDownloadAttachment : CircularDetailIntent
}

sealed interface CircularDetailEvent {
    data object NavigateBack : CircularDetailEvent
    data class ViewAttachment(val attachments: List<ECAttachment>) : CircularDetailEvent
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : CircularDetailEvent
}

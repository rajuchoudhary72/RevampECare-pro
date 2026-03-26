package com.app.ecarepro.feature.announcement.notice

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.Notice
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

@HiltViewModel(assistedFactory = NoticeDetailViewModel.Factory::class)
class NoticeDetailViewModel @AssistedInject constructor(
    @Assisted val navKey: AnnouncementNavGraph.NoticeDetail,
    private val announcementRepository: AnnouncementRepository,
    private val fileDownloader: FileDownloader,
) : BaseViewModel<NoticeDetailIntent, NoticeDetailEvent>() {

    private val noticeId: String = navKey.noticeId

    private val _uiState = MutableStateFlow<UiState<NoticeDetailUiState>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchDetail()
    }

    private fun fetchDetail() {
        viewModelScope.launch {
            announcementRepository.getNoticeDetail(noticeId)
                .onStart { _uiState.update { UiState.Loading } }
                .collect { result ->
                    result
                        .onSuccess { notice ->
                            _uiState.update { UiState.Success(NoticeDetailUiState(notice = notice)) }
                        }
                        .onFailure { error ->
                            _uiState.update { UiState.Error(error.errorMessage()) }
                        }
                }
        }
    }

    override fun handleIntent(intent: NoticeDetailIntent) {
        when (intent) {
            is NoticeDetailIntent.OnBackClicked -> sendEvent(NoticeDetailEvent.NavigateBack)
            is NoticeDetailIntent.OnViewAttachment -> viewAttachment()
            is NoticeDetailIntent.OnDownloadAttachment -> downloadAttachment()
        }
    }

    private fun viewAttachment() {
        val notice = (_uiState.value as? UiState.Success)?.data?.notice ?: return
        val filePath = notice.filePath ?: return
        val attachment = ECAttachment(
            id = notice.id,
            name = notice.heading,
            url = filePath
        )
        sendEvent(NoticeDetailEvent.ViewAttachment(listOf(attachment)))
    }

    private fun downloadAttachment() {
        val notice = (_uiState.value as? UiState.Success)?.data?.notice ?: return
        val filePath = notice.filePath ?: run {
            sendEvent(NoticeDetailEvent.ShowMessage(SnackbarMessage("No attachment available.", MessageType.ERROR)))
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                fileDownloader.download(DownloadRequest(url = filePath, fileName = notice.heading)).collect {}
                withContext(Dispatchers.Main) {
                    sendEvent(NoticeDetailEvent.ShowMessage(SnackbarMessage("Download started", MessageType.INFO)))
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    sendEvent(NoticeDetailEvent.ShowMessage(SnackbarMessage("Download failed. Please try again.", MessageType.ERROR)))
                }
            }
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<AnnouncementNavGraph.NoticeDetail, NoticeDetailViewModel> {
        override fun create(param: AnnouncementNavGraph.NoticeDetail): NoticeDetailViewModel
    }
}

@Immutable
data class NoticeDetailUiState(val notice: Notice)

sealed interface NoticeDetailIntent {
    data object OnBackClicked : NoticeDetailIntent
    data object OnViewAttachment : NoticeDetailIntent
    data object OnDownloadAttachment : NoticeDetailIntent
}

sealed interface NoticeDetailEvent {
    data object NavigateBack : NoticeDetailEvent
    data class ViewAttachment(val attachments: List<ECAttachment>) : NoticeDetailEvent
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : NoticeDetailEvent
}

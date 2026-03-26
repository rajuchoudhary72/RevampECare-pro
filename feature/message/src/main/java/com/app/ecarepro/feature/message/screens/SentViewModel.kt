package com.app.ecarepro.feature.message.screens

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.message.SentMessage
import com.app.ecarepro.core.domain.model.message.SentRecipient
import com.app.ecarepro.core.domain.repository.MessageRepository
import com.app.ecarepro.core.domain.util.ProfileUtils
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.pagination.Paginator
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.feature.message.common.updateSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SentViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
) : BaseViewModel<SentIntent, SentEvent>() {

    private val _uiState = MutableStateFlow<UiState<SentUiState>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val paginator = Paginator(
        scope = viewModelScope,
        onRequest = { page -> messageRepository.getSentMessages(page) },
        onSuccess = { newItems, isLastPage, isReset ->
            val presentationItems = newItems.map { it.toPresentation() }
            val existingItems = if (isReset) emptyList()
            else (_uiState.value as? UiState.Success)?.data?.messages ?: emptyList()
            val merged = (existingItems + presentationItems).distinctBy { it.id }
            _uiState.value = UiState.Success(
                SentUiState(
                    messages = merged,
                    isRefreshing = false,
                    showLoadMoreView = !isLastPage,
                    loadMoreError = null,
                )
            )
        },
        onError = { error, isReset ->
            if (isReset) {
                if (_uiState.value is UiState.Success) {
                    _uiState.updateSuccess { it.copy(isRefreshing = false) }
                } else {
                    _uiState.value = UiState.Error(error.message ?: UNKNOWN_ERROR_MESSAGE)
                }
            } else {
                _uiState.updateSuccess { it.copy(showLoadMoreView = false, loadMoreError = error) }
            }
        },
    )

    init {
        paginator.refresh()
    }

    override fun handleIntent(intent: SentIntent) {
        when (intent) {
            SentIntent.OnRetry -> {
                _uiState.value = UiState.Loading
                paginator.refresh()
            }
            SentIntent.OnRefresh -> {
                _uiState.updateSuccess { it.copy(isRefreshing = true) }
                paginator.refresh()
            }
            SentIntent.OnLoadMore -> paginator.loadNextPage()
            is SentIntent.OnDeleteClick -> {
                _uiState.updateSuccess { it.copy(deleteMessageId = intent.id) }
            }
            SentIntent.OnDeleteDismiss -> {
                _uiState.updateSuccess { it.copy(deleteMessageId = null) }
            }
            SentIntent.OnDeleteConfirm -> deleteMessage()
        }
    }

    private fun deleteMessage() {
        val id = (_uiState.value as? UiState.Success)?.data?.deleteMessageId ?: return
        _uiState.updateSuccess { it.copy(isDeletingMessage = true) }
        messageRepository.deleteSentMessage(id)
            .onEach { result ->
                result.fold(
                    onSuccess = {
                        _uiState.updateSuccess { state ->
                            state.copy(
                                messages = state.messages.filterNot { it.id == id },
                                deleteMessageId = null,
                                isDeletingMessage = false,
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.updateSuccess { it.copy(deleteMessageId = null, isDeletingMessage = false) }
                        sendEvent(SentEvent.ShowMessage(SnackbarMessage(text = error.message ?: UNKNOWN_ERROR_MESSAGE, type = MessageType.ERROR)))
                    },
                )
            }
            .launchIn(viewModelScope)
    }

    companion object {
        private const val UNKNOWN_ERROR_MESSAGE = "Unknown error, please try again."
    }
}

// ============== UI State ==============

@Immutable
data class SentUiState(
    val messages: List<SentMessageItemData> = emptyList(),
    val isRefreshing: Boolean = false,
    val showLoadMoreView: Boolean = false,
    val loadMoreError: Throwable? = null,
    val deleteMessageId: String? = null,
    val isDeletingMessage: Boolean = false,
)

@Immutable
data class RecipientData(
    val name: String = "",
    val designation: String? = "",
    val photo: String = "",
)

@Immutable
data class SentMessageItemData(
    val id: String = "",
    val title: String = "",
    val preview: String = "",
    val timestamp: String = "",
    val recipients: List<RecipientData> = emptyList(),
    val canDelete: Boolean = false,
)

// ============== Intents ==============

sealed interface SentIntent {
    data object OnRetry : SentIntent
    data object OnRefresh : SentIntent
    data object OnLoadMore : SentIntent
    data class OnDeleteClick(val id: String) : SentIntent
    data object OnDeleteConfirm : SentIntent
    data object OnDeleteDismiss : SentIntent
}

// ============== Events ==============

sealed interface SentEvent {
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : SentEvent
}

// ============== Domain → Presentation mapper ==============

private fun SentMessage.toPresentation() = SentMessageItemData(
    id = id,
    title = subject ?: "",
    preview = recipients.firstOrNull()?.let { buildRecipientPreview(it) } ?: "",
    timestamp = ProfileUtils.formatRelativeTime(sentOn),
    recipients = recipients.map { it.toPresentation() },
    canDelete = canDelete,
)

private fun buildRecipientPreview(recipient: SentRecipient): String = buildString {
    if (!recipient.childName.isNullOrBlank()) {
        append(recipient.childName)
        if (!recipient.className.isNullOrBlank()) append(" (${recipient.className})")
    } else if (!recipient.designation.isNullOrBlank()) {
        append(recipient.designation)
    } else if (!recipient.className.isNullOrBlank()) {
        append(recipient.className)
    }
}

private fun SentRecipient.toPresentation() = RecipientData(
    name = name,
    designation = when {
        !childName.isNullOrBlank() -> buildString {
            append(childName)
            if (!className.isNullOrBlank()) append(" ($className)")
        }
        !designation.isNullOrBlank() -> designation
        !className.isNullOrBlank() -> className
        else -> ""
    },
    photo = photo,
)

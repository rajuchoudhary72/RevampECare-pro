package com.app.ecarepro.feature.message.screens

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.message.InboxMessage
import com.app.ecarepro.core.domain.repository.MessageRepository
import com.app.ecarepro.core.domain.util.ProfileUtils
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.pagination.Paginator
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.feature.message.MessageItemData
import com.app.ecarepro.feature.message.common.updateSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class InboxViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
) : BaseViewModel<InboxIntent, InboxEvent>() {

    private val _uiState = MutableStateFlow<UiState<InboxListUiState>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val paginator = Paginator(
        scope = viewModelScope,
        onRequest = { page -> messageRepository.getInboxMessages(page) },
        onSuccess = { newItems, isLastPage, isReset ->
            val presentationItems = newItems.map { it.toPresentation() }
            val existingItems = if (isReset) emptyList()
            else (_uiState.value as? UiState.Success)?.data?.messages ?: emptyList()
            // distinctBy ensures no duplicate LazyColumn keys within or across pages
            val merged = (existingItems + presentationItems).distinctBy { it.id }
            _uiState.value = UiState.Success(
                InboxListUiState(
                    messages = merged,
                    isRefreshing = false,
                    showLoadMoreView = !isLastPage,
                    loadMoreError = null,
                )
            )
        },

        onError = { error, isReset ->
            if (isReset) {
                // Pull-to-refresh failed → keep existing list, stop the indicator
                // Initial load / retry failed → show full error screen
                if (_uiState.value is UiState.Success) {
                    _uiState.updateSuccess { it.copy(isRefreshing = false) }
                } else {
                    _uiState.value = UiState.Error(error.message ?: UNKNOWN_ERROR_MESSAGE)
                }
            } else {
                _uiState.updateSuccess {
                    it.copy(showLoadMoreView = false, loadMoreError = error)
                }
            }
        },
    )

    init {
        paginator.refresh()
    }

    override fun handleIntent(intent: InboxIntent) {
        when (intent) {
            InboxIntent.OnRetry -> {
                _uiState.value = UiState.Loading
                paginator.refresh()
            }
            InboxIntent.OnRefresh -> {
                // Pull-to-refresh: keep showing existing data, just show the indicator
                _uiState.updateSuccess { it.copy(isRefreshing = true) }
                paginator.refresh()
            }
            InboxIntent.OnLoadMore -> paginator.loadNextPage()
        }
    }

    companion object {
        private const val UNKNOWN_ERROR_MESSAGE = "Unknown error, please try again."
    }
}

// ============== UI State ==============

@Immutable
data class InboxListUiState(
    val messages: List<MessageItemData> = emptyList(),
    val isRefreshing: Boolean = false,
    val showLoadMoreView: Boolean = false,
    val loadMoreError: Throwable? = null,
)

// ============== Intents ==============

sealed interface InboxIntent {
    data object OnRetry : InboxIntent
    data object OnRefresh : InboxIntent
    data object OnLoadMore : InboxIntent
}

// ============== Events ==============

sealed interface InboxEvent {
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : InboxEvent
}

// ============== Domain → Presentation mapper ==============

private fun InboxMessage.toPresentation() = MessageItemData(
    id = id,
    title = name,
    preview = when {
        !childName.isNullOrBlank() -> buildString {
            append(childName)
            if (!className.isNullOrBlank()) append(" ($className)")
        }
        !designation.isNullOrBlank() -> designation ?: ""
        !className.isNullOrBlank() -> className ?: ""
        else -> ""
    },
    timestamp = ProfileUtils.formatRelativeTime(sentOn),
    senderName = name,
    senderPhoto = photo,
    unreadCount = unreadCount,
    attachmentType = null,
)

package com.app.ecarepro.feature.message

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.repository.MessageRepository
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.feature.message.common.updateSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
) : BaseViewModel<MessageIntent, MessageEvent>() {

    private val _uiState = MutableStateFlow<UiState<MessageUiState>>(
        UiState.Success(MessageUiState())
    )
    val uiState = _uiState.asStateFlow()

    init {
        fetchMessageSettings()
    }

    override fun handleIntent(intent: MessageIntent) {
        when (intent) {
            is MessageIntent.OnRetry -> fetchMessageSettings()
            is MessageIntent.OnTabSelected -> _uiState.updateSuccess { it.copy(selectedTab = intent.tab) }
            is MessageIntent.OnSearchQueryChanged -> _uiState.updateSuccess { it.copy(searchQuery = intent.query) }
        }
    }

    private fun fetchMessageSettings() {
        viewModelScope.launch {
            messageRepository.getMessageSettings().collectLatest { result ->
                result.fold(
                    onSuccess = { settings ->
                        _uiState.updateSuccess { it.copy(canCompose = settings.compose) }
                    },
                    onFailure = { /* settings failure is non-blocking; screen still shows */ },
                )
            }
        }
    }
}

// ============== UI State ==============

enum class MessageTab(val displayName: String) {
    INBOX("Inbox"),
    TIMELINE("Timeline"),
    SENT("Sent"),
    USERS("Users"),
}

@Immutable
data class MessageUiState(
    val selectedTab: MessageTab = MessageTab.INBOX,
    val searchQuery: String = "",
    val canCompose: Boolean = true,
)

enum class AttachmentType { IMAGE, AUDIO, PDF, CONTACT }

@Immutable
data class MessageItemData(
    val id: String = "",
    val title: String = "",
    val preview: String = "",
    val timestamp: String = "",
    val sentOnRaw: String = "",   // raw API date string — used for grouping in Timeline
    val senderName: String = "",
    val senderPhoto: String = "",
    val unreadCount: Int = 0,
    val attachmentType: AttachmentType? = null,
)

// ============== Intents ==============

sealed interface MessageIntent {
    data object OnRetry : MessageIntent
    data class OnTabSelected(val tab: MessageTab) : MessageIntent
    data class OnSearchQueryChanged(val query: String) : MessageIntent
}

// ============== Events ==============

sealed interface MessageEvent {
    data object NavigateBack : MessageEvent
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : MessageEvent
}


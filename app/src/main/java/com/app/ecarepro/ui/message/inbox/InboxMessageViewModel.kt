package com.app.ecarepro.ui.message.inbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.InboxMessage
import com.app.ecarepro.data.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class InboxMessageViewModel @Inject constructor(
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val pg = MutableStateFlow(1)
    private val _uiState =
        pg
            .flatMapLatest { page ->
                messageRepository.getInboxMessages(page)
            }
            .map { result: Result<List<InboxMessage>> ->
                if (result.isSuccess) {
                    val inboxMessages = result.getOrNull()
                    if (inboxMessages.isNullOrEmpty()) {
                        InboxMessageUiState.EmptyInbox
                    } else {
                        InboxMessageUiState.Success(inboxMessages)
                    }
                } else {
                    InboxMessageUiState.Error(result.exceptionOrNull()!!)
                }
            }
            .stateIn(
                scope = viewModelScope,
                initialValue = InboxMessageUiState.Loading,
                started = SharingStarted.WhileSubscribed(200)
            )
    val uiState = _uiState

}

sealed interface InboxMessageUiState {
    object Loading : InboxMessageUiState

    object EmptyInbox : InboxMessageUiState

    data class Success(
        val messages: List<InboxMessage>
    ) : InboxMessageUiState

    data class Error(
        val error: Throwable
    ) : InboxMessageUiState

    fun isLoading() = this == Loading

    fun getErrorOrNull() = if (this is Error) this.error else null
}

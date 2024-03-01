package com.app.ecarepro.ui.message.conversation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.Conversation
import com.app.ecarepro.data.network.model.Sender
import com.app.ecarepro.data.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val showSearchView = MutableStateFlow(false)

    private val id = savedStateHandle.getStateFlow("ID", initialValue = "")
    private val pg = MutableStateFlow(1)
    val searchQuery = MutableStateFlow("")


    private val _uiState =
        combine(
            flow = id,
            flow2 = pg,
            flow3 = searchQuery.distinctUntilChanged { old, new ->
                if (new.isEmpty()) {
                    false
                } else if (new.length < old.length && new.length < 3) {
                    true
                } else if (new.length < 3) {
                    true
                } else {
                    false
                }
            }
        ) { id, pg, query ->
            ConversationQueryParameter(id = id, pg = pg, query = query)
        }
            .flatMapLatest { parameter ->
                messageRepository.getConversation(
                    id = parameter.id,
                    pg = parameter.pg,
                    query = parameter.query
                )
            }
            .map { result ->
                if (result.isSuccess) {
                    val response = result.getOrNull()!!
                    val messages = response.allMessages
                    if (messages.isNullOrEmpty()) {
                        ConversationMessageUiState.EmptyInbox
                    } else {
                        ConversationMessageUiState.Success(
                            sender = response.senderDTL,
                            messages = messages
                        )
                    }
                } else {
                    ConversationMessageUiState.Error(result.exceptionOrNull()!!)
                }
            }
            .stateIn(
                scope = viewModelScope,
                initialValue = ConversationMessageUiState.Loading,
                started = SharingStarted.WhileSubscribed(200)
            )
    val uiState = _uiState


    fun showSearchBar() {
        showSearchView.update { true }
    }

    fun clearSearchQuery() {
        if (searchQuery.value.isEmpty()) {
            showSearchView.update { false }
        } else
            searchQuery.update {
                ""
            }
    }
}

data class ConversationQueryParameter(
    val id: String,
    val pg: Int,
    val query: String?
)

sealed interface ConversationMessageUiState {
    object Loading : ConversationMessageUiState

    object EmptyInbox : ConversationMessageUiState

    data class Success(
        val sender: Sender,
        val messages: List<Conversation>,
    ) : ConversationMessageUiState

    data class Error(
        val error: Throwable
    ) : ConversationMessageUiState

    fun isLoading() = this == Loading

    fun getErrorOrNull() = if (this is Error) this.error else null
}

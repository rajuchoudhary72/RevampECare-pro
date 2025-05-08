package com.app.ecarepro.ui.message.conversation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.Conversation
import com.app.ecarepro.data.network.model.Sender
import com.app.ecarepro.data.repository.MessageRepository
import com.app.ecarepro.ui.message.sent.DEFAULT_PAGE
import com.app.ecarepro.ui.message.sent.UNKNOWN_ERROR_MESSAGE
import com.app.ecarepro.ui.message.sent.calculateTotalPages
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val id = savedStateHandle.getStateFlow("ID", initialValue = "")
    val showSearchView = MutableStateFlow(false)
    val searchQuery = MutableStateFlow("")

    val uiState = MutableStateFlow<ConversationMessageUiState>(ConversationMessageUiState.Loading)

    private var page = DEFAULT_PAGE
    private var isLoading: Boolean = false
    private var isLastPage: Boolean = true
    private var totalPageCount: Int = DEFAULT_PAGE

    private var lastSearchQuery = ""

    init {
        fetchMessages()
        viewModelScope.launch {
            searchQuery
                .debounce(300)
                .filter {
                    if (lastSearchQuery.isNotEmpty() && it.isEmpty()) {
                        fetchMessages()
                    }
                    it.length > 2
                }
                .distinctUntilChanged()
                .collectLatest {
                    lastSearchQuery = it
                    refresh()
                }
        }
    }

    fun getConversationId() = id.value

    fun isLoading() = isLoading

    fun isLastPage() = isLastPage

    fun totalPageCount() = totalPageCount

    private fun fetchMessages(isRefresh: Boolean = false) {
        viewModelScope.launch {
            messageRepository
                .getConversation(
                    pg = page,
                    id = id.value,
                    query = searchQuery.value
                )
                .map { result ->
                    isLoading = false
                    if (result.isSuccess) {
                        result.getOrNull()!!.let { response ->
                            isLastPage =
                                com.app.ecarepro.ui.message.sent.isLastPage(response.total, page)
                            totalPageCount = calculateTotalPages(response.total)
                            val messages = mutableListOf<Conversation>()
                            val currentUiState = uiState.value
                            if (isRefresh.not() && currentUiState is ConversationMessageUiState.Success) {
                                messages.addAll(currentUiState.messages)
                            }
                            messages.addAll(response.allMessages ?: emptyList())

                            if (isRefresh && messages.isEmpty()) {
                                ConversationMessageUiState.EmptyInbox
                            } else {
                                ConversationMessageUiState.Success(
                                    messages = messages,
                                    sender = response.senderDTL
                                )
                            }

                        }

                    } else {
                        val error = result.exceptionOrNull() ?: IllegalArgumentException(
                            UNKNOWN_ERROR_MESSAGE
                        )
                        val currentUiState = uiState.value
                        if (currentUiState is ConversationMessageUiState.Success) {
                            currentUiState.copy(
                                showLoadMoreView = false,
                                loadMoreError = error
                            )
                        } else {
                            ConversationMessageUiState.Error(
                                error
                            )
                        }
                    }
                }
                .collectLatest { uiState ->
                    this@ConversationViewModel.uiState.update {
                        uiState
                    }
                }
        }
    }

    fun loadNextPage(retry: Boolean = false) {
        viewModelScope.launch {
            isLoading = true
            val currentUiState = uiState.value
            if (currentUiState is ConversationMessageUiState.Success) {
                uiState.update {
                    currentUiState.copy(showLoadMoreView = true)
                }
            }
            if (retry.not())
                page += 1

            fetchMessages()
        }
    }


    fun refresh() {
        page = DEFAULT_PAGE
        isLoading = false
        isLastPage = false
        totalPageCount = DEFAULT_PAGE
        fetchMessages(isRefresh = true)
    }

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

    fun updateMessageReadStatus(msg: Conversation) {
        viewModelScope.launch {
            val currentUiState = uiState.value

            if (currentUiState is ConversationMessageUiState.Success) {
                val updatedMessage = currentUiState.messages.map {
                    if (it.msgID == msg.msgID) {
                        it.copy(hasRead = true)
                    } else {
                        it
                    }
                }

                uiState.update {
                    currentUiState.copy(messages = updatedMessage)
                }
            }
        }
    }

    fun getUnReadMessageCount(): Int {
        val currentUiState = uiState.value
        return if (currentUiState is ConversationMessageUiState.Success) {
            currentUiState.messages.count { it.hasRead == false }
        } else {
            0
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
        val sender: Sender?,
        val messages: List<Conversation>,
        val showLoadMoreView: Boolean = false,
        val loadMoreError: Throwable? = null
    ) : ConversationMessageUiState

    data class Error(
        val error: Throwable
    ) : ConversationMessageUiState

    fun isLoading() = this == Loading

    fun getErrorOrNull() = if (this is Error) this.error else null
}

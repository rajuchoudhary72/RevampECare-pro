package com.app.ecarepro.ui.message.inbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.InboxMessage
import com.app.ecarepro.data.repository.MessageRepository
import com.app.ecarepro.ui.message.sent.DEFAULT_PAGE
import com.app.ecarepro.ui.message.sent.UNKNOWN_ERROR_MESSAGE
import com.app.ecarepro.ui.message.sent.calculateTotalPages
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsConstants
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsManager
import com.app.ecarepro.utils.badge_count.NotificationSyncManager
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class InboxMessageViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val syncManager: NotificationSyncManager,
    private val analyticsManager: AnalyticsManager
) : ViewModel() {
    private var page = DEFAULT_PAGE
    private var isLoading: Boolean = false
    private var isLastPage: Boolean = true
    private var totalPageCount: Int = DEFAULT_PAGE
    val badgeCountFlow: StateFlow<Int> get() = syncManager.badgeCountMessageFlow
    val uiState = MutableStateFlow<InboxMessageUiState>(InboxMessageUiState.Loading)

    init {
        fetchInboxMessages()
    }

    fun isLoading() = isLoading

    fun isLastPage() = isLastPage

    fun totalPageCount() = totalPageCount
    fun updateBadgeCount() {
        viewModelScope.launch {
            syncManager.fetchAndUpdateBadgeCount()
        }
    }

    private fun fetchInboxMessages(isRefresh: Boolean = false) {
        viewModelScope.launch {
            messageRepository
                .getInboxMessages(page)
                .map { result ->
                    isLoading = false
                    if (result.isSuccess) {
                        result.getOrNull()!!.let { response ->
                            isLastPage =
                                com.app.ecarepro.ui.message.sent.isLastPage(response.total, page)
                            totalPageCount = calculateTotalPages(response.total)
                            val messages = mutableListOf<InboxMessage>()
                            val currentUiState = uiState.value
                            if (isRefresh.not() && currentUiState is InboxMessageUiState.Success) {
                                messages.addAll(currentUiState.messages)
                            }
                            messages.addAll(response.sender ?: emptyList())

                            if (messages.isEmpty()) {
                                InboxMessageUiState.EmptyInbox

                            } else {
                                InboxMessageUiState.Success(messages)
                            }
                        }

                    } else {
                        val error = result.exceptionOrNull() ?: IllegalArgumentException(
                            UNKNOWN_ERROR_MESSAGE
                        )
                        val currentUiState = uiState.value
                        if (currentUiState is InboxMessageUiState.Success) {
                            currentUiState.copy(
                                showLoadMoreView = false,
                                loadMoreError = error
                            )
                        } else {
                            InboxMessageUiState.Error(
                                error
                            )
                        }
                    }
                }
                .collectLatest { uiState ->
                    this@InboxMessageViewModel.uiState.update {
                        uiState
                    }
                }
        }
    }

    fun loadNextPage(retry: Boolean = false) {
        viewModelScope.launch {
            isLoading = true
            val currentUiState = uiState.value
            if (currentUiState is InboxMessageUiState.Success) {
                uiState.update {
                    currentUiState.copy(showLoadMoreView = true)
                }
            }
            if (retry.not())
                page += 1

            fetchInboxMessages()
        }
    }


    fun refresh() {
        page = DEFAULT_PAGE
        isLoading = false
        isLastPage = false
        totalPageCount = DEFAULT_PAGE
        fetchInboxMessages(true)
    }
    fun sendScreenEvent(){
        analyticsManager.trackScreen(AnalyticsConstants.Screens.INBOX_MESSAGE_LIST)
    }
}

sealed interface InboxMessageUiState {
    object Loading : InboxMessageUiState

    object EmptyInbox : InboxMessageUiState

    data class Success(
        val messages: List<InboxMessage>,
        val showLoadMoreView: Boolean = false,
        val loadMoreError: Throwable? = null
    ) : InboxMessageUiState

    data class Error(
        val error: Throwable
    ) : InboxMessageUiState

    fun isLoading() = this == Loading

    fun getErrorOrNull() = if (this is Error) this.error else null
}

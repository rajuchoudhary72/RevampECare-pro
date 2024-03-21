package com.app.ecarepro.ui.message.sent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.SentMessage
import com.app.ecarepro.data.network.model.SentMessageDto
import com.app.ecarepro.data.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SentMessageViewModel @Inject constructor(
    private val messageRepository: MessageRepository
) : ViewModel() {

    private var startDate = MutableStateFlow<String?>(null)
    private var endDate = MutableStateFlow<String?>(null)

    private var page = DEFAULT_PAGE
    private var isLoading: Boolean = false
    private var isLastPage: Boolean = true
    private var totalPageCount: Int = DEFAULT_PAGE

    val uiState = MutableStateFlow<SentMessageUiState>(SentMessageUiState.Loading)

    init {
        fetchInboxMessages()
    }

    fun isLoading() = isLoading

    fun isLastPage() = isLastPage

    fun totalPageCount() = totalPageCount

    private fun fetchInboxMessages(isRefresh: Boolean = false) {
        viewModelScope.launch {
            messageRepository
                .getSentMessages(page, startDate.value, endDate.value)
                .map { result ->
                    isLoading = false
                    if (result.isSuccess) {
                        result.getOrNull()!!.let { response: SentMessageDto ->
                            isLastPage = isLastPage(response.total, page)
                            totalPageCount = calculateTotalPages(response.total)
                            val messages = mutableListOf<SentMessage>()
                            val currentUiState = uiState.value
                            if (isRefresh.not() && currentUiState is SentMessageUiState.Success) {
                                messages.addAll(currentUiState.messages)
                            }
                            messages.addAll(response.sentMessages ?: emptyList())

                            if (isRefresh && messages.isEmpty()) {
                                SentMessageUiState.EmptyInbox
                            } else {
                                SentMessageUiState.Success(messages)
                            }

                        }

                    } else {
                        val error = result.exceptionOrNull() ?: IllegalArgumentException(
                            UNKNOWN_ERROR_MESSAGE
                        )
                        val currentUiState = uiState.value
                        if (currentUiState is SentMessageUiState.Success) {
                            currentUiState.copy(
                                showLoadMoreView = false,
                                loadMoreError = error
                            )
                        } else {
                            SentMessageUiState.Error(
                                error
                            )
                        }
                    }
                }
                .collectLatest { uiState ->
                    this@SentMessageViewModel.uiState.update {
                        uiState
                    }
                }
        }
    }

    val isFilterApplied = startDate
        .map { it.isNullOrBlank().not() }
        .stateIn(
            scope = viewModelScope,
            initialValue = false,
            started = SharingStarted.WhileSubscribed(200)
        )


    fun updateDateFilter(from: String?, to: String?) {
        startDate.update { from }
        endDate.update { to }
        fetchInboxMessages(true)
    }

    fun loadNextPage(retry: Boolean = false) {
        viewModelScope.launch {
            isLoading = true
            val currentUiState = uiState.value
            if (currentUiState is SentMessageUiState.Success) {
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

}

sealed interface SentMessageUiState {
    object Loading : SentMessageUiState

    object EmptyInbox : SentMessageUiState

    data class Success(
        val messages: List<SentMessage>,
        val showLoadMoreView: Boolean = false,
        val loadMoreError: Throwable? = null
    ) : SentMessageUiState

    data class Error(
        val error: Throwable
    ) : SentMessageUiState

    fun isLoading() = this == Loading

    fun getErrorOrNull() = if (this is Error) this.error else null
}

const val DEFAULT_PAGE = 1
const val UNKNOWN_ERROR_MESSAGE = "Unknown error, please try again."

fun isLastPage(totalItem: Int, currentPage: Int): Boolean {
    val itemsPerPage = 50
    return totalItem - currentPage.times(itemsPerPage) <= 0
}

fun calculateTotalPages(totalItems: Int): Int {
    val itemsPerPage = 50
    return if (totalItems % itemsPerPage == 0) {
        totalItems / itemsPerPage
    } else {
        (totalItems / itemsPerPage) + 1
    }
}
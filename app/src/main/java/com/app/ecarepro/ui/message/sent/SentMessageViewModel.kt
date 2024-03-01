package com.app.ecarepro.ui.message.sent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.SentMessage
import com.app.ecarepro.data.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SentMessageViewModel @Inject constructor(
    private val messageRepository: MessageRepository
) : ViewModel() {
    private val filters = MutableStateFlow(Triple<Int, String?, String?>(1, null, null))
    private val _uiState =
        filters
            .flatMapLatest { filter ->
                messageRepository.getSentMessages(filter.first, filter.second, filter.third)
            }
            .map { result: Result<List<SentMessage>> ->
                if (result.isSuccess) {
                    val sentMessages = result.getOrNull()
                    if (sentMessages.isNullOrEmpty()) {
                        SentMessageUiState.EmptyInbox
                    } else {
                        SentMessageUiState.Success(sentMessages)
                    }
                } else {
                    SentMessageUiState.Error(result.exceptionOrNull()!!)
                }
            }
            .stateIn(
                scope = viewModelScope,
                initialValue = SentMessageUiState.Loading,
                started = SharingStarted.WhileSubscribed(200)
            )
    val uiState = _uiState

    val isFilterApplied = filters
        .map { filter -> filter.second.isNullOrBlank().not() }
        .stateIn(
            scope = viewModelScope,
            initialValue = false,
            started = SharingStarted.WhileSubscribed(200)
        )


    fun updateDateFilter(from: String?, to: String?) {
        viewModelScope.launch {
            filters.update {
                Triple(
                    it.first,
                    from,
                    to
                )
            }
        }
    }

}

sealed interface SentMessageUiState {
    object Loading : SentMessageUiState

    object EmptyInbox : SentMessageUiState

    data class Success(
        val messages: List<SentMessage>
    ) : SentMessageUiState

    data class Error(
        val error: Throwable
    ) : SentMessageUiState

    fun isLoading() = this == Loading

    fun getErrorOrNull() = if (this is Error) this.error else null
}

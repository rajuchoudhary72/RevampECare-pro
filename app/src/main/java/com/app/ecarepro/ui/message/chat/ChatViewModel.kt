package com.app.ecarepro.ui.message.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.Message
import com.app.ecarepro.data.network.model.NetworkUserDetailsDto
import com.app.ecarepro.data.network.model.Sender
import com.app.ecarepro.data.repository.MessageRepository
import com.app.ecarepro.ui.message.sent.UNKNOWN_ERROR_MESSAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val userDataStore: UserDataStore,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private lateinit var user: NetworkUserDetailsDto

    private val id = savedStateHandle.getLiveData("ID", initialValue = "")

    val uiState = id.asFlow()
        .flatMapLatest {
            messageRepository.getConversationDetails(it)
        }
        .map { result ->
            if (result.isSuccess) {
                result.getOrNull()!!.let { response ->
                    if (response.msgDTL.isNullOrEmpty()) {
                        ChatUiState.EmptyInbox
                    } else {
                        ChatUiState.Success(
                            messages = response.msgDTL,
                            msgID = response.msgID,
                            readCount = response.readCount,
                            receiverID = response.receiverID,
                            receiverType = response.receiverType,
                            canReply = response.canReply,
                            recipients = response.recipients,
                            subject = response.subject
                        )
                    }
                }

            } else {
                val error = result.exceptionOrNull() ?: IllegalArgumentException(
                    UNKNOWN_ERROR_MESSAGE
                )
                ChatUiState.Error(
                    error
                )
            }
        }
        .stateIn(
            initialValue = ChatUiState.Loading,
            started = SharingStarted.WhileSubscribed(300),
            scope = viewModelScope
        )

    init {
        viewModelScope.launch {
            user = userDataStore.getUser()
        }
    }

    fun refresh() {
        id.value = id.value
    }

    fun isMyMessage(senderDTL: Sender): Boolean {
        return /*senderDTL.senderID == user.userId &&*/ senderDTL.senderType == user.userType
    }

}

sealed interface ChatUiState {
    object Loading : ChatUiState

    object EmptyInbox : ChatUiState

    data class Success(
        val messages: List<Message>,
        val msgID: Int?,
        val readCount: Int?,
        val receiverID: Int?,
        val receiverType: Int?,
        val recipients: Any?,
        val subject: String?,
        val canReply: Boolean?
    ) : ChatUiState

    data class Error(
        val error: Throwable,
    ) : ChatUiState

    fun isLoading() = this == Loading

    fun getErrorOrNull() = if (this is Error) this.error else null
}

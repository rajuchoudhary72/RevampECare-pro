package com.app.ecarepro.ui.message.chat

import android.content.Context
import android.net.wifi.WifiManager
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.Message
import com.app.ecarepro.data.network.model.NetworkUserDetailsDto
import com.app.ecarepro.data.network.model.Recipient
import com.app.ecarepro.data.network.model.ReplyMessageRequestDto
import com.app.ecarepro.data.repository.MessageRepository
import com.app.ecarepro.ui.message.sent.UNKNOWN_ERROR_MESSAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChatViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val messageRepository: MessageRepository,
    private val userDataStore: UserDataStore,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val id = savedStateHandle.getLiveData("ID", initialValue = "")
    private val messageType = savedStateHandle.get<String>("MessageType") ?: MessageType.INBOX.value

    val messageBody = MutableStateFlow("")

    private lateinit var user: NetworkUserDetailsDto


    val uiState = id.asFlow()
        .flatMapLatest {
            messageRepository.getConversationDetails(it, MessageType.getMessageType(messageType))
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
                            recipients = response.recipients ?: emptyList(),
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
            user = userDataStore.getUser()!!
        }
    }

    fun refresh() {
        id.value = id.value
    }

    fun replyMessage(func: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val uiState = uiState.value as ChatUiState.Success
            messageRepository
                .replyMessage(
                    ReplyMessageRequestDto(
                        body = messageBody.value.trim(),
                        ipAddress = context.getDeviceIpAddress(),
                        msgType = 1,
                        receiverType = uiState.receiverType,
                        receiverID = uiState.receiverID,
                        msgID = uiState.msgID
                    )
                )
                .collectLatest { result ->
                    result
                        .onSuccess { message ->
                            messageBody.update { "" }
                            refresh()
                            func(true, message)
                        }
                        .onFailure {
                            func(false, it.message ?: UNKNOWN_ERROR_MESSAGE)
                        }
                }
        }
    }


}

fun Context.getDeviceIpAddress(): String {
    val wifiMan = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val wifiInf = wifiMan.connectionInfo
    val ipAddress = wifiInf.ipAddress
    return String.format(
        "%d.%d.%d.%d",
        (ipAddress and 0xff),
        (ipAddress shr 8 and 0xff),
        (ipAddress shr 16 and 0xff),
        (ipAddress shr 24 and 0xff)
    )
}

sealed interface ChatUiState {
    object Loading : ChatUiState

    object EmptyInbox : ChatUiState

    data class Success(
        val messages: List<Message>,
        val recipients: List<Recipient>,
        val msgID: Int?,
        val readCount: Int?,
        val receiverID: Int?,
        val receiverType: Int?,
        val subject: String?,
        val canReply: Boolean?
    ) : ChatUiState

    data class Error(
        val error: Throwable,
    ) : ChatUiState

    fun isLoading() = this == Loading

    fun getErrorOrNull() = if (this is Error) this.error else null
}

enum class MessageType(val value: String) {
    INBOX("inbox"),
    SENT("sent");

    companion object {
        fun getMessageType(value: String): MessageType {
            return values().firstOrNull { it.value == value } ?: INBOX
        }
    }

}

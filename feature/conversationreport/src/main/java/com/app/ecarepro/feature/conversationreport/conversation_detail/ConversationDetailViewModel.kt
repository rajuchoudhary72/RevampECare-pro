package com.app.ecarepro.feature.conversationreport.conversation_detail

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.conversation.ConversationRecipient
import com.app.ecarepro.core.domain.model.conversation.ConversationMessage
import com.app.ecarepro.core.domain.repository.ConversationRepository
import com.app.ecarepro.core.ui.viewmodel.AssistedViewModelFactory
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.feature.conversationreport.navigation.ConversationReportNavGraph
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@HiltViewModel(assistedFactory = ConversationDetailViewModel.Factory::class)
class ConversationDetailViewModel @AssistedInject constructor(
    @Assisted val navKey: ConversationReportNavGraph.ConversationDetail,
    private val repository: ConversationRepository,
) : BaseViewModel<ConversationDetailIntent, ConversationDetailEvent>() {

    private val _uiState = MutableStateFlow(ConversationDetailUiState())
    val uiState = _uiState.asStateFlow()

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<ConversationReportNavGraph.ConversationDetail, ConversationDetailViewModel> {
        override fun create(param: ConversationReportNavGraph.ConversationDetail): ConversationDetailViewModel
    }

    init {
        loadDetail()
    }

    override fun handleIntent(intent: ConversationDetailIntent) {
        when (intent) {
            is ConversationDetailIntent.OnBackClicked -> sendEvent(ConversationDetailEvent.NavigateBack)
            is ConversationDetailIntent.ShowRecipientSheet -> _uiState.update { it.copy(showRecipientSheet = true) }
            is ConversationDetailIntent.DismissRecipientSheet -> _uiState.update { it.copy(showRecipientSheet = false) }
            is ConversationDetailIntent.OnAttachmentClicked -> sendEvent(ConversationDetailEvent.OpenAttachment(intent.url))
            is ConversationDetailIntent.Retry -> loadDetail()
        }
    }

    private fun loadDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getConversationDetail(navKey.msgId).collect { result ->
                result.fold(
                    onSuccess = { detail ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                subject = detail.subject,
                                senderName = detail.firstSender?.name.orEmpty(),
                                senderDesignation = detail.firstSender?.designation.orEmpty(),
                                senderPhoto = detail.firstSender?.photo,
                                readCount = detail.readCount,
                                messages = detail.messages.map { msg -> MessagePresentation.from(msg) },
                                recipients = detail.recipients.map { r -> RecipientPresentation.from(r) },
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update { it.copy(isLoading = false, error = error.message) }
                    },
                )
            }
        }
    }
}

@Immutable
data class ConversationDetailUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val subject: String = "",
    val senderName: String = "",
    val senderDesignation: String = "",
    val senderPhoto: String? = null,
    val readCount: Int = 0,
    val messages: List<MessagePresentation> = emptyList(),
    val recipients: List<RecipientPresentation> = emptyList(),
    val showRecipientSheet: Boolean = false,
)

sealed interface ConversationDetailIntent {
    data object OnBackClicked : ConversationDetailIntent
    data object ShowRecipientSheet : ConversationDetailIntent
    data object DismissRecipientSheet : ConversationDetailIntent
    data class OnAttachmentClicked(val url: String) : ConversationDetailIntent
    data object Retry : ConversationDetailIntent
}

sealed interface ConversationDetailEvent {
    data object NavigateBack : ConversationDetailEvent
    data class OpenAttachment(val url: String) : ConversationDetailEvent
}

@Immutable
data class RecipientPresentation(
    val id: String,
    val name: String,
    val subtitle: String,
    val photo: String?,
    val isRead: Boolean,
    val readStatus: String,
) {
    companion object {
        fun from(recipient: ConversationRecipient): RecipientPresentation {
            val subtitle = when (recipient.receiverType) {
                1 -> recipient.className.orEmpty()
                2 -> buildString {
                    if (!recipient.childName.isNullOrBlank()) {
                        append("F/O: ${recipient.childName}")
                        if (!recipient.className.isNullOrBlank()) append(", ${recipient.className}")
                    }
                }
                else -> recipient.designation.orEmpty()
            }
            return RecipientPresentation(
                id = recipient.rcvID ?: recipient.receiverID.toString(),
                name = recipient.name,
                subtitle = subtitle,
                photo = recipient.photo,
                isRead = recipient.hasRead,
                readStatus = if (recipient.hasRead) "Read on ${recipient.readAt.orEmpty()}" else "Unread",
            )
        }
    }
}

@Immutable
data class MessagePresentation(
    val id: Int,
    val senderName: String,
    val senderPhoto: String?,
    val sentDate: String,
    val body: String,
    val attachments: List<String>,
    val msgType: Int,
) {
    companion object {
        fun from(message: ConversationMessage): MessagePresentation = MessagePresentation(
            id = message.msgID,
            senderName = message.sender?.name.orEmpty(),
            senderPhoto = message.sender?.photo,
            sentDate = formatDate(message.sentOn),
            body = message.body,
            attachments = message.filePaths,
            msgType = message.msgType,
        )

        private fun formatDate(sentOn: String): String {
            return try {
                val input = SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.US)
                val output = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                output.format(input.parse(sentOn)!!)
            } catch (e: Exception) {
                sentOn
            }
        }
    }
}

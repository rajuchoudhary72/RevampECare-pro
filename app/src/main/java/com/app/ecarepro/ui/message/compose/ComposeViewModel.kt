package com.app.ecarepro.ui.message.compose

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.Contact
import com.app.ecarepro.data.network.model.SmsType
import com.app.ecarepro.data.repository.MessageRepository
import com.app.ecarepro.model.ComposeMessageType
import com.lassi.data.media.MiMedia
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ComposeViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val composeMessageType =
        savedStateHandle.getStateFlow("composeMessageType", ComposeMessageType.SMS_AND_APP_MESSAGE)

    private val attachments = MutableStateFlow<List<MiMedia>>(emptyList())
    private val contacts = MutableStateFlow<List<Contact>>(emptyList())

    val message = MutableStateFlow("")


    val uiState =
        combine(
            flow = contacts,
            flow2 = attachments,
            flow3 = composeMessageType
        ) { contacts, attachments, composeMessageType ->
            Triple(contacts, attachments, composeMessageType)
        }
            .map { (contacts, attachments, composeMessageType) ->
                if (composeMessageType == ComposeMessageType.SMS_AND_APP_MESSAGE) {
                    val result = messageRepository.getSmsTemplates().first()
                    Triple(
                        contacts,
                        attachments,
                        Pair(composeMessageType, result.getOrNull() ?: emptyList())
                    )
                } else {
                    Triple(
                        contacts,
                        attachments,
                        Pair(composeMessageType, emptyList())
                    )
                }
            }
            .map {
                ComposeUiState.Success(
                    contacts = it.first,
                    attachments = it.second,
                    composeMessageType = it.third.first,
                    smsTypes = it.third.second
                )
            }
            .stateIn(
                scope = viewModelScope,
                initialValue = ComposeUiState.Loading,
                started = SharingStarted.WhileSubscribed(300)
            )


    fun setContacts(contacts: List<Contact>) {
        this@ComposeViewModel.contacts.update { contacts }
    }

    fun removeContacts(contact: Contact) {
        contacts.update { current -> current.filterNot { it == contact } }
    }

    fun setAttachments(attachments: List<MiMedia>) {
        this@ComposeViewModel.attachments.update { attachments }
    }

    fun removeAttachment(attachment: MiMedia) {
        attachments.update { current -> current.filterNot { it == attachment } }
    }
}

sealed interface ComposeUiState {

    object Loading : ComposeUiState

    data class Success(
        val composeMessageType: ComposeMessageType,
        val attachments: List<MiMedia>,
        val contacts: List<Contact>,
        val smsTypes: List<SmsType>
    ) : ComposeUiState

    data class Error(
        val error: Throwable
    ) : ComposeUiState

    fun isLoading() = this == Loading

    fun getErrorOrNull() = if (this is Error) this.error else null
}
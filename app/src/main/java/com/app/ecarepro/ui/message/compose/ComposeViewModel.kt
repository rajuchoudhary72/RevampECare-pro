package com.app.ecarepro.ui.message.compose

import androidx.lifecycle.ViewModel
import com.app.ecarepro.data.network.model.Contact
import com.app.ecarepro.data.repository.MessageRepository
import com.lassi.data.media.MiMedia
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ComposeViewModel @Inject constructor(
    private val messageRepository: MessageRepository
) : ViewModel() {
    val attachments = MutableStateFlow<List<MiMedia>>(emptyList())
    val contacts = MutableStateFlow<List<Contact>>(emptyList())


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
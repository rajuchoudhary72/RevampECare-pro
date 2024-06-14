package com.app.ecarepro.ui.message.compose

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.Attachment
import com.app.ecarepro.data.network.model.BulkMessageRequestDto
import com.app.ecarepro.data.network.model.Contact
import com.app.ecarepro.data.network.model.Data
import com.app.ecarepro.data.network.model.Recipients
import com.app.ecarepro.data.network.model.SendMessageRequest
import com.app.ecarepro.data.network.model.SmsType
import com.app.ecarepro.data.network.model.Template
import com.app.ecarepro.data.repository.MessageRepository
import com.app.ecarepro.model.ComposeMessageType
import com.app.ecarepro.ui.message.chat.getDeviceIpAddress
import com.app.ecarepro.ui.message.sent.UNKNOWN_ERROR_MESSAGE
import com.app.ecarepro.utils.FileAccess
import com.lassi.data.media.MiMedia
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ComposeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val messageRepository: MessageRepository,
    private val userDataStore: UserDataStore,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val composeMessageType =
        savedStateHandle.getStateFlow("composeMessageType", ComposeMessageType.ONLY_APP_MESSAGE)


    private val attachments = MutableStateFlow<List<MiMedia>>(emptyList())
    private val contacts = MutableStateFlow<List<Contact>>(emptyList())
    val attachmentVisible = combine(
        flow = composeMessageType,
        flow2 = userDataStore.getUserAsFlow()
    ) { messageType, user ->
        messageType == ComposeMessageType.ONLY_APP_MESSAGE && user?.userType == 3
    }.asLiveData()
    var currentLocation: Pair<Double, Double>? = null

    val message = MutableStateFlow("")
    val subject = MutableStateFlow("")

    var smsType: SmsType? = null
    var template: Template? = null


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
                started = SharingStarted.Lazily
            )


    fun setContacts(contacts: List<Contact>) {
        if (contacts.isNotEmpty())
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

    fun sendMessage(result: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            if (composeMessageType.value == ComposeMessageType.SMS_AND_APP_MESSAGE) {
                messageRepository
                    .sendBulkMessage(
                        BulkMessageRequestDto(
                            data = generateDataFromSelectedContacts(),
                            iPAddress = context.getDeviceIpAddress(),
                            schCode = userDataStore.getSchoolData()?.schoolCode,
                            isBulk = if (message.value.contains("____")) 0 else 1,
                            sMSType = smsType?.typeID,
                            geoCoordinate = currentLocation.toString().replace("(", "")
                                .replace(")", ""),
                            uID = /*userDataStore.getUser().userId*/ 32,
                            uType = userDataStore.getUser()?.userType
                        )
                    )
                    .collectLatest { response ->
                        if (response.isSuccess) {
                            result(true, response.getOrNull() ?: "")
                        } else {
                            result(
                                false,
                                response.exceptionOrNull()?.message ?: UNKNOWN_ERROR_MESSAGE
                            )
                        }

                    }
            } else {
                messageRepository.sendMessage(
                    SendMessageRequest(
                        device = 1,
                        geoCoordinate = currentLocation.toString().replace("(", "")
                            .replace(")", ""),
                        ipAddress = context.getDeviceIpAddress(),
                        subject = subject.value,
                        body = message.value,
                        classIDs = null,
                        recipient = contacts.value.map {
                            Recipients(
                                receiverID = it.receiverID,
                                receiverType = it.receiverType
                            )
                        },
                        recipientType = 3,
                        msgType = 2,
                        attachment = getAttachment(),
                        multipleAttachments = getMultipleAttachment()
                    )
                )
                    .collectLatest { response ->
                        if (response.isSuccess) {
                            result(true, response.getOrNull() ?: "")
                        } else {
                            result(
                                false,
                                response.exceptionOrNull()?.message ?: UNKNOWN_ERROR_MESSAGE
                            )
                        }
                    }
            }
        }

    }

    private fun getMultipleAttachment(): List<String>? {
        val attachments = attachments.value
        if (attachments.isEmpty() || attachments.size == 1)
            return null

        return attachments.map {
            FileAccess.bitmapToByteArrayBase64String(
                FileAccess.bitmapFromFile(
                    context,
                    it.path!!
                )
            )
        }
    }

    private fun getAttachment(): Attachment? {
        val attachments = attachments.value
        return if (attachments.isEmpty()) {
            null
        } else if (attachments.size == 1) {
            attachments.first()


            //val bitmap = FileAccess.bitmapFromUri(context, imgUri)

            val bitmap = FileAccess.bitmapFromFile(context, attachments.first().path!!)

            val imageString = FileAccess.bitmapToByteArrayBase64String(bitmap)

            val imageExt = FileAccess.getImageExtFromUri(context, bitmap).toString()
            Attachment(
                attachment = imageString,
                fileExt = imageExt,
                fileURL = null
            )
        } else {
            null
        }
    }

    private fun generateDataFromSelectedContacts(): List<Data>? {
        val data = mutableListOf<Data>()

        contacts.value.forEach { contact ->
            data.add(
                Data(
                    mobile = contact.mobile,
                    rCPTID = contact.receiverID,
                    rCPTType = contact.receiverType,
                    templateID = template?.templateID,
                    sMS =
                    if (contact.isParent()) {
                        template?.template?.replace("R____", contact.name)
                            ?.replace("S____", contact.childName ?: "")
                            ?.replace("C____", contact.className ?: "")
                            ?.replace("ADNo____", contact.admissionNo ?: "")
                    } else if (contact.isStaff()) {
                        template?.template?.replace("R____", "")?.replace("S____", contact.name)
                            ?.replace("C____", contact.className ?: "")
                            ?.replace("ADNo____", contact.admissionNo ?: "")

                    } else {
                        template?.template?.replace("R____", contact.name)
                    }
                )
            )
        }

        return data
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
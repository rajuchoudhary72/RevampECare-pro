package com.app.ecarepro.ui.message.compose

import android.content.Context
import android.net.Uri
import android.net.wifi.WifiManager
import android.util.Base64
import android.widget.Toast
import androidx.core.net.toUri
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
import com.app.ecarepro.utils.getFile
import com.google.firebase.messaging.FirebaseMessagingService
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
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
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
                            uID = userDataStore.getUser()?.userId,
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
            }
            else {
                if (contacts.value.isEmpty()){
                    result(false,"Please Select recipient")
                }else{
                    val wifiManager = context.getSystemService(FirebaseMessagingService.WIFI_SERVICE) as WifiManager
                    val wInfo = wifiManager.connectionInfo
                    val macAddress = wInfo.macAddress
                    messageRepository.sendMessage(
                        SendMessageRequest(
                            device = 1,
                            geoCoordinate = currentLocation.toString().replace("(", "")
                                .replace(")", ""),
                            ipAddress = macAddress,
                            subject = subject.value,
                            body = message.value,
                            classIDs = null,
                            recipient = contacts.value.map {
                                Recipients(
                                    receiverID = it.receiverID,
                                    receiverType = it.receiverType
                                )
                            },
                            recipientType = contacts.value.firstOrNull()?.receiverType,
                            msgType = getMessageType(),
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

    }
    private fun getMessageType(): Int {
        val attachments = attachments.value
        return if (attachments.isEmpty()) {
            1
        } else if (attachments.all { AttachmentType.PDF.name == it.name }) {
            5
        } else if (attachments.all { AttachmentType.AUDIO.name == it.name }) {
            3
        } else {
            2
        }
    }
    private fun getMultipleAttachment(): List<String>? {
        val attachments = attachments.value
        if (attachments.isEmpty() || attachments.size == 1)
            return null

        return attachments.map { attachment ->
            if (isPdf(attachment)) {
                if (attachment.name == AttachmentType.RECORDING.name) {
                    val file = File(attachment.path)
                    getBase64StringFromUri(file) ?: ""
                } else {
                    val file = context.getFile(attachment.path?.toUri())
                    getBase64StringFromUri(file!!.toUri()) ?: ""
                }
            } else {
                FileAccess.bitmapToByteArrayBase64String(
                    FileAccess.bitmapFromFile(
                        context,
                        attachment.path!!
                    )
                )
            }
        }
    }

    private fun getAttachment(): Attachment? {
        val attachments = attachments.value
        return if (attachments.isEmpty()) {
            null
        } else if (attachments.size == 1) {
            val attachment = attachments.first()
            if (isPdf(attachment)) {
                if (attachment.name == AttachmentType.RECORDING.name) {
                    val file = File(attachment.path)
                    val attach = getBase64StringFromUri(file)
                    Attachment(
                        attachment = attach,
                        fileExt = getFileExtension(file),
                        fileURL = null
                    )
                } else {
                    val file = context.getFile(attachment.path?.toUri())
                    val attach = getBase64StringFromUri(file!!.toUri())
                    Attachment(
                        attachment = attach,
                        fileExt = getFileExtension(file),
                        fileURL = null
                    )
                }
            } else {
                val bitmap = FileAccess.bitmapFromFile(context, attachments.first().path!!)
                val imageString = FileAccess.bitmapToByteArrayBase64String(bitmap)
                val imageExt = FileAccess.getImageExtFromUri(context, bitmap).toString()
                Attachment(
                    attachment = imageString,
                    fileExt = imageExt,
                    fileURL = null
                )
            }
        } else {
            null
        }
    }

    private fun isPdf(attachment: MiMedia) =
        mutableListOf(
            AttachmentType.PDF.name,
            AttachmentType.AUDIO.name,
            AttachmentType.RECORDING.name
        ).contains(attachment.name)

    private fun getFileExtension(file: File): String {
        val name = file.name
        val lastIndexOf = name.lastIndexOf(".")
        if (lastIndexOf == -1) {
            return ""
        }
        return name.substring(lastIndexOf + 1)
    }

    private fun getBase64StringFromUri(uri: Uri): String? {
        val imageStream: InputStream
        return try {
            imageStream = requireNotNull(context.contentResolver.openInputStream(uri))
            val bytes: ByteArray = readBytes(
                imageStream
            )
            Base64.encodeToString(bytes, Base64.NO_WRAP)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
    private fun getBase64StringFromUri(file: File): String? {
        val imageStream: InputStream
        return try {
            imageStream = FileInputStream(file)
            val bytes: ByteArray = readBytes(
                imageStream
            )
            Base64.encodeToString(bytes, Base64.NO_WRAP)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
    @Throws(IOException::class)
    private fun readBytes(inputStream: InputStream): ByteArray {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)

        var len: Int
        while ((inputStream.read(buffer).also { len = it }) != -1) {
            byteBuffer.write(buffer, 0, len)
        }

        return byteBuffer.toByteArray()
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
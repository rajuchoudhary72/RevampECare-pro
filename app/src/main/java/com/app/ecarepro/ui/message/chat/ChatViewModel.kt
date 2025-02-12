package com.app.ecarepro.ui.message.chat

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.net.wifi.WifiManager
import android.util.Base64
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.Attachment
import com.app.ecarepro.data.network.model.Contact
import com.app.ecarepro.data.network.model.Message
import com.app.ecarepro.data.network.model.MessageSettings
import com.app.ecarepro.data.network.model.NetworkUserDetailsDto
import com.app.ecarepro.data.network.model.Recipient
import com.app.ecarepro.data.network.model.ReplyMessageRequestDto
import com.app.ecarepro.data.network.model.Sender
import com.app.ecarepro.data.network.model.SmsType
import com.app.ecarepro.data.repository.MessageRepository
import com.app.ecarepro.model.ComposeMessageType
import com.app.ecarepro.ui.message.compose.AttachmentType
import com.app.ecarepro.ui.message.sent.UNKNOWN_ERROR_MESSAGE
import com.app.ecarepro.utils.FileAccess
import com.app.ecarepro.utils.getFile
import com.lassi.data.media.MiMedia
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream


@HiltViewModel
class ChatViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val messageRepository: MessageRepository,
    private val userDataStore: UserDataStore,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val id = savedStateHandle.getLiveData("ID", initialValue = "")
     val messageType = savedStateHandle.get<String>("MessageType") ?: MessageType.INBOX.value

    val messageBody = MutableStateFlow("")
    val message = MutableStateFlow("")

    val composeMessageType =
        savedStateHandle.getStateFlow("composeMessageType", ComposeMessageType.ONLY_APP_MESSAGE)

    private lateinit var user: NetworkUserDetailsDto

    fun setAttachments(attachments: List<MiMedia>) {
        this@ChatViewModel.attachments.update { attachments }
    }

    fun removeAttachment(attachment: MiMedia) {
        attachments.update { current -> current.filterNot { it == attachment } }
    }

    fun clearAttachment(){
        attachments.update {
            emptyList()
        }
    }

    private val attachments = MutableStateFlow<List<MiMedia>>(emptyList())
    val attachmentVisible = combine(
        flow = composeMessageType,
        flow2 = userDataStore.getUserAsFlow(),
        flow3 = userDataStore.getMessageSettings()
    ) { messageType, user, messageSettings ->
        val hideMessageAttachment =
            messageSettings?.media == null || (messageSettings.media.browseAudio == false && messageSettings.media.browsePDF == false && messageSettings.media.browseImg == false)
        messageType == ComposeMessageType.ONLY_APP_MESSAGE && hideMessageAttachment.not()
    }.asLiveData()

    val uiState =
        combine(
            flow = id.asFlow(),
            flow2 = attachments
        ) { id, attachments ->
            Pair(id, attachments)
        }
        .flatMapLatest {(id, _) ->
            messageRepository.getConversationDetails(id, MessageType.getMessageType(messageType))
        }
        .map { result ->
            if (result.isSuccess) {
                result.getOrNull()!!.let { response ->
                    if (response.msgDTL.isNullOrEmpty()) {
                        ChatUiState.EmptyInbox
                    } else {
                        /*  senderDTL =  response.senderDTL*/
                        ChatUiState.Success(
                            messages = response.msgDTL,
                            msgID = response.msgID,
                            readCount = response.readCount,
                            receiverID = response.receiverID,
                            receiverType = response.receiverType,
                            canReply = response.canReply,
                            recipients = response.recipients ?: emptyList(),
                            subject = response.subject,
                            senderDTL =  response.senderDTL,
                            messageSettings = userDataStore.getMessageSettings().firstOrNull(),
                            attachments = attachments.value

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
                        body = message.value.trim(),
                        ipAddress = context.getDeviceIpAddress(),
                        msgType = getMessageType(),
                        receiverType = uiState.receiverType,
                        receiverID = uiState.receiverID,
                        msgID = uiState.msgID,
                        attachment = getAttachment()
                    )
                )
                .collectLatest { result ->
                    result
                        .onSuccess { message ->
                            this@ChatViewModel.message.update { "" }
                            attachments.update { emptyList() }
                            refresh()
                            func(true, message)
                        }
                        .onFailure {
                            func(false, it.message ?: UNKNOWN_ERROR_MESSAGE)
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
        } else if (attachments.all { AttachmentType.RECORDING.name == it.name }) {
            3
        }else {
            2
        }
    }
    private fun getMultipleAttachment(): List<String>? {
        val attachments = attachments.value
        if (getMessageType()==1)
            return null
        /*  if (attachments.isEmpty() || attachments.size == 1)
                    return null*/
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
        }else  if (getMessageType()==1)
            return null
        else if (attachments.size == 1) {
            val attachment = attachments.first()
            if (isPdf(attachment)) {
                if (attachment.name == AttachmentType.RECORDING.name) {
                    val file = File(attachment.path)
                    val attach = getBase64StringFromUri(file)
                    Attachment(
                        attachment = attach,
                        fileExt = "mp3",
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
                //  saveBitmapAndGetExtension(bitmap)
                val imageExt = getImageExtension(bitmap, Bitmap.CompressFormat.JPEG)
                //      val imageExt = FileAccess.getImageExtFromUri(context, bitmap).toString()
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

    fun getImageExtension(bitmap: Bitmap, compressFormat: Bitmap.CompressFormat): String {
        return when (compressFormat) {
            Bitmap.CompressFormat.JPEG -> "jpg"
            Bitmap.CompressFormat.PNG -> "png"
            Bitmap.CompressFormat.WEBP -> "webp"
            else -> "unknown"
        }
    }
    private fun isPdf(attachment: MiMedia) =
        mutableListOf(
            AttachmentType.PDF.name,
            AttachmentType.AUDIO.name,
            AttachmentType.RECORDING.name,
            AttachmentType.GALLERY.name,
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

    /*  val senderDTL: Sender*/
    data class Success(
        val messages: List<Message>,
        val recipients: List<Recipient>,
        val msgID: Int?,
        val readCount: Int?,
        val receiverID: Int?,
        val receiverType: Int?,
        val subject: String?,
        val canReply: Boolean?,
        val senderDTL: Sender?,
        val messageSettings: MessageSettings?,
        val attachments: List<MiMedia>,
    ) : ChatUiState

    data class Error(
        val error: Throwable,
    ) : ChatUiState

    fun isLoading() = this == Loading

    fun getErrorOrNull() = if (this is Error) this.error else null
}

enum class MessageType(val value: String) {
    INBOX("inbox"),
    SENT("sent"),
    CONV("conv")
    ;

    companion object {
        fun getMessageType(value: String): MessageType {
            return values().firstOrNull { it.value == value } ?: INBOX
        }
    }

}

sealed interface ComposeUiState {

    object Loading : ComposeUiState

    data class Success(
        val composeMessageType: ComposeMessageType,
        val attachments: List<MiMedia>,
        val contacts: List<Contact>,
        val smsTypes: List<SmsType>,
        val messageSettings: MessageSettings?
    ) : ComposeUiState

    data class Error(
        val error: Throwable
    ) : ComposeUiState

    fun isLoading() = this == Loading

    fun getErrorOrNull() = if (this is Error) this.error else null
}

package com.app.ecarepro.ui.assignment.staff.postAssignment

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkMyClass
import com.app.ecarepro.data.network.model.NetworkMySubjects
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkStudentParentComms
import com.app.ecarepro.data.network.model.NetworkViewAssignment
import com.app.ecarepro.data.network.model.post_question.Attachment
import com.app.ecarepro.data.repository.MessageRepository
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.model.ClassID_StID
import com.app.ecarepro.ui.message.compose.AttachmentType
import com.app.ecarepro.utils.FileAccess
import com.app.ecarepro.utils.getFile
import com.lassi.data.media.MiMedia
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject
@HiltViewModel
class PostAssignmentViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val messageRepository: MessageRepository,
    private val  userRepository: UserRepository
) : ViewModel() {


    private val attachments = MutableStateFlow<List<MiMedia>>(emptyList())


    private val myClassMutableStateFlow: MutableStateFlow<NetworkResult<NetworkMyClass>> = MutableStateFlow(
        NetworkResult.Loading())
    val myClassStateFlow: StateFlow<NetworkResult<NetworkMyClass>> = myClassMutableStateFlow

    private val subjectsMutableStateFlow: MutableStateFlow<NetworkResult<NetworkMySubjects>> = MutableStateFlow(
        NetworkResult.Loading())
    val subjectsStateFlow: StateFlow<NetworkResult<NetworkMySubjects>> = subjectsMutableStateFlow

    private val createAssignmentMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> = MutableStateFlow(
        NetworkResult.Loading())
    val createAssignmentStateFlow: StateFlow<NetworkResult<CommonResponse>> = createAssignmentMutableStateFlow

    private val studentParentCommsMutableStateFlow: MutableStateFlow<NetworkResult<NetworkStudentParentComms>> = MutableStateFlow(
        NetworkResult.Loading())
    val studentParentCommsStateFlow: StateFlow<NetworkResult<NetworkStudentParentComms>> = studentParentCommsMutableStateFlow

    private val viewAssignmentMutableStateFlow: MutableStateFlow<NetworkResult<NetworkViewAssignment>> = MutableStateFlow(
        NetworkResult.Loading())
    val viewAssignmentStateFlow: StateFlow<NetworkResult<NetworkViewAssignment>> = viewAssignmentMutableStateFlow

    fun setAttachments(attachments: List<MiMedia>) {
        this@PostAssignmentViewModel.attachments.update { attachments }
    }

    fun removeAttachment( ) {
        attachments.update { emptyList() }
    }

    fun getMyClass(subID: Int, iD: Int  )=viewModelScope.launch {
          runCatching {
              myClassMutableStateFlow.value =NetworkResult.Loading( )
              userRepository.staffMyClass(subID, iD)
          }.onSuccess {
              myClassMutableStateFlow.value =NetworkResult.Success(it)
          }.onFailure {
              myClassMutableStateFlow.value = NetworkResult.Error(it.message)
          }
      }

    fun mySubjects(classID :Int )=viewModelScope.launch {
        runCatching {
            subjectsMutableStateFlow.value =NetworkResult.Loading( )
            userRepository.mySubjects(classID )
        }.onSuccess {
            subjectsMutableStateFlow.value =NetworkResult.Success(it)
        }.onFailure {
            subjectsMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }

    fun studentParentComms(
        recipientType: Int,
        classIDs: String,
        scholarType: Int,
        byRollNo: Boolean,
    )=viewModelScope.launch {
        runCatching {
            studentParentCommsMutableStateFlow.value =NetworkResult.Loading( )
            messageRepository.studentParentComms(recipientType, classIDs, scholarType, byRollNo )
        }.onSuccess {
            studentParentCommsMutableStateFlow.value =NetworkResult.Success(it)
        }.onFailure {
            studentParentCommsMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }



    fun createAssignment(
        asgDate: String,
        asgID: Int,

        classID: Int,
        classIDs: String,
        data: String,
        file: String,
        id: String,
        isActive: Boolean,
        isFileRemoved: Boolean,
        multipleSubmission: Boolean,

        subjectID: Int,
        submitDate: String,
        title: String,
        lateSubmission: Boolean,
        attachments: List<Attachment>,
        classID_StID: List<ClassID_StID>,
        stIDs: String?


    )=viewModelScope.launch {
        runCatching {
            createAssignmentMutableStateFlow.value =NetworkResult.Loading( )
            userRepository.createAssignment( asgDate, asgID,   classID, classIDs,
                data, file, id, isActive, isFileRemoved, multipleSubmission, subjectID, submitDate, title,
                lateSubmission,getAttachment(),classID_StID,stIDs)
        }.onSuccess {
            createAssignmentMutableStateFlow.value =NetworkResult.Success(it)
        }.onFailure {
            createAssignmentMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }





    fun viewAssignment(  iD: String )=viewModelScope.launch {
        runCatching {
            viewAssignmentMutableStateFlow.value = NetworkResult.Loading()
            userRepository.viewAssignment(iD )
        }.onSuccess {
            viewAssignmentMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            viewAssignmentMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }


    private fun getAttachment(): com.app.ecarepro.data.network.model.Attachment? {
        val attachments = attachments.value
        return if (attachments.isEmpty()) {
            null
        } else if (attachments.size == 1) {
            val attachment = attachments.first()
            if (isPdf(attachment)) {
                if (attachment.name == AttachmentType.RECORDING.name) {
                    val file = File(attachment.path)
                    val attach = getBase64StringFromUri(file)
                    com.app.ecarepro.data.network.model.Attachment(
                        attachment = attach,
                        fileExt = getFileExtension(file),
                        fileURL = null
                    )
                } else {
                    val file = context.getFile(attachment.path?.toUri())
                    val attach = getBase64StringFromUri(file!!.toUri())
                    com.app.ecarepro.data.network.model.Attachment(
                        attachment = attach,
                        fileExt = getFileExtension(file),
                        fileURL = null
                    )
                }
            } else {
                val bitmap = FileAccess.bitmapFromFile(context, attachments.first().path!!)
                val imageString = FileAccess.bitmapToByteArrayBase64String(bitmap)
                val imageExt = FileAccess.getImageExtFromUri(context, bitmap).toString()
                com.app.ecarepro.data.network.model.Attachment(
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


}
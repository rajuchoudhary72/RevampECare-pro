package com.app.ecarepro.ui.syllabus.teacher.add_syllabus

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
import com.app.ecarepro.data.network.model.create_syllabus.BrowsedFile
import com.app.ecarepro.data.network.model.create_syllabus.PostSyllabus
import com.app.ecarepro.data.repository.UserRepository
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
class AddSyllabusViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val  userRepository: UserRepository
) : ViewModel() {


    private val attachments = MutableStateFlow<List<MiMedia>>(emptyList())
    fun setAttachments(attachments: List<MiMedia>) {
        this@AddSyllabusViewModel.attachments.update { attachments }
    }

    private val myClassMutableStateFlow: MutableStateFlow<NetworkResult<NetworkMyClass>> = MutableStateFlow(
        NetworkResult.Loading())
    val myClassStateFlow: StateFlow<NetworkResult<NetworkMyClass>> = myClassMutableStateFlow

    private val subjectsMutableStateFlow: MutableStateFlow<NetworkResult<NetworkMySubjects>> = MutableStateFlow(
        NetworkResult.Loading())
    val subjectsStateFlow: StateFlow<NetworkResult<NetworkMySubjects>> = subjectsMutableStateFlow

    private val saveSyllabusMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> = MutableStateFlow(
        NetworkResult.Loading())
    val saveSyllabusStateFlow: StateFlow<NetworkResult<CommonResponse>> = saveSyllabusMutableStateFlow

    fun getMyClass(subID: Int   )=viewModelScope.launch {
        runCatching {
            myClassMutableStateFlow.value =NetworkResult.Loading( )
            userRepository.staffMyClass(subID, true)
        }.onSuccess {
            myClassMutableStateFlow.value =NetworkResult.Success(it)
        }.onFailure {
            myClassMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }

    fun staffSubjects( classSTD: Int )=viewModelScope.launch {
        runCatching {
            subjectsMutableStateFlow.value =NetworkResult.Loading( )
            userRepository.staffSubjects(classSTD)
        }.onSuccess {
            subjectsMutableStateFlow.value =NetworkResult.Success(it)
        }.onFailure {
            subjectsMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }

    fun saveSyllabus(
        classID: Int,
        id: String,
        subID: Int,
        title: String
    )=viewModelScope.launch {
        runCatching {
            saveSyllabusMutableStateFlow.value =NetworkResult.Loading( )
            userRepository.saveSyllabus(PostSyllabus(getAttachment()!!,classID, id, subID, title))
        }.onSuccess {
            saveSyllabusMutableStateFlow.value =NetworkResult.Success(it)
        }.onFailure {
            saveSyllabusMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }


    private fun getAttachment(): BrowsedFile? {
        val attachments = attachments.value
        return if (attachments.isEmpty()) {
            null
        } else if (attachments.size == 1) {
            val attachment = attachments.first()
            if (isPdf(attachment)) {
                if (attachment.name == AttachmentType.RECORDING.name) {
                    val file = File(attachment.path)
                    val attach = getBase64StringFromUri(file)
                    BrowsedFile(
                        attachment = attach,
                        fileExt = getFileExtension(file),

                    )
                } else {
                    val file = context.getFile(attachment.path?.toUri())
                    val attach = getBase64StringFromUri(file!!.toUri())
                    BrowsedFile(
                        attachment = attach,
                        fileExt = getFileExtension(file),

                    )
                }
            } else {
                val bitmap = FileAccess.bitmapFromFile(context, attachments.first().path!!)
                val imageString = FileAccess.bitmapToByteArrayBase64String(bitmap)
                val imageExt = FileAccess.getImageExtFromUri(context, bitmap).toString()
                BrowsedFile(
                    attachment = imageString,
                    fileExt = imageExt,

                )
            }
        } else {
            null
        }
    }


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

    private fun isPdf(attachment: MiMedia) =
        mutableListOf(
            AttachmentType.PDF.name,
            AttachmentType.AUDIO.name,
            AttachmentType.RECORDING.name
        ).contains(attachment.name)

}
package com.app.ecarepro.ui.discipline_log.infraction.add_infraction

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkAddInfraction
import com.app.ecarepro.data.network.model.NetworkInfractionInstance
import com.app.ecarepro.data.network.model.NetworkInfractionTypes
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkSubInfractionTypes
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.model.BrowsedFile
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
class AddInfractionViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private   val userRepository: UserRepository
) : ViewModel() {


    private val attachments = MutableStateFlow<List<MiMedia>>(emptyList())

    fun setAttachments(attachments: List<MiMedia>) {
        this@AddInfractionViewModel.attachments.update { attachments }
    }

    fun removeAttachment( ) {
        attachments.update { emptyList() }
    }

    private val infractionTypesMutableStateFlow: MutableStateFlow<NetworkResult<NetworkInfractionTypes>> = MutableStateFlow(
        NetworkResult.Loading())
    val infractionTypesStateFlow: StateFlow<NetworkResult<NetworkInfractionTypes>> = infractionTypesMutableStateFlow


    private val infractionInstanceMutableStateFlow: MutableStateFlow<NetworkResult<NetworkInfractionInstance>> = MutableStateFlow(
        NetworkResult.Loading())
    val infractionInstanceStateFlow: StateFlow<NetworkResult<NetworkInfractionInstance>> = infractionInstanceMutableStateFlow


    private val addInfractionMutableStateFlow: MutableStateFlow<NetworkResult<NetworkAddInfraction>> = MutableStateFlow(
        NetworkResult.Loading())
    val addInfractionStateFlow: StateFlow<NetworkResult<NetworkAddInfraction>> = addInfractionMutableStateFlow

 private val subInfractionTypesMutableStateFlow: MutableStateFlow<NetworkResult<NetworkSubInfractionTypes>> = MutableStateFlow(
        NetworkResult.Loading())
    val subInfractionTypesStateFlow: StateFlow<NetworkResult<NetworkSubInfractionTypes>> = subInfractionTypesMutableStateFlow

    private val saveInfractionMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> = MutableStateFlow(
        NetworkResult.Loading())
    val saveInfractionStateFlow: StateFlow<NetworkResult<CommonResponse>> = saveInfractionMutableStateFlow


    fun getInfractionTypes( )=viewModelScope.launch {
        runCatching {
            infractionTypesMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getInfractionTypes()
        }.onSuccess {
            infractionTypesMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            infractionTypesMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

    fun  addInfraction( stID: Int  )=viewModelScope.launch {
        runCatching {
            addInfractionMutableStateFlow.value = NetworkResult.Loading()
            userRepository.addInfraction(stID)
        }.onSuccess {
            addInfractionMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            addInfractionMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

    fun  getSubInfractionTypes(infrTypeID: Int)=viewModelScope.launch {
        runCatching {
            subInfractionTypesMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getSubInfractionTypes(infrTypeID)
        }.onSuccess {
            subInfractionTypesMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            subInfractionTypesMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

    fun  getinfractionInstance(
        infrTypeID: Int,
        InfrSubTypeID: Int,
        StID: Int
    )=viewModelScope.launch {
        runCatching {
            infractionInstanceMutableStateFlow.value = NetworkResult.Loading()
            userRepository.infractionInstance(infrTypeID, InfrSubTypeID, StID)
        }.onSuccess {
            infractionInstanceMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            infractionInstanceMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

    fun   saveInfraction(
        uType: Int,
        action:Int,
        stID:Int,
        infrSubTypeID:Int,
        consID:Int,
        instance:Int,
        infractionOn:String,
        correctiveAction:String,
        isComplianceActive:Boolean
        )=viewModelScope.launch {
        runCatching {
            saveInfractionMutableStateFlow.value = NetworkResult.Loading()
            userRepository.saveInfraction(uType,action, stID, infrSubTypeID, consID, instance, infractionOn, correctiveAction,getAttachment(),isComplianceActive)
        }.onSuccess {
            saveInfractionMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            saveInfractionMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }


    private fun getAttachment(): BrowsedFile? {
        val attachments = attachments.value
        var  browsedFile= BrowsedFile(null,null,null)
        return if (attachments.isEmpty()) {
            null
        } else   {
            for (attachment in attachments) {
                if (isPdf(attachment)) {
                    val file = context.getFile(attachment.path?.toUri())
                    val attach = getBase64StringFromUri(file!!.toUri())
                    browsedFile = BrowsedFile(
                        attachment = attach.toString(),
                        fileExt = getFileExtension(file),
                        fileURL = null
                    )
                } else {

                    val bitmap = FileAccess.bitmapFromFile(context, attachment.path!!)
                    val imageString = FileAccess.bitmapToByteArrayBase64String(bitmap)
                    val imageExt = getImageExtension(bitmap, Bitmap.CompressFormat.JPEG)

                    browsedFile = BrowsedFile(
                        attachment = imageString,
                        fileExt = imageExt,
                        fileURL = null
                    )
                }
            }

            return browsedFile
        }
    }

    private fun getImageExtension(bitmap: Bitmap, compressFormat: Bitmap.CompressFormat): String {
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
package com.app.ecarepro.feature.questionner.view.create_question

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.AddQuestionRequest
import com.app.ecarepro.core.domain.model.AttachmentData
import com.app.ecarepro.core.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddQuestionViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddQuestionUiState())
    val uiState: StateFlow<AddQuestionUiState> = _uiState.asStateFlow()

    private var cameraImageUri: Uri? = null

    fun handleIntent(intent: AddQuestionIntent) {
        when (intent) {
            is AddQuestionIntent.OnQuestionTextChanged -> {
                _uiState.update { it.copy(questionText = intent.text) }
            }
            is AddQuestionIntent.OnImageSelected -> {
                processSelectedImage(intent.context, intent.uri)
            }
            is AddQuestionIntent.OnCameraImageCaptured -> {
                intent.context?.let { context ->
                    cameraImageUri?.let { uri ->
                        processSelectedImage(context, uri)
                    }
                }
            }
            is AddQuestionIntent.OnRemoveImage -> {
                _uiState.update {
                    it.copy(
                        selectedImageUri = null,
                        imageBase64 = null,
                        imageExtension = null
                    )
                }
            }
            is AddQuestionIntent.SubmitQuestion -> {
                submitQuestion()
            }
            is AddQuestionIntent.ClearMessages -> {
                _uiState.update { it.copy(errorMessage = null, successMessage = null) }
            }
        }
    }

    fun createCameraImageUri(context: Context): Uri? {
        return try {
            val imageFile = File(context.cacheDir, "camera_${System.currentTimeMillis()}.jpg")
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                imageFile
            )
            cameraImageUri = uri
            uri
        } catch (e: Exception) {
            _uiState.update { it.copy(errorMessage = "Failed to create camera image: ${e.message}") }
            null
        }
    }

    private fun processSelectedImage(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val contentResolver = context.contentResolver

                // Get file extension from URI
                val extension = getFileExtension(context, uri)

                // Convert image to base64
                val inputStream = contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if (bitmap == null) {
                    _uiState.update { it.copy(errorMessage = "Failed to load image") }
                    return@launch
                }

                // Check file size (10 MB limit)
                val outputStream = ByteArrayOutputStream()
                var quality = 100
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

                while (outputStream.size() > 10 * 1024 * 1024 && quality > 10) {
                    outputStream.reset()
                    quality -= 10
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                }

                if (outputStream.size() > 10 * 1024 * 1024) {
                    _uiState.update { it.copy(errorMessage = "Image size exceeds 10 MB limit") }
                    return@launch
                }

                val base64String = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)

                _uiState.update {
                    it.copy(
                        selectedImageUri = uri,
                        imageBase64 = base64String,
                        imageExtension = extension
                    )
                }

                bitmap.recycle()
                outputStream.close()

            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to process image: ${e.message}") }
            }
        }
    }

    private fun getFileExtension(context: Context, uri: Uri): String {
        val mimeType = context.contentResolver.getType(uri)
        return when {
            mimeType?.contains("png") == true -> ".png"
            mimeType?.contains("jpg") == true || mimeType?.contains("jpeg") == true -> ".jpg"
            else -> ".jpg" // Default to jpg
        }
    }

    private fun submitQuestion() {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value

                if (currentState.questionText.isBlank()) {
                    _uiState.update { it.copy(errorMessage = "Please enter a question") }
                    return@launch
                }

                _uiState.update { it.copy(isLoading = true) }

                // Create request with attachment
                val attachmentData = AttachmentData(
                    attachment = currentState.imageBase64 ?: "",
                    fileExt = currentState.imageExtension ?: "",
                    fileURL = ""
                )

                val request = AddQuestionRequest(
                    question = currentState.questionText,
                    attachment = attachmentData
                )

                userRepository.addQuestion(request).collect { result ->
                    result.fold(
                        onSuccess = { response ->
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    successMessage = response.message,
                                    isSuccess = true
                                )
                            }
                        },
                        onFailure = { error ->
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = error.message ?: "Failed to add question"
                                )
                            }
                        }
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "An error occurred"
                    )
                }
            }
        }
    }
}

data class AddQuestionUiState(
    val questionText: String = "",
    val selectedImageUri: Uri? = null,
    val imageBase64: String? = null,
    val imageExtension: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isSuccess: Boolean = false
)

sealed class AddQuestionIntent {
    data class OnQuestionTextChanged(val text: String) : AddQuestionIntent()
    data class OnImageSelected(val context: Context, val uri: Uri) : AddQuestionIntent()
    data class OnCameraImageCaptured(val context: Context?) : AddQuestionIntent()
    data object OnRemoveImage : AddQuestionIntent()
    data object SubmitQuestion : AddQuestionIntent()
    data object ClearMessages : AddQuestionIntent()
}

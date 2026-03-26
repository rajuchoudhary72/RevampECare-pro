package com.app.ecarepro.feature.update_record.update_profile_picture

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.Class
import com.app.ecarepro.core.domain.model.ProfilePictureStudent
import com.app.ecarepro.core.domain.repository.UpdateProfilePictureRepository
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SelectedFileDetails
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.component.SortConfig
import com.app.ecarepro.designsystem.core.component.SortDirection
import com.app.ecarepro.designsystem.core.component.SortOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class UpdateProfilePictureViewModel @Inject constructor(
    private val repository: UpdateProfilePictureRepository,
) : BaseViewModel<UpdateProfilePictureIntent, UpdateProfilePictureEvent>() {

    private val _uiState = MutableStateFlow(UpdateProfilePictureUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadClasses()
    }

    override fun handleIntent(intent: UpdateProfilePictureIntent) {
        when (intent) {
            is UpdateProfilePictureIntent.OnBackClicked -> sendEvent(UpdateProfilePictureEvent.NavigateBack)
            is UpdateProfilePictureIntent.SelectClass -> onClassTabSelected(intent.index)
            is UpdateProfilePictureIntent.OnEditClicked -> _uiState.update { it.copy(pickerForStID = intent.stID) }
            is UpdateProfilePictureIntent.DismissPicker -> _uiState.update { it.copy(pickerForStID = null) }
            is UpdateProfilePictureIntent.OnImageSelected -> onImageSelected(intent.stID, intent.file)
            is UpdateProfilePictureIntent.OnSearchQueryChanged -> _uiState.update { it.copy(searchQuery = intent.query) }
            is UpdateProfilePictureIntent.OnSortClick -> _uiState.update { it.copy(showSortSheet = true) }
            is UpdateProfilePictureIntent.OnSortSelected -> onSortSelected(intent.sortConfig)
            is UpdateProfilePictureIntent.DismissSortSheet -> _uiState.update { it.copy(showSortSheet = false) }
        }
    }

    private fun loadClasses() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingClasses = true, errorMessage = null) }
            repository.getClassTeacherOf().collect { result ->
                result
                    .onSuccess { classes ->
                        _uiState.update { it.copy(isLoadingClasses = false, classes = classes) }
                        if (classes.isNotEmpty()) loadStudents(classes[0].id)
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(isLoadingClasses = false, errorMessage = error.errorMessage()) }
                    }
            }
        }
    }

    private fun onClassTabSelected(index: Int) {
        val classes = _uiState.value.classes
        if (index >= classes.size) return
        _uiState.update { it.copy(selectedClassIndex = index, students = emptyList(), searchQuery = "") }
        loadStudents(classes[index].id)
    }

    private fun loadStudents(classId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingStudents = true, errorMessage = null) }
            repository.getStudents(classId).collect { result ->
                result
                    .onSuccess { students ->
                        _uiState.update { it.copy(isLoadingStudents = false, students = students) }
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(isLoadingStudents = false, errorMessage = error.errorMessage()) }
                    }
            }
        }
    }

    private fun onImageSelected(stID: Int, file: SelectedFileDetails) {
        _uiState.update { it.copy(pickerForStID = null, uploadingStID = stID) }
        viewModelScope.launch {
            try {
                val (base64, ext) = withContext(Dispatchers.IO) {
                    val bytes = file.file.readBytes()
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    val out = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
                    val compressed = out.toByteArray()
                    val encoded = Base64.encodeToString(compressed, Base64.NO_WRAP)
                    encoded to "jpeg"
                }
                repository.uploadStudentPhoto(stID, base64, ext).collect { result ->
                    result
                        .onSuccess { message ->
                            _uiState.update { state ->
                                state.copy(
                                    uploadingStID = null,
                                    students = state.students.map { s ->
                                        if (s.stID == stID) s.copy(photo = s.photo + "?t=${System.currentTimeMillis()}") else s
                                    },
                                )
                            }
                            sendEvent(UpdateProfilePictureEvent.ShowMessage(SnackbarMessage(message.ifBlank { "Photo updated successfully" }, MessageType.SUCCESS)))
                        }
                        .onFailure { error ->
                            _uiState.update { it.copy(uploadingStID = null) }
                            sendEvent(UpdateProfilePictureEvent.ShowMessage(SnackbarMessage(error.errorMessage(), MessageType.ERROR)))
                        }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(uploadingStID = null) }
                sendEvent(UpdateProfilePictureEvent.ShowMessage(SnackbarMessage("Failed to process image", MessageType.ERROR)))
            }
        }
    }

    private fun onSortSelected(sortConfig: SortConfig) {
        val sorted = sortStudents(_uiState.value.students, sortConfig)
        _uiState.update { it.copy(showSortSheet = false, sortConfig = sortConfig, students = sorted) }
    }

    private fun sortStudents(students: List<ProfilePictureStudent>, sortConfig: SortConfig): List<ProfilePictureStudent> {
        val comparator: Comparator<ProfilePictureStudent> = when (sortConfig.option) {
            SortOption.ROLL_NUMBER -> compareBy { it.rollNumber.toIntOrNull() ?: Int.MAX_VALUE }
            SortOption.ADMISSION_NUMBER -> compareBy { it.admissionNumber }
            SortOption.NAME -> compareBy { it.name }
            else -> compareBy { it.rollNumber.toIntOrNull() ?: Int.MAX_VALUE }
        }
        return if (sortConfig.direction == SortDirection.ASCENDING) {
            students.sortedWith(comparator)
        } else {
            students.sortedWith(comparator.reversed())
        }
    }
}

@Immutable
data class UpdateProfilePictureUiState(
    val classes: List<Class> = emptyList(),
    val selectedClassIndex: Int = 0,
    val isLoadingClasses: Boolean = false,
    val isLoadingStudents: Boolean = false,
    val students: List<ProfilePictureStudent> = emptyList(),
    val pickerForStID: Int? = null,
    val uploadingStID: Int? = null,
    val sortConfig: SortConfig = SortConfig(option = SortOption.ROLL_NUMBER),
    val showSortSheet: Boolean = false,
    val searchQuery: String = "",
    val errorMessage: String? = null,
)

sealed interface UpdateProfilePictureIntent {
    data object OnBackClicked : UpdateProfilePictureIntent
    data class SelectClass(val index: Int) : UpdateProfilePictureIntent
    data class OnEditClicked(val stID: Int) : UpdateProfilePictureIntent
    data object DismissPicker : UpdateProfilePictureIntent
    data class OnImageSelected(val stID: Int, val file: SelectedFileDetails) : UpdateProfilePictureIntent
    data class OnSearchQueryChanged(val query: String) : UpdateProfilePictureIntent
    data object OnSortClick : UpdateProfilePictureIntent
    data class OnSortSelected(val sortConfig: SortConfig) : UpdateProfilePictureIntent
    data object DismissSortSheet : UpdateProfilePictureIntent
}

sealed interface UpdateProfilePictureEvent {
    data object NavigateBack : UpdateProfilePictureEvent
    data class ShowMessage(val message: SnackbarMessage) : UpdateProfilePictureEvent
}

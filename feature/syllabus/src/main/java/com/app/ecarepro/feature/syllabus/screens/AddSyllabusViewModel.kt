package com.app.ecarepro.feature.syllabus.screens

import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.SelectedFileType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddSyllabusViewModel @Inject constructor() :
    BaseViewModel<AddSyllabusIntent, AddSyllabusEvent>() {

    private val _uiState: MutableStateFlow<UiState<AddSyllabusUiState>> =
        MutableStateFlow(UiState.Success(AddSyllabusUiState()))
    val uiState = _uiState.asStateFlow()

    override fun handleIntent(intent: AddSyllabusIntent) {
        when (intent) {
            AddSyllabusIntent.OnBackClicked -> {
                viewModelScope.launch { sendEvent(AddSyllabusEvent.NavigateBack) }
            }

            is AddSyllabusIntent.OnTabSelected -> {
                updateState { it.copy(selectedTabIndex = intent.index) }
            }

            is AddSyllabusIntent.OnClassChanged -> {
                updateState { it.copy(selectedClass = intent.value) }
            }

            is AddSyllabusIntent.OnSectionChanged -> {
                updateState { it.copy(selectedSection = intent.value) }
            }

            is AddSyllabusIntent.OnSubjectChanged -> {
                updateState { it.copy(selectedSubject = intent.value) }
            }

            is AddSyllabusIntent.OnTitleChanged -> {
                updateState { it.copy(title = intent.value) }
            }

            AddSyllabusIntent.OnSubmitClicked -> {
                // Handle submission logic
                viewModelScope.launch { sendEvent(AddSyllabusEvent.ShowSuccessMessage) }
            }

            AddSyllabusIntent.OnAddFileClicked -> {
                updateState { it.copy(isFileUploadSheetVisible = true) }
            }

            AddSyllabusIntent.OnDismissFileUploadSheet -> {
                updateState { it.copy(isFileUploadSheetVisible = false) }
            }

            is AddSyllabusIntent.OnFileSelected -> {
                updateState {
                    it.copy(
                        isFileUploadSheetVisible = false,
                        selectedFileUri = intent.uri,
                        selectedFileType = intent.type
                    )
                }
            }
        }
    }



    private fun updateState(update: (AddSyllabusUiState) -> AddSyllabusUiState) {
        _uiState.update { currentState ->
            if (currentState is UiState.Success) {
                UiState.Success(update(currentState.data))
            } else {
                currentState
            }
        }
    }
}

@Immutable
data class AddSyllabusUiState(
    val selectedTabIndex: Int = 0,
    val tabs: List<String> = listOf("Class wise", "Section wise"),
    val selectedClass: String = "",
    val selectedSection: String = "",
    val selectedSubject: String = "",
    val title: String = "",
    val isLoading: Boolean = false,
    val isFileUploadSheetVisible: Boolean = false,
    val selectedFileUri: Uri? = null,
    val selectedFileType: SelectedFileType? = null,
)


sealed interface AddSyllabusIntent {
    data object OnBackClicked : AddSyllabusIntent
    data class OnTabSelected(val index: Int) : AddSyllabusIntent
    data class OnClassChanged(val value: String) : AddSyllabusIntent
    data class OnSectionChanged(val value: String) : AddSyllabusIntent
    data class OnSubjectChanged(val value: String) : AddSyllabusIntent
    data class OnTitleChanged(val value: String) : AddSyllabusIntent
    data object OnAddFileClicked : AddSyllabusIntent
    data object OnSubmitClicked : AddSyllabusIntent

    data object OnDismissFileUploadSheet : AddSyllabusIntent

    data class OnFileSelected(val uri: Uri, val type: SelectedFileType) : AddSyllabusIntent

}


sealed interface AddSyllabusEvent {
    data object NavigateBack : AddSyllabusEvent
    data object ShowSuccessMessage : AddSyllabusEvent
}
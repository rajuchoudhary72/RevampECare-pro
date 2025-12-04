package com.app.ecarepro.feature.syllabus.screens

import androidx.compose.runtime.Immutable
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.common.Base64Utils
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.BrowsedFile
import com.app.ecarepro.core.domain.model.Class
import com.app.ecarepro.core.domain.model.SaveSyllabus
import com.app.ecarepro.core.domain.model.Section
import com.app.ecarepro.core.domain.model.Subject
import com.app.ecarepro.core.domain.model.Syllabus
import com.app.ecarepro.core.domain.repository.SyllabusRepository
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.AssistedViewModelFactory
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SelectedFileDetails
import com.app.ecarepro.designsystem.core.component.SelectedFileType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.feature.syllabus.navigation.SyllabusNavigationGraph
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@HiltViewModel(assistedFactory = AddSyllabusViewModel.Factory::class)
class AddSyllabusViewModel @AssistedInject constructor(
    @Assisted val navKey: SyllabusNavigationGraph.AddSyllabus,
    private val syllabusRepository: SyllabusRepository,
) : BaseViewModel<AddSyllabusIntent, AddSyllabusEvent>() {

    private val syllabus: Syllabus? = navKey.syllabus

    private val _uiState: MutableStateFlow<UiState<AddSyllabusUiState>> =
        MutableStateFlow(UiState.Success(AddSyllabusUiState()))
    val uiState = _uiState.asStateFlow()

    init {
        fetchClasses()
    }

    override fun handleIntent(intent: AddSyllabusIntent) {
        when (intent) {
            AddSyllabusIntent.OnBackClicked -> {
                viewModelScope.launch { sendEvent(AddSyllabusEvent.NavigateBack) }
            }

            is AddSyllabusIntent.OnTabSelected -> {
                updateState { it.copy(selectedTabIndex = intent.index) }
            }

            is AddSyllabusIntent.OnClassChanged -> {
                onClassSelected(intent.value)
            }

            is AddSyllabusIntent.OnSectionChanged -> {
                onSectionSelected(intent.value)
            }

            is AddSyllabusIntent.OnSubjectChanged -> {
                onSubjectSelected(intent.value)
            }

            is AddSyllabusIntent.OnTitleChanged -> {
                updateState { it.copy(title = intent.value) }
            }

            AddSyllabusIntent.OnSubmitClicked -> {
                saveSyllabus()
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
                        selectedFile = intent.file
                    )
                }
            }

            AddSyllabusIntent.OnClassSelectClicked -> {
                updateState {
                    it.copy(
                        isClassSelectSheetVisible = true
                    )
                }
            }

            AddSyllabusIntent.OnSectionSelectClicked -> {
                updateState {
                    it.copy(
                        isSectionSelectSheetVisible = true
                    )
                }
            }

            AddSyllabusIntent.OnSubjectSelectClicked -> {
                updateState {
                    it.copy(
                        isSubjectSelectSheetVisible = true
                    )
                }
            }

            AddSyllabusIntent.OnDismissClassSelectSheet -> {
                updateState {
                    it.copy(
                        isClassSelectSheetVisible = false
                    )
                }
            }

            AddSyllabusIntent.OnDismissSectionSelectSheet -> {
                updateState {
                    it.copy(
                        isSectionSelectSheetVisible = false
                    )
                }
            }

            AddSyllabusIntent.OnDismissSubjectSelectSheet -> {
                updateState {
                    it.copy(
                        isSubjectSelectSheetVisible = false
                    )
                }
            }

            is AddSyllabusIntent.OnShowError -> {
                sendError(intent.error)
            }
        }
    }


    private fun fetchClasses() {
        viewModelScope.launch {
            syllabusRepository.getClasses().onStart {
                updateState { it.copy(isLoading = true) }
            }.collect { result ->
                result.onSuccess { classes ->
                    val defaultSelectedClass = if (syllabus != null) {
                        // In edit mode, try to find the class matching the syllabus
                        classes.find { it.classID == syllabus.classID }?.className
                    } else {
                        // In add mode, default to first class
                        classes.firstOrNull()?.className
                    }

                    updateState {
                        it.copy(
                            isLoading = false,
                            classes = classes,
                            selectedClass = defaultSelectedClass,
                            title = syllabus?.title ?: "",
                            // If editing, pre-fill the file details (without the File object)
                           /* selectedFile = syllabus?.fileName?.let { fileName ->
                                SelectedFileDetails(
                                    name = fileName,
                                    file = File(syllabus.filePath.orEmpty()),
                                    uri = null,
                                    size = 0,
                                    formattedSize = "",
                                    mimeType = "",
                                    type = SelectedFileType.DOCUMENT
                                )
                            }*/
                        )
                    }

                    // If a class was selected (either default or from syllabus), fetch sub-data
                    // Pass 'true' for isRestoring if we are in edit mode and successfully matched a class
                    val matchedClass = classes.find { it.className == defaultSelectedClass }
                    if (matchedClass != null) {
                        fetchSectionsAndSubjects(
                            classStd = matchedClass.classID.toString(),
                            isRestoring = syllabus != null && matchedClass.classID == syllabus.classID
                        )
                    }
                }.onFailure { error ->
                    updateState { it.copy(isLoading = false) }
                    sendError(error.errorMessage())
                }
            }
        }
    }

    private fun onClassSelected(className: String) {
        updateState {
            it.copy(
                isClassSelectSheetVisible = false,
                selectedClass = className,
                selectedSection = null,
                selectedSubject = null,
                sections = emptyList(),
                subject = emptyList()
            )
        }

        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        val selectedClass = currentState.classes.find { it.className == className }

        if (selectedClass != null) {
            fetchSectionsAndSubjects(selectedClass.classID.toString())
        }
    }


    private fun fetchSectionsAndSubjects(classStd: String, isRestoring: Boolean = false) {
        viewModelScope.launch {
            combine(
                syllabusRepository.getSections(classStd), syllabusRepository.getSubjects(classStd)
            ) { sectionsResult, subjectsResult ->
                sectionsResult to subjectsResult
            }.onStart {
                updateState { it.copy(isLoading = true) }
            }.collect { (sectionsResult, subjectsResult) ->
                updateState { currentState ->
                    val sections = sectionsResult.getOrElse { emptyList() }
                    val subjects = subjectsResult.getOrElse { emptyList() }.toMutableList().apply {
                        add(0, Subject.SUBJECT_ALL)
                    }

                    // Default Selections
                    var selectedSectionNames = sections.map { it.secName.orEmpty() }
                    var selectedSubjectName = subjects.firstOrNull()?.subjectName
                    var selectedTabIndex = currentState.selectedTabIndex

                    // Logic for Restoring Data in Edit Mode
                    if (isRestoring && syllabus != null) {
                        // Restore Subject
                        val matchedSubject = subjects.find { it.subID == syllabus.subID }
                        matchedSubject?.let { selectedSubjectName = it.subjectName }

                        // Restore Sections
                        val syllabusSectionIds = syllabus.classIDs?.split(",")?.mapNotNull { it.trim().toIntOrNull() } ?: emptyList()
                        if (syllabusSectionIds.isNotEmpty()) {
                            val matchedSections = sections.filter { it.secID in syllabusSectionIds }
                            if (matchedSections.isNotEmpty()) {
                                selectedSectionNames = matchedSections.map { it.secName.orEmpty() }
                                selectedTabIndex = 1 // Switch to "Section wise" if specific sections are selected
                            }
                        }
                    }

                    currentState.copy(
                        isLoading = false,
                        sections = sections,
                        subject = subjects,
                        selectedSection = selectedSectionNames,
                        selectedSubject = selectedSubjectName,
                        selectedTabIndex = selectedTabIndex
                    )
                }

                if (sectionsResult.isFailure || subjectsResult.isFailure) {
                    sendError(
                        sectionsResult.exceptionOrNull()?.errorMessage()
                            ?: subjectsResult.exceptionOrNull()?.errorMessage()
                            ?: "Something went wrong"
                    )
                }
            }
        }
    }

    private fun onSectionSelected(sectionValue: List<String>) {
        updateState {
            it.copy(
                selectedSection = sectionValue
            )
        }
    }

    private fun onSubjectSelected(subjectValue: String) {
        updateState {
            it.copy(
                selectedSubject = subjectValue
            )
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

    private fun validateForm(updatedState: AddSyllabusUiState): Boolean {
        with(updatedState) {
            if (selectedClass.isNullOrEmpty()) {
                showValidateWarning("Please select class")
                return false
            } else if (selectedTabIndex == 1 && selectedSection.isNullOrEmpty()) {
                showValidateWarning("Please select section")
                return false
            } else if (selectedSubject.isNullOrEmpty()) {
                showValidateWarning("Please select subject")
                return false
            } else if (title.isBlank()) {
                showValidateWarning("Please enter title")
                return false
            } else if (selectedFile == null) {
                showValidateWarning("Please select file")
                return false
            } else {
                return true
            }
        }
    }

    fun showValidateWarning(string: String) {
        sendEvent(
            AddSyllabusEvent.ShowSuccessMessage(
                SnackbarMessage(
                    text = string, type = MessageType.WARNING
                )
            )
        )
    }

    private fun saveSyllabus() = viewModelScope.launch {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return@launch

        if (!validateForm(currentState)) return@launch

        updateState { it.copy(isLoading = true) }

        val browsedFileResult = withContext(Dispatchers.IO) {
            val file = currentState.selectedFile?.file
            if (file != null) {
                // New file selected
                val base64 = Base64Utils.getBase64StringFromFile(file)
                val ext = Base64Utils.getFileExtension(file)
                BrowsedFile(attachment = base64, fileExt = ext)
            } else {
                // No new file selected (Edit mode with existing file)
                null
            }
        }

        // Validation: If we are NOT editing (syllabus == null) AND browsedFileResult is null, it's an error.
        // If we ARE editing, null browsedFileResult means "keep existing".
        if (browsedFileResult == null && syllabus == null) {
            updateState { it.copy(isLoading = false) }
            sendError("File not found or invalid.")
            return@launch
        }

        val selectedClass = currentState.classes.find { it.className == currentState.selectedClass }
        val selectedSubject =
            currentState.subject.find { it.subjectName == currentState.selectedSubject }

        val selectedSectionNames = currentState.selectedSection?.toSet() ?: emptySet()
        val selectedSections = currentState.sections.filter { it.secName in selectedSectionNames }

        if (selectedClass?.classID == null || selectedSubject?.subID == null) {
            updateState { it.copy(isLoading = false) }
            sendError("Invalid Class or Subject selection.")
            return@launch
        }

        val payload = SaveSyllabus(
            id = syllabus?.id.orEmpty(), // Pass ID if editing
            classID = selectedClass.classID!!,
            classIDs = selectedSections.joinToString(",") { it.secID.toString() },
            subID = selectedSubject.subID!!,
            title = currentState.title,
            browsedFile = browsedFileResult, // Null here implies no change to file in backend logic usually
            fileName = currentState.selectedFile?.name ?: "unknown"
        )

        syllabusRepository.saveSyllabus(payload)
            .collect { result ->
                updateState { it.copy(isLoading = false) }
                result.onSuccess {
                    sendEvent(
                        AddSyllabusEvent.ShowSuccessMessage(
                            SnackbarMessage(
                                text = it,
                                type = MessageType.SUCCESS
                            )
                        )
                    )
                    sendEvent(AddSyllabusEvent.NavigateBack)
                }.onFailure {
                    sendError(it.errorMessage())
                }
            }
    }

    private fun sendError(message: String) {
        sendEvent(
            AddSyllabusEvent.ShowSuccessMessage(
                SnackbarMessage(text = message, type = MessageType.ERROR)
            )
        )
    }

    @AssistedFactory
    interface Factory :
        AssistedViewModelFactory<SyllabusNavigationGraph.AddSyllabus, AddSyllabusViewModel> {
        override fun create(param: SyllabusNavigationGraph.AddSyllabus): AddSyllabusViewModel
    }

}

@Immutable
data class AddSyllabusUiState(
    val selectedTabIndex: Int = 0,
    val tabs: List<String> = listOf("Class wise", "Section wise"),
    val classes: List<Class> = emptyList(),
    val sections: List<Section> = emptyList(),
    val subject: List<Subject> = emptyList(),
    val selectedClass: String? = null,
    val selectedSection: List<String>? = null,
    val selectedSubject: String? = null,
    val title: String = "",
    val isLoading: Boolean = false,
    val isFileUploadSheetVisible: Boolean = false,
    val selectedFile: SelectedFileDetails? = null,
    val isClassSelectSheetVisible: Boolean = false,
    val isSectionSelectSheetVisible: Boolean = false,
    val isSubjectSelectSheetVisible: Boolean = false,
)


sealed interface AddSyllabusIntent {
    data object OnBackClicked : AddSyllabusIntent
    data class OnTabSelected(val index: Int) : AddSyllabusIntent
    data class OnClassChanged(val value: String) : AddSyllabusIntent
    data class OnSectionChanged(val value: List<String>) : AddSyllabusIntent
    data class OnSubjectChanged(val value: String) : AddSyllabusIntent
    data class OnTitleChanged(val value: String) : AddSyllabusIntent
    data object OnAddFileClicked : AddSyllabusIntent
    data object OnSubmitClicked : AddSyllabusIntent
    data object OnDismissFileUploadSheet : AddSyllabusIntent
    data class OnFileSelected(val file: SelectedFileDetails) : AddSyllabusIntent
    data class OnShowError(val error: String) : AddSyllabusIntent
    data object OnClassSelectClicked : AddSyllabusIntent
    data object OnSectionSelectClicked : AddSyllabusIntent
    data object OnSubjectSelectClicked : AddSyllabusIntent
    data object OnDismissClassSelectSheet : AddSyllabusIntent
    data object OnDismissSectionSelectSheet : AddSyllabusIntent
    data object OnDismissSubjectSelectSheet : AddSyllabusIntent
}


sealed interface AddSyllabusEvent {
    data object NavigateBack : AddSyllabusEvent
    data class ShowSuccessMessage(val snackbarMessage: SnackbarMessage) : AddSyllabusEvent
}

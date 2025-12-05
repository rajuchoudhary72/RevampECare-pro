package com.app.ecarepro.feature.assignment.screens

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.Class
import com.app.ecarepro.core.domain.model.Subject
import com.app.ecarepro.core.domain.repository.SyllabusRepository
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.AssistedViewModelFactory
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SelectedFileDetails
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.feature.assignment.navigation.AssignmentNavigationGraph
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = AddAssignmentViewModel.Factory::class)
class AddAssignmentViewModel @AssistedInject constructor(
    @Assisted val navKey: AssignmentNavigationGraph.AddAssignment,
    private val syllabusRepository: SyllabusRepository,
) : BaseViewModel<AddAssignmentIntent, AddAssignmentEvent>() {

    private val assignment = navKey.assignment

    private val _uiState: MutableStateFlow<UiState<AddAssignmentUiState>> =
        MutableStateFlow(UiState.Success(AddAssignmentUiState()))
    val uiState = _uiState.asStateFlow()

    init {
        fetchClasses()
    }

    override fun handleIntent(intent: AddAssignmentIntent) {
        when (intent) {
            AddAssignmentIntent.OnBackClicked -> {
                viewModelScope.launch { sendEvent(AddAssignmentEvent.NavigateBack) }
            }

            is AddAssignmentIntent.OnTabSelected -> {
                updateState { it.copy(selectedTabIndex = intent.index) }
            }

            is AddAssignmentIntent.OnClassChanged -> {
                onClassSelected(intent.value)
            }

            is AddAssignmentIntent.OnSubjectChanged -> {
                onSubjectSelected(intent.value)
            }

            is AddAssignmentIntent.OnTitleChanged -> {
                updateState { it.copy(title = intent.value) }
            }

            is AddAssignmentIntent.OnTypeChanged -> {
                updateState { it.copy(type = intent.value) }
            }

            AddAssignmentIntent.OnSubmitClicked -> {
            }

            AddAssignmentIntent.OnAddFileClicked -> {
                updateState { it.copy(isFileUploadSheetVisible = true) }
            }

            AddAssignmentIntent.OnDismissFileUploadSheet -> {
                updateState { it.copy(isFileUploadSheetVisible = false) }
            }

            is AddAssignmentIntent.OnFileSelected -> {
                updateState {
                    it.copy(
                        isFileUploadSheetVisible = false,
                        selectedFiles = intent.file
                    )
                }
            }

            AddAssignmentIntent.OnClassSelectClicked -> {
                updateState {
                    it.copy(
                        isClassSelectSheetVisible = true
                    )
                }
            }

            AddAssignmentIntent.OnSectionSelectClicked -> {
                updateState {
                    it.copy(
                        isSectionSelectSheetVisible = true
                    )
                }
            }

            AddAssignmentIntent.OnSubjectSelectClicked -> {
                updateState {
                    it.copy(
                        isSubjectSelectSheetVisible = true
                    )
                }
            }

            AddAssignmentIntent.OnDismissClassSelectSheet -> {
                updateState {
                    it.copy(
                        isClassSelectSheetVisible = false
                    )
                }
            }

            AddAssignmentIntent.OnDismissSectionSelectSheet -> {
                updateState {
                    it.copy(
                        isSectionSelectSheetVisible = false
                    )
                }
            }

            AddAssignmentIntent.OnDismissSubjectSelectSheet -> {
                updateState {
                    it.copy(
                        isSubjectSelectSheetVisible = false
                    )
                }
            }

            is AddAssignmentIntent.OnShowError -> {
                sendError(intent.error)
            }

            AddAssignmentIntent.ToggleSubmissionDateVisibility -> updateState {
                it.copy(
                    isSubmissionDateVisible = it.isSubmissionDateVisible.not()
                )
            }

            AddAssignmentIntent.ToggleIsActive -> updateState {
                it.copy(
                    isActive = it.isActive.not()
                )
            }

            AddAssignmentIntent.ToggleIsAllowedForLateSubmission -> updateState {
                it.copy(
                    isAllowedForMultipleSubmission = it.isAllowedForMultipleSubmission.not()
                )
            }

            AddAssignmentIntent.ToggleIsAllowedForMultipleSubmission -> updateState {
                it.copy(
                    isAllowedForLateSubmission = it.isAllowedForLateSubmission.not()
                )
            }

            AddAssignmentIntent.OnAssignmentDateSelectClicked -> updateState {
                it.copy(
                    isAssignmentDatePickerVisible = true
                )
            }

            AddAssignmentIntent.OnDismissSubmissionDateSheet -> updateState {
                it.copy(
                    isSubmissionDateVisible = false
                )
            }

            is AddAssignmentIntent.OnSelectSubmissionDate -> TODO()

            AddAssignmentIntent.OnDismissAssignmentDateSheet -> updateState {
                it.copy(
                    isAssignmentDatePickerVisible = false
                )
            }

            is AddAssignmentIntent.OnSelectAssignmentDate -> updateState {
                it.copy(
                    assignmentDate = intent.date,
                    isAssignmentDatePickerVisible = false
                )
            }

            AddAssignmentIntent.OnSubmissionDateSelectClicked -> updateState {
                it.copy(
                    isAssignmentDatePickerVisible = false
                )
            }

            is AddAssignmentIntent.OnDeleteSelectedFile ->  updateState {
                it.copy(
                    selectedFiles = it.selectedFiles.filter { file -> file != intent.file }
                )
            }
        }
    }


    private fun fetchClasses() {
        viewModelScope.launch {
            syllabusRepository.getClasses().onStart {
                updateState { it.copy(isLoading = true) }
            }.collect { result ->
                result.onSuccess { classes ->
                    val defaultSelectedClass =
                        assignment?.classX ?: classes.firstOrNull()?.className

                    updateState {
                        it.copy(
                            isLoading = false,
                            classes = classes,
                            selectedClass = defaultSelectedClass,
                            title = assignment?.title ?: "",
                        )
                    }

                    // If a class was selected (either default or from syllabus), fetch sub-data
                    // Pass 'true' for isRestoring if we are in edit mode and successfully matched a class
                    val matchedClass = classes.find { it.className == defaultSelectedClass }
                    if (matchedClass != null) {
                        fetchSectionsAndSubjects(
                            classStd = matchedClass.classID.toString(),
                            isRestoring = assignment != null && matchedClass.className == assignment.classX
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
                selectedSubject = null,
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
            syllabusRepository.getSubjects(classStd)
                .onStart {
                    updateState { it.copy(isLoading = true) }
                }
                .collect { subjectsResult ->

                    subjectsResult
                        .onSuccess {
                            updateState { currentState ->
                                val subjects =
                                    subjectsResult.getOrElse { emptyList() }.toMutableList().apply {
                                        add(0, Subject.SUBJECT_ALL)
                                    }

                                // Default Selections
                                var selectedSubjectName = subjects.firstOrNull()?.subjectName
                                var selectedTabIndex = currentState.selectedTabIndex

                                // Logic for Restoring Data in Edit Mode
                                if (isRestoring && assignment != null) {
                                    // Restore Subject
                                    val matchedSubject =
                                        subjects.find { it.subjectName == assignment.subject }
                                    matchedSubject?.let { selectedSubjectName = it.subjectName }
                                }

                                currentState.copy(
                                    isLoading = false,
                                    subject = subjects,
                                    selectedSubject = selectedSubjectName,
                                    selectedTabIndex = selectedTabIndex
                                )
                            }
                        }
                        .onFailure { error ->
                            sendError(
                                error.errorMessage()
                            )
                        }
                }
        }
    }

    private fun onSectionSelected(sectionValue: List<String>) {
        /* updateState {
             it.copy(
                 selectedSection = sectionValue
             )
         }*/
    }

    private fun onSubjectSelected(subjectValue: String) {
        updateState {
            it.copy(
                selectedSubject = subjectValue
            )
        }
    }


    private fun updateState(update: (AddAssignmentUiState) -> AddAssignmentUiState) {
        _uiState.update { currentState ->
            if (currentState is UiState.Success) {
                UiState.Success(update(currentState.data))
            } else {
                currentState
            }
        }
    }

    private fun validateForm(updatedState: AddAssignmentUiState): Boolean {
        with(updatedState) {
            if (selectedClass.isNullOrEmpty()) {
                showValidateWarning("Please select class")
                return false
            } /*else if (selectedTabIndex == 1 && selectedSection.isNullOrEmpty()) {
                showValidateWarning("Please select section")
                return false
            }*/ else if (selectedSubject.isNullOrEmpty()) {
                showValidateWarning("Please select subject")
                return false
            } else if (title.isBlank()) {
                showValidateWarning("Please enter title")
                return false
            } else if (selectedFiles.isEmpty()) {
                showValidateWarning("Please select file")
                return false
            } else {
                return true
            }
        }
    }

    fun showValidateWarning(string: String) {
        sendEvent(
            AddAssignmentEvent.ShowSuccessMessage(
                SnackbarMessage(
                    text = string, type = MessageType.WARNING
                )
            )
        )
    }

    private fun sendError(message: String) {
        sendEvent(
            AddAssignmentEvent.ShowSuccessMessage(
                SnackbarMessage(text = message, type = MessageType.ERROR)
            )
        )
    }

    @AssistedFactory
    interface Factory :
        AssistedViewModelFactory<AssignmentNavigationGraph.AddAssignment, AddAssignmentViewModel> {
        override fun create(param: AssignmentNavigationGraph.AddAssignment): AddAssignmentViewModel
    }

}

@Immutable
data class AddAssignmentUiState(
    val isLoading: Boolean = false,
    val selectedTabIndex: Int = 0,
    val tabs: List<String> = listOf("Class wise", "Student wise"),
    val classes: List<Class> = emptyList(),
    val students: List<String> = emptyList(),
    val subject: List<Subject> = emptyList(),
    val selectedClass: String? = null,
    val selectedStudents: List<String> = emptyList(),
    val selectedSubject: String? = null,
    val isSubmissionDateVisible: Boolean = true,
    val submissionDate: String = "",
    val assignmentDate: String = "",
    val title: String = "",
    val type: String = "",
    val isFileUploadSheetVisible: Boolean = false,
    val selectedFiles: List<SelectedFileDetails> = emptyList(),
    val isClassSelectSheetVisible: Boolean = false,
    val isSectionSelectSheetVisible: Boolean = false,
    val isSubjectSelectSheetVisible: Boolean = false,
    val isAssignmentDatePickerVisible: Boolean = false,
    val isAllowedForMultipleSubmission: Boolean = true,
    val isAllowedForLateSubmission: Boolean = true,
    val isActive: Boolean = true,
)


sealed interface AddAssignmentIntent {
    data object OnBackClicked : AddAssignmentIntent
    data class OnTabSelected(val index: Int) : AddAssignmentIntent
    data class OnClassChanged(val value: String) : AddAssignmentIntent
    data class OnSubjectChanged(val value: String) : AddAssignmentIntent
    data class OnTitleChanged(val value: String) : AddAssignmentIntent
    data class OnTypeChanged(val value: String) : AddAssignmentIntent
    data object OnAddFileClicked : AddAssignmentIntent
    data class OnDeleteSelectedFile(val file: SelectedFileDetails) : AddAssignmentIntent
    data object OnSubmitClicked : AddAssignmentIntent
    data object OnDismissFileUploadSheet : AddAssignmentIntent
    data class OnFileSelected(val file: List<SelectedFileDetails>) : AddAssignmentIntent
    data class OnShowError(val error: String) : AddAssignmentIntent
    data object OnClassSelectClicked : AddAssignmentIntent
    data object OnSectionSelectClicked : AddAssignmentIntent
    data object OnSubjectSelectClicked : AddAssignmentIntent
    data object OnDismissClassSelectSheet : AddAssignmentIntent
    data object OnDismissSectionSelectSheet : AddAssignmentIntent
    data object OnDismissSubjectSelectSheet : AddAssignmentIntent

    data object OnAssignmentDateSelectClicked : AddAssignmentIntent
    data object OnDismissAssignmentDateSheet : AddAssignmentIntent
    data class OnSelectAssignmentDate(val date: String) : AddAssignmentIntent

    data object ToggleSubmissionDateVisibility : AddAssignmentIntent
    data object OnSubmissionDateSelectClicked : AddAssignmentIntent
    data object OnDismissSubmissionDateSheet : AddAssignmentIntent
    data class OnSelectSubmissionDate(val date: String) : AddAssignmentIntent

    data object ToggleIsActive : AddAssignmentIntent
    data object ToggleIsAllowedForMultipleSubmission : AddAssignmentIntent
    data object ToggleIsAllowedForLateSubmission : AddAssignmentIntent

}


sealed interface AddAssignmentEvent {
    data object NavigateBack : AddAssignmentEvent
    data class ShowSuccessMessage(val snackbarMessage: SnackbarMessage) : AddAssignmentEvent
}
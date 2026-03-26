package com.app.ecarepro.feature.assignment.screens

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.designsystem.common.Base64Utils
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.AssignmentAttachment
import com.app.ecarepro.core.domain.model.Class
import com.app.ecarepro.core.domain.model.ClassIDStID
import com.app.ecarepro.core.domain.model.SaveAssignment
import com.app.ecarepro.core.domain.model.Student
import com.app.ecarepro.core.domain.model.Subject
import com.app.ecarepro.core.domain.repository.AcademicRepository
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = AddAssignmentViewModel.Factory::class)
class AddAssignmentViewModel @AssistedInject constructor(
    @Assisted val navKey: AssignmentNavigationGraph.AddAssignment,
    private val syllabusRepository: SyllabusRepository,
    private val academicRepository: AcademicRepository,
) : BaseViewModel<AddAssignmentIntent, AddAssignmentEvent>() {

    private val assignment = navKey.assignment

    private val _uiState: MutableStateFlow<UiState<AddAssignmentUiState>> =
        MutableStateFlow(UiState.Success(AddAssignmentUiState()))
    val uiState = _uiState.asStateFlow()

    init {
        fetchAssignmentSubjectsAndClasses()
    }

    override fun handleIntent(intent: AddAssignmentIntent) {
        when (intent) {
            // --- Navigation & Tab Control ---
            AddAssignmentIntent.OnBackClicked -> {
                viewModelScope.launch { sendEvent(AddAssignmentEvent.NavigateBack) }
            }

            is AddAssignmentIntent.OnTabSelected -> {
                updateState { it.copy(selectedTabIndex = intent.index) }
            }

            // --- Basic Form Fields & Toggles ---
            is AddAssignmentIntent.OnTitleChanged -> {
                updateState { it.copy(title = intent.value) }
            }

            is AddAssignmentIntent.OnTypeChanged -> {
                updateState { it.copy(type = intent.value) }
            }

            AddAssignmentIntent.ToggleIsActive -> updateState {
                it.copy(isActive = it.isActive.not())
            }

            AddAssignmentIntent.ToggleIsAllowedForLateSubmission -> updateState {
                it.copy(isAllowedForLateSubmission = it.isAllowedForLateSubmission.not())
            }

            AddAssignmentIntent.ToggleIsAllowedForMultipleSubmission -> updateState {
                it.copy(isAllowedForMultipleSubmission = it.isAllowedForMultipleSubmission.not())
            }

            // --- Class Selection ---
            AddAssignmentIntent.OnClassSelectClicked -> {
                updateState { it.copy(isClassSelectSheetVisible = true) }
            }

            AddAssignmentIntent.OnDismissClassSelectSheet -> {
                updateState { it.copy(isClassSelectSheetVisible = false) }
            }

            is AddAssignmentIntent.OnClassChanged -> {
                onClassSelected(intent.value)
            }

            // --- Subject Selection ---
            AddAssignmentIntent.OnSubjectSelectClicked -> {
                updateState { it.copy(isSubjectSelectSheetVisible = true) }
            }

            AddAssignmentIntent.OnDismissSubjectSelectSheet -> {
                updateState { it.copy(isSubjectSelectSheetVisible = false) }
            }

            is AddAssignmentIntent.OnSubjectChanged -> {
                onSubjectSelected(intent.value)
            }

            // --- Student Selection ---
            AddAssignmentIntent.OnStudentSelectClicked -> {
                updateState { it.copy(isStudentSelectSheetVisible = true) }
            }

            AddAssignmentIntent.OnDismissStudentSelectSheet -> {
                updateState { it.copy(isStudentSelectSheetVisible = false) }
            }

            is AddAssignmentIntent.OnStudentChanged -> {
                updateState { it.copy(selectedStudents = intent.value) }
            }

            // --- Assignment Date Handling ---
            AddAssignmentIntent.OnAssignmentDateSelectClicked -> updateState {
                it.copy(isAssignmentDatePickerVisible = true)
            }

            AddAssignmentIntent.OnDismissAssignmentDateSheet -> updateState {
                it.copy(isAssignmentDatePickerVisible = false)
            }

            is AddAssignmentIntent.OnSelectAssignmentDate -> updateState {
                it.copy(
                    assignmentDate = intent.date,
                    isAssignmentDatePickerVisible = false
                )
            }

            // --- Submission Date Handling ---
            AddAssignmentIntent.ToggleSubmissionDateVisibility -> updateState {
                it.copy(isSubmissionDateVisible = it.isSubmissionDateVisible.not())
            }

            AddAssignmentIntent.OnSubmissionDateSelectClicked -> updateState {
                it.copy(isSubmissionDatePickerVisible = true)
            }

            AddAssignmentIntent.OnDismissSubmissionDateSheet -> updateState {
                it.copy(isSubmissionDatePickerVisible = false)
            }

            is AddAssignmentIntent.OnSelectSubmissionDate -> {
                updateState {
                    it.copy(
                        submissionDate = intent.date,
                        isSubmissionDatePickerVisible = false
                    )
                }
            }

            // --- File Attachment Handling ---
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
                        selectedFiles = it.selectedFiles + intent.file
                    )
                }
            }

            is AddAssignmentIntent.OnDeleteSelectedFile -> updateState {
                it.copy(
                    selectedFiles = it.selectedFiles.filter { file -> file != intent.file }
                )
            }

            // --- Submission & Error Handling ---
            AddAssignmentIntent.OnSubmitClicked -> {
                saveAssignment()
            }

            is AddAssignmentIntent.OnShowError -> {
                sendError(intent.error)
            }
        }
    }

    private fun fetchAssignmentSubjectsAndClasses() {
        viewModelScope.launch {
            combine(
                syllabusRepository.getAssignmentClasses(),
                syllabusRepository.getAssignmentSubjects()
            ) { classes, subjects ->
                classes to subjects
            }
                .onStart { }
                .collect { (classes, subjects) ->
                    if (classes.isFailure || subjects.isFailure) {
                        _uiState.update {
                            UiState.Error(
                                classes.exceptionOrNull()?.errorMessage()
                                    ?: subjects.exceptionOrNull()?.errorMessage()
                                    ?: "Unknown error"
                            )
                        }
                    } else {
                        val classList = classes.getOrNull() ?: emptyList()
                        val subjectList = subjects.getOrNull() ?: emptyList()

                        val selectedSubject =
                            subjectList.firstOrNull { it.subjectName == assignment?.subject }?.subjectName


                        updateState { currentState ->
                            currentState.copy(
                                classes = classList,
                                subject = subjectList,
                                selectedSubject = selectedSubject,
                                title = assignment?.title.orEmpty(),
                                isActive = assignment?.isActive ?: true,
                                isAllowedForLateSubmission = assignment?.lateSubmission ?: true,
                                assignmentDate = assignment?.asgDate.orEmpty(),
                                submissionDate = assignment?.submitDate.orEmpty()
                            )
                        }
                    }
                }
        }
    }

    private fun onClassSelected(selectedClass: List<String>) {
        updateState {
            val classes =
                it.classes.filter { classObj -> selectedClass.contains(classObj.className) }

            if (selectedClass.isNotEmpty()) {
                fetchStudents(classes.joinToString { it.classID.toString() })
            }

            it.copy(
                selectedClass = classes,
            )
        }
    }


    private fun fetchStudents(classStd: String, isRestoring: Boolean = false) {
        viewModelScope.launch {

            syllabusRepository.getStudents(
                teacherId = "1",
                classId = classStd,
                scholarType = "2"
            )
                .onStart {
                    updateState { it.copy(isLoading = true) }
                }
                .collect { studentsResult ->
                    updateState { currentState ->
                        val students = studentsResult.getOrElse { emptyList() }

                        // Default Selections
                        var selectedStudentName = emptyList<Student>()
                        var selectedTabIndex = currentState.selectedTabIndex


                        // Logic for Restoring Data in Edit Mode
                        if (isRestoring && assignment != null) {
                            // Restore Subject


                            // Restore Students
                            val syllabusStudentsIds =
                                assignment.stIDs?.split(",")?.mapNotNull { it.trim().toIntOrNull() }
                                    ?: emptyList()
                            if (syllabusStudentsIds.isNotEmpty()) {
                                val matchedStudents =
                                    students.filter { it.stID in syllabusStudentsIds }
                                if (matchedStudents.isNotEmpty()) {
                                    selectedStudentName = matchedStudents
                                    selectedTabIndex =
                                        1 // Switch to "Student wise" if specific sections are selected
                                }
                            }
                        }

                        currentState.copy(
                            isLoading = false,
                            students = students,
                            selectedStudents = selectedStudentName,
                            selectedTabIndex = selectedTabIndex
                        )
                    }
                }
        }
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
            } else if (selectedSubject.isNullOrEmpty()) {
                showValidateWarning("Please select subject")
                return false
            } else if (selectedTabIndex == 1 && selectedStudents.isEmpty()) {
                showValidateWarning("Please select student")
                return false
            } else if (assignmentDate.isBlank()) {
                showValidateWarning("Please pick assignment date")
                return false
            } else if (isSubmissionDateVisible && submissionDate.isBlank()) {
                showValidateWarning("Please pick submission date")
                return false
            } else if (title.isBlank()) {
                showValidateWarning("Please enter title")
                return false
            } else if (type.isBlank()) {
                showValidateWarning("Please enter type")
                return false
            } else if (selectedFiles.isEmpty() && assignment == null) {
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

    fun saveAssignment() {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return

        if (!validateForm(currentState)) return

        updateState { it.copy(isLoading = true) }

        viewModelScope.launch(Dispatchers.IO) {
            // 1. Resolve Class and Subject IDs
            val subjectObj =
                currentState.subject.find { it.subjectName == currentState.selectedSubject }
            val classIds =
                currentState.selectedClass.joinToString(separator = ",") { it.classID.toString() }
            val subjectId = subjectObj?.subID ?: 0

            // 2. Process Files (Convert to Base64)
            val attachments = currentState.selectedFiles.map { fileDetails ->
                val base64String = Base64Utils.getBase64StringFromFile(fileDetails.file) ?: ""
                AssignmentAttachment(
                    attachment = base64String,
                    fileExt = Base64Utils.getFileExtension(fileDetails.file)
                )
            }

            // Handle legacy single-file fields if necessary, or just pick the first one
            val firstAttachment = attachments.firstOrNull()
            val firstFileDetails = currentState.selectedFiles.firstOrNull()

            // 3. Prepare Student Mapping (If "Student wise" tab is selected)
            val studentMappings = if (currentState.selectedTabIndex == 1) {
                currentState.selectedStudents.map { student ->
                    ClassIDStID(
                        classID = student.classID,
                        stIDs = student.stID.toString()
                    )
                }
            } else {
                emptyList()
            }

            // 4. Construct the Domain Model
            val saveAssignmentReq = SaveAssignment(
                asgDate = currentState.assignmentDate,
                asgID = assignment?.asgID ?: 0,
                attachments = attachments,
                classID = 0,
                classIDStID = studentMappings,
                classIDs = classIds,
                data = currentState.type,
                file = "",
                files = emptyList(),
                id = assignment?.id ?: "", // Assuming UUID or empty for new
                isActive = currentState.isActive,
                // If in edit mode and no files selected, mark as removed
                isFileRemoved = false,
                lateSubMission = currentState.isAllowedForLateSubmission,
                multipleSubMission = currentState.isAllowedForMultipleSubmission,
                submitDate = currentState.submissionDate,
                subjectID = subjectId,
                title = currentState.title,
                removedFiles = emptyList()
            )

            // 5. Call Repository
            // Note: Ensure saveAssignment is defined in SyllabusRepository or inject the appropriate repository
            academicRepository.saveAssignment(saveAssignmentReq).collect { result ->
                updateState { it.copy(isLoading = false) }
                result.onSuccess {
                    sendEvent(
                        AddAssignmentEvent.ShowSuccessMessage(
                            SnackbarMessage(it, MessageType.SUCCESS)
                        )
                    )
                    sendEvent(AddAssignmentEvent.NavigateBack)
                }.onFailure { error ->
                    sendError(error.errorMessage())
                }
            }
        }
    }

    @AssistedFactory
    interface Factory :
        AssistedViewModelFactory<AssignmentNavigationGraph.AddAssignment, AddAssignmentViewModel> {
        override fun create(param: AssignmentNavigationGraph.AddAssignment): AddAssignmentViewModel
    }

}

@Immutable
data class AddAssignmentUiState(
    // --- Meta & Navigation ---
    val isLoading: Boolean = false,
    val selectedTabIndex: Int = 0,
    val tabs: List<String> = listOf("Class wise", "Student wise"),

    // --- Basic Form Fields ---
    val title: String = "",
    val type: String = "",

    // --- Toggles ---
    val isActive: Boolean = true,
    val isAllowedForMultipleSubmission: Boolean = true,
    val isAllowedForLateSubmission: Boolean = true,

    // --- Data Sources (Lists) ---
    val classes: List<Class> = emptyList(),
    val subject: List<Subject> = emptyList(),
    val students: List<Student> = emptyList(),

    // --- Selection State ---
    val selectedClass: List<Class> = emptyList(),
    val selectedSubject: String? = null,
    val selectedStudents: List<Student> = emptyList(),

    // --- Selection Sheet Visibility ---
    val isClassSelectSheetVisible: Boolean = false,
    val isSubjectSelectSheetVisible: Boolean = false,
    val isStudentSelectSheetVisible: Boolean = false,

    // --- Date Handling ---
    val assignmentDate: String = "",
    val isAssignmentDatePickerVisible: Boolean = false,

    val submissionDate: String = "",
    val isSubmissionDateVisible: Boolean = true,
    val isSubmissionDatePickerVisible: Boolean = false,

    // --- File Attachments ---
    val selectedFiles: List<SelectedFileDetails> = emptyList(),
    val isFileUploadSheetVisible: Boolean = false,
)


sealed interface AddAssignmentIntent {
    // --- Navigation & Tab Control ---
    data object OnBackClicked : AddAssignmentIntent
    data class OnTabSelected(val index: Int) : AddAssignmentIntent

    // --- Basic Form Fields & Toggles ---
    data class OnTitleChanged(val value: String) : AddAssignmentIntent
    data class OnTypeChanged(val value: String) : AddAssignmentIntent
    data object ToggleIsActive : AddAssignmentIntent
    data object ToggleIsAllowedForMultipleSubmission : AddAssignmentIntent
    data object ToggleIsAllowedForLateSubmission : AddAssignmentIntent

    // --- Class Selection ---
    data object OnClassSelectClicked : AddAssignmentIntent
    data object OnDismissClassSelectSheet : AddAssignmentIntent
    data class OnClassChanged(val value: List<String>) : AddAssignmentIntent

    // --- Subject Selection ---
    data object OnSubjectSelectClicked : AddAssignmentIntent
    data object OnDismissSubjectSelectSheet : AddAssignmentIntent
    data class OnSubjectChanged(val value: String) : AddAssignmentIntent

    // --- Student Selection ---
    data object OnStudentSelectClicked : AddAssignmentIntent
    data object OnDismissStudentSelectSheet : AddAssignmentIntent
    data class OnStudentChanged(val value: List<Student>) : AddAssignmentIntent

    // --- Assignment Date Handling ---
    data object OnAssignmentDateSelectClicked : AddAssignmentIntent
    data object OnDismissAssignmentDateSheet : AddAssignmentIntent
    data class OnSelectAssignmentDate(val date: String) : AddAssignmentIntent

    // --- Submission Date Handling ---
    data object ToggleSubmissionDateVisibility : AddAssignmentIntent
    data object OnSubmissionDateSelectClicked : AddAssignmentIntent
    data object OnDismissSubmissionDateSheet : AddAssignmentIntent
    data class OnSelectSubmissionDate(val date: String) : AddAssignmentIntent

    // --- File Attachment Handling ---
    data object OnAddFileClicked : AddAssignmentIntent
    data object OnDismissFileUploadSheet : AddAssignmentIntent
    data class OnFileSelected(val file: List<SelectedFileDetails>) : AddAssignmentIntent
    data class OnDeleteSelectedFile(val file: SelectedFileDetails) : AddAssignmentIntent

    // --- Submission & Error Handling ---
    data object OnSubmitClicked : AddAssignmentIntent
    data class OnShowError(val error: String) : AddAssignmentIntent
}


sealed interface AddAssignmentEvent {
    data object NavigateBack : AddAssignmentEvent
    data class ShowSuccessMessage(val snackbarMessage: SnackbarMessage) : AddAssignmentEvent
}
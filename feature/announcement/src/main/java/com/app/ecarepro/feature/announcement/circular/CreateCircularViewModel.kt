package com.app.ecarepro.feature.announcement.circular

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.CircularAttachment
import com.app.ecarepro.core.domain.model.Class
import com.app.ecarepro.core.domain.model.SaveCircularRequest
import com.app.ecarepro.core.domain.model.StaffContact
import com.app.ecarepro.core.domain.model.StaffType
import com.app.ecarepro.core.domain.model.StudentParentContact
import com.app.ecarepro.core.domain.repository.AnnouncementRepository
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.common.Base64Utils
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SelectedFileDetails
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

enum class RecipientType(val value: String, val label: String) {
    ALL("0", "All User"),
    ALL_STUDENTS_PARENTS("1", "All students/parents"),
    ALL_STAFF("3", "All Staff"),
    STAFF_TYPE("6", "Staff type"),
    ALL_CLASSES("4", "Classes"),
    STUDENTS_PARENTS("5", "Students/Parents"),
}

enum class ScholarType(val value: String, val label: String) {
    ALL("2", "All"),
    BOARDING("1", "Boarding"),
    DAY_SCHOLAR("0", "Day Scholar"),
}

@HiltViewModel
class CreateCircularViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository,
) : BaseViewModel<CreateCircularIntent, CreateCircularEvent>() {

    private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val displayDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.US)

    private val _uiState = MutableStateFlow<UiState<CreateCircularUiState>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadFormData()
    }

    private fun loadFormData() {
        viewModelScope.launch {
            announcementRepository.getCreateCircularData()
                .onStart { _uiState.update { UiState.Loading } }
                .collect { result ->
                    result.onSuccess { data ->
                        val todayApi = apiDateFormat.format(Date())
                        val todayDisplay = displayDateFormat.format(Date())
                        _uiState.update {
                            UiState.Success(
                                CreateCircularUiState(
                                    staffTypes = data.staffTypes,
                                    classes = data.classes,
                                    isBoardingSchool = data.isBoardingSchool,
                                    releadOnApi = todayApi,
                                    releadOnDisplay = todayDisplay,
                                )
                            )
                        }
                    }.onFailure { error ->
                        _uiState.update { UiState.Error(error.errorMessage()) }
                    }
                }
        }
    }

    override fun handleIntent(intent: CreateCircularIntent) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        when (intent) {
            is CreateCircularIntent.OnBackClicked -> sendEvent(CreateCircularEvent.NavigateBack)
            is CreateCircularIntent.OnTitleChanged -> _uiState.update { UiState.Success(current.copy(title = intent.title)) }
            is CreateCircularIntent.OnDescriptionChanged -> _uiState.update { UiState.Success(current.copy(description = intent.description)) }
            is CreateCircularIntent.OnDateSelected -> _uiState.update { UiState.Success(current.copy(releadOnApi = intent.apiDate, releadOnDisplay = intent.displayDate)) }
            is CreateCircularIntent.OnStatusChanged -> _uiState.update { UiState.Success(current.copy(isActive = intent.isActive)) }
            is CreateCircularIntent.OnMustReadChanged -> _uiState.update { UiState.Success(current.copy(mustRead = intent.mustRead)) }
            is CreateCircularIntent.OnFileSelected -> _uiState.update { UiState.Success(current.copy(selectedFile = intent.file)) }
            is CreateCircularIntent.OnFileRemoved -> _uiState.update { UiState.Success(current.copy(selectedFile = null)) }
            is CreateCircularIntent.OnShowFilePicker -> _uiState.update { UiState.Success(current.copy(isFilePickerVisible = true)) }
            is CreateCircularIntent.OnDismissFilePicker -> _uiState.update { UiState.Success(current.copy(isFilePickerVisible = false)) }
            is CreateCircularIntent.OnShowRecipientPicker -> _uiState.update { UiState.Success(current.copy(isRecipientPickerVisible = true)) }
            is CreateCircularIntent.OnDismissRecipientPicker -> _uiState.update { UiState.Success(current.copy(isRecipientPickerVisible = false)) }
            is CreateCircularIntent.OnRecipientTypeSelected -> onRecipientTypeSelected(current, intent.type)
            is CreateCircularIntent.OnShowStaffTypePicker -> _uiState.update { UiState.Success(current.copy(isStaffTypePickerVisible = true)) }
            is CreateCircularIntent.OnDismissStaffTypePicker -> _uiState.update { UiState.Success(current.copy(isStaffTypePickerVisible = false)) }
            is CreateCircularIntent.OnStaffTypesSelected -> onStaffTypesSelected(current, intent.staffTypes)
            is CreateCircularIntent.OnShowStaffPicker -> _uiState.update { UiState.Success(current.copy(isStaffPickerVisible = true)) }
            is CreateCircularIntent.OnDismissStaffPicker -> _uiState.update { UiState.Success(current.copy(isStaffPickerVisible = false)) }
            is CreateCircularIntent.OnStaffSelected -> _uiState.update { UiState.Success(current.copy(selectedStaff = intent.staff, isStaffPickerVisible = false)) }
            is CreateCircularIntent.OnScholarTypeSelected -> _uiState.update { UiState.Success(current.copy(selectedScholarType = intent.scholarType, isScholarTypePickerVisible = false)) }
            is CreateCircularIntent.OnShowClassPicker -> _uiState.update { UiState.Success(current.copy(isClassPickerVisible = true)) }
            is CreateCircularIntent.OnDismissClassPicker -> _uiState.update { UiState.Success(current.copy(isClassPickerVisible = false)) }
            is CreateCircularIntent.OnClassesSelected -> onClassesSelected(current, intent.classes)
            is CreateCircularIntent.OnShowStudentParentPicker -> _uiState.update { UiState.Success(current.copy(isStudentParentPickerVisible = true)) }
            is CreateCircularIntent.OnDismissStudentParentPicker -> _uiState.update { UiState.Success(current.copy(isStudentParentPickerVisible = false)) }
            is CreateCircularIntent.OnStudentParentsSelected -> _uiState.update { UiState.Success(current.copy(selectedStudentParents = intent.contacts)) }
            is CreateCircularIntent.OnShowDatePicker -> _uiState.update { UiState.Success(current.copy(isDatePickerVisible = true)) }
            is CreateCircularIntent.OnDismissDatePicker -> _uiState.update { UiState.Success(current.copy(isDatePickerVisible = false)) }
            is CreateCircularIntent.OnShowScholarTypePicker -> _uiState.update { UiState.Success(current.copy(isScholarTypePickerVisible = true)) }
            is CreateCircularIntent.OnDismissScholarTypePicker -> _uiState.update { UiState.Success(current.copy(isScholarTypePickerVisible = false)) }
            is CreateCircularIntent.OnSubmit -> onSubmit(current)
            is CreateCircularIntent.OnRetry -> loadFormData()
        }
    }

    private fun onRecipientTypeSelected(current: CreateCircularUiState, type: RecipientType) {
        _uiState.update {
            UiState.Success(
                current.copy(
                    selectedRecipientType = type,
                    isRecipientPickerVisible = false,
                    selectedStaffTypes = emptyList(),
                    selectedStaff = emptyList(),
                    staffContacts = emptyList(),
                    selectedClasses = emptyList(),
                    selectedStudentParents = emptyList(),
                    studentParentContacts = emptyList(),
                    selectedScholarType = ScholarType.ALL,
                )
            )
        }
    }

    private fun onStaffTypesSelected(current: CreateCircularUiState, staffTypes: List<StaffType>) {
        _uiState.update {
            UiState.Success(
                current.copy(
                    selectedStaffTypes = staffTypes,
                    isStaffTypePickerVisible = false,
                    staffContacts = emptyList(),
                    selectedStaff = emptyList(),
                )
            )
        }
        if (staffTypes.isNotEmpty()) fetchStaffContacts(staffTypes.map { it.staffTypeID })
    }

    private fun onClassesSelected(current: CreateCircularUiState, classes: List<Class>) {
        _uiState.update {
            UiState.Success(
                current.copy(
                    selectedClasses = classes,
                    isClassPickerVisible = false,
                    studentParentContacts = emptyList(),
                    selectedStudentParents = emptyList(),
                )
            )
        }
        if (current.selectedRecipientType == RecipientType.STUDENTS_PARENTS && classes.isNotEmpty()) {
            fetchStudentParentContacts(classes.mapNotNull { it.classID }, current.selectedScholarType)
        }
    }

    private fun fetchStaffContacts(staffTypeIds: List<Int>) {
        viewModelScope.launch {
            announcementRepository.getStaffContacts(staffTypeIds).collect { result ->
                result.onSuccess { contacts ->
                    val current = (_uiState.value as? UiState.Success)?.data ?: return@onSuccess
                    _uiState.update { UiState.Success(current.copy(staffContacts = contacts)) }
                }.onFailure { error ->
                    sendEvent(CreateCircularEvent.ShowMessage(SnackbarMessage(error.errorMessage(), MessageType.ERROR)))
                }
            }
        }
    }

    private fun fetchStudentParentContacts(classIds: List<Int>, scholarType: ScholarType) {
        viewModelScope.launch {
            announcementRepository.getStudentParentContacts(classIds, scholarType.value.toInt()).collect { result ->
                result.onSuccess { contacts ->
                    val current = (_uiState.value as? UiState.Success)?.data ?: return@onSuccess
                    _uiState.update { UiState.Success(current.copy(studentParentContacts = contacts)) }
                }.onFailure { error ->
                    sendEvent(CreateCircularEvent.ShowMessage(SnackbarMessage(error.errorMessage(), MessageType.ERROR)))
                }
            }
        }
    }

    private fun onSubmit(current: CreateCircularUiState) {
        if (current.title.isBlank()) {
            sendEvent(CreateCircularEvent.ShowMessage(SnackbarMessage("Please enter a title", MessageType.ERROR)))
            return
        }
        viewModelScope.launch {
            _uiState.update { UiState.Success(current.copy(isSaving = true)) }
            try {
                val attachment: CircularAttachment? = current.selectedFile?.let { file ->
                    withContext(Dispatchers.IO) {
                        val base64 = Base64Utils.getBase64StringFromFile(file.file) ?: return@withContext null
                        val ext = Base64Utils.getFileExtension(file.file)
                        CircularAttachment(attachment = base64, fileExt = ext)
                    }
                }

                val details = current.description.trim().let {
                    if (it.isBlank()) null else "<p>$it</p>\n"
                }

                val request = SaveCircularRequest(
                    title = current.title,
                    details = details,
                    releadOn = current.releadOnApi,
                    recipientType = current.selectedRecipientType.value,
                    status = current.isActive,
                    mustRead = current.mustRead,
                    scholarType = current.selectedScholarType.value,
                    siDs = when (current.selectedRecipientType) {
                        RecipientType.STAFF_TYPE ->
                            if (current.selectedStaff.isNotEmpty())
                                current.selectedStaff.joinToString(",") { it.receiverID.toString() }
                            else null
                        else -> null
                    },
                    classIDs = when (current.selectedRecipientType) {
                        RecipientType.ALL_CLASSES, RecipientType.STUDENTS_PARENTS ->
                            if (current.selectedClasses.isNotEmpty())
                                current.selectedClasses.joinToString(",") { it.classID?.toString() ?: "" }
                            else null
                        else -> null
                    },
                    stIDs = when (current.selectedRecipientType) {
                        RecipientType.STUDENTS_PARENTS ->
                            if (current.selectedStudentParents.isNotEmpty())
                                current.selectedStudentParents.joinToString(",") {
                                    "${it.classID}|${it.receiverID}"
                                }
                            else null
                        else -> null
                    },
                    browsedFile = attachment,
                )

                announcementRepository.saveCircular(request).collect { result ->
                    result.onSuccess {
                        _uiState.update { UiState.Success(current.copy(isSaving = false)) }
                        sendEvent(CreateCircularEvent.CircularSaved)
                    }.onFailure { error ->
                        _uiState.update { UiState.Success(current.copy(isSaving = false)) }
                        sendEvent(CreateCircularEvent.ShowMessage(SnackbarMessage(error.errorMessage(), MessageType.ERROR)))
                    }
                }
            } catch (e: Exception) {
                _uiState.update { UiState.Success(current.copy(isSaving = false)) }
                sendEvent(CreateCircularEvent.ShowMessage(SnackbarMessage(e.message ?: "Failed to save", MessageType.ERROR)))
            }
        }
    }
}

@Immutable
data class CreateCircularUiState(
    val title: String = "",
    val description: String = "",
    val releadOnApi: String = "",
    val releadOnDisplay: String = "",
    val selectedRecipientType: RecipientType = RecipientType.ALL,
    val isActive: Boolean = true,
    val mustRead: Boolean = false,
    val selectedFile: SelectedFileDetails? = null,
    val isBoardingSchool: Boolean = false,
    // Form data
    val staffTypes: List<StaffType> = emptyList(),
    val classes: List<Class> = emptyList(),
    // Staff type selection
    val selectedStaffTypes: List<StaffType> = emptyList(),
    val staffContacts: List<StaffContact> = emptyList(),
    val selectedStaff: List<StaffContact> = emptyList(),
    // Classes / Students-Parents selection
    val selectedScholarType: ScholarType = ScholarType.ALL,
    val selectedClasses: List<Class> = emptyList(),
    val studentParentContacts: List<StudentParentContact> = emptyList(),
    val selectedStudentParents: List<StudentParentContact> = emptyList(),
    // UI visibility
    val isRecipientPickerVisible: Boolean = false,
    val isStaffTypePickerVisible: Boolean = false,
    val isStaffPickerVisible: Boolean = false,
    val isClassPickerVisible: Boolean = false,
    val isStudentParentPickerVisible: Boolean = false,
    val isFilePickerVisible: Boolean = false,
    val isDatePickerVisible: Boolean = false,
    val isScholarTypePickerVisible: Boolean = false,
    val isSaving: Boolean = false,
)

sealed interface CreateCircularIntent {
    data object OnBackClicked : CreateCircularIntent
    data class OnTitleChanged(val title: String) : CreateCircularIntent
    data class OnDescriptionChanged(val description: String) : CreateCircularIntent
    data class OnDateSelected(val apiDate: String, val displayDate: String) : CreateCircularIntent
    data class OnStatusChanged(val isActive: Boolean) : CreateCircularIntent
    data class OnMustReadChanged(val mustRead: Boolean) : CreateCircularIntent
    data class OnFileSelected(val file: SelectedFileDetails) : CreateCircularIntent
    data object OnFileRemoved : CreateCircularIntent
    data object OnShowFilePicker : CreateCircularIntent
    data object OnDismissFilePicker : CreateCircularIntent
    data object OnShowRecipientPicker : CreateCircularIntent
    data object OnDismissRecipientPicker : CreateCircularIntent
    data class OnRecipientTypeSelected(val type: RecipientType) : CreateCircularIntent
    data object OnShowStaffTypePicker : CreateCircularIntent
    data object OnDismissStaffTypePicker : CreateCircularIntent
    data class OnStaffTypesSelected(val staffTypes: List<StaffType>) : CreateCircularIntent
    data object OnShowStaffPicker : CreateCircularIntent
    data object OnDismissStaffPicker : CreateCircularIntent
    data class OnStaffSelected(val staff: List<StaffContact>) : CreateCircularIntent
    data class OnScholarTypeSelected(val scholarType: ScholarType) : CreateCircularIntent
    data object OnShowClassPicker : CreateCircularIntent
    data object OnDismissClassPicker : CreateCircularIntent
    data class OnClassesSelected(val classes: List<Class>) : CreateCircularIntent
    data object OnShowStudentParentPicker : CreateCircularIntent
    data object OnDismissStudentParentPicker : CreateCircularIntent
    data class OnStudentParentsSelected(val contacts: List<StudentParentContact>) : CreateCircularIntent
    data object OnShowDatePicker : CreateCircularIntent
    data object OnDismissDatePicker : CreateCircularIntent
    data object OnShowScholarTypePicker : CreateCircularIntent
    data object OnDismissScholarTypePicker : CreateCircularIntent
    data object OnSubmit : CreateCircularIntent
    data object OnRetry : CreateCircularIntent
}

sealed interface CreateCircularEvent {
    data object NavigateBack : CreateCircularEvent
    data object CircularSaved : CreateCircularEvent
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : CreateCircularEvent
}

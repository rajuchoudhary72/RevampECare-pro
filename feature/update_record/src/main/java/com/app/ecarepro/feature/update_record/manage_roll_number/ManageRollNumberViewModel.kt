package com.app.ecarepro.feature.update_record.manage_roll_number

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.Class
import com.app.ecarepro.core.domain.repository.ManageRollNumberRepository
import com.app.ecarepro.core.domain.repository.RollNumberAssignment
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.component.SortConfig
import com.app.ecarepro.designsystem.core.component.SortDirection
import com.app.ecarepro.designsystem.core.component.SortOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageRollNumberViewModel @Inject constructor(
    private val repository: ManageRollNumberRepository,
) : BaseViewModel<ManageRollNumberIntent, ManageRollNumberEvent>() {

    private val _uiState = MutableStateFlow(ManageRollNumberUiState())
    val uiState = _uiState.asStateFlow()

    private val pendingUpdateJobs = mutableMapOf<Int, Job>()

    init {
        loadClasses()
    }

    override fun handleIntent(intent: ManageRollNumberIntent) {
        when (intent) {
            is ManageRollNumberIntent.OnBackClicked -> sendEvent(ManageRollNumberEvent.NavigateBack)
            is ManageRollNumberIntent.SelectClass -> onClassTabSelected(intent.index)
            is ManageRollNumberIntent.OnRollNumberChanged -> onRollNumberChanged(intent.stID, intent.rollNumber)
            is ManageRollNumberIntent.OnAutoAssign -> onAutoAssign()
            is ManageRollNumberIntent.OnSortClick -> _uiState.update { it.copy(showSortSheet = true) }
            is ManageRollNumberIntent.OnSortSelected -> onSortSelected(intent.sortConfig)
            is ManageRollNumberIntent.DismissSortSheet -> _uiState.update { it.copy(showSortSheet = false) }
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
        _uiState.update { it.copy(selectedClassIndex = index, students = emptyList(), originalRollNumbers = emptyMap()) }
        loadStudents(classes[index].id)
    }

    private fun loadStudents(classId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingStudents = true, errorMessage = null) }
            repository.getStudentsForRollNumber(classId).collect { result ->
                result
                    .onSuccess { students ->
                        val uiStudents = students.map {
                            RollNumberStudentUiState(
                                stID = it.stID,
                                name = it.name,
                                photo = it.photo,
                                admissionNumber = it.admissionNumber,
                                houseID = it.houseID,
                                currentRollNumber = it.rollNumber,
                            )
                        }
                        val originals = students.associate { it.stID to it.rollNumber }
                        val withDuplicates = computeDuplicates(uiStudents)
                        _uiState.update {
                            it.copy(
                                isLoadingStudents = false,
                                students = withDuplicates,
                                originalRollNumbers = originals,
                            )
                        }
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(isLoadingStudents = false, errorMessage = error.errorMessage()) }
                    }
            }
        }
    }

    private fun onRollNumberChanged(stID: Int, rollNumber: String) {
        val updated = _uiState.value.students.map { s ->
            if (s.stID == stID) s.copy(currentRollNumber = rollNumber) else s
        }
        _uiState.update { it.copy(students = computeDuplicates(updated)) }

        pendingUpdateJobs[stID]?.cancel()
        if (rollNumber.isBlank()) return
        pendingUpdateJobs[stID] = viewModelScope.launch {
            delay(700)
            val student = _uiState.value.students.find { it.stID == stID } ?: return@launch
            if (student.isDuplicate) return@launch
            val original = _uiState.value.originalRollNumbers[stID] ?: ""
            if (student.currentRollNumber == original) return@launch
            submitSingleStudent(student)
        }
    }

    private fun onAutoAssign() {
        val assigned = _uiState.value.students.mapIndexed { index, s ->
            s.copy(currentRollNumber = (index + 1).toString(), isDuplicate = false)
        }
        _uiState.update { it.copy(students = assigned) }
        submitAll(assigned)
    }

    private fun onSortSelected(sortConfig: SortConfig) {
        val sorted = sortStudents(_uiState.value.students, sortConfig)
        _uiState.update { it.copy(showSortSheet = false, sortConfig = sortConfig, students = computeDuplicates(sorted)) }
    }

    private fun submitSingleStudent(student: RollNumberStudentUiState) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            val assignments = listOf(RollNumberAssignment(stID = student.stID, houseID = student.houseID, rollNumber = student.currentRollNumber))
            repository.assignRollNumbers(assignments).collect { result ->
                result
                    .onSuccess {
                        val newOriginals = _uiState.value.originalRollNumbers.toMutableMap().apply {
                            put(student.stID, student.currentRollNumber)
                        }
                        _uiState.update { it.copy(isSubmitting = false, originalRollNumbers = newOriginals) }
                        sendEvent(ManageRollNumberEvent.ShowMessage(SnackbarMessage("Roll number updated", MessageType.SUCCESS)))
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(isSubmitting = false) }
                        sendEvent(ManageRollNumberEvent.ShowMessage(SnackbarMessage(error.errorMessage(), MessageType.ERROR)))
                    }
            }
        }
    }

    private fun submitAll(students: List<RollNumberStudentUiState>) {
        val originals = _uiState.value.originalRollNumbers
        val changed = students.filter { s ->
            s.currentRollNumber != (originals[s.stID] ?: "")
        }
        if (changed.isEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            val assignments = changed.map { RollNumberAssignment(stID = it.stID, houseID = it.houseID, rollNumber = it.currentRollNumber) }
            repository.assignRollNumbers(assignments).collect { result ->
                result
                    .onSuccess { message ->
                        val newOriginals = originals.toMutableMap().apply {
                            changed.forEach { put(it.stID, it.currentRollNumber) }
                        }
                        _uiState.update { it.copy(isSubmitting = false, originalRollNumbers = newOriginals) }
                        sendEvent(ManageRollNumberEvent.ShowMessage(SnackbarMessage(message.ifBlank { "Roll numbers saved successfully" }, MessageType.SUCCESS)))
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(isSubmitting = false) }
                        sendEvent(ManageRollNumberEvent.ShowMessage(SnackbarMessage(error.errorMessage(), MessageType.ERROR)))
                    }
            }
        }
    }

    private fun computeDuplicates(students: List<RollNumberStudentUiState>): List<RollNumberStudentUiState> {
        val rollCounts = students
            .filter { it.currentRollNumber.isNotBlank() }
            .groupingBy { it.currentRollNumber }
            .eachCount()
        return students.map { s ->
            s.copy(isDuplicate = s.currentRollNumber.isNotBlank() && (rollCounts[s.currentRollNumber] ?: 0) > 1)
        }
    }

    private fun sortStudents(students: List<RollNumberStudentUiState>, sortConfig: SortConfig): List<RollNumberStudentUiState> {
        val comparator: Comparator<RollNumberStudentUiState> = when (sortConfig.option) {
            SortOption.ROLL_NUMBER -> compareBy { it.currentRollNumber.toIntOrNull() ?: Int.MAX_VALUE }
            SortOption.ADMISSION_NUMBER -> compareBy { it.admissionNumber }
            SortOption.NAME -> compareBy { it.name }
            else -> compareBy { it.currentRollNumber.toIntOrNull() ?: Int.MAX_VALUE }
        }
        return if (sortConfig.direction == SortDirection.ASCENDING) {
            students.sortedWith(comparator)
        } else {
            students.sortedWith(comparator.reversed())
        }
    }
}

@Immutable
data class ManageRollNumberUiState(
    val classes: List<Class> = emptyList(),
    val selectedClassIndex: Int = 0,
    val isLoadingClasses: Boolean = false,
    val isLoadingStudents: Boolean = false,
    val isSubmitting: Boolean = false,
    val students: List<RollNumberStudentUiState> = emptyList(),
    val originalRollNumbers: Map<Int, String> = emptyMap(),
    val sortConfig: SortConfig = SortConfig(),
    val showSortSheet: Boolean = false,
    val errorMessage: String? = null,
)

@Immutable
data class RollNumberStudentUiState(
    val stID: Int,
    val name: String,
    val photo: String,
    val admissionNumber: String,
    val houseID: Int,
    val currentRollNumber: String,
    val isDuplicate: Boolean = false,
)

sealed interface ManageRollNumberIntent {
    data object OnBackClicked : ManageRollNumberIntent
    data class SelectClass(val index: Int) : ManageRollNumberIntent
    data class OnRollNumberChanged(val stID: Int, val rollNumber: String) : ManageRollNumberIntent
    data object OnAutoAssign : ManageRollNumberIntent
    data object OnSortClick : ManageRollNumberIntent
    data class OnSortSelected(val sortConfig: SortConfig) : ManageRollNumberIntent
    data object DismissSortSheet : ManageRollNumberIntent
}

sealed interface ManageRollNumberEvent {
    data object NavigateBack : ManageRollNumberEvent
    data class ShowMessage(val message: SnackbarMessage) : ManageRollNumberEvent
}

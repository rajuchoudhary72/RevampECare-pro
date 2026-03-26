package com.app.ecarepro.feature.update_record.update_house

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.Class
import com.app.ecarepro.core.domain.model.HouseItem
import com.app.ecarepro.core.domain.repository.HouseAssignment
import com.app.ecarepro.core.domain.repository.UpdateHouseRepository
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.component.SortConfig
import com.app.ecarepro.designsystem.core.component.SortDirection
import com.app.ecarepro.designsystem.core.component.SortOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateHouseViewModel @Inject constructor(
    private val repository: UpdateHouseRepository,
) : BaseViewModel<UpdateHouseIntent, UpdateHouseEvent>() {

    private val _uiState = MutableStateFlow(UpdateHouseUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadClasses()
    }

    override fun handleIntent(intent: UpdateHouseIntent) {
        when (intent) {
            is UpdateHouseIntent.OnBackClicked -> sendEvent(UpdateHouseEvent.NavigateBack)
            is UpdateHouseIntent.SelectClass -> onClassTabSelected(intent.index)
            is UpdateHouseIntent.OnHouseButtonClicked -> _uiState.update { it.copy(houseSheetForStID = intent.stID) }
            is UpdateHouseIntent.OnHouseSelected -> onHouseSelected(intent.stID, intent.houseID)
            is UpdateHouseIntent.DismissHouseSheet -> _uiState.update { it.copy(houseSheetForStID = null) }
            is UpdateHouseIntent.OnSearchQueryChanged -> _uiState.update { it.copy(searchQuery = intent.query) }
            is UpdateHouseIntent.OnSortClick -> _uiState.update { it.copy(showSortSheet = true) }
            is UpdateHouseIntent.OnSortSelected -> onSortSelected(intent.sortConfig)
            is UpdateHouseIntent.DismissSortSheet -> _uiState.update { it.copy(showSortSheet = false) }
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
        _uiState.update {
            it.copy(
                selectedClassIndex = index,
                students = emptyList(),
                originalHouses = emptyMap(),
                houses = emptyList(),
                searchQuery = "",
            )
        }
        loadStudents(classes[index].id)
    }

    private fun loadStudents(classId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingStudents = true, errorMessage = null) }
            repository.getStudentsForHouse(classId).collect { result ->
                result
                    .onSuccess { data ->
                        val uiStudents = data.students.map {
                            HouseStudentUiState(
                                stID = it.stID,
                                name = it.name,
                                photo = it.photo,
                                admissionNumber = it.admissionNumber,
                                rollNumber = it.rollNumber,
                                currentHouseID = it.houseID,
                                currentHouseName = it.houseName,
                            )
                        }
                        val originals = data.students.associate { it.stID to it.houseID }
                        _uiState.update {
                            it.copy(
                                isLoadingStudents = false,
                                students = uiStudents,
                                originalHouses = originals,
                                houses = data.houses,
                            )
                        }
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(isLoadingStudents = false, errorMessage = error.errorMessage()) }
                    }
            }
        }
    }

    private fun onHouseSelected(stID: Int, houseID: Int) {
        val house = _uiState.value.houses.find { it.houseID == houseID } ?: return
        val student = _uiState.value.students.find { it.stID == stID } ?: return
        val updated = _uiState.value.students.map { s ->
            if (s.stID == stID) s.copy(currentHouseID = houseID, currentHouseName = house.houseName) else s
        }
        _uiState.update { it.copy(students = updated, houseSheetForStID = null) }
        if (houseID == (_uiState.value.originalHouses[stID] ?: 0)) return
        submitSingleStudent(student.copy(currentHouseID = houseID))
    }

    private fun onSortSelected(sortConfig: SortConfig) {
        val sorted = sortStudents(_uiState.value.students, sortConfig)
        _uiState.update { it.copy(showSortSheet = false, sortConfig = sortConfig, students = sorted) }
    }

    private fun submitSingleStudent(student: HouseStudentUiState) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            val assignment = listOf(HouseAssignment(stID = student.stID, houseID = student.currentHouseID, rollNumber = student.rollNumber))
            repository.assignHouses(assignment).collect { result ->
                result
                    .onSuccess { message ->
                        val newOriginals = _uiState.value.originalHouses.toMutableMap().apply {
                            put(student.stID, student.currentHouseID)
                        }
                        _uiState.update { it.copy(isSubmitting = false, originalHouses = newOriginals) }
                        sendEvent(UpdateHouseEvent.ShowMessage(SnackbarMessage(message.ifBlank { "House updated successfully" }, MessageType.SUCCESS)))
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(isSubmitting = false) }
                        sendEvent(UpdateHouseEvent.ShowMessage(SnackbarMessage(error.errorMessage(), MessageType.ERROR)))
                    }
            }
        }
    }

    private fun sortStudents(students: List<HouseStudentUiState>, sortConfig: SortConfig): List<HouseStudentUiState> {
        val comparator: Comparator<HouseStudentUiState> = when (sortConfig.option) {
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
data class UpdateHouseUiState(
    val classes: List<Class> = emptyList(),
    val selectedClassIndex: Int = 0,
    val isLoadingClasses: Boolean = false,
    val isLoadingStudents: Boolean = false,
    val isSubmitting: Boolean = false,
    val students: List<HouseStudentUiState> = emptyList(),
    val originalHouses: Map<Int, Int> = emptyMap(),
    val houses: List<HouseItem> = emptyList(),
    val houseSheetForStID: Int? = null,
    val sortConfig: SortConfig = SortConfig(option = SortOption.NAME),
    val showSortSheet: Boolean = false,
    val searchQuery: String = "",
    val errorMessage: String? = null,
)

@Immutable
data class HouseStudentUiState(
    val stID: Int,
    val name: String,
    val photo: String,
    val admissionNumber: String,
    val rollNumber: String,
    val currentHouseID: Int,
    val currentHouseName: String,
)

sealed interface UpdateHouseIntent {
    data object OnBackClicked : UpdateHouseIntent
    data class SelectClass(val index: Int) : UpdateHouseIntent
    data class OnHouseButtonClicked(val stID: Int) : UpdateHouseIntent
    data class OnHouseSelected(val stID: Int, val houseID: Int) : UpdateHouseIntent
    data object DismissHouseSheet : UpdateHouseIntent
    data class OnSearchQueryChanged(val query: String) : UpdateHouseIntent
    data object OnSortClick : UpdateHouseIntent
    data class OnSortSelected(val sortConfig: SortConfig) : UpdateHouseIntent
    data object DismissSortSheet : UpdateHouseIntent
}

sealed interface UpdateHouseEvent {
    data object NavigateBack : UpdateHouseEvent
    data class ShowMessage(val message: SnackbarMessage) : UpdateHouseEvent
}

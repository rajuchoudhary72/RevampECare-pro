package com.app.ecarepro.feature.transport_att.screens

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.transport.TransportStudent
import com.app.ecarepro.core.domain.repository.TransportRepository
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class OutPassViewModel @Inject constructor(
    private val transportRepository: TransportRepository,
) : BaseViewModel<OutPassIntent, OutPassEvent>() {

    private val _uiState: MutableStateFlow<UiState<OutPassUiState>> =
        MutableStateFlow(UiState.Success(OutPassUiState()))
    val uiState = _uiState.asStateFlow()

    override fun handleIntent(intent: OutPassIntent) {
        when (intent) {
            is OutPassIntent.OnBackClicked -> sendEvent(OutPassEvent.NavigateBack)
            is OutPassIntent.OnDateSelected -> onDateSelected(intent.dateMillis)
            is OutPassIntent.ShowDatePicker -> showDatePicker()
            is OutPassIntent.DismissDatePicker -> dismissDatePicker()
            is OutPassIntent.OnDropStudentClicked -> onDropStudentClicked(intent.studentIndex)
            is OutPassIntent.OnConfirmDropStudent -> confirmDropStudent(intent.studentIndex)
            is OutPassIntent.OnDismissDropConfirm -> dismissDropConfirm()
        }
    }

    private fun onDateSelected(dateMillis: Long) {
        val displayDate = formatDateForDisplay(dateMillis)
        val systemDate = formatDateForSystem(dateMillis)
        updateState {
            it.copy(
                selectedDate = displayDate,
                selectedDateSystem = systemDate,
                isDatePickerVisible = false,
            )
        }
        fetchOutPassReport(systemDate)
    }

    private fun fetchOutPassReport(attDate: String) {
        viewModelScope.launch {
            transportRepository.getOutPassReport(attDate)
                .onStart { updateState { it.copy(isLoading = true) } }
                .collect { result ->
                    result
                        .onSuccess { students ->
                            updateState {
                                it.copy(
                                    isLoading = false,
                                    studentList = students,
                                    showReport = students.isNotEmpty(),
                                )
                            }
                        }
                        .onFailure { error ->
                            updateState { it.copy(isLoading = false) }
                            sendEvent(OutPassEvent.ShowMessage(SnackbarMessage(error.errorMessage(), MessageType.ERROR)))
                        }
                }
        }
    }

    private fun onDropStudentClicked(studentIndex: Int) {
        updateState { it.copy(droppingStudentIndex = studentIndex, isDropConfirmVisible = true) }
    }

    private fun confirmDropStudent(studentIndex: Int) {
        val state = currentState() ?: return
        val students = state.studentList
        if (studentIndex !in students.indices) return
        val student = students[studentIndex]

        viewModelScope.launch {
            transportRepository.dropToStudent(
                stID = student.stID,
                attDate = state.selectedDateSystem,
                hasDropped = true,
            )
                .onStart {
                    updateState { it.copy(isDropConfirmVisible = false, isLoading = true) }
                }
                .collect { result ->
                    result
                        .onSuccess {
                            val updatedStudents = students.toMutableList()
                            updatedStudents[studentIndex] = student.copy(isDropped = true)
                            updateState {
                                it.copy(
                                    isLoading = false,
                                    studentList = updatedStudents,
                                    droppingStudentIndex = null,
                                )
                            }
                            sendEvent(OutPassEvent.ShowMessage(SnackbarMessage("Updated successfully", MessageType.SUCCESS)))
                        }
                        .onFailure { error ->
                            updateState { it.copy(isLoading = false, droppingStudentIndex = null) }
                            sendEvent(OutPassEvent.ShowMessage(SnackbarMessage(error.errorMessage(), MessageType.ERROR)))
                        }
                }
        }
    }

    private fun dismissDropConfirm() {
        updateState { it.copy(isDropConfirmVisible = false, droppingStudentIndex = null) }
    }

    private fun showDatePicker() {
        updateState { it.copy(isDatePickerVisible = true) }
    }

    private fun dismissDatePicker() {
        updateState { it.copy(isDatePickerVisible = false) }
    }

    private fun currentState(): OutPassUiState? {
        return (_uiState.value as? UiState.Success)?.data
    }

    private fun updateState(update: (OutPassUiState) -> OutPassUiState) {
        val current = currentState() ?: return
        _uiState.update { UiState.Success(update(current)) }
    }

    private fun formatDateForDisplay(millis: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date(millis))
    }

    private fun formatDateForSystem(millis: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date(millis))
    }
}

@Immutable
data class OutPassUiState(
    val selectedDate: String = "",
    val selectedDateSystem: String = "",
    val studentList: List<TransportStudent> = emptyList(),
    val isLoading: Boolean = false,
    val showReport: Boolean = false,
    val isDatePickerVisible: Boolean = false,
    val isDropConfirmVisible: Boolean = false,
    val droppingStudentIndex: Int? = null,
)

sealed interface OutPassIntent {
    data object OnBackClicked : OutPassIntent
    data class OnDateSelected(val dateMillis: Long) : OutPassIntent
    data object ShowDatePicker : OutPassIntent
    data object DismissDatePicker : OutPassIntent
    data class OnDropStudentClicked(val studentIndex: Int) : OutPassIntent
    data class OnConfirmDropStudent(val studentIndex: Int) : OutPassIntent
    data object OnDismissDropConfirm : OutPassIntent
}

sealed interface OutPassEvent {
    data object NavigateBack : OutPassEvent
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : OutPassEvent
}

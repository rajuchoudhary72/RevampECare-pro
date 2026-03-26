package com.app.ecarepro.feature.leave

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.LeaveType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApplyLeaveViewModel @Inject constructor(
    // TODO: Inject repository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ApplyLeaveUiState())
    val uiState: StateFlow<ApplyLeaveUiState> = _uiState.asStateFlow()

    init {
        loadLeaveSettings()
    }

    fun handleIntent(intent: ApplyLeaveIntent) {
        when (intent) {
            is ApplyLeaveIntent.OnLeaveTypeSelected -> onLeaveTypeSelected(intent.leaveType)
            is ApplyLeaveIntent.OnStartDateSelected -> onStartDateSelected(intent.date)
            is ApplyLeaveIntent.OnEndDateSelected -> onEndDateSelected(intent.date)
            is ApplyLeaveIntent.OnReasonChanged -> onReasonChanged(intent.reason)
            is ApplyLeaveIntent.OnHalfDayChanged -> onHalfDayChanged(intent.isHalfDay)
            is ApplyLeaveIntent.OnAttachmentSelected -> onAttachmentSelected(intent.attachment)
            is ApplyLeaveIntent.OnSubmit -> submitLeaveApplication()
        }
    }

    private fun loadLeaveSettings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // TODO: Load leave settings from repository
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun onLeaveTypeSelected(leaveType: LeaveType) {
        _uiState.update { it.copy(selectedLeaveType = leaveType) }
        calculateDuration()
    }

    private fun onStartDateSelected(date: String) {
        _uiState.update { it.copy(startDate = date) }
        calculateDuration()
    }

    private fun onEndDateSelected(date: String) {
        _uiState.update { it.copy(endDate = date) }
        calculateDuration()
    }

    private fun onReasonChanged(reason: String) {
        _uiState.update { it.copy(reason = reason) }
    }

    private fun onHalfDayChanged(isHalfDay: Boolean) {
        _uiState.update { it.copy(isHalfDay = isHalfDay) }
        calculateDuration()
    }

    private fun onAttachmentSelected(attachment: String) {
        _uiState.update { it.copy(attachmentPath = attachment) }
    }

    private fun calculateDuration() {
        // TODO: Implement duration calculation logic
        _uiState.update { it.copy(duration = 1.0) }
    }

    private fun submitLeaveApplication() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

            // TODO: Validate and submit leave application
            // For now, just simulate success
            _uiState.update {
                it.copy(
                    isSubmitting = false,
                    isSuccess = true
                )
            }
        }
    }
}

data class ApplyLeaveUiState(
    val leaveTypes: List<LeaveType> = emptyList(),
    val selectedLeaveType: LeaveType? = null,
    val startDate: String = "",
    val endDate: String = "",
    val duration: Double = 0.0,
    val reason: String = "",
    val isHalfDay: Boolean = false,
    val attachmentPath: String? = null,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

sealed class ApplyLeaveIntent {
    data class OnLeaveTypeSelected(val leaveType: LeaveType) : ApplyLeaveIntent()
    data class OnStartDateSelected(val date: String) : ApplyLeaveIntent()
    data class OnEndDateSelected(val date: String) : ApplyLeaveIntent()
    data class OnReasonChanged(val reason: String) : ApplyLeaveIntent()
    data class OnHalfDayChanged(val isHalfDay: Boolean) : ApplyLeaveIntent()
    data class OnAttachmentSelected(val attachment: String) : ApplyLeaveIntent()
    data object OnSubmit : ApplyLeaveIntent()
}

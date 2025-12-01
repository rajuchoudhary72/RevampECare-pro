package com.app.ecarepro.feature.leave.leave_report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.ApplType
import com.app.ecarepro.core.domain.model.LeaveAction
import com.app.ecarepro.core.domain.model.LeaveActionRequest
import com.app.ecarepro.core.domain.model.LeaveReportItem
import com.app.ecarepro.core.domain.model.LeaveReportTab
import com.app.ecarepro.core.domain.model.ReportingManager
import com.app.ecarepro.core.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LeaveReportUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedTab: LeaveReportTab = LeaveReportTab.PENDING,
    val applType: ApplType = ApplType.STAFF,
    val leaveItems: List<LeaveReportItem> = emptyList(),
    val reportingManagers: List<ReportingManager> = emptyList(),
    val isRejectionReasonRequired: Boolean = false,
    val selectedLeaveIds: Set<Int> = emptySet(),
    val showAttendancePercentage: Boolean = false,
    val showApproveBottomSheet: Boolean = false,
    val showRejectBottomSheet: Boolean = false,
    val showForwardBottomSheet: Boolean = false,
    val showCancelBottomSheet: Boolean = false,
    val selectedLeaveForAction: LeaveReportItem? = null,
    val isSubmitting: Boolean = false
)

sealed class LeaveReportIntent {
    data class OnTabChanged(val tab: LeaveReportTab) : LeaveReportIntent()
    data class OnApplTypeChanged(val applType: ApplType) : LeaveReportIntent()
    data class OnLeaveSelected(val leaveId: Int, val isSelected: Boolean) : LeaveReportIntent()
    data object OnSelectAllToggled : LeaveReportIntent()
    data object OnAttendancePercentageToggled : LeaveReportIntent()
    data class OnApproveClicked(val leave: LeaveReportItem) : LeaveReportIntent()
    data class OnRejectClicked(val leave: LeaveReportItem) : LeaveReportIntent()
    data class OnForwardClicked(val leave: LeaveReportItem) : LeaveReportIntent()
    data class OnCancelClicked(val leave: LeaveReportItem) : LeaveReportIntent()
    data object OnApproveAllClicked : LeaveReportIntent()
    data object OnRejectAllClicked : LeaveReportIntent()
    data object OnBottomSheetDismissed : LeaveReportIntent()
    data class OnApproveConfirmed(val startDate: String?, val endDate: String?) : LeaveReportIntent()
    data class OnRejectConfirmed(val reason: String?) : LeaveReportIntent()
    data class OnForwardConfirmed(val managerId: Int) : LeaveReportIntent()
    data object OnCancelConfirmed : LeaveReportIntent()
    data object OnRefresh : LeaveReportIntent()
}

@HiltViewModel
class LeaveReportViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeaveReportUiState())
    val uiState: StateFlow<LeaveReportUiState> = _uiState.asStateFlow()

    init {
        loadLeaveReports()
    }

    fun handleIntent(intent: LeaveReportIntent) {
        when (intent) {
            is LeaveReportIntent.OnTabChanged -> {
                _uiState.update { it.copy(selectedTab = intent.tab, selectedLeaveIds = emptySet()) }
                loadLeaveReports()
            }
            is LeaveReportIntent.OnApplTypeChanged -> {
                _uiState.update {
                    it.copy(
                        applType = intent.applType,
                        selectedTab = LeaveReportTab.PENDING,
                        selectedLeaveIds = emptySet()
                    )
                }
                loadLeaveReports()
            }
            is LeaveReportIntent.OnLeaveSelected -> {
                _uiState.update { state ->
                    val updatedSelection = if (intent.isSelected) {
                        state.selectedLeaveIds + intent.leaveId
                    } else {
                        state.selectedLeaveIds - intent.leaveId
                    }
                    state.copy(selectedLeaveIds = updatedSelection)
                }
            }
            is LeaveReportIntent.OnSelectAllToggled -> {
                _uiState.update { state ->
                    val allIds = state.leaveItems.map { it.lvID }.toSet()
                    val newSelection = if (state.selectedLeaveIds.size == allIds.size) {
                        emptySet()
                    } else {
                        allIds
                    }
                    state.copy(selectedLeaveIds = newSelection)
                }
            }
            is LeaveReportIntent.OnAttendancePercentageToggled -> {
                _uiState.update { it.copy(showAttendancePercentage = !it.showAttendancePercentage) }
                if (_uiState.value.applType == ApplType.STUDENT) {
                    loadLeaveReports()
                }
            }
            is LeaveReportIntent.OnApproveClicked -> {
                _uiState.update {
                    it.copy(
                        showApproveBottomSheet = true,
                        selectedLeaveForAction = intent.leave
                    )
                }
            }
            is LeaveReportIntent.OnRejectClicked -> {
                _uiState.update {
                    it.copy(
                        showRejectBottomSheet = true,
                        selectedLeaveForAction = intent.leave
                    )
                }
            }
            is LeaveReportIntent.OnForwardClicked -> {
                _uiState.update {
                    it.copy(
                        showForwardBottomSheet = true,
                        selectedLeaveForAction = intent.leave
                    )
                }
            }
            is LeaveReportIntent.OnCancelClicked -> {
                _uiState.update {
                    it.copy(
                        showCancelBottomSheet = true,
                        selectedLeaveForAction = intent.leave
                    )
                }
            }
            is LeaveReportIntent.OnApproveAllClicked -> {
                val selectedLeaves = _uiState.value.leaveItems.filter {
                    it.lvID in _uiState.value.selectedLeaveIds
                }
                if (selectedLeaves.isNotEmpty()) {
                    // For bulk approval, use first item for bottom sheet
                    _uiState.update {
                        it.copy(
                            showApproveBottomSheet = true,
                            selectedLeaveForAction = selectedLeaves.first()
                        )
                    }
                }
            }
            is LeaveReportIntent.OnRejectAllClicked -> {
                val selectedLeaves = _uiState.value.leaveItems.filter {
                    it.lvID in _uiState.value.selectedLeaveIds
                }
                if (selectedLeaves.isNotEmpty()) {
                    _uiState.update {
                        it.copy(
                            showRejectBottomSheet = true,
                            selectedLeaveForAction = selectedLeaves.first()
                        )
                    }
                }
            }
            is LeaveReportIntent.OnBottomSheetDismissed -> {
                _uiState.update {
                    it.copy(
                        showApproveBottomSheet = false,
                        showRejectBottomSheet = false,
                        showForwardBottomSheet = false,
                        showCancelBottomSheet = false,
                        selectedLeaveForAction = null
                    )
                }
            }
            is LeaveReportIntent.OnApproveConfirmed -> {
                approveLeaves(intent.startDate, intent.endDate)
            }
            is LeaveReportIntent.OnRejectConfirmed -> {
                rejectLeaves(intent.reason)
            }
            is LeaveReportIntent.OnForwardConfirmed -> {
                forwardLeave(intent.managerId)
            }
            is LeaveReportIntent.OnCancelConfirmed -> {
                cancelLeave()
            }
            is LeaveReportIntent.OnRefresh -> {
                loadLeaveReports()
            }
        }
    }

    private fun loadLeaveReports() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            userRepository.getLeaveReport(
                status = _uiState.value.selectedTab.statusValue,
                order = if (_uiState.value.applType == ApplType.STUDENT) 2 else 1,
                applType = _uiState.value.applType.value,
                page = 1,
                showAttendance = _uiState.value.showAttendancePercentage,
                duration = 0
            ).collect { result ->
                result
                    .onSuccess { response ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                leaveItems = response.dtl,
                                reportingManagers = response.myReporting,
                                isRejectionReasonRequired = response.isRejectionReasonReq,
                                errorMessage = null
                            )
                        }
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = error.message ?: "Failed to load leave reports"
                            )
                        }
                    }
            }
        }
    }

    private fun approveLeaves(startDate: String?, endDate: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }

            val leaveIds = if (_uiState.value.selectedLeaveIds.isNotEmpty()) {
                _uiState.value.selectedLeaveIds.toList()
            } else {
                listOfNotNull(_uiState.value.selectedLeaveForAction?.lvID)
            }

            val isPartialApproved = startDate != null && endDate != null

            leaveIds.forEach { leaveId ->
                userRepository.leaveAction(
                    LeaveActionRequest(
                        applType = _uiState.value.applType.value,
                        lvID = leaveId,
                        action = LeaveAction.APPROVE,
                        isPartialApproved = isPartialApproved,
                        partialFromDate = startDate,
                        partialTillDate = endDate
                    )
                ).collect { result ->
                    result
                        .onSuccess {
                            _uiState.update {
                                it.copy(
                                    isSubmitting = false,
                                    showApproveBottomSheet = false,
                                    selectedLeaveForAction = null,
                                    selectedLeaveIds = emptySet()
                                )
                            }
                            loadLeaveReports()
                        }
                        .onFailure { error ->
                            _uiState.update {
                                it.copy(
                                    isSubmitting = false,
                                    errorMessage = error.message ?: "Failed to approve leave"
                                )
                            }
                        }
                }
            }
        }
    }

    private fun rejectLeaves(reason: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }

            val leaveIds = if (_uiState.value.selectedLeaveIds.isNotEmpty()) {
                _uiState.value.selectedLeaveIds.toList()
            } else {
                listOfNotNull(_uiState.value.selectedLeaveForAction?.lvID)
            }

            leaveIds.forEach { leaveId ->
                userRepository.leaveAction(
                    LeaveActionRequest(
                        applType = _uiState.value.applType.value,
                        lvID = leaveId,
                        action = LeaveAction.REJECT,
                        rejectionReason = reason
                    )
                ).collect { result ->
                    result
                        .onSuccess {
                            _uiState.update {
                                it.copy(
                                    isSubmitting = false,
                                    showRejectBottomSheet = false,
                                    selectedLeaveForAction = null,
                                    selectedLeaveIds = emptySet()
                                )
                            }
                            loadLeaveReports()
                        }
                        .onFailure { error ->
                            _uiState.update {
                                it.copy(
                                    isSubmitting = false,
                                    errorMessage = error.message ?: "Failed to reject leave"
                                )
                            }
                        }
                }
            }
        }
    }

    private fun forwardLeave(managerId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }

            val leaveId = _uiState.value.selectedLeaveForAction?.lvID ?: return@launch

            userRepository.leaveAction(
                LeaveActionRequest(
                    applType = _uiState.value.applType.value,
                    lvID = leaveId,
                    action = LeaveAction.FORWARD,
                    forwardedTo = managerId
                )
            ).collect { result ->
                result
                    .onSuccess {
                        _uiState.update {
                            it.copy(
                                isSubmitting = false,
                                showForwardBottomSheet = false,
                                selectedLeaveForAction = null
                            )
                        }
                        loadLeaveReports()
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(
                                isSubmitting = false,
                                errorMessage = error.message ?: "Failed to forward leave"
                            )
                        }
                    }
            }
        }
    }

    private fun cancelLeave() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }

            val leaveId = _uiState.value.selectedLeaveForAction?.lvID ?: return@launch

            userRepository.leaveAction(
                LeaveActionRequest(
                    applType = _uiState.value.applType.value,
                    lvID = leaveId,
                    action = LeaveAction.CANCEL
                )
            ).collect { result ->
                result
                    .onSuccess {
                        _uiState.update {
                            it.copy(
                                isSubmitting = false,
                                showCancelBottomSheet = false,
                                selectedLeaveForAction = null
                            )
                        }
                        loadLeaveReports()
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(
                                isSubmitting = false,
                                errorMessage = error.message ?: "Failed to cancel leave"
                            )
                        }
                    }
            }
        }
    }
}

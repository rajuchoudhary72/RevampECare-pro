package com.app.ecarepro.feature.leave

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.LeaveApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaveListViewModel @Inject constructor(
    // TODO: Inject repository when available
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeaveListUiState())
    val uiState: StateFlow<LeaveListUiState> = _uiState.asStateFlow()

    init {
        loadLeaveHistory()
    }

    fun handleIntent(intent: LeaveListIntent) {
        when (intent) {
            is LeaveListIntent.OnTabChanged -> onTabChanged(intent.tab)
            is LeaveListIntent.OnRefresh -> loadLeaveHistory()
            is LeaveListIntent.OnDeleteLeave -> deleteLeave(intent.leaveId)
            is LeaveListIntent.OnLeaveClicked -> onLeaveClicked(intent.leaveId)
            is LeaveListIntent.OnApplyLeaveClicked -> onApplyLeaveClicked()
        }
    }

    private fun onTabChanged(tab: LeaveTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    private fun loadLeaveHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // TODO: Call repository to fetch leave history
            // For now, using mock data
            val mockData = getMockLeaveData()

            _uiState.update {
                it.copy(
                    leaveApplications = mockData,
                    isLoading = false
                )
            }
        }
    }

    private fun deleteLeave(leaveId: Int) {
        viewModelScope.launch {
            // TODO: Call repository to delete leave
            _uiState.update {
                it.copy(
                    leaveApplications = it.leaveApplications.filter { leave -> leave.lvID != leaveId }
                )
            }
        }
    }

    private fun onLeaveClicked(leaveId: Int) {
        // TODO: Navigate to leave details
    }

    private fun onApplyLeaveClicked() {
        // TODO: Navigate to apply leave screen
    }

    private fun getMockLeaveData(): List<LeaveApplication> {
        // TODO: Replace with actual API data
        return emptyList()
    }
}

data class LeaveListUiState(
    val selectedTab: LeaveTab = LeaveTab.ALL,
    val leaveApplications: List<LeaveApplication> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class LeaveListIntent {
    data class OnTabChanged(val tab: LeaveTab) : LeaveListIntent()
    data object OnRefresh : LeaveListIntent()
    data class OnDeleteLeave(val leaveId: Int) : LeaveListIntent()
    data class OnLeaveClicked(val leaveId: Int) : LeaveListIntent()
    data object OnApplyLeaveClicked : LeaveListIntent()
}

enum class LeaveTab(val displayName: String) {
    ALL("All"),
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    CANCELLED("Cancelled")
}

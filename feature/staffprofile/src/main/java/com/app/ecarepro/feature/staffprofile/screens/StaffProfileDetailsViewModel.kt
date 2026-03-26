package com.app.ecarepro.feature.staffprofile.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.StaffAttendanceDetail
import com.app.ecarepro.core.domain.model.StaffPersonalDetailSection
import com.app.ecarepro.core.domain.model.StaffProfileDetail
import com.app.ecarepro.core.domain.model.StaffProfileSection
import com.app.ecarepro.core.domain.model.StaffSectionControl
import com.app.ecarepro.core.domain.model.SalaryStructure
import com.app.ecarepro.core.domain.model.TimetableSummary
import com.app.ecarepro.core.domain.repository.StaffProfileRepository
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SalaryCardSection { PAYSLIP, EARNINGS, DEDUCTIONS }

data class StaffProfileDetailsUiState(
    val profileData: StaffProfileDetail? = null,
    val attendanceDetail: StaffAttendanceDetail? = null,
    val timetableSummary: TimetableSummary? = null,
    val salaryStructure: SalaryStructure? = null,
    val visibleTabs: List<StaffProfileSection> = emptyList(),
    val selectedTabIndex: Int = 0,
    val expandedSections: Set<StaffPersonalDetailSection> = emptySet(),
    val isAttendanceExpanded: Boolean = true,
    val isTimetableExpanded: Boolean = true,
    // Only one salary card open at a time (accordion) – Payslip expanded by default
    val expandedSalaryCards: Set<SalaryCardSection> = setOf(SalaryCardSection.PAYSLIP)
)

sealed interface StaffProfileDetailsIntent {
    data object OnBackClicked : StaffProfileDetailsIntent
    data class OnTabSelected(val index: Int) : StaffProfileDetailsIntent
    data class OnToggleSection(val section: StaffPersonalDetailSection) : StaffProfileDetailsIntent
    data object OnToggleAttendance : StaffProfileDetailsIntent
    data object OnToggleTimetable : StaffProfileDetailsIntent
    data class OnToggleSalaryCard(val section: SalaryCardSection) : StaffProfileDetailsIntent
}

sealed interface StaffProfileDetailsEvent {
    data object NavigateBack : StaffProfileDetailsEvent
    data class ShowMessage(val message: SnackbarMessage) : StaffProfileDetailsEvent
}

@HiltViewModel
class StaffProfileDetailsViewModel @Inject constructor(
    private val repository: StaffProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<StaffProfileDetailsUiState>>(UiState.Loading)
    val uiState: StateFlow<UiState<StaffProfileDetailsUiState>> = _uiState.asStateFlow()

    private val _screenEvent = MutableSharedFlow<StaffProfileDetailsEvent>()
    val screenEvent = _screenEvent.asSharedFlow()

    fun loadStaffProfile(staffId: Int, forceRefresh: Boolean = false) {
        // Skip loading if data is already loaded and not forcing refresh
        if (!forceRefresh && _uiState.value is UiState.Success) {
            return
        }

        viewModelScope.launch {
            _uiState.value = UiState.Loading

            try {
                val response = repository.getStaffProfileDetails(staffId)

                if (response.errorCode == 0) {
                    val visibleTabs = getVisibleTabs(response.sectionControl)

                    _uiState.value = UiState.Success(
                        StaffProfileDetailsUiState(
                            profileData = response.details,
                            attendanceDetail = response.attendanceDTL,
                            timetableSummary = response.timetableSummary,
                            salaryStructure = response.salaryStructure,
                            visibleTabs = visibleTabs,
                            selectedTabIndex = 0
                        )
                    )
                } else {
                    _uiState.value = UiState.Error(response.message ?: "Failed to load profile")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun handleIntent(intent: StaffProfileDetailsIntent) {
        when (intent) {
            is StaffProfileDetailsIntent.OnBackClicked -> {
                viewModelScope.launch {
                    _screenEvent.emit(StaffProfileDetailsEvent.NavigateBack)
                }
            }

            is StaffProfileDetailsIntent.OnTabSelected -> {
                updateUiState { copy(selectedTabIndex = intent.index) }
            }

            is StaffProfileDetailsIntent.OnToggleSection -> {
                updateUiState {
                    val newExpandedSections = if (expandedSections.contains(intent.section)) {
                        // Close the section if it's already open
                        emptySet()
                    } else {
                        // Open only this section, close all others (accordion behavior)
                        setOf(intent.section)
                    }
                    copy(expandedSections = newExpandedSections)
                }
            }

            is StaffProfileDetailsIntent.OnToggleAttendance -> {
                updateUiState { copy(isAttendanceExpanded = !isAttendanceExpanded) }
            }

            is StaffProfileDetailsIntent.OnToggleTimetable -> {
                updateUiState { copy(isTimetableExpanded = !isTimetableExpanded) }
            }

            is StaffProfileDetailsIntent.OnToggleSalaryCard -> {
                updateUiState {
                    val newCards = if (expandedSalaryCards.contains(intent.section)) {
                        emptySet()
                    } else {
                        setOf(intent.section)  // open only this card, collapse all others
                    }
                    copy(expandedSalaryCards = newCards)
                }
            }
        }
    }

    private fun updateUiState(update: StaffProfileDetailsUiState.() -> StaffProfileDetailsUiState) {
        val currentState = _uiState.value
        if (currentState is UiState.Success) {
            _uiState.value = UiState.Success(currentState.data.update())
        }
    }

    private fun getVisibleTabs(sectionControl: StaffSectionControl?): List<StaffProfileSection> {
        val sections = sectionControl?.sections ?: return emptyList()

        return StaffProfileSection.values()
            .filter { section ->
                sections.find { it.name == section.apiName }?.isShow == true
            }
            .sortedBy { it.tabOrder }
    }
}

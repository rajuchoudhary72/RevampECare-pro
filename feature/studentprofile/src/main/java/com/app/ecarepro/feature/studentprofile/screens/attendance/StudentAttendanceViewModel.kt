package com.app.ecarepro.feature.studentprofile.screens.attendance

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.AcademicYear
import com.app.ecarepro.core.domain.model.AttendanceDetail
import com.app.ecarepro.core.domain.model.MonthlyAttendanceDetailResponse
import com.app.ecarepro.core.domain.repository.StudentProfileRepository
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.AssistedViewModelFactory
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.feature.studentprofile.navigation.StudentProfileNavigationGraph
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Immutable
data class StudentAttendanceUiState(
    val studentName: String = "",
    val attendanceDTL: AttendanceDetail? = null,
    val academicYears: List<AcademicYear> = emptyList(),
    val selectedAttendanceYearId: Int = 0,
    val expandedMonthIds: Set<Int> = emptySet(),
    val monthlyDetailCache: Map<Int, MonthlyAttendanceDetailResponse> = emptyMap(),
    val loadingMonthIds: Set<Int> = emptySet(),
    val isLoadingAttendanceYear: Boolean = false,
)

sealed interface StudentAttendanceIntent {
    data object OnBackClicked : StudentAttendanceIntent
    data class OnAttendanceYearSelected(val yearId: Int) : StudentAttendanceIntent
    data class OnToggleAttendanceMonth(val monthId: Int) : StudentAttendanceIntent
}

sealed interface StudentAttendanceEvent {
    data object NavigateBack : StudentAttendanceEvent
}

@HiltViewModel(assistedFactory = StudentAttendanceViewModel.Factory::class)
class StudentAttendanceViewModel @AssistedInject constructor(
    @Assisted val navKey: StudentProfileNavigationGraph.StudentAttendance,
    private val repository: StudentProfileRepository,
) : BaseViewModel<StudentAttendanceIntent, StudentAttendanceEvent>() {

    private val _uiState = MutableStateFlow<UiState<StudentAttendanceUiState>>(UiState.Loading)
    val uiState: StateFlow<UiState<StudentAttendanceUiState>> = _uiState.asStateFlow()

    init {
        loadAttendance()
    }

    private fun loadAttendance() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = repository.getStudentProfileDetails(navKey.studentId)
                if (response.errorCode == 0) {
                    val currentYear = response.academicYears?.firstOrNull { it.isCur }
                    val selectedYearId = currentYear?.yrID ?: response.academicYears?.firstOrNull()?.yrID ?: 0
                    _uiState.value = UiState.Success(
                        StudentAttendanceUiState(
                            studentName = navKey.studentName,
                            attendanceDTL = response.attendanceDTL,
                            academicYears = response.academicYears ?: emptyList(),
                            selectedAttendanceYearId = selectedYearId,
                        )
                    )
                } else {
                    _uiState.value = UiState.Error(response.message ?: "Failed to load attendance")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "An error occurred")
            }
        }
    }

    override fun handleIntent(intent: StudentAttendanceIntent) {
        when (intent) {
            is StudentAttendanceIntent.OnBackClicked -> sendEvent(StudentAttendanceEvent.NavigateBack)
            is StudentAttendanceIntent.OnAttendanceYearSelected -> changeAttendanceYear(intent.yearId)
            is StudentAttendanceIntent.OnToggleAttendanceMonth -> toggleAttendanceMonth(intent.monthId)
        }
    }

    private fun changeAttendanceYear(yearId: Int) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        if (current.selectedAttendanceYearId == yearId) return

        updateUiState {
            copy(
                selectedAttendanceYearId = yearId,
                expandedMonthIds = emptySet(),
                monthlyDetailCache = emptyMap(),
                loadingMonthIds = emptySet(),
                isLoadingAttendanceYear = true,
            )
        }

        viewModelScope.launch {
            try {
                val response = repository.getAttendanceByYear(navKey.studentId, yearId)
                if (response.errorCode == 0) {
                    updateUiState { copy(attendanceDTL = response.attDTL, isLoadingAttendanceYear = false) }
                } else {
                    updateUiState { copy(isLoadingAttendanceYear = false) }
                }
            } catch (e: Exception) {
                updateUiState { copy(isLoadingAttendanceYear = false) }
            }
        }
    }

    private fun toggleAttendanceMonth(monthId: Int) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        val isExpanding = monthId !in current.expandedMonthIds
        updateUiState {
            val newExpanded = if (isExpanding) expandedMonthIds + monthId else expandedMonthIds - monthId
            copy(expandedMonthIds = newExpanded)
        }
        if (isExpanding && monthId !in current.monthlyDetailCache) {
            fetchMonthlyDetail(monthId)
        }
    }

    private fun fetchMonthlyDetail(monthId: Int) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        val monthData = current.attendanceDTL?.summaryAttendance?.find { it.monthID == monthId } ?: return

        updateUiState { copy(loadingMonthIds = loadingMonthIds + monthId) }

        viewModelScope.launch {
            try {
                val response = repository.getMonthlyAttendanceDetail(
                    studentId = navKey.studentId,
                    from = monthData.startDate,
                    till = monthData.endDate,
                    yearId = current.selectedAttendanceYearId
                )
                if (response.errorCode == 0) {
                    updateUiState {
                        copy(
                            monthlyDetailCache = monthlyDetailCache + (monthId to response),
                            loadingMonthIds = loadingMonthIds - monthId,
                        )
                    }
                } else {
                    updateUiState { copy(loadingMonthIds = loadingMonthIds - monthId) }
                }
            } catch (e: Exception) {
                updateUiState { copy(loadingMonthIds = loadingMonthIds - monthId) }
            }
        }
    }

    private fun updateUiState(update: StudentAttendanceUiState.() -> StudentAttendanceUiState) {
        val current = _uiState.value
        if (current is UiState.Success) {
            _uiState.value = UiState.Success(current.data.update())
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<StudentProfileNavigationGraph.StudentAttendance, StudentAttendanceViewModel> {
        override fun create(param: StudentProfileNavigationGraph.StudentAttendance): StudentAttendanceViewModel
    }
}

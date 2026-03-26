package com.app.ecarepro.feature.studentprofile.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.AcademicYear
import com.app.ecarepro.core.domain.model.Appreciation
import com.app.ecarepro.core.domain.model.AttendanceDetail
import com.app.ecarepro.core.domain.model.FeeSummary
import com.app.ecarepro.core.domain.model.Infraction
import com.app.ecarepro.core.domain.model.LibraryDetail
import com.app.ecarepro.core.domain.model.MedicalCard
import com.app.ecarepro.core.domain.model.MedicineIssued
import com.app.ecarepro.core.domain.model.MonthlyAttendance
import com.app.ecarepro.core.domain.model.MonthlyAttendanceDetailResponse
import com.app.ecarepro.core.domain.model.PersonalDetailSection
import com.app.ecarepro.core.domain.model.ProfileSection
import com.app.ecarepro.core.domain.model.ReportCardDetail
import com.app.ecarepro.core.domain.model.SectionControl
import com.app.ecarepro.core.domain.model.SiblingDetail
import com.app.ecarepro.core.domain.model.StudentProfileDetail
import com.app.ecarepro.core.domain.model.TransportDetails
import com.app.ecarepro.core.domain.model.StudentProfileDetailsResponse
import com.app.ecarepro.core.domain.repository.StudentProfileRepository
import com.app.ecarepro.core.download.FileDownloader
import com.app.ecarepro.core.download.model.DownloadRequest
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class StudentProfileDetailsUiState(
    val profileData: StudentProfileDetail? = null,
    val siblingDetails: List<SiblingDetail> = emptyList(),
    val medicineIssued: List<MedicineIssued> = emptyList(),
    val libraryDetail: LibraryDetail? = null,
    val transportDetails: TransportDetails? = null,
    val recentAppreciations: List<Appreciation> = emptyList(),
    val recentInfractions: List<Infraction> = emptyList(),
    val medicalCard: MedicalCard? = null,
    val feeSummery: FeeSummary? = null,
    val expandedFeeInstallmentIndices: Set<Int> = emptySet(),
    val reportCardDetails: List<ReportCardDetail> = emptyList(),
    val selectedReportCardClassIndex: Int = 0,
    val visibleTabs: List<ProfileSection> = emptyList(),
    val selectedTabIndex: Int = 0,
    val expandedSections: Set<PersonalDetailSection> = emptySet(),
    val expandedInfirmaryVisits: Set<Int> = emptySet(),
    val expandedAppreciations: Set<Int> = setOf(0),
    val expandedInfractions: Set<Int> = setOf(0),
    // Attendance
    val attendanceDTL: AttendanceDetail? = null,
    val academicYears: List<AcademicYear> = emptyList(),
    val selectedAttendanceYearId: Int = 0,
    val expandedMonthIds: Set<Int> = emptySet(),
    val monthlyDetailCache: Map<Int, MonthlyAttendanceDetailResponse> = emptyMap(),
    val loadingMonthIds: Set<Int> = emptySet(),
    val isLoadingAttendanceYear: Boolean = false,
)

sealed interface StudentProfileDetailsIntent {
    data object OnBackClicked : StudentProfileDetailsIntent
    data class OnTabSelected(val index: Int) : StudentProfileDetailsIntent
    data class OnToggleSection(val section: PersonalDetailSection) : StudentProfileDetailsIntent
    data class OnToggleInfirmaryVisit(val index: Int) : StudentProfileDetailsIntent
    data class OnToggleAppreciation(val index: Int) : StudentProfileDetailsIntent
    data class OnToggleInfraction(val index: Int) : StudentProfileDetailsIntent
    data class OnToggleFeeInstallment(val index: Int) : StudentProfileDetailsIntent
    data class OnReportCardClassSelected(val index: Int) : StudentProfileDetailsIntent
    data class OnReportCardClicked(val reportCard: com.app.ecarepro.core.domain.model.ReportCard) : StudentProfileDetailsIntent
    data class OnViewReportCard(val title: String, val url: String) : StudentProfileDetailsIntent
    data class OnDownloadReportCard(val fileName: String, val url: String) : StudentProfileDetailsIntent
    // Attendance
    data class OnAttendanceYearSelected(val yearId: Int) : StudentProfileDetailsIntent
    data class OnToggleAttendanceMonth(val monthId: Int) : StudentProfileDetailsIntent
}

sealed interface StudentProfileDetailsEvent {
    data object NavigateBack : StudentProfileDetailsEvent
    data class NavigateToDocViewer(val title: String, val url: String) : StudentProfileDetailsEvent
    data class ShowMessage(val message: SnackbarMessage) : StudentProfileDetailsEvent
}

@HiltViewModel
class StudentProfileDetailsViewModel @Inject constructor(
    private val repository: StudentProfileRepository,
    private val fileDownloader: FileDownloader
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<StudentProfileDetailsUiState>>(UiState.Loading)
    val uiState: StateFlow<UiState<StudentProfileDetailsUiState>> = _uiState.asStateFlow()

    private val _screenEvent = MutableSharedFlow<StudentProfileDetailsEvent>()
    val screenEvent = _screenEvent.asSharedFlow()

    private var currentStudentId: Int = 0

    fun loadStudentProfile(studentId: Int, forceRefresh: Boolean = false) {
        if (!forceRefresh && _uiState.value is UiState.Success) return

        currentStudentId = studentId
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            try {
                val response = repository.getStudentProfileDetails(studentId)

                if (response.errorCode == 0) {
                    val visibleTabs = getVisibleTabs(response.sectionControl)
                    val currentYear = response.academicYears?.firstOrNull { it.isCur }
                    val selectedYearId = currentYear?.yrID ?: response.academicYears?.firstOrNull()?.yrID ?: 0

                    _uiState.value = UiState.Success(
                        StudentProfileDetailsUiState(
                            profileData = response.profile,
                            siblingDetails = response.siblingDetails ?: emptyList(),
                            medicineIssued = response.medicineIssued ?: emptyList(),
                            libraryDetail = response.library,
                            transportDetails = response.transDetails,
                            recentAppreciations = response.recentAppreciations ?: emptyList(),
                            recentInfractions = response.recentInfractions ?: emptyList(),
                            medicalCard = response.medicalCard,
                            feeSummery = response.feeSummery,
                            reportCardDetails = response.reportCardDTLs ?: emptyList(),
                            selectedReportCardClassIndex = 0,
                            visibleTabs = visibleTabs,
                            selectedTabIndex = 0,
                            attendanceDTL = response.attendanceDTL,
                            academicYears = response.academicYears ?: emptyList(),
                            selectedAttendanceYearId = selectedYearId,
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

    fun handleIntent(intent: StudentProfileDetailsIntent) {
        when (intent) {
            is StudentProfileDetailsIntent.OnBackClicked -> {
                viewModelScope.launch {
                    _screenEvent.emit(StudentProfileDetailsEvent.NavigateBack)
                }
            }

            is StudentProfileDetailsIntent.OnTabSelected -> {
                updateUiState { copy(selectedTabIndex = intent.index) }
            }

            is StudentProfileDetailsIntent.OnToggleSection -> {
                updateUiState {
                    val newExpandedSections = if (expandedSections.contains(intent.section)) {
                        emptySet()
                    } else {
                        setOf(intent.section)
                    }
                    copy(expandedSections = newExpandedSections)
                }
            }

            is StudentProfileDetailsIntent.OnToggleInfirmaryVisit -> {
                updateUiState {
                    val newExpandedVisits = if (expandedInfirmaryVisits.contains(intent.index)) {
                        expandedInfirmaryVisits - intent.index
                    } else {
                        expandedInfirmaryVisits + intent.index
                    }
                    copy(expandedInfirmaryVisits = newExpandedVisits)
                }
            }

            is StudentProfileDetailsIntent.OnToggleAppreciation -> {
                updateUiState {
                    val newExpandedAppreciations = if (expandedAppreciations.contains(intent.index)) {
                        expandedAppreciations - intent.index
                    } else {
                        expandedAppreciations + intent.index
                    }
                    copy(expandedAppreciations = newExpandedAppreciations)
                }
            }

            is StudentProfileDetailsIntent.OnToggleInfraction -> {
                updateUiState {
                    val newExpandedInfractions = if (expandedInfractions.contains(intent.index)) {
                        expandedInfractions - intent.index
                    } else {
                        expandedInfractions + intent.index
                    }
                    copy(expandedInfractions = newExpandedInfractions)
                }
            }

            is StudentProfileDetailsIntent.OnToggleFeeInstallment -> {
                updateUiState {
                    val newSet = if (expandedFeeInstallmentIndices.contains(intent.index)) {
                        expandedFeeInstallmentIndices - intent.index
                    } else {
                        expandedFeeInstallmentIndices + intent.index
                    }
                    copy(expandedFeeInstallmentIndices = newSet)
                }
            }

            is StudentProfileDetailsIntent.OnReportCardClassSelected -> {
                updateUiState { copy(selectedReportCardClassIndex = intent.index) }
            }

            is StudentProfileDetailsIntent.OnReportCardClicked -> { /* handled in UI */ }

            is StudentProfileDetailsIntent.OnViewReportCard -> {
                viewModelScope.launch {
                    _screenEvent.emit(
                        StudentProfileDetailsEvent.NavigateToDocViewer(
                            title = intent.title,
                            url = intent.url
                        )
                    )
                }
            }

            is StudentProfileDetailsIntent.OnDownloadReportCard -> {
                downloadReportCard(intent.fileName, intent.url)
            }

            is StudentProfileDetailsIntent.OnAttendanceYearSelected -> {
                changeAttendanceYear(intent.yearId)
            }

            is StudentProfileDetailsIntent.OnToggleAttendanceMonth -> {
                toggleAttendanceMonth(intent.monthId)
            }
        }
    }

    private fun changeAttendanceYear(yearId: Int) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        if (current.selectedAttendanceYearId == yearId) return

        // Clear caches and collapse all months
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
                val response = repository.getAttendanceByYear(currentStudentId, yearId)
                if (response.errorCode == 0) {
                    updateUiState {
                        copy(
                            attendanceDTL = response.attDTL,
                            isLoadingAttendanceYear = false,
                        )
                    }
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
                    studentId = currentStudentId,
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

    private fun downloadReportCard(fileName: String, url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                fileDownloader.download(DownloadRequest(url = url, fileName = fileName)).first()
                withContext(Dispatchers.Main) {
                    _screenEvent.emit(
                        StudentProfileDetailsEvent.ShowMessage(SnackbarMessage("Download Started", MessageType.INFO))
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _screenEvent.emit(
                        StudentProfileDetailsEvent.ShowMessage(
                            SnackbarMessage("Download failed. Please try again.", MessageType.ERROR)
                        )
                    )
                }
            }
        }
    }

    private fun updateUiState(update: StudentProfileDetailsUiState.() -> StudentProfileDetailsUiState) {
        val currentState = _uiState.value
        if (currentState is UiState.Success) {
            _uiState.value = UiState.Success(currentState.data.update())
        }
    }

    private fun getVisibleTabs(sectionControl: SectionControl?): List<ProfileSection> {
        val sections = sectionControl?.sections ?: return emptyList()
        return ProfileSection.values()
            .filter { section -> sections.find { it.name == section.apiName }?.isShow == true }
            .sortedBy { it.tabOrder }
    }
}

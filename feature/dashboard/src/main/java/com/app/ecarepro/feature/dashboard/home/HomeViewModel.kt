package com.app.ecarepro.feature.dashboard.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.dashboard.AdmissionComparison
import com.app.ecarepro.core.domain.model.dashboard.BankBalance
import com.app.ecarepro.core.domain.model.dashboard.BirthdaySummary
import com.app.ecarepro.core.domain.model.dashboard.ClassSummary
import com.app.ecarepro.core.domain.model.dashboard.DashFeedItem
import com.app.ecarepro.core.domain.model.dashboard.DashboardActivity
import com.app.ecarepro.core.domain.model.dashboard.DashboardCard
import com.app.ecarepro.core.domain.model.dashboard.DashboardQuestionnaire
import com.app.ecarepro.core.domain.model.dashboard.DashboardResponse
import com.app.ecarepro.core.domain.model.dashboard.DashboardVisibility
import com.app.ecarepro.core.domain.model.dashboard.FeeCollectionData
import com.app.ecarepro.core.domain.model.dashboard.FeeDefaulterSummary
import com.app.ecarepro.core.domain.model.dashboard.LibraryData
import com.app.ecarepro.core.domain.model.dashboard.ModeWiseData
import com.app.ecarepro.core.domain.model.dashboard.StaffAttendance
import com.app.ecarepro.core.domain.model.dashboard.StatItem
import com.app.ecarepro.core.domain.model.dashboard.StudentBirthday
import com.app.ecarepro.core.domain.model.dashboard.TimetablePeriod
import com.app.ecarepro.core.domain.repository.DashboardRepository
import com.app.ecarepro.core.domain.repository.UserRepository
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: DashboardRepository,
    private val userRepository: UserRepository,
) : BaseViewModel<HomeIntent, HomeEvent>() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadUser()
        fetchDashboard()
    }

    private fun loadUser() {
        viewModelScope.launch {
            val user = userRepository.getActiveUser()
            _uiState.update {
                it.copy(
                    userName = user?.name?.split(" ")?.firstOrNull().orEmpty(),
                    userPhotoUrl = user?.photoPath.orEmpty(),
                )
            }
        }
    }

    private fun fetchDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isError = false) }
            repository.getDashboard().collect { result ->
                result.onSuccess { response ->
                    applyDashboardResponse(response)
                    fetchSecondaryApis(response)
                }.onFailure {
                    _uiState.update { state -> state.copy(isLoading = false, isError = true) }
                }
            }
        }
    }

    private fun applyDashboardResponse(r: DashboardResponse) {
        _uiState.update { state ->
            state.copy(
                isLoading = false,
                visibility = r.visibility,
                proCards = r.proCards,
                cards = r.cards,
                activities = r.activities,
                questionnaires = r.questionnaires,
                studentBirthdays = r.studentBirthdays,
                birthdaySummaries = r.birthdaySummaries,
                timetablePeriods = r.timetablePeriods,
                classSummaries = r.classSummaries,
                staffAttendance = r.staffAttendance,
                bankBalances = r.bankBalances,
                feeDefaulter = r.feeDefaulter,
                library = r.library,
                admissionComparison = r.admissionComparison,
                stuStatusStats = r.stuStatusStats,
                stuCategoryStats = r.stuCategoryStats,
                stuReligionStats = r.stuReligionStats,
                admissionModeStats = r.admissionModeStats,
            )
        }
    }

    private fun fetchSecondaryApis(r: DashboardResponse) {
        viewModelScope.launch {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val sessionStart = r.sessionStartDate.ifEmpty { today }
            val sessionEnd = r.sessionEndDate.ifEmpty { today }

            val feeJob = async {
                if (_uiState.value.visibility.showFeeCollection) {
                    repository.getFeeCollection(0, sessionStart, sessionEnd).collect { result ->
                        result.onSuccess { data ->
                            _uiState.update { it.copy(feeCollection = data) }
                        }
                    }
                }
            }
            val modeJob = async {
                if (_uiState.value.visibility.showCollectionModeWise) {
                    repository.getModeWiseCollection(today).collect { result ->
                        result.onSuccess { data ->
                            _uiState.update { it.copy(modeWiseCollection = data) }
                        }
                    }
                }
            }
            val feedJob = async {
                if (_uiState.value.visibility.showFeed) {
                    repository.getFeed().collect { result ->
                        result.onSuccess { items ->
                            _uiState.update { it.copy(feedItems = items) }
                        }
                    }
                }
            }
            feeJob.await()
            modeJob.await()
            feedJob.await()
        }
    }

    override fun handleIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.OnRetry -> fetchDashboard()
            is HomeIntent.ToggleSection -> toggleSection(intent.section)
            is HomeIntent.OnCardClicked -> sendEvent(HomeEvent.NavigateToModule(intent.menuID, intent.chMenuID, intent.sbChMenuID))
            HomeIntent.OnSettingsClicked -> sendEvent(HomeEvent.NavigateToSettings)
            HomeIntent.OnNotificationClicked -> sendEvent(HomeEvent.NavigateToNotifications)
            HomeIntent.OnQuestionnaireClicked -> sendEvent(HomeEvent.NavigateToQuestionnaire)
            HomeIntent.OnHomeSelectionClicked -> sendEvent(HomeEvent.NavigateToHomeSelection)
            HomeIntent.RefreshFeeCollection -> refreshFeeCollection()
            HomeIntent.RefreshModeWise -> refreshModeWise()
            HomeIntent.ToggleShowAllClasses -> _uiState.update { it.copy(showAllClasses = !it.showAllClasses) }
        }
    }

    private fun toggleSection(section: HomeSectionType) {
        _uiState.update { state ->
            val current = state.expandedSections.toMutableSet()
            if (section in current) current.remove(section) else current.add(section)
            state.copy(expandedSections = current)
        }
    }

    private fun refreshFeeCollection() {
        viewModelScope.launch {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            repository.getFeeCollection(0, today, today).collect { result ->
                result.onSuccess { data -> _uiState.update { it.copy(feeCollection = data) } }
            }
        }
    }

    private fun refreshModeWise() {
        viewModelScope.launch {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            repository.getModeWiseCollection(today).collect { result ->
                result.onSuccess { data -> _uiState.update { it.copy(modeWiseCollection = data) } }
            }
        }
    }
}

@Immutable
data class HomeUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val userName: String = "",
    val userPhotoUrl: String = "",
    val expandedSections: Set<HomeSectionType> = setOf(HomeSectionType.OVERVIEW),
    val showAllClasses: Boolean = false,
    val visibility: DashboardVisibility = DashboardVisibility(),
    val proCards: List<DashboardCard> = emptyList(),
    val cards: List<DashboardCard> = emptyList(),
    val activities: List<DashboardActivity> = emptyList(),
    val questionnaires: List<DashboardQuestionnaire> = emptyList(),
    val studentBirthdays: List<StudentBirthday> = emptyList(),
    val birthdaySummaries: List<BirthdaySummary> = emptyList(),
    val timetablePeriods: List<TimetablePeriod> = emptyList(),
    val classSummaries: List<ClassSummary> = emptyList(),
    val staffAttendance: StaffAttendance? = null,
    val bankBalances: List<BankBalance> = emptyList(),
    val feeDefaulter: FeeDefaulterSummary? = null,
    val library: LibraryData? = null,
    val admissionComparison: AdmissionComparison? = null,
    val stuStatusStats: List<StatItem> = emptyList(),
    val stuCategoryStats: List<StatItem> = emptyList(),
    val stuReligionStats: List<StatItem> = emptyList(),
    val admissionModeStats: List<StatItem> = emptyList(),
    val feeCollection: FeeCollectionData? = null,
    val modeWiseCollection: ModeWiseData? = null,
    val feedItems: List<DashFeedItem> = emptyList(),
) {
    val totalStudentsCard: DashboardCard? get() = proCards.firstOrNull { it.heading.contains("Student", ignoreCase = true) }
    val totalStaffCard: DashboardCard? get() = proCards.firstOrNull { it.heading.contains("Staff", ignoreCase = true) }
    val gridProCards: List<DashboardCard> get() = proCards.filter { it != totalStudentsCard && it != totalStaffCard }
    fun isSectionExpanded(section: HomeSectionType): Boolean = section in expandedSections
}

enum class HomeSectionType {
    OVERVIEW, FINANCIAL, CALENDAR, ATTENDANCE, BIRTHDAYS,
    QUESTIONNAIRE, TIMETABLE, LIBRARY, ADMISSIONS, FEED
}

sealed interface HomeIntent {
    data object OnRetry : HomeIntent
    data class ToggleSection(val section: HomeSectionType) : HomeIntent
    data class OnCardClicked(val menuID: Int?, val chMenuID: Int?, val sbChMenuID: Int?) : HomeIntent
    data object OnSettingsClicked : HomeIntent
    data object OnNotificationClicked : HomeIntent
    data object OnQuestionnaireClicked : HomeIntent
    data object OnHomeSelectionClicked : HomeIntent
    data object RefreshFeeCollection : HomeIntent
    data object RefreshModeWise : HomeIntent
    data object ToggleShowAllClasses : HomeIntent
}

sealed interface HomeEvent {
    data class NavigateToModule(val menuID: Int?, val chMenuID: Int?, val sbChMenuID: Int?) : HomeEvent
    data object NavigateToSettings : HomeEvent
    data object NavigateToNotifications : HomeEvent
    data object NavigateToQuestionnaire : HomeEvent
    data object NavigateToHomeSelection : HomeEvent
}

package com.app.ecarepro.feature.studentprofile

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.StudentProfile
import com.app.ecarepro.core.domain.repository.StudentProfileRepository
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.component.personlist.Gender
import com.app.ecarepro.designsystem.core.component.personlist.ListStatsPresentation
import com.app.ecarepro.designsystem.core.component.personlist.ListViewMode
import com.app.ecarepro.designsystem.core.component.personlist.PersonPresentation
import com.app.ecarepro.designsystem.core.component.personlist.ScholarType
import com.app.ecarepro.designsystem.core.component.SortConfig
import com.app.ecarepro.designsystem.core.component.SortDirection
import com.app.ecarepro.designsystem.core.component.SortOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentProfileViewModel @Inject constructor(
    private val studentProfileRepository: StudentProfileRepository,
) : BaseViewModel<StudentProfileIntent, StudentProfileEvent>() {

    private val _uiState: MutableStateFlow<UiState<StudentProfileUiState>> =
        MutableStateFlow(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    // All loaded student profiles
    private var allStudentProfiles: List<StudentProfile> = emptyList()

    companion object {
        const val DEFAULT_SELECTED_CLASS_INDEX = 0
    }

    init {
        fetchStudentProfiles(scholarType = ScholarType.DAY_SCHOLAR.value)
    }

    private fun fetchStudentProfiles(isRefreshing: Boolean = false, scholarType: Int? = null) {
        viewModelScope.launch {
            studentProfileRepository
                .getStudentProfiles(scholarType)
                .onStart {
                    _uiState.update { currentState ->
                        if (isRefreshing) {
                            (currentState as? UiState.Success)?.let { successState ->
                                UiState.Success(successState.data.copy(isRefreshing = true))
                            } ?: currentState
                        } else {
                            UiState.Loading
                        }
                    }
                }.collect { result ->
                    result
                        .onSuccess { profiles ->
                            allStudentProfiles = profiles

                            // Group by class
                            val profileMap = mutableMapOf<String, List<StudentProfile>>()
                            profileMap["All"] = profiles

                            // Group by full class name (classSTD-section)
                            profiles.forEach { profile ->
                                val fullClassName = if (profile.section != null) {
                                    "${profile.classSTD}-${profile.section}"
                                } else {
                                    profile.classSTD
                                }
                                val existing = profileMap[fullClassName] ?: emptyList()
                                profileMap[fullClassName] = existing + profile
                            }

                            val classTabs = getUniqueClassesSorted(profiles)

                            val presentations = profiles.map { it.toPresentation() }

                            _uiState.update {
                                UiState.Success(
                                    StudentProfileUiState(
                                        selectedClassIndex = DEFAULT_SELECTED_CLASS_INDEX,
                                        classTabs = listOf("All") + classTabs,
                                        studentProfiles = profileMap,
                                        filteredProfiles = presentations,
                                        stats = calculateStats(presentations),
                                        selectedScholarType = ScholarType.fromValue(scholarType ?: 0)
                                    )
                                )
                            }
                        }
                        .onFailure { error ->
                            handleFetchError(error, isRefreshing)
                        }
                }
        }
    }

    private fun handleFetchError(error: Throwable, isRefresh: Boolean) {
        val message = error.errorMessage()

        if (isRefresh) {
            _uiState.update { currentState ->
                (currentState as? UiState.Success)?.let { successState ->
                    UiState.Success(successState.data.copy(isRefreshing = false))
                } ?: currentState
            }

            sendEvent(
                StudentProfileEvent.ShowMessage(
                    SnackbarMessage(message, MessageType.ERROR)
                )
            )
        } else {
            _uiState.update { UiState.Error(message) }
        }
    }

    override fun handleIntent(intent: StudentProfileIntent) {
        when (intent) {
            is StudentProfileIntent.OnBackClicked -> viewModelScope.launch {
                sendEvent(StudentProfileEvent.NavigateBack)
            }
            is StudentProfileIntent.OnSearchQueryChanged -> onSearchQueryChanged(intent.query)
            is StudentProfileIntent.OnClassSelected -> onClassSelected(intent.index)
            is StudentProfileIntent.OnProfileClicked -> onProfileClicked(intent.profileId)
            is StudentProfileIntent.OnRefresh -> fetchStudentProfiles(true)
            is StudentProfileIntent.OnViewModeToggle -> toggleViewMode()
            is StudentProfileIntent.OnScholarTypeSelected -> onScholarTypeSelected(intent.scholarType)
            is StudentProfileIntent.OnSortSelected -> onSortSelected(intent.sortConfig)
        }
    }

    private fun toggleViewMode() {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.update {
            UiState.Success(
                currentState.copy(
                    viewMode = if (currentState.viewMode == ListViewMode.GRID) {
                        ListViewMode.LIST
                    } else {
                        ListViewMode.GRID
                    }
                )
            )
        }
    }

    private fun onScholarTypeSelected(scholarType: ScholarType) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return

        // Only fetch if different from current
        if (currentState.selectedScholarType == scholarType) return

        fetchStudentProfiles(isRefreshing = false, scholarType = scholarType.value)
    }

    private fun onSortSelected(sortConfig: SortConfig) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return

        _uiState.update {
            UiState.Success(
                currentState.copy(
                    sortConfig = sortConfig,
                    filteredProfiles = filterProfiles(
                        currentState.studentProfiles,
                        currentState.selectedClassIndex,
                        currentState.classTabs,
                        currentState.searchQuery,
                        sortConfig
                    ).also { filtered ->
                        // Update stats when filtered
                        _uiState.update { state ->
                            (state as? UiState.Success)?.let { successState ->
                                UiState.Success(
                                    successState.data.copy(stats = calculateStats(filtered))
                                )
                            } ?: state
                        }
                    }
                )
            )
        }
    }

    private fun onProfileClicked(profileId: String) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        val profile = allStudentProfiles.find { it.id == profileId }

        if (profile == null) {
            sendEvent(
                StudentProfileEvent.ShowMessage(
                    SnackbarMessage("Profile not found.", MessageType.ERROR)
                )
            )
            return
        }

        sendEvent(StudentProfileEvent.NavigateToProfileDetail(profile))
    }

    private fun onSearchQueryChanged(query: String) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.update {
            UiState.Success(
                currentState.copy(
                    searchQuery = query,
                    filteredProfiles = filterProfiles(
                        currentState.studentProfiles,
                        currentState.selectedClassIndex,
                        currentState.classTabs,
                        query,
                        currentState.sortConfig
                    ).also { filtered ->
                        // Update stats when filtered
                        _uiState.update { state ->
                            (state as? UiState.Success)?.let { successState ->
                                UiState.Success(
                                    successState.data.copy(stats = calculateStats(filtered))
                                )
                            } ?: state
                        }
                    }
                )
            )
        }
    }

    private fun onClassSelected(index: Int) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.update {
            UiState.Success(
                currentState.copy(
                    selectedClassIndex = index,
                    filteredProfiles = filterProfiles(
                        currentState.studentProfiles,
                        index,
                        currentState.classTabs,
                        currentState.searchQuery,
                        currentState.sortConfig
                    ).also { filtered ->
                        // Update stats when filtered
                        _uiState.update { state ->
                            (state as? UiState.Success)?.let { successState ->
                                UiState.Success(
                                    successState.data.copy(stats = calculateStats(filtered))
                                )
                            } ?: state
                        }
                    }
                )
            )
        }
    }

    private fun filterProfiles(
        profiles: Map<String, List<StudentProfile>>,
        selectedTabIndex: Int,
        classTabs: List<String>,
        searchQuery: String,
        sortConfig: SortConfig = SortConfig()
    ): List<PersonPresentation> {
        val selectedClass = classTabs.getOrNull(selectedTabIndex) ?: "All"

        val profilesForSelectedClass = if (selectedClass == "All") {
            profiles["All"] ?: emptyList()
        } else {
            profiles[selectedClass] ?: emptyList()
        }

        val filtered = if (searchQuery.isBlank()) {
            profilesForSelectedClass
        } else {
            profilesForSelectedClass.filter {
                it.name?.contains(searchQuery, ignoreCase = true) == true ||
                it.rollNumber?.contains(searchQuery, ignoreCase = true) == true ||
                it.admissionNumber?.contains(searchQuery, ignoreCase = true) == true ||
                it.fatherName?.contains(searchQuery, ignoreCase = true) == true
            }
        }

        // Apply sorting
        val sorted = when (sortConfig.option) {
            SortOption.ROLL_NUMBER -> {
                filtered.sortedWith(compareBy(nullsLast()) { it.rollNumber?.toIntOrNull() })
            }
            SortOption.ADMISSION_NUMBER -> {
                filtered.sortedWith(compareBy(nullsLast()) { it.admissionNumber })
            }
            SortOption.NAME -> {
                filtered.sortedWith(compareBy(nullsLast()) { it.name })
            }
            SortOption.DATE_OF_JOINING -> {
                // DATE_OF_JOINING not applicable for students, fallback to ROLL_NUMBER
                filtered.sortedWith(compareBy(nullsLast()) { it.rollNumber?.toIntOrNull() })
            }
        }

        // Apply direction
        val finalSorted = if (sortConfig.direction == SortDirection.DESCENDING) {
            sorted.reversed()
        } else {
            sorted
        }

        return finalSorted.map { it.toPresentation() }
    }

    private fun calculateStats(presentations: List<PersonPresentation>): ListStatsPresentation {
        val maleCount = presentations.count { it.gender == Gender.MALE }
        val femaleCount = presentations.count { it.gender == Gender.FEMALE }

        return ListStatsPresentation(
            total = presentations.size,
            maleCount = maleCount,
            femaleCount = femaleCount,
            maleLabel = "Boys",
            femaleLabel = "Girls"
        )
    }

    /**
     * Returns unique full class names sorted by class order.
     * Sorting priority:
     * 1. Pre-primary classes: LKG, HKG, UKG, Nursery, KG (in this order)
     * 2. Numeric classes: 1st, 2nd, ... 12th (numerically sorted)
     * 3. Within same class, sort by section alphabetically
     */
    private fun getUniqueClassesSorted(profiles: List<StudentProfile>): List<String> {
        val prePrimaryOrder = listOf("LKG", "HKG", "UKG", "Nursery", "KG")

        val uniqueClasses = profiles.mapNotNull { profile ->
            if (profile.section != null) {
                "${profile.classSTD}-${profile.section}"
            } else {
                profile.classSTD
            }
        }.distinct()

        return uniqueClasses.sortedWith { class1, class2 ->
            val baseClass1 = class1.split("-").firstOrNull() ?: class1
            val baseClass2 = class2.split("-").firstOrNull() ?: class2

            val isPrePrimary1 = baseClass1 in prePrimaryOrder
            val isPrePrimary2 = baseClass2 in prePrimaryOrder

            when {
                isPrePrimary1 && isPrePrimary2 -> {
                    val idx1 = prePrimaryOrder.indexOf(baseClass1)
                    val idx2 = prePrimaryOrder.indexOf(baseClass2)
                    if (idx1 != idx2) idx1 - idx2
                    else class1.compareTo(class2)
                }
                isPrePrimary1 -> -1
                isPrePrimary2 -> 1
                else -> {
                    val num1 = baseClass1.filter { it.isDigit() }.toIntOrNull()
                    val num2 = baseClass2.filter { it.isDigit() }.toIntOrNull()

                    when {
                        num1 != null && num2 != null -> {
                            if (num1 != num2) num1 - num2
                            else class1.compareTo(class2)
                        }
                        else -> class1.compareTo(class2)
                    }
                }
            }
        }
    }
}

@Immutable
data class StudentProfileUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val searchQuery: String = "",
    val selectedClassIndex: Int = StudentProfileViewModel.DEFAULT_SELECTED_CLASS_INDEX,
    val classTabs: List<String> = emptyList(),
    val studentProfiles: Map<String, List<StudentProfile>> = emptyMap(),
    val filteredProfiles: List<PersonPresentation> = emptyList(),
    val stats: ListStatsPresentation = ListStatsPresentation.EMPTY,
    val viewMode: ListViewMode = ListViewMode.GRID,
    val selectedScholarType: ScholarType = ScholarType.DAY_SCHOLAR,
    val sortConfig: SortConfig = SortConfig()
)

sealed interface StudentProfileIntent {
    data object OnBackClicked : StudentProfileIntent
    data class OnSearchQueryChanged(val query: String) : StudentProfileIntent
    data class OnClassSelected(val index: Int) : StudentProfileIntent
    data class OnProfileClicked(val profileId: String) : StudentProfileIntent
    data object OnRefresh : StudentProfileIntent
    data object OnViewModeToggle : StudentProfileIntent
    data class OnScholarTypeSelected(val scholarType: ScholarType) : StudentProfileIntent
    data class OnSortSelected(val sortConfig: SortConfig) : StudentProfileIntent
}

sealed interface StudentProfileEvent {
    data object NavigateBack : StudentProfileEvent
    data class NavigateToProfileDetail(val profile: StudentProfile) : StudentProfileEvent
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : StudentProfileEvent
}

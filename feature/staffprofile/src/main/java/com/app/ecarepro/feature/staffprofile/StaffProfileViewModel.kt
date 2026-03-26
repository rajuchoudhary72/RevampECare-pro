package com.app.ecarepro.feature.staffprofile

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.StaffProfile
import com.app.ecarepro.core.domain.repository.StaffProfileRepository
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.FilterSection
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.component.SortConfig
import com.app.ecarepro.designsystem.core.component.SortDirection
import com.app.ecarepro.designsystem.core.component.SortOption
import com.app.ecarepro.designsystem.core.component.personlist.Gender
import com.app.ecarepro.designsystem.core.component.personlist.ListStatsPresentation
import com.app.ecarepro.designsystem.core.component.personlist.ListViewMode
import com.app.ecarepro.designsystem.core.component.personlist.PersonPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StaffProfileViewModel @Inject constructor(
    private val staffProfileRepository: StaffProfileRepository,
) : BaseViewModel<StaffProfileIntent, StaffProfileEvent>() {

    private val _uiState: MutableStateFlow<UiState<StaffProfileUiState>> =
        MutableStateFlow(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    // All loaded staff profiles
    private var allStaffProfiles: List<StaffProfile> = emptyList()

    init {
        fetchStaffProfiles()
    }

    private fun fetchStaffProfiles(isRefreshing: Boolean = false) {
        viewModelScope.launch {
            staffProfileRepository
                .getStaffProfiles()
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
                            allStaffProfiles = profiles

                            val filterSections = getFilterSections()
                            val presentations = filterProfiles("", SortConfig(SortOption.DATE_OF_JOINING, SortDirection.ASCENDING), emptyMap())

                            _uiState.update {
                                UiState.Success(
                                    StaffProfileUiState(
                                        filteredProfiles = presentations,
                                        stats = calculateStats(presentations),
                                        filterSections = filterSections,
                                        sortConfig = SortConfig(SortOption.DATE_OF_JOINING, SortDirection.ASCENDING)
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
                StaffProfileEvent.ShowMessage(
                    SnackbarMessage(message, MessageType.ERROR)
                )
            )
        } else {
            _uiState.update { UiState.Error(message) }
        }
    }

    override fun handleIntent(intent: StaffProfileIntent) {
        when (intent) {
            is StaffProfileIntent.OnBackClicked -> viewModelScope.launch {
                sendEvent(StaffProfileEvent.NavigateBack)
            }
            is StaffProfileIntent.OnSearchQueryChanged -> onSearchQueryChanged(intent.query)
            is StaffProfileIntent.OnProfileClicked -> onProfileClicked(intent.profileId)
            is StaffProfileIntent.OnRefresh -> fetchStaffProfiles(true)
            is StaffProfileIntent.OnViewModeToggle -> toggleViewMode()
            is StaffProfileIntent.OnSortSelected -> onSortSelected(intent.sortConfig)
            is StaffProfileIntent.OnFiltersApplied -> onFiltersApplied(intent.filters)
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

    private fun onSortSelected(sortConfig: SortConfig) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return

        _uiState.update {
            UiState.Success(
                currentState.copy(
                    sortConfig = sortConfig,
                    filteredProfiles = filterProfiles(
                        currentState.searchQuery,
                        sortConfig,
                        currentState.selectedFilters
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

    private fun onFiltersApplied(filters: Map<String, Set<String>>) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return

        _uiState.update {
            UiState.Success(
                currentState.copy(
                    selectedFilters = filters,
                    filteredProfiles = filterProfiles(
                        currentState.searchQuery,
                        currentState.sortConfig,
                        filters
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
        val profile = allStaffProfiles.find { it.id == profileId }

        if (profile == null) {
            sendEvent(
                StaffProfileEvent.ShowMessage(
                    SnackbarMessage("Profile not found.", MessageType.ERROR)
                )
            )
            return
        }

        sendEvent(StaffProfileEvent.NavigateToProfileDetail(profile))
    }

    private fun onSearchQueryChanged(query: String) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.update {
            UiState.Success(
                currentState.copy(
                    searchQuery = query,
                    filteredProfiles = filterProfiles(
                        query,
                        currentState.sortConfig,
                        currentState.selectedFilters
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
        searchQuery: String,
        sortConfig: SortConfig = SortConfig(),
        selectedFilters: Map<String, Set<String>> = emptyMap()
    ): List<PersonPresentation> {
        var result = allStaffProfiles

        // Apply staff type filter
        selectedFilters["staffType"]?.takeIf { it.isNotEmpty() }?.let { staffTypes ->
            result = result.filter { it.staffType in staffTypes }
        }

        // Apply designation filter
        selectedFilters["designation"]?.takeIf { it.isNotEmpty() }?.let { designations ->
            result = result.filter { it.designation in designations }
        }

        // Apply search
        if (searchQuery.isNotBlank()) {
            val query = searchQuery.lowercase()
            result = result.filter { staff ->
                staff.name.lowercase().contains(query) ||
                staff.designation.lowercase().contains(query) ||
                (staff.mobile?.contains(query) == true) ||
                (staff.email?.lowercase()?.contains(query) == true)
            }
        }

        // Apply sorting
        val sorted = when (sortConfig.option) {
            SortOption.DATE_OF_JOINING -> {
                // TODO: Sort by actual DOJ when available in API
                // For now, sort by sid
                result.sortedWith(compareBy(nullsLast()) { it.sid })
            }
            SortOption.NAME -> {
                result.sortedWith(compareBy(nullsLast()) { it.name })
            }
            else -> result // Roll number, Admission number not applicable for staff
        }

        // Apply direction
        val finalSorted = if (sortConfig.direction == SortDirection.DESCENDING) {
            sorted.reversed()
        } else {
            sorted
        }

        return finalSorted.map { it.toPresentation() }
    }

    private fun getFilterSections(): List<FilterSection> {
        val sections = mutableListOf<FilterSection>()

        // Staff Type Section
        val staffTypes = allStaffProfiles
            .map { it.staffType }
            .filter { it.isNotEmpty() }
            .distinct()
            .sorted()

        if (staffTypes.isNotEmpty()) {
            sections.add(FilterSection(
                id = "staffType",
                title = "Staff Type",
                options = staffTypes
            ))
        }

        // Designation Section
        val designations = allStaffProfiles
            .map { it.designation }
            .filter { it.isNotEmpty() }
            .distinct()
            .sorted()

        if (designations.isNotEmpty()) {
            sections.add(FilterSection(
                id = "designation",
                title = "Designation",
                options = designations
            ))
        }

        return sections
    }

    private fun calculateStats(presentations: List<PersonPresentation>): ListStatsPresentation {
        val maleCount = presentations.count { it.gender == Gender.MALE }
        val femaleCount = presentations.count { it.gender == Gender.FEMALE }

        return ListStatsPresentation(
            total = presentations.size,
            maleCount = maleCount,
            femaleCount = femaleCount,
            maleLabel = "Male",
            femaleLabel = "Female"
        )
    }
}

@Immutable
data class StaffProfileUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val searchQuery: String = "",
    val filteredProfiles: List<PersonPresentation> = emptyList(),
    val stats: ListStatsPresentation = ListStatsPresentation.EMPTY,
    val viewMode: ListViewMode = ListViewMode.GRID,
    val sortConfig: SortConfig = SortConfig(SortOption.DATE_OF_JOINING, SortDirection.ASCENDING),
    val selectedFilters: Map<String, Set<String>> = emptyMap(),
    val filterSections: List<FilterSection> = emptyList()
) {
    val hasActiveFilters: Boolean
        get() = selectedFilters.values.any { it.isNotEmpty() }

    val activeFilterCount: Int
        get() = selectedFilters.values.sumOf { it.size }
}

sealed interface StaffProfileIntent {
    data object OnBackClicked : StaffProfileIntent
    data class OnSearchQueryChanged(val query: String) : StaffProfileIntent
    data class OnProfileClicked(val profileId: String) : StaffProfileIntent
    data object OnRefresh : StaffProfileIntent
    data object OnViewModeToggle : StaffProfileIntent
    data class OnSortSelected(val sortConfig: SortConfig) : StaffProfileIntent
    data class OnFiltersApplied(val filters: Map<String, Set<String>>) : StaffProfileIntent
}

sealed interface StaffProfileEvent {
    data object NavigateBack : StaffProfileEvent
    data class NavigateToProfileDetail(val profile: StaffProfile) : StaffProfileEvent
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : StaffProfileEvent
}

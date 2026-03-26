package com.app.ecarepro.feature.knowyourteacher

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.StaffProfile
import com.app.ecarepro.core.domain.repository.KnowYourTeacherRepository
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
class KnowYourTeacherViewModel @Inject constructor(
    private val repository: KnowYourTeacherRepository,
) : BaseViewModel<KnowYourTeacherIntent, KnowYourTeacherEvent>() {

    private val _uiState: MutableStateFlow<UiState<KnowYourTeacherUiState>> =
        MutableStateFlow(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var allTeachers: List<StaffProfile> = emptyList()

    init {
        fetchTeachers()
    }

    private fun fetchTeachers(isRefreshing: Boolean = false) {
        viewModelScope.launch {
            repository
                .getTeacherList()
                .onStart {
                    _uiState.update { currentState ->
                        if (isRefreshing) {
                            (currentState as? UiState.Success)?.let {
                                UiState.Success(it.data.copy(isRefreshing = true))
                            } ?: currentState
                        } else {
                            UiState.Loading
                        }
                    }
                }.collect { result ->
                    result
                        .onSuccess { profiles ->
                            allTeachers = profiles
                            val presentations = filterTeachers("", SortConfig(SortOption.NAME, SortDirection.ASCENDING), emptyMap())
                            _uiState.update {
                                UiState.Success(
                                    KnowYourTeacherUiState(
                                        filteredTeachers = presentations,
                                        stats = calculateStats(presentations),
                                        filterSections = getFilterSections(),
                                        sortConfig = SortConfig(SortOption.NAME, SortDirection.ASCENDING)
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
                (currentState as? UiState.Success)?.let {
                    UiState.Success(it.data.copy(isRefreshing = false))
                } ?: currentState
            }
            sendEvent(KnowYourTeacherEvent.ShowMessage(SnackbarMessage(message, MessageType.ERROR)))
        } else {
            _uiState.update { UiState.Error(message) }
        }
    }

    override fun handleIntent(intent: KnowYourTeacherIntent) {
        when (intent) {
            is KnowYourTeacherIntent.OnBackClicked -> sendEvent(KnowYourTeacherEvent.NavigateBack)
            is KnowYourTeacherIntent.OnSearchQueryChanged -> onSearchQueryChanged(intent.query)
            is KnowYourTeacherIntent.OnRefresh -> fetchTeachers(true)
            is KnowYourTeacherIntent.OnViewModeToggle -> toggleViewMode()
            is KnowYourTeacherIntent.OnSortSelected -> onSortSelected(intent.sortConfig)
            is KnowYourTeacherIntent.OnFiltersApplied -> onFiltersApplied(intent.filters)
        }
    }

    private fun toggleViewMode() {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.update {
            UiState.Success(
                current.copy(
                    viewMode = if (current.viewMode == ListViewMode.GRID) ListViewMode.LIST else ListViewMode.GRID
                )
            )
        }
    }

    private fun onSortSelected(sortConfig: SortConfig) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        val filtered = filterTeachers(current.searchQuery, sortConfig, current.selectedFilters)
        _uiState.update {
            UiState.Success(current.copy(sortConfig = sortConfig, filteredTeachers = filtered, stats = calculateStats(filtered)))
        }
    }

    private fun onFiltersApplied(filters: Map<String, Set<String>>) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        val filtered = filterTeachers(current.searchQuery, current.sortConfig, filters)
        _uiState.update {
            UiState.Success(current.copy(selectedFilters = filters, filteredTeachers = filtered, stats = calculateStats(filtered)))
        }
    }

    private fun onSearchQueryChanged(query: String) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        val filtered = filterTeachers(query, current.sortConfig, current.selectedFilters)
        _uiState.update {
            UiState.Success(current.copy(searchQuery = query, filteredTeachers = filtered, stats = calculateStats(filtered)))
        }
    }

    private fun filterTeachers(
        searchQuery: String,
        sortConfig: SortConfig,
        selectedFilters: Map<String, Set<String>>,
    ): List<PersonPresentation> {
        var result = allTeachers

        selectedFilters["designation"]?.takeIf { it.isNotEmpty() }?.let { designations ->
            result = result.filter { it.designation in designations }
        }

        if (searchQuery.isNotBlank()) {
            val query = searchQuery.lowercase()
            result = result.filter { teacher ->
                teacher.name.lowercase().contains(query) ||
                teacher.designation.lowercase().contains(query) ||
                (teacher.mobile?.contains(query) == true)
            }
        }

        val sorted = when (sortConfig.option) {
            SortOption.NAME -> result.sortedWith(compareBy(nullsLast()) { it.name })
            else -> result
        }

        val finalSorted = if (sortConfig.direction == SortDirection.DESCENDING) sorted.reversed() else sorted
        return finalSorted.map { it.toPresentation() }
    }

    private fun getFilterSections(): List<FilterSection> {
        val sections = mutableListOf<FilterSection>()
        val designations = allTeachers.map { it.designation }.filter { it.isNotEmpty() }.distinct().sorted()
        if (designations.isNotEmpty()) {
            sections.add(FilterSection(id = "designation", title = "Designation", options = designations))
        }
        return sections
    }

    private fun calculateStats(presentations: List<PersonPresentation>): ListStatsPresentation {
        return ListStatsPresentation(
            total = presentations.size,
            maleCount = presentations.count { it.gender == Gender.MALE },
            femaleCount = presentations.count { it.gender == Gender.FEMALE },
            maleLabel = "Male",
            femaleLabel = "Female"
        )
    }
}

@Immutable
data class KnowYourTeacherUiState(
    val isRefreshing: Boolean = false,
    val searchQuery: String = "",
    val filteredTeachers: List<PersonPresentation> = emptyList(),
    val stats: ListStatsPresentation = ListStatsPresentation.EMPTY,
    val viewMode: ListViewMode = ListViewMode.GRID,
    val sortConfig: SortConfig = SortConfig(SortOption.NAME, SortDirection.ASCENDING),
    val selectedFilters: Map<String, Set<String>> = emptyMap(),
    val filterSections: List<FilterSection> = emptyList(),
) {
    val hasActiveFilters: Boolean get() = selectedFilters.values.any { it.isNotEmpty() }
    val activeFilterCount: Int get() = selectedFilters.values.sumOf { it.size }
}

sealed interface KnowYourTeacherIntent {
    data object OnBackClicked : KnowYourTeacherIntent
    data class OnSearchQueryChanged(val query: String) : KnowYourTeacherIntent
    data object OnRefresh : KnowYourTeacherIntent
    data object OnViewModeToggle : KnowYourTeacherIntent
    data class OnSortSelected(val sortConfig: SortConfig) : KnowYourTeacherIntent
    data class OnFiltersApplied(val filters: Map<String, Set<String>>) : KnowYourTeacherIntent
}

sealed interface KnowYourTeacherEvent {
    data object NavigateBack : KnowYourTeacherEvent
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : KnowYourTeacherEvent
}

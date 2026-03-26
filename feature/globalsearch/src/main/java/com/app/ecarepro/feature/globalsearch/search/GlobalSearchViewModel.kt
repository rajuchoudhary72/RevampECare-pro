package com.app.ecarepro.feature.globalsearch.search

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.globalsearch.SearchFilterType
import com.app.ecarepro.core.domain.model.globalsearch.SearchResultPresentation
import com.app.ecarepro.core.domain.model.globalsearch.SearchResultType
import com.app.ecarepro.core.domain.model.globalsearch.SearchStaff
import com.app.ecarepro.core.domain.model.globalsearch.SearchStudent
import com.app.ecarepro.core.domain.model.menu.MenuCategory
import com.app.ecarepro.core.domain.model.menu.MenuItem
import com.app.ecarepro.core.domain.repository.GlobalSearchRepository
import com.app.ecarepro.core.domain.repository.MenuRepository
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GlobalSearchViewModel @Inject constructor(
    private val repository: GlobalSearchRepository,
    private val menuRepository: MenuRepository,
) : BaseViewModel<GlobalSearchIntent, GlobalSearchEvent>() {

    private val _uiState = MutableStateFlow(GlobalSearchUiState())
    val uiState = _uiState.asStateFlow()

    private var allStudents: List<SearchStudent> = emptyList()
    private var allStaff: List<SearchStaff> = emptyList()
    private var allModules: List<Pair<MenuItem, String>> = emptyList()

    private var filterJob: Job? = null

    init {
        loadData()
    }

    override fun handleIntent(intent: GlobalSearchIntent) {
        when (intent) {
            is GlobalSearchIntent.OnSearchTextChanged -> onSearchTextChanged(intent.text)
            is GlobalSearchIntent.OnFilterSelected -> onFilterSelected(intent.filter)
            is GlobalSearchIntent.OnFilterClicked -> _uiState.update { it.copy(showFilterSheet = true) }
            is GlobalSearchIntent.DismissFilterSheet -> _uiState.update { it.copy(showFilterSheet = false) }
            is GlobalSearchIntent.OnResultTapped -> onResultTapped(intent.result)
            is GlobalSearchIntent.OnRetry -> loadData()
            is GlobalSearchIntent.OnBackClicked -> sendEvent(GlobalSearchEvent.NavigateBack)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isError = false) }
            try {
                coroutineScope {
                    val studentsDeferred = async { repository.getStudents().first().getOrThrow() }
                    val staffDeferred = async { repository.getStaff().first().getOrThrow() }
                    val menuDeferred = async { menuRepository.fetchMenu("en").first().getOrThrow() }

                    allStudents = studentsDeferred.await()
                    allStaff = staffDeferred.await()
                    allModules = flattenMenuItems(menuDeferred.await())
                }
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, isError = true) }
            }
        }
    }

    private fun onSearchTextChanged(text: String) {
        _uiState.update { it.copy(searchText = text, isFiltering = text.length >= 3) }
        filterJob?.cancel()
        if (text.length >= 3) {
            filterJob = viewModelScope.launch {
                delay(300)
                filterResults()
                _uiState.update { it.copy(isFiltering = false) }
            }
        } else {
            _uiState.update { it.copy(results = emptyList(), isFiltering = false) }
        }
    }

    private fun onFilterSelected(filter: SearchFilterType) {
        _uiState.update { it.copy(selectedFilter = filter, showFilterSheet = false) }
        if (_uiState.value.searchText.length >= 3) {
            filterResults()
        }
    }

    private fun filterResults() {
        val query = _uiState.value.searchText.lowercase()
        val filter = _uiState.value.selectedFilter
        val results = mutableListOf<SearchResultPresentation>()

        if (filter == SearchFilterType.ALL || filter == SearchFilterType.MODULES) {
            allModules
                .filter { (item, _) -> matchesQuery(query, listOf(item.title)) }
                .mapTo(results) { (item, catName) -> SearchResultPresentation.fromMenuItem(item, catName) }
        }

        if (filter == SearchFilterType.ALL || filter == SearchFilterType.STUDENTS) {
            allStudents
                .filter { student ->
                    matchesQuery(query, listOf(
                        student.name, student.studentClass, student.admissionNumber,
                        student.contactMob, student.fatherName, student.rollNumber,
                    ))
                }
                .mapTo(results) { SearchResultPresentation.fromStudent(it) }
        }

        if (filter == SearchFilterType.ALL || filter == SearchFilterType.STAFF) {
            allStaff
                .filter { staff -> matchesQuery(query, listOf(staff.name, staff.designation, staff.mobile)) }
                .mapTo(results) { SearchResultPresentation.fromStaff(it) }
        }

        _uiState.update { it.copy(results = results) }
    }

    private fun matchesQuery(query: String, fields: List<String>): Boolean =
        fields.any { it.lowercase().contains(query) }

    private fun flattenMenuItems(categories: List<MenuCategory>): List<Pair<MenuItem, String>> {
        val result = mutableListOf<Pair<MenuItem, String>>()
        for (category in categories) {
            for (item in category.menuItems) {
                flattenMenuItem(item, category.title, result)
            }
        }
        return result
    }

    private fun flattenMenuItem(
        item: MenuItem,
        categoryName: String,
        result: MutableList<Pair<MenuItem, String>>,
    ) {
        if (item.isNavigable) {
            result.add(item to categoryName)
        }
        for (child in item.children) {
            flattenMenuItem(child, categoryName, result)
        }
    }

    private fun onResultTapped(result: SearchResultPresentation) {
        when (result.type) {
            SearchResultType.STUDENT -> {
                val id = result.studentId ?: return
                sendEvent(GlobalSearchEvent.NavigateToStudentProfile(id))
            }
            SearchResultType.STAFF -> Unit
            SearchResultType.MODULE -> {
                val menuId = result.menuId ?: return
                sendEvent(GlobalSearchEvent.NavigateToModule(menuId))
            }
        }
    }
}

@Immutable
data class GlobalSearchUiState(
    val searchText: String = "",
    val selectedFilter: SearchFilterType = SearchFilterType.MODULES,
    val results: List<SearchResultPresentation> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isFiltering: Boolean = false,
    val showFilterSheet: Boolean = false,
)

sealed interface GlobalSearchIntent {
    data class OnSearchTextChanged(val text: String) : GlobalSearchIntent
    data class OnFilterSelected(val filter: SearchFilterType) : GlobalSearchIntent
    data object OnFilterClicked : GlobalSearchIntent
    data object DismissFilterSheet : GlobalSearchIntent
    data class OnResultTapped(val result: SearchResultPresentation) : GlobalSearchIntent
    data object OnRetry : GlobalSearchIntent
    data object OnBackClicked : GlobalSearchIntent
}

sealed interface GlobalSearchEvent {
    data object NavigateBack : GlobalSearchEvent
    data class NavigateToStudentProfile(val studentId: Int) : GlobalSearchEvent
    data class NavigateToModule(val menuId: Int) : GlobalSearchEvent
}

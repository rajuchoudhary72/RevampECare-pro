package com.app.ecarepro.feature.schoolcode.search

import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.School
import com.app.ecarepro.core.domain.repository.SchoolRepository
import com.app.ecarepro.core.ui.BaseViewModel
import com.app.ecarepro.core.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SchoolSearchViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository,
) : BaseViewModel<SearchSchoolCodeIntent, SearchSchoolCodeEvent>() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow<UiState<List<School>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<School>>> = _uiState.asStateFlow()

    private var allSchools: List<School> = emptyList()

    init {
        getSchools()
    }

    override fun handleIntent(intent: SearchSchoolCodeIntent) {
        when (intent) {
            is SearchSchoolCodeIntent.CopySchoolCode -> {
                sendEvent(SearchSchoolCodeEvent.NavigateToBack(intent.school.schoolCode))
            }

            SearchSchoolCodeIntent.RefetchSchools -> {
                getSchools()
            }

            is SearchSchoolCodeIntent.SearchQueryChanged -> {
                _searchQuery.value = intent.query
                filterSchools(intent.query)
            }

            is SearchSchoolCodeIntent.ClearQueryChanged -> {
                _searchQuery.value = ""
                // After clearing the query, you might want to show all schools again
                filterSchools("")
            }
        }
    }

    private fun filterSchools(query: String) {
        val filteredSchools = if (query.isBlank()) {
            allSchools
        } else {
            allSchools.filter { school ->
                school.name?.contains(query, ignoreCase = true) == true
                        || school.address?.contains(query, ignoreCase = true) == true
                        || school.schoolCode.contains(query, ignoreCase = true)
            }
        }
        _uiState.value = UiState.Success(filteredSchools)
    }

    private fun getSchools() {
        viewModelScope.launch {
            schoolRepository
                .getSchools()
                .onStart {
                    _uiState.value = UiState.Loading
                }
                .collect { result ->
                    result
                        .onSuccess { schools ->
                            allSchools = schools
                            _uiState.value = UiState.Success(schools)
                        }
                        .onFailure {
                            _uiState.value =
                                UiState.Error(it.message ?: "An unexpected error occurred")
                        }
                }
        }
    }
}

sealed interface SearchSchoolCodeIntent {
    data object ClearQueryChanged : SearchSchoolCodeIntent
    data class SearchQueryChanged(val query: String) : SearchSchoolCodeIntent
    data class CopySchoolCode(val school: School) : SearchSchoolCodeIntent
    data object RefetchSchools : SearchSchoolCodeIntent
}

sealed interface SearchSchoolCodeEvent {
    data class NavigateToBack(val schoolCode: String) : SearchSchoolCodeEvent
}
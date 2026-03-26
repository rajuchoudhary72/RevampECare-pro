package com.app.ecarepro.feature.discipline.screens.appreciationlistscreen

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.StudentProfile
import com.app.ecarepro.core.domain.repository.StudentProfileRepository
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.AssistedViewModelFactory
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.component.personlist.Gender
import com.app.ecarepro.designsystem.core.component.personlist.ListStatsPresentation
import com.app.ecarepro.designsystem.core.component.personlist.PersonPresentation
import com.app.ecarepro.designsystem.core.component.personlist.PersonType
import com.app.ecarepro.feature.discipline.navigation.DisciplineNavigationGraph
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = AppreciationListViewModel.Factory::class)
class AppreciationListViewModel @AssistedInject constructor(
    @Assisted val navKey: DisciplineNavigationGraph.AppreciationList,
    private val studentProfileRepository: StudentProfileRepository,
) : BaseViewModel<AppreciationListIntent, AppreciationListEvent>() {

    private val _uiState: MutableStateFlow<UiState<AppreciationListUiState>> =
        MutableStateFlow(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var allPersons: List<PersonPresentation> = emptyList()
    private var allStudents: List<StudentProfile> = emptyList()

    init {
        fetchStudents()
    }

    override fun handleIntent(intent: AppreciationListIntent) {
        when (intent) {
            AppreciationListIntent.OnBackClicked -> sendEvent(AppreciationListEvent.NavigateBack)
            is AppreciationListIntent.OnSearchQueryChanged -> onSearchQueryChanged(intent.query)
            is AppreciationListIntent.OnClassSelected -> onClassSelected(intent.index)
            is AppreciationListIntent.OnScholarTypeChanged -> fetchStudents(intent.scholarType)
            is AppreciationListIntent.OnAddClicked -> {
                val id = intent.person.id.toIntOrNull() ?: return
                sendEvent(AppreciationListEvent.NavigateToAddAppreciation(id))
            }
            is AppreciationListIntent.OnViewAllClicked -> {
                val id = intent.person.id.toIntOrNull() ?: return
                sendEvent(AppreciationListEvent.NavigateToViewAll(id))
            }
            AppreciationListIntent.OnRefresh -> fetchStudents(isRefreshing = true)
        }
    }

    private fun fetchStudents(scholarType: Int? = null, isRefreshing: Boolean = false) {
        viewModelScope.launch {
            studentProfileRepository.getStudentProfiles(scholarType)
                .onStart {
                    if (!isRefreshing) _uiState.update { UiState.Loading }
                    else updateState { it.copy(isRefreshing = true) }
                }
                .collect { result ->
                    result.onSuccess { students ->
                        allStudents = students
                        allPersons = students.map { it.toPersonPresentation() }
                        val classTabs = listOf("All") + students.map { it.classSTD }.distinct().sorted()
                        _uiState.update {
                            UiState.Success(
                                AppreciationListUiState(
                                    classTabs = classTabs,
                                    filteredPersons = allPersons,
                                    stats = calculateStats(allPersons),
                                )
                            )
                        }
                    }.onFailure { error ->
                        if (isRefreshing) {
                            updateState { it.copy(isRefreshing = false) }
                            sendEvent(AppreciationListEvent.ShowMessage(SnackbarMessage(error.errorMessage(), MessageType.ERROR)))
                        } else {
                            _uiState.update { UiState.Error(error.errorMessage()) }
                        }
                    }
                }
        }
    }

    private fun onSearchQueryChanged(query: String) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        val filtered = filterPersons(currentState.selectedClassIndex, query)
        _uiState.update {
            UiState.Success(currentState.copy(searchQuery = query, filteredPersons = filtered, stats = calculateStats(filtered)))
        }
    }

    private fun onClassSelected(index: Int) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        val filtered = filterPersons(index, currentState.searchQuery)
        _uiState.update {
            UiState.Success(currentState.copy(selectedClassIndex = index, filteredPersons = filtered, stats = calculateStats(filtered)))
        }
    }

    private fun filterPersons(classIndex: Int, searchQuery: String): List<PersonPresentation> {
        val currentState = (_uiState.value as? UiState.Success)?.data
        val classTabs = currentState?.classTabs ?: emptyList()
        val selectedClass = classTabs.getOrNull(classIndex) ?: "All"

        var filtered = if (selectedClass == "All") allPersons
        else allPersons.filter { person -> allStudents.find { it.id == person.id }?.classSTD == selectedClass }

        if (searchQuery.isNotBlank()) {
            filtered = filtered.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.subtitle.contains(searchQuery, ignoreCase = true) ||
                        it.detail.contains(searchQuery, ignoreCase = true)
            }
        }
        return filtered
    }

    private fun calculateStats(persons: List<PersonPresentation>) = ListStatsPresentation(
        total = persons.size,
        maleCount = persons.count { it.gender == Gender.MALE },
        femaleCount = persons.count { it.gender == Gender.FEMALE },
        maleLabel = "Boys",
        femaleLabel = "Girls",
    )

    private fun updateState(update: (AppreciationListUiState) -> AppreciationListUiState) {
        _uiState.update { if (it is UiState.Success) UiState.Success(update(it.data)) else it }
    }

    private fun StudentProfile.toPersonPresentation() = PersonPresentation(
        id = (stID ?: 0).toString(),
        name = name.orEmpty(),
        subtitle = "Class: $classSTD${section?.let { "-$it" } ?: ""} | Roll: ${rollNumber.orEmpty()}",
        detail = "Adm No: ${admissionNumber.orEmpty()}",
        profileImageURL = photo,
        gender = Gender.fromString(gender.orEmpty()),
        personType = PersonType.STUDENT,
    )

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<DisciplineNavigationGraph.AppreciationList, AppreciationListViewModel> {
        override fun create(param: DisciplineNavigationGraph.AppreciationList): AppreciationListViewModel
    }
}

@Immutable
data class AppreciationListUiState(
    val isRefreshing: Boolean = false,
    val searchQuery: String = "",
    val selectedClassIndex: Int = 0,
    val classTabs: List<String> = emptyList(),
    val filteredPersons: List<PersonPresentation> = emptyList(),
    val stats: ListStatsPresentation = ListStatsPresentation.EMPTY,
)

sealed interface AppreciationListIntent {
    data object OnBackClicked : AppreciationListIntent
    data class OnSearchQueryChanged(val query: String) : AppreciationListIntent
    data class OnClassSelected(val index: Int) : AppreciationListIntent
    data class OnScholarTypeChanged(val scholarType: Int?) : AppreciationListIntent
    data class OnAddClicked(val person: PersonPresentation) : AppreciationListIntent
    data class OnViewAllClicked(val person: PersonPresentation) : AppreciationListIntent
    data object OnRefresh : AppreciationListIntent
}

sealed interface AppreciationListEvent {
    data object NavigateBack : AppreciationListEvent
    data class NavigateToAddAppreciation(val studentId: Int) : AppreciationListEvent
    data class NavigateToViewAll(val studentId: Int) : AppreciationListEvent
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : AppreciationListEvent
}

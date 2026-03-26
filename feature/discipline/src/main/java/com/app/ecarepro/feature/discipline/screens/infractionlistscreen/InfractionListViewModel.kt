package com.app.ecarepro.feature.discipline.screens.infractionlistscreen

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.StudentProfile
import com.app.ecarepro.core.domain.model.StaffProfile
import com.app.ecarepro.core.domain.repository.StudentProfileRepository
import com.app.ecarepro.core.domain.repository.StaffProfileRepository
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.AssistedViewModelFactory
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.component.personlist.Gender
import com.app.ecarepro.designsystem.core.component.personlist.ListStatsPresentation
import com.app.ecarepro.designsystem.core.component.personlist.PersonPresentation
import com.app.ecarepro.designsystem.core.component.personlist.PersonType
import com.app.ecarepro.feature.discipline.DisciplineUserType
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

@HiltViewModel(assistedFactory = InfractionListViewModel.Factory::class)
class InfractionListViewModel @AssistedInject constructor(
    @Assisted val navKey: DisciplineNavigationGraph.InfractionList,
    private val studentProfileRepository: StudentProfileRepository,
    private val staffProfileRepository: StaffProfileRepository,
) : BaseViewModel<InfractionListIntent, InfractionListEvent>() {

    val userType: DisciplineUserType = navKey.userType

    private val _uiState: MutableStateFlow<UiState<InfractionListUiState>> =
        MutableStateFlow(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var allPersons: List<PersonPresentation> = emptyList()
    private var allStudentProfiles: List<StudentProfile> = emptyList()
    private var allStaffProfiles: List<StaffProfile> = emptyList()

    init {
        fetchList()
    }

    override fun handleIntent(intent: InfractionListIntent) {
        when (intent) {
            InfractionListIntent.OnBackClicked -> sendEvent(InfractionListEvent.NavigateBack)
            is InfractionListIntent.OnSearchQueryChanged -> onSearchQueryChanged(intent.query)
            is InfractionListIntent.OnClassSelected -> onClassSelected(intent.index)
            is InfractionListIntent.OnScholarTypeChanged -> fetchList(intent.scholarType)
            is InfractionListIntent.OnAddClicked -> {
                val id = intent.person.id.toIntOrNull() ?: return
                sendEvent(InfractionListEvent.NavigateToAddInfraction(userType, id))
            }
            is InfractionListIntent.OnViewAllClicked -> {
                val id = intent.person.id.toIntOrNull() ?: return
                sendEvent(InfractionListEvent.NavigateToViewAll(userType, id))
            }
            InfractionListIntent.OnRefresh -> fetchList(isRefreshing = true)
        }
    }

    private fun fetchList(scholarType: Int? = null, isRefreshing: Boolean = false) {
        viewModelScope.launch {
            if (userType == DisciplineUserType.STUDENT) {
                studentProfileRepository.getStudentProfiles(scholarType)
                    .onStart {
                        if (!isRefreshing) _uiState.update { UiState.Loading }
                        else updateState { it.copy(isRefreshing = true) }
                    }
                    .collect { result ->
                        result.onSuccess { students ->
                            allStudentProfiles = students
                            allPersons = students.map { it.toPersonPresentation() }
                            val classTabs = listOf("All") + students.map { it.classSTD }.distinct().sorted()
                            _uiState.update {
                                UiState.Success(
                                    InfractionListUiState(
                                        classTabs = classTabs,
                                        filteredPersons = allPersons,
                                        stats = calculateStats(allPersons),
                                    )
                                )
                            }
                        }.onFailure { error ->
                            if (isRefreshing) {
                                updateState { it.copy(isRefreshing = false) }
                                sendEvent(InfractionListEvent.ShowMessage(SnackbarMessage(error.errorMessage(), MessageType.ERROR)))
                            } else {
                                _uiState.update { UiState.Error(error.errorMessage()) }
                            }
                        }
                    }
            } else {
                staffProfileRepository.getStaffProfiles()
                    .onStart {
                        if (!isRefreshing) _uiState.update { UiState.Loading }
                        else updateState { it.copy(isRefreshing = true) }
                    }
                    .collect { result ->
                        result.onSuccess { staffList ->
                            allStaffProfiles = staffList
                            allPersons = staffList.map { it.toPersonPresentation() }
                            _uiState.update {
                                UiState.Success(
                                    InfractionListUiState(
                                        classTabs = emptyList(),
                                        filteredPersons = allPersons,
                                        stats = calculateStaffStats(allPersons),
                                    )
                                )
                            }
                        }.onFailure { error ->
                            if (isRefreshing) {
                                updateState { it.copy(isRefreshing = false) }
                                sendEvent(InfractionListEvent.ShowMessage(SnackbarMessage(error.errorMessage(), MessageType.ERROR)))
                            } else {
                                _uiState.update { UiState.Error(error.errorMessage()) }
                            }
                        }
                    }
            }
        }
    }

    private fun onSearchQueryChanged(query: String) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        val filtered = filterPersons(currentState.selectedClassIndex, query)
        _uiState.update {
            UiState.Success(
                currentState.copy(
                    searchQuery = query,
                    filteredPersons = filtered,
                    stats = if (userType == DisciplineUserType.STUDENT) calculateStats(filtered) else calculateStaffStats(filtered),
                )
            )
        }
    }

    private fun onClassSelected(index: Int) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        val filtered = filterPersons(index, currentState.searchQuery)
        _uiState.update {
            UiState.Success(
                currentState.copy(
                    selectedClassIndex = index,
                    filteredPersons = filtered,
                    stats = calculateStats(filtered),
                )
            )
        }
    }

    private fun filterPersons(classIndex: Int, searchQuery: String): List<PersonPresentation> {
        val currentState = (_uiState.value as? UiState.Success)?.data
        val classTabs = currentState?.classTabs ?: emptyList()
        val selectedClass = classTabs.getOrNull(classIndex) ?: "All"

        var filtered = if (selectedClass == "All" || userType == DisciplineUserType.STAFF) {
            allPersons
        } else {
            allPersons.filter { person ->
                allStudentProfiles.find { it.id == person.id }?.classSTD == selectedClass
            }
        }

        if (searchQuery.isNotBlank()) {
            filtered = filtered.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.subtitle.contains(searchQuery, ignoreCase = true) ||
                        it.detail.contains(searchQuery, ignoreCase = true)
            }
        }
        return filtered
    }

    private fun calculateStats(persons: List<PersonPresentation>): ListStatsPresentation {
        return ListStatsPresentation(
            total = persons.size,
            maleCount = persons.count { it.gender == Gender.MALE },
            femaleCount = persons.count { it.gender == Gender.FEMALE },
            maleLabel = "Boys",
            femaleLabel = "Girls",
        )
    }

    private fun calculateStaffStats(persons: List<PersonPresentation>): ListStatsPresentation {
        return ListStatsPresentation(
            total = persons.size,
            maleCount = persons.count { it.gender == Gender.MALE },
            femaleCount = persons.count { it.gender == Gender.FEMALE },
            maleLabel = "Male",
            femaleLabel = "Female",
        )
    }

    private fun updateState(update: (InfractionListUiState) -> InfractionListUiState) {
        _uiState.update { currentState ->
            if (currentState is UiState.Success) {
                UiState.Success(update(currentState.data))
            } else currentState
        }
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

    private fun StaffProfile.toPersonPresentation() = PersonPresentation(
        id = sid.toString(),
        name = name,
        subtitle = "Designation: $designation",
        detail = "Mobile: ${mobile.orEmpty()}",
        profileImageURL = photo,
        gender = Gender.fromString(gender.orEmpty()),
        personType = PersonType.STAFF,
    )

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<DisciplineNavigationGraph.InfractionList, InfractionListViewModel> {
        override fun create(param: DisciplineNavigationGraph.InfractionList): InfractionListViewModel
    }
}

@Immutable
data class InfractionListUiState(
    val isRefreshing: Boolean = false,
    val searchQuery: String = "",
    val selectedClassIndex: Int = 0,
    val classTabs: List<String> = emptyList(),
    val filteredPersons: List<PersonPresentation> = emptyList(),
    val stats: ListStatsPresentation = ListStatsPresentation.EMPTY,
)

sealed interface InfractionListIntent {
    data object OnBackClicked : InfractionListIntent
    data class OnSearchQueryChanged(val query: String) : InfractionListIntent
    data class OnClassSelected(val index: Int) : InfractionListIntent
    data class OnScholarTypeChanged(val scholarType: Int?) : InfractionListIntent
    data class OnAddClicked(val person: PersonPresentation) : InfractionListIntent
    data class OnViewAllClicked(val person: PersonPresentation) : InfractionListIntent
    data object OnRefresh : InfractionListIntent
}

sealed interface InfractionListEvent {
    data object NavigateBack : InfractionListEvent
    data class NavigateToAddInfraction(val userType: DisciplineUserType, val userId: Int) : InfractionListEvent
    data class NavigateToViewAll(val userType: DisciplineUserType, val userId: Int) : InfractionListEvent
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : InfractionListEvent
}

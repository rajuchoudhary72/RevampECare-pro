package com.app.ecarepro.feature.discipline.screens.viewallinfractionscreen

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.discipline.DisciplineUserInfo
import com.app.ecarepro.core.domain.model.discipline.InfractionRecord
import com.app.ecarepro.core.domain.repository.DisciplineRepository
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.AssistedViewModelFactory
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.feature.discipline.DisciplineUserType
import com.app.ecarepro.feature.discipline.navigation.DisciplineNavigationGraph
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ViewAllInfractionsViewModel.Factory::class)
class ViewAllInfractionsViewModel @AssistedInject constructor(
    @Assisted val navKey: DisciplineNavigationGraph.ViewAllInfractions,
    private val disciplineRepository: DisciplineRepository,
) : BaseViewModel<ViewAllInfractionsIntent, ViewAllInfractionsEvent>() {

    val userType: DisciplineUserType = navKey.userType
    private val userId = navKey.userId

    private val _uiState: MutableStateFlow<UiState<ViewAllInfractionsUiState>> =
        MutableStateFlow(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchInfractions()
    }

    override fun handleIntent(intent: ViewAllInfractionsIntent) {
        when (intent) {
            ViewAllInfractionsIntent.OnBackClicked -> sendEvent(ViewAllInfractionsEvent.NavigateBack)
            is ViewAllInfractionsIntent.OnDeleteClicked -> deleteInfraction(intent.infractionID)
            is ViewAllInfractionsIntent.OnComplianceClicked -> {
                sendEvent(ViewAllInfractionsEvent.NavigateToCompliance(intent.infractionID, userType, userId))
            }
            ViewAllInfractionsIntent.OnDismissDeleteSheet -> updateState { it.copy(isDeleteSheetVisible = false, selectedInfractionId = null) }
            is ViewAllInfractionsIntent.OnShowDeleteSheet -> updateState { it.copy(isDeleteSheetVisible = true, selectedInfractionId = intent.infractionID) }
            ViewAllInfractionsIntent.OnConfirmDelete -> {
                val id = (_uiState.value as? UiState.Success)?.data?.selectedInfractionId
                if (id != null) deleteInfraction(id)
            }
            is ViewAllInfractionsIntent.OnSearchQueryChanged -> onSearchQueryChanged(intent.query)
        }
    }

    private fun fetchInfractions() {
        viewModelScope.launch {
            val flow = if (userType == DisciplineUserType.STUDENT) {
                disciplineRepository.getStudentInfractions(userId)
            } else {
                disciplineRepository.getStaffInfractions(userId)
            }

            flow.collect { result ->
                result.onSuccess { details ->
                    _uiState.update {
                        UiState.Success(
                            ViewAllInfractionsUiState(
                                records = details.records,
                                filteredRecords = details.records,
                                userInfo = details.userInfo,
                                showPoints = details.showPoints,
                                totalPoints = details.totalPoints,
                            )
                        )
                    }
                }.onFailure { error ->
                    _uiState.update { UiState.Error(error.errorMessage()) }
                }
            }
        }
    }

    private fun deleteInfraction(infractionID: String) {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, isDeleteSheetVisible = false) }
            val uType = if (userType == DisciplineUserType.STUDENT) null else userType.complianceUType
            disciplineRepository.deleteInfraction(infractionID, uType).collect { result ->
                updateState { it.copy(isLoading = false) }
                result.onSuccess { message ->
                    sendEvent(ViewAllInfractionsEvent.ShowMessage(SnackbarMessage(message, MessageType.SUCCESS)))
                    fetchInfractions()
                }.onFailure { error ->
                    sendEvent(ViewAllInfractionsEvent.ShowMessage(SnackbarMessage(error.errorMessage(), MessageType.ERROR)))
                }
            }
        }
    }

    private fun onSearchQueryChanged(query: String) {
        val state = (_uiState.value as? UiState.Success)?.data ?: return
        val filtered = if (query.isBlank()) {
            state.records
        } else {
            state.records.filter {
                it.infraction.orEmpty().contains(query, ignoreCase = true) ||
                        it.subInfraction.orEmpty().contains(query, ignoreCase = true) ||
                        it.consequences.orEmpty().contains(query, ignoreCase = true)
            }
        }
        updateState { it.copy(searchQuery = query, filteredRecords = filtered) }
    }

    private fun updateState(update: (ViewAllInfractionsUiState) -> ViewAllInfractionsUiState) {
        _uiState.update { currentState ->
            if (currentState is UiState.Success) UiState.Success(update(currentState.data))
            else currentState
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<DisciplineNavigationGraph.ViewAllInfractions, ViewAllInfractionsViewModel> {
        override fun create(param: DisciplineNavigationGraph.ViewAllInfractions): ViewAllInfractionsViewModel
    }
}

@Immutable
data class ViewAllInfractionsUiState(
    val isLoading: Boolean = false,
    val records: List<InfractionRecord> = emptyList(),
    val filteredRecords: List<InfractionRecord> = emptyList(),
    val userInfo: DisciplineUserInfo? = null,
    val showPoints: Boolean = false,
    val totalPoints: Int = 0,
    val searchQuery: String = "",
    val isDeleteSheetVisible: Boolean = false,
    val selectedInfractionId: String? = null,
)

sealed interface ViewAllInfractionsIntent {
    data object OnBackClicked : ViewAllInfractionsIntent
    data class OnDeleteClicked(val infractionID: String) : ViewAllInfractionsIntent
    data class OnComplianceClicked(val infractionID: String) : ViewAllInfractionsIntent
    data class OnShowDeleteSheet(val infractionID: String) : ViewAllInfractionsIntent
    data object OnDismissDeleteSheet : ViewAllInfractionsIntent
    data object OnConfirmDelete : ViewAllInfractionsIntent
    data class OnSearchQueryChanged(val query: String) : ViewAllInfractionsIntent
}

sealed interface ViewAllInfractionsEvent {
    data object NavigateBack : ViewAllInfractionsEvent
    data class NavigateToCompliance(val infractionID: String, val userType: DisciplineUserType, val userId: Int) : ViewAllInfractionsEvent
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : ViewAllInfractionsEvent
}

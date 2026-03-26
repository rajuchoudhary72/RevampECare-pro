package com.app.ecarepro.feature.discipline.screens.viewallappreciationscreen

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.discipline.AppreciationRecord
import com.app.ecarepro.core.domain.model.discipline.DisciplineUserInfo
import com.app.ecarepro.core.domain.repository.DisciplineRepository
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.AssistedViewModelFactory
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.feature.discipline.navigation.DisciplineNavigationGraph
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ViewAllAppreciationsViewModel.Factory::class)
class ViewAllAppreciationsViewModel @AssistedInject constructor(
    @Assisted val navKey: DisciplineNavigationGraph.ViewAllAppreciations,
    private val disciplineRepository: DisciplineRepository,
) : BaseViewModel<ViewAllAppreciationsIntent, ViewAllAppreciationsEvent>() {

    private val studentId = navKey.studentId

    private val _uiState: MutableStateFlow<UiState<ViewAllAppreciationsUiState>> =
        MutableStateFlow(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchAppreciations()
    }

    override fun handleIntent(intent: ViewAllAppreciationsIntent) {
        when (intent) {
            ViewAllAppreciationsIntent.OnBackClicked -> sendEvent(ViewAllAppreciationsEvent.NavigateBack)
            is ViewAllAppreciationsIntent.OnShowDeleteSheet -> updateState { it.copy(isDeleteSheetVisible = true, selectedAppreciationId = intent.appreciationID) }
            ViewAllAppreciationsIntent.OnDismissDeleteSheet -> updateState { it.copy(isDeleteSheetVisible = false, selectedAppreciationId = null) }
            ViewAllAppreciationsIntent.OnConfirmDelete -> {
                val id = (_uiState.value as? UiState.Success)?.data?.selectedAppreciationId
                if (id != null) deleteAppreciation(id)
            }
            is ViewAllAppreciationsIntent.OnSearchQueryChanged -> onSearchQueryChanged(intent.query)
        }
    }

    private fun fetchAppreciations() {
        viewModelScope.launch {
            disciplineRepository.getAppreciations(studentId).collect { result ->
                result.onSuccess { details ->
                    _uiState.update {
                        UiState.Success(
                            ViewAllAppreciationsUiState(
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

    private fun deleteAppreciation(appreciationID: String) {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, isDeleteSheetVisible = false) }
            disciplineRepository.deleteAppreciation(appreciationID).collect { result ->
                updateState { it.copy(isLoading = false) }
                result.onSuccess { message ->
                    sendEvent(ViewAllAppreciationsEvent.ShowMessage(SnackbarMessage(message, MessageType.SUCCESS)))
                    fetchAppreciations()
                }.onFailure { error ->
                    sendEvent(ViewAllAppreciationsEvent.ShowMessage(SnackbarMessage(error.errorMessage(), MessageType.ERROR)))
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
                it.appreciation.orEmpty().contains(query, ignoreCase = true) ||
                        it.subAppreciation.orEmpty().contains(query, ignoreCase = true) ||
                        it.reward.orEmpty().contains(query, ignoreCase = true)
            }
        }
        updateState { it.copy(searchQuery = query, filteredRecords = filtered) }
    }

    private fun updateState(update: (ViewAllAppreciationsUiState) -> ViewAllAppreciationsUiState) {
        _uiState.update { currentState ->
            if (currentState is UiState.Success) UiState.Success(update(currentState.data))
            else currentState
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<DisciplineNavigationGraph.ViewAllAppreciations, ViewAllAppreciationsViewModel> {
        override fun create(param: DisciplineNavigationGraph.ViewAllAppreciations): ViewAllAppreciationsViewModel
    }
}

@Immutable
data class ViewAllAppreciationsUiState(
    val isLoading: Boolean = false,
    val records: List<AppreciationRecord> = emptyList(),
    val filteredRecords: List<AppreciationRecord> = emptyList(),
    val userInfo: DisciplineUserInfo? = null,
    val showPoints: Boolean = false,
    val totalPoints: Int = 0,
    val searchQuery: String = "",
    val isDeleteSheetVisible: Boolean = false,
    val selectedAppreciationId: String? = null,
)

sealed interface ViewAllAppreciationsIntent {
    data object OnBackClicked : ViewAllAppreciationsIntent
    data class OnShowDeleteSheet(val appreciationID: String) : ViewAllAppreciationsIntent
    data object OnDismissDeleteSheet : ViewAllAppreciationsIntent
    data object OnConfirmDelete : ViewAllAppreciationsIntent
    data class OnSearchQueryChanged(val query: String) : ViewAllAppreciationsIntent
}

sealed interface ViewAllAppreciationsEvent {
    data object NavigateBack : ViewAllAppreciationsEvent
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : ViewAllAppreciationsEvent
}

package com.app.ecarepro.feature.fee.estimate

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.repository.FeeRepository
import com.app.ecarepro.core.ui.viewmodel.AssistedViewModelFactory
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.feature.fee.common.formatAsCurrency
import com.app.ecarepro.feature.fee.navigation.FeeNavGraph
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = EstimateListViewModel.Factory::class)
class EstimateListViewModel @AssistedInject constructor(
    @Assisted val navKey: FeeNavGraph.EstimateList,
    private val repository: FeeRepository,
) : BaseViewModel<EstimateListIntent, EstimateListEvent>() {

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<FeeNavGraph.EstimateList, EstimateListViewModel> {
        override fun create(param: FeeNavGraph.EstimateList): EstimateListViewModel
    }

    private val _uiState = MutableStateFlow(EstimateListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        handleIntent(EstimateListIntent.LoadData)
    }

    override fun handleIntent(intent: EstimateListIntent) {
        when (intent) {
            is EstimateListIntent.LoadData -> loadData()
            is EstimateListIntent.ToggleExpanded -> toggleExpanded(intent.index)
            is EstimateListIntent.OnBackClicked -> sendEvent(EstimateListEvent.NavigateBack)
        }
    }

    private fun loadData() {
        _uiState.update { it.copy(isLoading = true, isError = false) }
        viewModelScope.launch {
            repository.getEstimateReport(
                dateFrom = navKey.dateFrom,
                dateTo = navKey.dateTo,
                classId = navKey.classId,
                feeTypeId = navKey.feeTypeId,
                schoolId = navKey.schoolId,
                sectionId = navKey.sectionId,
                installId = navKey.installId,
            ).collect { result ->
                result.onSuccess { items ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isError = false,
                            items = items.map { e ->
                                EstimateItemUi(
                                    headName = e.headName,
                                    actualAmount = formatAsCurrency(e.actualAmount),
                                    concession = formatAsCurrency(e.concession),
                                    receivedAmount = formatAsCurrency(e.receivedAmount),
                                    duesAmount = formatAsCurrency(e.duesAmount),
                                )
                            },
                        )
                    }
                }.onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, isError = true) }
                    sendEvent(EstimateListEvent.ShowMessage(SnackbarMessage(error.message ?: "Failed to load data", MessageType.ERROR)))
                }
            }
        }
    }

    private fun toggleExpanded(index: Int) {
        _uiState.update { state ->
            state.copy(expandedIndex = if (state.expandedIndex == index) null else index)
        }
    }
}

@Immutable
data class EstimateListUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val items: List<EstimateItemUi> = emptyList(),
    val expandedIndex: Int? = null,
)

@Immutable
data class EstimateItemUi(
    val headName: String,
    val actualAmount: String,
    val concession: String,
    val receivedAmount: String,
    val duesAmount: String,
)

sealed interface EstimateListIntent {
    data object LoadData : EstimateListIntent
    data class ToggleExpanded(val index: Int) : EstimateListIntent
    data object OnBackClicked : EstimateListIntent
}

sealed interface EstimateListEvent {
    data object NavigateBack : EstimateListEvent
    data class ShowMessage(val message: SnackbarMessage) : EstimateListEvent
}

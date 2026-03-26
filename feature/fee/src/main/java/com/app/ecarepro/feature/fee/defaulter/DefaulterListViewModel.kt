package com.app.ecarepro.feature.fee.defaulter

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

@HiltViewModel(assistedFactory = DefaulterListViewModel.Factory::class)
class DefaulterListViewModel @AssistedInject constructor(
    @Assisted val navKey: FeeNavGraph.DefaulterList,
    private val repository: FeeRepository,
) : BaseViewModel<DefaulterListIntent, DefaulterListEvent>() {

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<FeeNavGraph.DefaulterList, DefaulterListViewModel> {
        override fun create(param: FeeNavGraph.DefaulterList): DefaulterListViewModel
    }

    private val _uiState = MutableStateFlow(
        DefaulterListUiState(installmentNames = navKey.installmentNames)
    )
    val uiState = _uiState.asStateFlow()

    init {
        handleIntent(DefaulterListIntent.LoadData)
    }

    override fun handleIntent(intent: DefaulterListIntent) {
        when (intent) {
            is DefaulterListIntent.LoadData -> loadData()
            is DefaulterListIntent.OnBackClicked -> sendEvent(DefaulterListEvent.NavigateBack)
        }
    }

    private fun loadData() {
        _uiState.update { it.copy(isLoading = true, isError = false) }
        viewModelScope.launch {
            repository.getDefaulterReport(
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
                            items = items.map { d ->
                                DefaulterItemUi(
                                    nameWithClass = d.nameWithClass,
                                    admNo = d.admNo,
                                    contactNo = d.contactNo,
                                    formattedAmount = formatAsCurrency(d.amount),
                                    installmentName = d.installmentName,
                                )
                            },
                        )
                    }
                }.onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, isError = true) }
                    sendEvent(DefaulterListEvent.ShowMessage(SnackbarMessage(error.message ?: "Failed to load data", MessageType.ERROR)))
                }
            }
        }
    }
}

@Immutable
data class DefaulterListUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val installmentNames: String = "",
    val items: List<DefaulterItemUi> = emptyList(),
)

@Immutable
data class DefaulterItemUi(
    val nameWithClass: String,
    val admNo: String,
    val contactNo: String,
    val formattedAmount: String,
    val installmentName: String,
)

sealed interface DefaulterListIntent {
    data object LoadData : DefaulterListIntent
    data object OnBackClicked : DefaulterListIntent
}

sealed interface DefaulterListEvent {
    data object NavigateBack : DefaulterListEvent
    data class ShowMessage(val message: SnackbarMessage) : DefaulterListEvent
}

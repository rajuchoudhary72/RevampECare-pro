package com.app.ecarepro.feature.fee.filter

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.fee.FilterItemDomain
import com.app.ecarepro.core.domain.repository.FeeRepository
import com.app.ecarepro.core.ui.viewmodel.AssistedViewModelFactory
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.feature.fee.navigation.FeeNavGraph
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@HiltViewModel(assistedFactory = ReportFilterViewModel.Factory::class)
class ReportFilterViewModel @AssistedInject constructor(
    @Assisted val navKey: FeeNavGraph.ReportFilter,
    private val repository: FeeRepository,
) : BaseViewModel<ReportFilterIntent, ReportFilterEvent>() {

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<FeeNavGraph.ReportFilter, ReportFilterViewModel> {
        override fun create(param: FeeNavGraph.ReportFilter): ReportFilterViewModel
    }

    private val today = LocalDate.now()
    private val firstOfMonth = today.withDayOfMonth(1)
    private val apiFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US)

    private val _uiState = MutableStateFlow(
        ReportFilterUiState(
            title = if (navKey.flowType == "defaulter") "Defaulter Report" else "Estimate Report",
            startMillis = firstOfMonth.toMillis(),
            endMillis = today.toMillis(),
            startDateApi = firstOfMonth.format(apiFormatter),
            endDateApi = today.format(apiFormatter),
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        handleIntent(ReportFilterIntent.LoadFilters)
    }

    override fun handleIntent(intent: ReportFilterIntent) {
        when (intent) {
            is ReportFilterIntent.LoadFilters -> loadFilters()
            is ReportFilterIntent.SelectDateRange -> {
                val startDate = java.time.Instant.ofEpochMilli(intent.startMillis)
                    .atZone(ZoneId.systemDefault()).toLocalDate()
                val endDate = java.time.Instant.ofEpochMilli(intent.endMillis)
                    .atZone(ZoneId.systemDefault()).toLocalDate()
                _uiState.update {
                    it.copy(
                        startMillis = intent.startMillis,
                        endMillis = intent.endMillis,
                        startDateApi = startDate.format(apiFormatter),
                        endDateApi = endDate.format(apiFormatter),
                    )
                }
            }
            is ReportFilterIntent.OpenSheet -> _uiState.update { it.copy(activeSheet = intent.sheetType) }
            is ReportFilterIntent.DismissSheet -> _uiState.update { it.copy(activeSheet = null) }
            is ReportFilterIntent.ConfirmClasses -> _uiState.update { it.copy(selectedClassIds = intent.ids, activeSheet = null) }
            is ReportFilterIntent.ConfirmSchools -> _uiState.update { it.copy(selectedSchoolIds = intent.ids, activeSheet = null) }
            is ReportFilterIntent.ConfirmFeeTypes -> _uiState.update { it.copy(selectedFeeTypeIds = intent.ids, activeSheet = null) }
            is ReportFilterIntent.ConfirmInstallments -> _uiState.update { it.copy(selectedInstallmentIds = intent.ids, activeSheet = null) }
            is ReportFilterIntent.ConfirmSections -> _uiState.update { it.copy(selectedSectionIds = intent.ids, activeSheet = null) }
            is ReportFilterIntent.Submit -> submit()
            is ReportFilterIntent.OnBackClicked -> sendEvent(ReportFilterEvent.NavigateBack)
        }
    }

    private fun loadFilters() {
        _uiState.update { it.copy(isLoading = true, isError = false) }
        viewModelScope.launch {
            repository.getReportFilters().collect { result ->
                result.onSuccess { filters ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            classes = filters.classes,
                            schools = filters.schools,
                            feeTypes = filters.feeTypes,
                            installments = filters.installments,
                            sections = filters.sections,
                        )
                    }
                }.onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, isError = true) }
                    sendEvent(ReportFilterEvent.ShowMessage(SnackbarMessage(error.message ?: "Failed to load filters", MessageType.ERROR)))
                }
            }
        }
    }

    private fun submit() {
        val state = _uiState.value
        val installmentNames = state.installments
            .filter { it.id in state.selectedInstallmentIds }
            .joinToString(", ") { it.name }

        val classId = state.selectedClassIds.joinToString(",")
        val feeTypeId = state.selectedFeeTypeIds.joinToString(",")
        val schoolId = state.selectedSchoolIds.joinToString(",")
        val sectionId = state.selectedSectionIds.joinToString(",")
        val installId = state.selectedInstallmentIds.joinToString(",")

        if (navKey.flowType == "defaulter") {
            sendEvent(
                ReportFilterEvent.NavigateToDefaulter(
                    dateFrom = state.startDateApi,
                    dateTo = state.endDateApi,
                    classId = classId,
                    feeTypeId = feeTypeId,
                    schoolId = schoolId,
                    sectionId = sectionId,
                    installId = installId,
                    installmentNames = installmentNames,
                )
            )
        } else {
            sendEvent(
                ReportFilterEvent.NavigateToEstimate(
                    dateFrom = state.startDateApi,
                    dateTo = state.endDateApi,
                    classId = classId,
                    feeTypeId = feeTypeId,
                    schoolId = schoolId,
                    sectionId = sectionId,
                    installId = installId,
                )
            )
        }
    }

    private fun LocalDate.toMillis(): Long =
        atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

@Immutable
data class ReportFilterUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val title: String = "",
    val startMillis: Long = 0L,
    val endMillis: Long = 0L,
    val startDateApi: String = "",
    val endDateApi: String = "",
    val classes: List<FilterItemDomain> = emptyList(),
    val schools: List<FilterItemDomain> = emptyList(),
    val feeTypes: List<FilterItemDomain> = emptyList(),
    val installments: List<FilterItemDomain> = emptyList(),
    val sections: List<FilterItemDomain> = emptyList(),
    val selectedClassIds: Set<String> = emptySet(),
    val selectedSchoolIds: Set<String> = emptySet(),
    val selectedFeeTypeIds: Set<String> = emptySet(),
    val selectedInstallmentIds: Set<String> = emptySet(),
    val selectedSectionIds: Set<String> = emptySet(),
    val activeSheet: FilterSheetType? = null,
) {
    val isSubmitEnabled: Boolean get() = selectedInstallmentIds.isNotEmpty()
}

enum class FilterSheetType {
    CLASSES, SCHOOLS, FEE_TYPES, INSTALLMENTS, SECTIONS
}

sealed interface ReportFilterIntent {
    data object LoadFilters : ReportFilterIntent
    data class SelectDateRange(val startMillis: Long, val endMillis: Long) : ReportFilterIntent
    data class OpenSheet(val sheetType: FilterSheetType) : ReportFilterIntent
    data object DismissSheet : ReportFilterIntent
    data class ConfirmClasses(val ids: Set<String>) : ReportFilterIntent
    data class ConfirmSchools(val ids: Set<String>) : ReportFilterIntent
    data class ConfirmFeeTypes(val ids: Set<String>) : ReportFilterIntent
    data class ConfirmInstallments(val ids: Set<String>) : ReportFilterIntent
    data class ConfirmSections(val ids: Set<String>) : ReportFilterIntent
    data object Submit : ReportFilterIntent
    data object OnBackClicked : ReportFilterIntent
}

sealed interface ReportFilterEvent {
    data object NavigateBack : ReportFilterEvent
    data class ShowMessage(val message: SnackbarMessage) : ReportFilterEvent
    data class NavigateToDefaulter(
        val dateFrom: String,
        val dateTo: String,
        val classId: String,
        val feeTypeId: String,
        val schoolId: String,
        val sectionId: String,
        val installId: String,
        val installmentNames: String,
    ) : ReportFilterEvent
    data class NavigateToEstimate(
        val dateFrom: String,
        val dateTo: String,
        val classId: String,
        val feeTypeId: String,
        val schoolId: String,
        val sectionId: String,
        val installId: String,
    ) : ReportFilterEvent
}

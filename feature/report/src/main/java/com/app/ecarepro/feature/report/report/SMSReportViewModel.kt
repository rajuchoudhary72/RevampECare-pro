package com.app.ecarepro.feature.report.report

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.smsreport.SMSReportItem
import com.app.ecarepro.core.domain.model.smsreport.SMSTypeItem
import com.app.ecarepro.core.domain.repository.SMSReportRepository
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SMSReportViewModel @Inject constructor(
    private val repository: SMSReportRepository,
) : BaseViewModel<SMSReportIntent, SMSReportEvent>() {

    private val _uiState = MutableStateFlow(SMSReportUiState())
    val uiState = _uiState.asStateFlow()

    init {
        handleIntent(SMSReportIntent.LoadInitialData)
    }

    override fun handleIntent(intent: SMSReportIntent) {
        when (intent) {
            is SMSReportIntent.LoadInitialData -> loadInitialData()
            is SMSReportIntent.SelectDateRange -> {
                _uiState.update { it.copy(startDate = intent.startMillis, endDate = intent.endMillis) }
                fetchReport()
            }
            is SMSReportIntent.SelectSMSType -> onSMSTypeSelected(intent.typeID)
            is SMSReportIntent.OpenTypePicker -> _uiState.update { it.copy(showTypePicker = true) }
            is SMSReportIntent.DismissTypePicker -> _uiState.update { it.copy(showTypePicker = false) }
            is SMSReportIntent.Retry -> fetchReport()
            is SMSReportIntent.OnBackClicked -> sendEvent(SMSReportEvent.NavigateBack)
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            repository.getSMSTypes().collect { result ->
                result.onSuccess { types -> _uiState.update { it.copy(smsTypes = types) } }
            }
        }
        fetchReport()
    }

    private fun onSMSTypeSelected(typeID: Int) {
        _uiState.update {
            it.copy(
                selectedTypeID = if (typeID == 0) null else typeID,
                showTypePicker = false,
            )
        }
        fetchReport()
    }

    private fun fetchReport() {
        val state = _uiState.value
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val fromDate = sdf.format(state.startDate)
        val tillDate = sdf.format(state.endDate)
        val smsType = state.selectedTypeID ?: 0

        _uiState.update { it.copy(isLoading = true, isError = false) }
        viewModelScope.launch {
            repository.getSMSReport(fromDate, tillDate, smsType).collect { result ->
                result.onSuccess { items ->
                    _uiState.update { it.copy(isLoading = false, items = items) }
                }.onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, isError = true) }
                    sendEvent(
                        SMSReportEvent.ShowMessage(
                            SnackbarMessage(error.message ?: "Error loading SMS report", MessageType.ERROR)
                        )
                    )
                }
            }
        }
    }
}

@Immutable
data class SMSReportUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val startDate: Long = getFirstDayOfMonth(),
    val endDate: Long = getLastDayOfMonth(),
    val smsTypes: List<SMSTypeItem> = emptyList(),
    val selectedTypeID: Int? = null,
    val items: List<SMSReportItem> = emptyList(),
    val showTypePicker: Boolean = false,
) {
    val selectedTypeName: String
        get() = if (selectedTypeID == null) "Select SMS type"
                else smsTypes.find { it.typeID == selectedTypeID }?.subject ?: "Select SMS type"

    val typePickerOptions: List<String>
        get() = listOf("All") + smsTypes.map { it.subject }

    val selectedTypeOption: String
        get() = if (selectedTypeID == null) "All"
                else smsTypes.find { it.typeID == selectedTypeID }?.subject ?: "All"
}

private fun getFirstDayOfMonth(): Long {
    val cal = Calendar.getInstance()
    cal.set(Calendar.DAY_OF_MONTH, 1)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}

private fun getLastDayOfMonth(): Long {
    val cal = Calendar.getInstance()
    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
    cal.set(Calendar.HOUR_OF_DAY, 23)
    cal.set(Calendar.MINUTE, 59)
    cal.set(Calendar.SECOND, 59)
    return cal.timeInMillis
}

sealed interface SMSReportIntent {
    data object LoadInitialData : SMSReportIntent
    data class SelectDateRange(val startMillis: Long, val endMillis: Long) : SMSReportIntent
    data class SelectSMSType(val typeID: Int) : SMSReportIntent
    data object OpenTypePicker : SMSReportIntent
    data object DismissTypePicker : SMSReportIntent
    data object Retry : SMSReportIntent
    data object OnBackClicked : SMSReportIntent
}

sealed interface SMSReportEvent {
    data object NavigateBack : SMSReportEvent
    data class ShowMessage(val message: SnackbarMessage) : SMSReportEvent
}

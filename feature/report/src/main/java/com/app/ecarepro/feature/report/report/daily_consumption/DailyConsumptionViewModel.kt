package com.app.ecarepro.feature.report.report.daily_consumption

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.sms.DailyConsumptionItem
import com.app.ecarepro.core.domain.repository.SmsConsumptionRepository
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
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DailyConsumptionViewModel @Inject constructor(
    private val repository: SmsConsumptionRepository,
) : BaseViewModel<DailyConsumptionIntent, DailyConsumptionEvent>() {

    private val _uiState = MutableStateFlow(DailyConsumptionUiState())
    val uiState = _uiState.asStateFlow()

    init {
        handleIntent(DailyConsumptionIntent.LoadData)
    }

    override fun handleIntent(intent: DailyConsumptionIntent) {
        when (intent) {
            is DailyConsumptionIntent.LoadData -> fetchConsumption()
            is DailyConsumptionIntent.SelectDateRange -> {
                _uiState.update { it.copy(startDate = intent.startMillis, endDate = intent.endMillis) }
                fetchConsumption()
            }
            is DailyConsumptionIntent.Retry -> fetchConsumption()
            is DailyConsumptionIntent.OnBackClicked -> sendEvent(DailyConsumptionEvent.NavigateBack)
        }
    }

    private fun fetchConsumption() {
        val state = _uiState.value
        val fromDate = formatDate(state.startDate)
        val toDate = formatDate(state.endDate)

        _uiState.update { it.copy(isLoading = true, isError = false) }
        viewModelScope.launch {
            repository.getSMSConsumption(fromDate, toDate).collect { result ->
                result.onSuccess { items ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            items = items,
                            grandTotal = items.sumOf { item -> item.count }.toString(),
                        )
                    }
                }.onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, isError = true) }
                    sendEvent(
                        DailyConsumptionEvent.ShowMessage(
                            SnackbarMessage(error.message ?: "Error", MessageType.ERROR)
                        )
                    )
                }
            }
        }
    }

    private fun formatDate(millis: Long): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(millis))
}

@Immutable
data class DailyConsumptionUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val startDate: Long = getFirstDayOfMonth(),
    val endDate: Long = System.currentTimeMillis(),
    val items: List<DailyConsumptionItem> = emptyList(),
    val grandTotal: String = "0",
)

private fun getFirstDayOfMonth(): Long {
    val cal = Calendar.getInstance()
    cal.set(Calendar.DAY_OF_MONTH, 1)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}

sealed interface DailyConsumptionIntent {
    data object LoadData : DailyConsumptionIntent
    data class SelectDateRange(val startMillis: Long, val endMillis: Long) : DailyConsumptionIntent
    data object Retry : DailyConsumptionIntent
    data object OnBackClicked : DailyConsumptionIntent
}

sealed interface DailyConsumptionEvent {
    data object NavigateBack : DailyConsumptionEvent
    data class ShowMessage(val message: SnackbarMessage) : DailyConsumptionEvent
}

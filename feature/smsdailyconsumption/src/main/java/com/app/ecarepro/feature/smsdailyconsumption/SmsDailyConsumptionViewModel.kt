package com.app.ecarepro.feature.smsdailyconsumption

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.sms.DailyConsumptionItem
import com.app.ecarepro.core.domain.repository.SmsConsumptionRepository
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
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
class SmsDailyConsumptionViewModel @Inject constructor(
    private val repository: SmsConsumptionRepository,
) : BaseViewModel<SmsDailyConsumptionIntent, SmsDailyConsumptionEvent>() {

    private val _uiState = MutableStateFlow(SmsDailyConsumptionUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetch()
    }

    override fun handleIntent(intent: SmsDailyConsumptionIntent) {
        when (intent) {
            SmsDailyConsumptionIntent.OnBackClicked -> sendEvent(SmsDailyConsumptionEvent.NavigateBack)
            SmsDailyConsumptionIntent.OnRetry -> fetch()
            is SmsDailyConsumptionIntent.OnDateRangeSelected -> {
                _uiState.update { it.copy(fromDate = intent.fromDate, toDate = intent.toDate) }
                fetch()
            }
        }
    }

    private fun fetch() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isError = false) }
            repository.getSMSConsumption(_uiState.value.fromDate, _uiState.value.toDate)
                .collect { result ->
                    result.fold(
                        onSuccess = { items ->
                            _uiState.update {
                                it.copy(isLoading = false, isError = false, items = items)
                            }
                        },
                        onFailure = { error ->
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isError = true,
                                    errorMessage = error.message ?: "Something went wrong",
                                )
                            }
                        },
                    )
                }
        }
    }
}

@Immutable
data class SmsDailyConsumptionUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val errorMessage: String = "",
    val fromDate: String = defaultFromDate(),
    val toDate: String = defaultToDate(),
    val items: List<DailyConsumptionItem> = emptyList(),
) {
    val totalSms: Int get() = items.sumOf { it.count }
}

private fun defaultFromDate(): String {
    val cal = Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1) }
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
}

private fun defaultToDate(): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)

sealed interface SmsDailyConsumptionIntent {
    data object OnBackClicked : SmsDailyConsumptionIntent
    data object OnRetry : SmsDailyConsumptionIntent
    data class OnDateRangeSelected(val fromDate: String, val toDate: String) : SmsDailyConsumptionIntent
}

sealed interface SmsDailyConsumptionEvent {
    data object NavigateBack : SmsDailyConsumptionEvent
}

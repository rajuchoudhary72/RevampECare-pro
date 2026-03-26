package com.app.ecarepro.feature.fee.collection

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.repository.FeeRepository
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.feature.fee.common.formatAsCurrency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val repository: FeeRepository,
) : BaseViewModel<CollectionIntent, CollectionEvent>() {

    private val apiFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private val _uiState = MutableStateFlow(
        CollectionUiState(
            startMillis = firstOfCurrentMonthMillis(),
            endMillis = todayMillis(),
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        handleIntent(CollectionIntent.Load)
    }

    override fun handleIntent(intent: CollectionIntent) {
        when (intent) {
            is CollectionIntent.Load -> loadCollections()
            is CollectionIntent.SelectDateRange -> {
                _uiState.update { it.copy(startMillis = intent.startMillis, endMillis = intent.endMillis) }
                loadCollections()
            }
            is CollectionIntent.OnBackClicked -> sendEvent(CollectionEvent.NavigateBack)
        }
    }

    private fun loadCollections() {
        val state = _uiState.value
        val dateFrom = apiFormatter.format(state.startMillis)
        val dateTo = apiFormatter.format(state.endMillis)
        _uiState.update { it.copy(isLoading = true, isError = false, message = "") }
        viewModelScope.launch {
            repository.getCollections(dateFrom = dateFrom, dateTo = dateTo).collect { result ->
                result.onSuccess { collections ->
                    val items = collections.map { collection ->
                        CollectionItemUi(
                            displayDate = collection.formattedDate,
                            displayDayOfWeek = collection.dayOfWeek,
                            displayAmount = collection.formattedAmount,
                            amount = collection.amount,
                        )
                    }
                    val grandTotal = items.sumOf { it.amount }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isError = false,
                            items = items,
                            grandTotal = formatAsCurrency(grandTotal),
                        )
                    }
                }.onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isError = true,
                            message = error.message ?: "Failed to load collections",
                        )
                    }
                    sendEvent(CollectionEvent.ShowMessage(SnackbarMessage(error.message ?: "Failed to load collections", MessageType.ERROR)))
                }
            }
        }
    }

    private fun firstOfCurrentMonthMillis(): Long {
        val today = LocalDate.now()
        return today.withDayOfMonth(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    private fun todayMillis(): Long {
        return LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }
}

@Immutable
data class CollectionUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val message: String = "",
    val startMillis: Long = 0L,
    val endMillis: Long = 0L,
    val items: List<CollectionItemUi> = emptyList(),
    val grandTotal: String = "₹ 0.00",
)

@Immutable
data class CollectionItemUi(
    val displayDate: String,
    val displayDayOfWeek: String,
    val displayAmount: String,
    val amount: Double,
)

sealed interface CollectionIntent {
    data object Load : CollectionIntent
    data class SelectDateRange(val startMillis: Long, val endMillis: Long) : CollectionIntent
    data object OnBackClicked : CollectionIntent
}

sealed interface CollectionEvent {
    data object NavigateBack : CollectionEvent
    data class ShowMessage(val message: SnackbarMessage) : CollectionEvent
}

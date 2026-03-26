package com.app.ecarepro.feature.calendar

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.activity_calendar.ActivityMonth
import com.app.ecarepro.core.domain.repository.ActivityCalendarRepository
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: ActivityCalendarRepository,
) : BaseViewModel<CalendarIntent, CalendarEvent>() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchActivityCalendar()
    }

    override fun handleIntent(intent: CalendarIntent) {
        when (intent) {
            CalendarIntent.OnBackClicked -> viewModelScope.launch {
                sendEvent(CalendarEvent.NavigateBack)
            }
            is CalendarIntent.OnMonthTabClicked -> selectMonth(intent.index)
            is CalendarIntent.OnPageSwiped -> selectMonth(intent.index)
            CalendarIntent.OnRetry -> fetchActivityCalendar()
        }
    }

    private fun fetchActivityCalendar() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isError = false) }
            repository.getActivityCalendar().collect { result ->
                result.fold(
                    onSuccess = { (session, months) ->
                        val currentMonthIndex = months.indexOfFirst { it.isCurrent }
                            .takeIf { it >= 0 } ?: 0
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isError = false,
                                session = session,
                                months = months,
                                selectedMonthIndex = currentMonthIndex,
                            )
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

    private fun selectMonth(index: Int) {
        _uiState.update { it.copy(selectedMonthIndex = index) }
    }
}

@Immutable
data class CalendarUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val errorMessage: String = "",
    val session: String = "",
    val months: List<ActivityMonth> = emptyList(),
    val selectedMonthIndex: Int = 0,
) {
    val navTitle: String
        get() = if (session.isEmpty()) "Activity Calendar" else "Activity Calendar ($session)"

    val monthTabLabels: List<String>
        get() = months.map { it.monthName }

    val currentMonth: ActivityMonth?
        get() = months.getOrNull(selectedMonthIndex)
}

sealed interface CalendarIntent {
    data object OnBackClicked : CalendarIntent
    data class OnMonthTabClicked(val index: Int) : CalendarIntent
    data class OnPageSwiped(val index: Int) : CalendarIntent
    data object OnRetry : CalendarIntent
}

sealed interface CalendarEvent {
    data object NavigateBack : CalendarEvent
}

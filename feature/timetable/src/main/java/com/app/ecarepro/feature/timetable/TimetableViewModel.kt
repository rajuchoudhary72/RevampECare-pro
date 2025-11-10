package com.app.ecarepro.feature.timetable

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimetableViewModel @Inject constructor(
) : BaseViewModel<TimetableIntent, TimetableEvent>() {

    private val _uiState = MutableStateFlow(TimetableUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val mockTimetable = listOf(
            TimetableEntry(
                "1",
                "11-C",
                "Business studies",
                "07:30 AM - 08:30 AM",
                "60 mins",
                isCurrent = false
            ),
            TimetableEntry(
                "2",
                "12-A",
                "Business studies",
                "08:30 AM - 09:30 AM",
                "60 mins",
                isCurrent = false
            ),
            TimetableEntry(
                "3",
                "11-A",
                "Business studies",
                "09:30 AM - 10:30 AM",
                "60 mins",
                isCurrent = true
            ),
            TimetableEntry(
                "4",
                "10-A",
                "Physics",
                "10:30 AM - 11:30 AM",
                "60 mins",
                isCurrent = false
            ),
            TimetableEntry(type = "Recess", details = "Recess (11:30 PM - 12:30 AM)"),
            TimetableEntry(
                "5",
                "2-A",
                "Physics",
                "12:30 AM - 01:30 PM",
                "60 mins",
                isCurrent = false
            ),
            TimetableEntry(
                "6",
                "2-A",
                "Activity",
                "01:30 PM - 02:30 PM",
                "60 mins",
                isCurrent = false
            ),
            TimetableEntry(
                "7",
                "12-B",
                "Business studies",
                "02:30 PM - 03:30 AM",
                "60 mins",
                isCurrent = false
            ),
        )

        _uiState.update {
            it.copy(
                days = listOf("Day 1", "Day 2", "Day 3", "Day 4", "Day 5"),
                timetables = mapOf(
                    0 to mockTimetable, // Day 1
                    1 to emptyList(),      // Day 2
                    2 to mockTimetable.shuffled(), // Day 3
                    3 to emptyList(),      // Day 4
                    4 to mockTimetable.take(3) // Day 5
                )
            )
        }
    }

    override fun handleIntent(intent: TimetableIntent) {
        when (intent) {
            is TimetableIntent.OnDaySelected -> {
                _uiState.update { it.copy(selectedDayIndex = intent.index) }
            }

            TimetableIntent.OnBackClicked -> {
                viewModelScope.launch {
                    sendEvent(TimetableEvent.NavigateBack)
                }
            }
        }
    }
}

@Immutable
data class TimetableUiState(
    val isLoading: Boolean = false,
    val days: List<String> = emptyList(),
    val selectedDayIndex: Int = 0,
    val timetables: Map<Int, List<TimetableEntry>> = emptyMap(),
)

@Immutable
data class TimetableEntry(
    val period: String? = null,
    val className: String? = null,
    val subject: String? = null,
    val time: String? = null,
    val duration: String? = null,
    val isCurrent: Boolean = false,
    val type: String = "Period", // "Period" or "Recess"
    val details: String? = null, // For recess
)

sealed interface TimetableIntent {
    data class OnDaySelected(val index: Int) : TimetableIntent
    data object OnBackClicked : TimetableIntent
}

sealed interface TimetableEvent {
    data object NavigateBack : TimetableEvent
}
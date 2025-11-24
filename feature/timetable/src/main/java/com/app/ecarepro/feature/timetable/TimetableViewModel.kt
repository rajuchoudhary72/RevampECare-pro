package com.app.ecarepro.feature.timetable

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.ext.getTodayDayNumber
import com.app.ecarepro.core.domain.model.Timetable
import com.app.ecarepro.core.domain.model.TimetableData
import com.app.ecarepro.core.domain.repository.AcademicRepository
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimetableViewModel @Inject constructor(
    private val academicRepository: AcademicRepository,
) : BaseViewModel<TimetableIntent, TimetableEvent>() {

    private val _uiState: MutableStateFlow<UiState<TimetableUiState>> =
        MutableStateFlow(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchTeacherTimetable()
    }

    private fun fetchTeacherTimetable() {
        viewModelScope.launch {
            academicRepository
                .getTeacherTimeline()
                .onStart {
                    _uiState.update { UiState.Loading }
                }
                .collect { result ->
                    result
                        .onSuccess { data ->
                            val days = data.data ?: emptyList()
                            val timetableMap: Map<Int, List<Timetable>> = days
                                .mapIndexedNotNull() { index, data -> data.timeTable?.let { index to it } }
                                .associate { it }

                            // Calculate today's day index
                            val todayDayNo = getTodayDayNumber()
                            val initialDayIndex = days.indexOfFirst { it.dayNo == todayDayNo }
                                .takeIf { it >= 0 } ?: 0

                            _uiState.update {
                                UiState.Success(
                                    TimetableUiState(
                                        days = days,
                                        timetables = timetableMap,
                                        selectedDayIndex = initialDayIndex
                                    )
                                )
                            }

                        }
                        .onFailure { error ->
                            UiState.Error(error.errorMessage())
                        }
                }
        }
    }


    override fun handleIntent(intent: TimetableIntent) {
        when (intent) {
            is TimetableIntent.OnDaySelected -> {
                onDaySelected(intent)
            }

            TimetableIntent.OnBackClicked -> {
                viewModelScope.launch {
                    sendEvent(TimetableEvent.NavigateBack)
                }
            }
        }
    }
    private fun onDaySelected(intent: TimetableIntent.OnDaySelected) {
        when (val currentUiState = _uiState.value) {
            is UiState.Success<TimetableUiState> -> {
                _uiState.update { UiState.Success(currentUiState.data.copy(selectedDayIndex = intent.index)) }
            }
            else -> {
            }
        }
    }
}

@Immutable
data class TimetableUiState(
    val days: List<TimetableData> = emptyList(),
    val selectedDayIndex: Int = 0,
    val timetables: Map<Int, List<Timetable>> = emptyMap(),
)

sealed interface TimetableIntent {
    data class OnDaySelected(val index: Int) : TimetableIntent
    data object OnBackClicked : TimetableIntent
}

sealed interface TimetableEvent {
    data object NavigateBack : TimetableEvent
}
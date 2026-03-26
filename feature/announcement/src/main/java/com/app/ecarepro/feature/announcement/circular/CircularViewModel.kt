package com.app.ecarepro.feature.announcement.circular

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.AcademicYear
import com.app.ecarepro.core.domain.model.Circular
import com.app.ecarepro.core.domain.repository.AnnouncementRepository
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.feature.announcement.common.NoticeFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CircularViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository,
) : BaseViewModel<CircularIntent, CircularEvent>() {

    private val _uiState = MutableStateFlow<UiState<CircularUiState>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchCirculars()
    }

    private fun fetchCirculars(
        yrId: Int = 0,
        isRefreshing: Boolean = false,
    ) {
        viewModelScope.launch {
            announcementRepository.getCirculars(yrId = yrId)
                .onStart {
                    _uiState.update { currentState ->
                        if (isRefreshing) {
                            (currentState as? UiState.Success)?.let { UiState.Success(it.data.copy(isRefreshing = true)) } ?: currentState
                        } else {
                            UiState.Loading
                        }
                    }
                }
                .collect { result ->
                    result
                        .onSuccess { (circulars, years) ->
                            val currentState = (_uiState.value as? UiState.Success)?.data
                            val selectedYear = currentState?.selectedYear ?: years.find { it.isCur }
                            _uiState.update {
                                UiState.Success(
                                    CircularUiState(
                                        allCirculars = circulars,
                                        filteredCirculars = circulars,
                                        academicYears = years,
                                        selectedYear = selectedYear,
                                        isRefreshing = false
                                    )
                                )
                            }
                        }
                        .onFailure { error ->
                            if (isRefreshing) {
                                _uiState.update { currentState ->
                                    (currentState as? UiState.Success)?.let { UiState.Success(it.data.copy(isRefreshing = false)) } ?: currentState
                                }
                                sendEvent(CircularEvent.ShowMessage(SnackbarMessage(error.errorMessage(), MessageType.ERROR)))
                            } else {
                                _uiState.update { UiState.Error(error.errorMessage()) }
                            }
                        }
                }
        }
    }

    override fun handleIntent(intent: CircularIntent) {
        when (intent) {
            is CircularIntent.OnBackClicked -> sendEvent(CircularEvent.NavigateBack)
            is CircularIntent.OnCircularClicked -> sendEvent(CircularEvent.NavigateToDetail(intent.circularId))
            is CircularIntent.OnSearchQueryChanged -> onSearchQueryChanged(intent.query)
            is CircularIntent.OnFilterSelected -> onFilterSelected(intent.filter)
            is CircularIntent.OnShowFilter -> {
                val current = (_uiState.value as? UiState.Success)?.data ?: return
                _uiState.update { UiState.Success(current.copy(isFilterVisible = true)) }
            }
            is CircularIntent.OnDismissFilter -> {
                val current = (_uiState.value as? UiState.Success)?.data ?: return
                _uiState.update { UiState.Success(current.copy(isFilterVisible = false)) }
            }
            is CircularIntent.OnYearSelected -> onYearSelected(intent.year)
            is CircularIntent.OnShowYearPicker -> {
                val current = (_uiState.value as? UiState.Success)?.data ?: return
                _uiState.update { UiState.Success(current.copy(isYearPickerVisible = true)) }
            }
            is CircularIntent.OnDismissYearPicker -> {
                val current = (_uiState.value as? UiState.Success)?.data ?: return
                _uiState.update { UiState.Success(current.copy(isYearPickerVisible = false)) }
            }
            is CircularIntent.OnRefresh -> {
                val current = (_uiState.value as? UiState.Success)?.data
                fetchCirculars(yrId = current?.selectedYear?.yrID ?: 0, isRefreshing = true)
            }
        }
    }

    private fun onYearSelected(year: AcademicYear) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.update { UiState.Success(current.copy(selectedYear = year, isYearPickerVisible = false)) }
        fetchCirculars(yrId = year.yrID)
    }

    private fun onSearchQueryChanged(query: String) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.update { UiState.Success(current.copy(searchQuery = query)) }
        applyFilter()
    }

    private fun onFilterSelected(filter: NoticeFilter) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.update { UiState.Success(current.copy(selectedFilter = filter, isFilterVisible = false)) }
        applyFilter()
    }

    private fun applyFilter() {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        val filtered = current.allCirculars
            .filter { circular ->
                when (current.selectedFilter) {
                    NoticeFilter.ALL -> true
                    NoticeFilter.UNREAD -> !circular.isRead
                    NoticeFilter.READ -> circular.isRead
                }
            }
            .filter { circular ->
                current.searchQuery.isBlank() ||
                        circular.title.contains(current.searchQuery, ignoreCase = true)
            }
        _uiState.update { UiState.Success(current.copy(filteredCirculars = filtered)) }
    }
}

@Immutable
data class CircularUiState(
    val isRefreshing: Boolean = false,
    val searchQuery: String = "",
    val selectedFilter: NoticeFilter = NoticeFilter.ALL,
    val isFilterVisible: Boolean = false,
    val allCirculars: List<Circular> = emptyList(),
    val filteredCirculars: List<Circular> = emptyList(),
    val academicYears: List<AcademicYear> = emptyList(),
    val selectedYear: AcademicYear? = null,
    val isYearPickerVisible: Boolean = false,
)

sealed interface CircularIntent {
    data object OnBackClicked : CircularIntent
    data class OnCircularClicked(val circularId: String) : CircularIntent
    data class OnSearchQueryChanged(val query: String) : CircularIntent
    data class OnFilterSelected(val filter: NoticeFilter) : CircularIntent
    data object OnShowFilter : CircularIntent
    data object OnDismissFilter : CircularIntent
    data class OnYearSelected(val year: AcademicYear) : CircularIntent
    data object OnShowYearPicker : CircularIntent
    data object OnDismissYearPicker : CircularIntent
    data object OnRefresh : CircularIntent
}

sealed interface CircularEvent {
    data object NavigateBack : CircularEvent
    data class NavigateToDetail(val circularId: String) : CircularEvent
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : CircularEvent
}

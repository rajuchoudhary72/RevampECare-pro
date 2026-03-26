package com.app.ecarepro.feature.transport_att.screens

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.transport.Route
import com.app.ecarepro.core.domain.model.transport.Stop
import com.app.ecarepro.core.domain.repository.TransportRepository
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ViewAttendanceViewModel @Inject constructor(
    private val transportRepository: TransportRepository,
) : BaseViewModel<ViewAttendanceIntent, ViewAttendanceEvent>() {

    private val _uiState: MutableStateFlow<UiState<ViewAttendanceUiState>> =
        MutableStateFlow(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchRoutes()
    }

    override fun handleIntent(intent: ViewAttendanceIntent) {
        when (intent) {
            is ViewAttendanceIntent.OnBackClicked -> sendEvent(ViewAttendanceEvent.NavigateBack)
            is ViewAttendanceIntent.OnDateSelected -> onDateSelected(intent.dateMillis)
            is ViewAttendanceIntent.OnRouteSelected -> onRouteSelected(intent.route)
            is ViewAttendanceIntent.OnStopToggled -> onStopToggled(intent.stop)
            is ViewAttendanceIntent.OnSelectAllStops -> onSelectAllStops()
            is ViewAttendanceIntent.OnFetchReport -> fetchReport()
            is ViewAttendanceIntent.ShowRouteSheet -> showRouteSheet()
            is ViewAttendanceIntent.DismissRouteSheet -> dismissRouteSheet()
            is ViewAttendanceIntent.ShowDatePicker -> showDatePicker()
            is ViewAttendanceIntent.DismissDatePicker -> dismissDatePicker()
            is ViewAttendanceIntent.DismissReportView -> dismissReportView()
        }
    }

    private fun fetchRoutes() {
        viewModelScope.launch {
            transportRepository.getRoutesList()
                .onStart { _uiState.update { UiState.Loading } }
                .collect { result ->
                    result
                        .onSuccess { routes ->
                            _uiState.update {
                                UiState.Success(
                                    ViewAttendanceUiState(
                                        routeList = routes,
                                        selectedDate = formatDateForDisplay(System.currentTimeMillis()),
                                        selectedDateSystem = formatDateForSystem(System.currentTimeMillis()),
                                    )
                                )
                            }
                        }
                        .onFailure { error ->
                            _uiState.update { UiState.Error(error.errorMessage()) }
                        }
                }
        }
    }

    private fun onDateSelected(dateMillis: Long) {
        updateState {
            it.copy(
                selectedDate = formatDateForDisplay(dateMillis),
                selectedDateSystem = formatDateForSystem(dateMillis),
                isDatePickerVisible = false,
            )
        }
    }

    private fun onRouteSelected(route: Route) {
        updateState {
            it.copy(
                selectedRoute = route,
                routeSelected = true,
                isRouteSheetVisible = false,
                stopList = emptyList(),
                stoppersSelected = false,
                reportData = emptyList(),
                showReport = false,
            )
        }
        fetchStoppageList()
    }

    private fun fetchStoppageList() {
        val state = currentState() ?: return
        val route = state.selectedRoute ?: return

        viewModelScope.launch {
            transportRepository.getStoppageList(route.routeID.toString(), 0)
                .onStart { updateState { it.copy(isLoading = true) } }
                .collect { result ->
                    result
                        .onSuccess { stops ->
                            updateState { it.copy(isLoading = false, stopList = stops) }
                        }
                        .onFailure { error ->
                            updateState { it.copy(isLoading = false) }
                            sendEvent(ViewAttendanceEvent.ShowMessage(SnackbarMessage(error.errorMessage(), MessageType.ERROR)))
                        }
                }
        }
    }

    private fun onStopToggled(stop: Stop) {
        val state = currentState() ?: return
        val updatedStops = state.stopList.toMutableList()
        val index = updatedStops.indexOfFirst { it.stopID == stop.stopID }
        if (index < 0) return

        updatedStops[index] = updatedStops[index].copy(checked = !updatedStops[index].checked)
        val allChecked = updatedStops.all { it.checked }
        updateState { it.copy(stopList = updatedStops, selectAllStops = allChecked) }
    }

    private fun onSelectAllStops() {
        val state = currentState() ?: return
        val newSelectAll = !state.selectAllStops
        val updatedStops = state.stopList.map { it.copy(checked = newSelectAll) }
        updateState { it.copy(selectAllStops = newSelectAll, stopList = updatedStops) }
    }

    private fun fetchReport() {
        val state = currentState() ?: return
        val route = state.selectedRoute

        if (route == null) {
            sendEvent(ViewAttendanceEvent.ShowMessage(SnackbarMessage("Please select route", MessageType.ERROR)))
            return
        }

        val selectedStops = state.stopList.filter { it.checked }
        if (selectedStops.isEmpty()) {
            sendEvent(ViewAttendanceEvent.ShowMessage(SnackbarMessage("Please select stop", MessageType.ERROR)))
            return
        }

        val stopIDs = selectedStops.joinToString(",") { it.stopID.toString() }

        viewModelScope.launch {
            transportRepository.getTransAttendanceReport(
                routeID = route.routeID,
                stopIDs = stopIDs,
                attDate = state.selectedDateSystem,
            )
                .onStart { updateState { it.copy(isLoading = true) } }
                .collect { result ->
                    result
                        .onSuccess { reportStops ->
                            updateState {
                                it.copy(
                                    isLoading = false,
                                    reportData = reportStops,
                                    showReport = true,
                                    stoppersSelected = true,
                                )
                            }
                        }
                        .onFailure { error ->
                            updateState { it.copy(isLoading = false) }
                            sendEvent(ViewAttendanceEvent.ShowMessage(SnackbarMessage(error.errorMessage(), MessageType.ERROR)))
                        }
                }
        }
    }

    private fun showRouteSheet() {
        val state = currentState() ?: return
        if (state.routeList.isEmpty()) {
            sendEvent(ViewAttendanceEvent.ShowMessage(SnackbarMessage("No route data", MessageType.ERROR)))
            return
        }
        updateState { it.copy(isRouteSheetVisible = true) }
    }

    private fun dismissRouteSheet() {
        updateState { it.copy(isRouteSheetVisible = false) }
    }

    private fun showDatePicker() {
        updateState { it.copy(isDatePickerVisible = true) }
    }

    private fun dismissDatePicker() {
        updateState { it.copy(isDatePickerVisible = false) }
    }

    private fun dismissReportView() {
        updateState { it.copy(showReport = false) }
    }

    private fun currentState(): ViewAttendanceUiState? {
        return (_uiState.value as? UiState.Success)?.data
    }

    private fun updateState(update: (ViewAttendanceUiState) -> ViewAttendanceUiState) {
        val current = currentState() ?: return
        _uiState.update { UiState.Success(update(current)) }
    }

    private fun formatDateForDisplay(millis: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date(millis))
    }

    private fun formatDateForSystem(millis: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date(millis))
    }
}

@Immutable
data class ViewAttendanceUiState(
    val selectedDate: String = "",
    val selectedDateSystem: String = "",
    val routeList: List<Route> = emptyList(),
    val stopList: List<Stop> = emptyList(),
    val reportData: List<Stop> = emptyList(),
    val selectedRoute: Route? = null,
    val routeSelected: Boolean = false,
    val stoppersSelected: Boolean = false,
    val selectAllStops: Boolean = false,
    val isLoading: Boolean = false,
    val showReport: Boolean = false,
    val isDatePickerVisible: Boolean = false,
    val isRouteSheetVisible: Boolean = false,
)

sealed interface ViewAttendanceIntent {
    data object OnBackClicked : ViewAttendanceIntent
    data class OnDateSelected(val dateMillis: Long) : ViewAttendanceIntent
    data class OnRouteSelected(val route: Route) : ViewAttendanceIntent
    data class OnStopToggled(val stop: Stop) : ViewAttendanceIntent
    data object OnSelectAllStops : ViewAttendanceIntent
    data object OnFetchReport : ViewAttendanceIntent
    data object ShowRouteSheet : ViewAttendanceIntent
    data object DismissRouteSheet : ViewAttendanceIntent
    data object ShowDatePicker : ViewAttendanceIntent
    data object DismissDatePicker : ViewAttendanceIntent
    data object DismissReportView : ViewAttendanceIntent
}

sealed interface ViewAttendanceEvent {
    data object NavigateBack : ViewAttendanceEvent
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : ViewAttendanceEvent
}

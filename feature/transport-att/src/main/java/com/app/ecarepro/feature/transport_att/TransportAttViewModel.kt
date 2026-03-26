package com.app.ecarepro.feature.transport_att

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.transport.Route
import com.app.ecarepro.core.domain.model.transport.Stop
import com.app.ecarepro.core.domain.model.transport.StudentAttendanceRequest
import com.app.ecarepro.core.domain.model.transport.TransportConstants
import com.app.ecarepro.core.domain.model.transport.TransportStudent
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
class TransportAttViewModel @Inject constructor(
    private val transportRepository: TransportRepository,
) : BaseViewModel<TransportAttIntent, TransportAttEvent>() {

    private val _uiState: MutableStateFlow<UiState<TransportAttUiState>> =
        MutableStateFlow(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchRoutes()
    }

    override fun handleIntent(intent: TransportAttIntent) {
        when (intent) {
            is TransportAttIntent.OnBackClicked -> sendEvent(TransportAttEvent.NavigateBack)
            is TransportAttIntent.OnDateSelected -> onDateSelected(intent.dateMillis)
            is TransportAttIntent.OnTripTypeSelected -> onTripTypeSelected(intent.tripType)
            is TransportAttIntent.OnRouteSelected -> onRouteSelected(intent.route)
            is TransportAttIntent.OnStopToggled -> onStopToggled(intent.stop)
            is TransportAttIntent.OnSelectAllStops -> onSelectAllStops()
            is TransportAttIntent.OnMarkAttendanceClicked -> onMarkAttendanceClicked()
            is TransportAttIntent.OnStudentAttendanceChanged -> onStudentAttendanceChanged(
                intent.studentIndex, intent.status
            )
            is TransportAttIntent.OnSaveAttendanceClicked -> showConfirmDialog()
            is TransportAttIntent.OnConfirmSaveAttendance -> saveAttendance()
            is TransportAttIntent.OnDismissConfirmDialog -> dismissConfirmDialog()
            is TransportAttIntent.OnDropStudentClicked -> onDropStudentClicked(intent.studentIndex)
            is TransportAttIntent.OnConfirmDropStudent -> confirmDropStudent(intent.studentIndex)
            is TransportAttIntent.OnCancelDropStudent -> cancelDropStudent(intent.studentIndex)
            is TransportAttIntent.OnDismissStudentList -> dismissStudentList()
            is TransportAttIntent.OnViewAttendanceClicked -> sendEvent(TransportAttEvent.NavigateToViewAttendance)
            is TransportAttIntent.OnViewOutPassClicked -> sendEvent(TransportAttEvent.NavigateToOutPass)
            is TransportAttIntent.ShowTripTypeSheet -> showTripTypeSheet()
            is TransportAttIntent.DismissTripTypeSheet -> dismissTripTypeSheet()
            is TransportAttIntent.ShowRouteSheet -> showRouteSheet()
            is TransportAttIntent.DismissRouteSheet -> dismissRouteSheet()
            is TransportAttIntent.ShowDatePicker -> showDatePicker()
            is TransportAttIntent.DismissDatePicker -> dismissDatePicker()
        }
    }

    private fun fetchRoutes() {
        viewModelScope.launch {
            transportRepository.getRoutesList()
                .onStart {
                    _uiState.update { UiState.Loading }
                }
                .collect { result ->
                    result
                        .onSuccess { routes ->
                            _uiState.update {
                                UiState.Success(
                                    TransportAttUiState(
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
        val state = currentState() ?: return
        val displayDate = formatDateForDisplay(dateMillis)
        val systemDate = formatDateForSystem(dateMillis)
        updateState {
            it.copy(selectedDate = displayDate, selectedDateSystem = systemDate, isDatePickerVisible = false)
        }
        if (state.routeSelected && state.stoppersSelected) {
            fetchStudents()
        }
    }

    private fun onTripTypeSelected(tripType: Int) {
        updateState {
            it.copy(
                tripType = tripType,
                isTripTypeSheetVisible = false,
                stopList = emptyList(),
                studentList = emptyList(),
                stoppersSelected = false,
                showStudentList = false,
                selectedStops = emptyList(),
            )
        }
        val state = currentState() ?: return
        if (tripType != 0 && state.routeSelected) {
            fetchStoppageList()
        }
    }

    private fun onRouteSelected(route: Route) {
        updateState {
            it.copy(
                selectedRoute = route,
                routeSelected = true,
                isRouteSheetVisible = false,
                stopList = emptyList(),
                studentList = emptyList(),
                stoppersSelected = false,
                showStudentList = false,
                selectedStops = emptyList(),
            )
        }
        val state = currentState() ?: return
        if (state.tripType != 0) {
            fetchStoppageList()
        }
    }

    private fun onStopToggled(stop: Stop) {
        val state = currentState() ?: return
        val updatedStops = state.stopList.toMutableList()
        val index = updatedStops.indexOfFirst { it.stopID == stop.stopID }
        if (index < 0) return

        if (state.tripType == TransportConstants.DROP_STUDENT_TRIP) {
            updatedStops.forEachIndexed { i, s ->
                updatedStops[i] = s.copy(checked = i == index)
            }
        } else {
            updatedStops[index] = updatedStops[index].copy(checked = !updatedStops[index].checked)
        }

        val allChecked = updatedStops.all { it.checked }
        updateState {
            it.copy(stopList = updatedStops, selectAllStops = allChecked)
        }
    }

    private fun onSelectAllStops() {
        val state = currentState() ?: return
        val newSelectAll = !state.selectAllStops
        val updatedStops = state.stopList.map { it.copy(checked = newSelectAll) }
        updateState {
            it.copy(selectAllStops = newSelectAll, stopList = updatedStops)
        }
    }

    private fun onMarkAttendanceClicked() {
        val state = currentState() ?: return

        if (!state.routeSelected) {
            sendEvent(TransportAttEvent.ShowMessage(SnackbarMessage("Please select route", MessageType.ERROR)))
            return
        }
        if (state.tripType == 0) {
            sendEvent(TransportAttEvent.ShowMessage(SnackbarMessage("Please select trip type", MessageType.ERROR)))
            return
        }

        val selectedStops = state.stopList.filter { it.checked }
        if (selectedStops.isEmpty()) {
            sendEvent(TransportAttEvent.ShowMessage(SnackbarMessage("Please select stop", MessageType.ERROR)))
            return
        }

        updateState {
            it.copy(
                selectedStops = selectedStops,
                stoppersSelected = true,
            )
        }
        fetchStudents()
    }

    private fun fetchStoppageList() {
        val state = currentState() ?: return
        val route = state.selectedRoute ?: return
        val trip = if (state.tripType == TransportConstants.DROP_STUDENT_TRIP) 0 else state.tripType

        viewModelScope.launch {
            transportRepository.getStoppageList(route.routeID.toString(), trip)
                .onStart { updateState { it.copy(isLoading = true) } }
                .collect { result ->
                    result
                        .onSuccess { stops ->
                            updateState {
                                it.copy(isLoading = false, stopList = stops)
                            }
                        }
                        .onFailure { error ->
                            updateState { it.copy(isLoading = false) }
                            sendEvent(TransportAttEvent.ShowMessage(SnackbarMessage(error.errorMessage(), MessageType.ERROR)))
                        }
                }
        }
    }

    private fun fetchStudents() {
        val state = currentState() ?: return
        val route = state.selectedRoute ?: return
        val selectedStops = state.stopList.filter { it.checked }
        if (selectedStops.isEmpty()) return

        viewModelScope.launch {
            if (state.tripType == TransportConstants.DROP_STUDENT_TRIP) {
                val selectedStop = selectedStops.first()
                transportRepository.getStudentToDrop(
                    routeID = route.routeID,
                    stopID = selectedStop.stopID,
                    attDate = state.selectedDateSystem,
                )
            } else {
                val stopIDs = selectedStops.joinToString(",") { it.stopID.toString() }
                transportRepository.getStudentToMarkTransAttendance(
                    routeIDs = route.routeID.toString(),
                    stopID = 0,
                    trip = state.tripType,
                    attDate = state.selectedDateSystem,
                    stopIDs = stopIDs,
                )
            }
                .onStart { updateState { it.copy(isLoading = true) } }
                .collect { result ->
                    result
                        .onSuccess { data ->
                            updateState {
                                it.copy(
                                    isLoading = false,
                                    studentList = data.students,
                                    freezDrop = data.freezDrop,
                                    freezPickup = data.freezPickup,
                                    showStudentList = true,
                                    busCount = calculateBusCount(data.students, it.tripType),
                                )
                            }
                        }
                        .onFailure { error ->
                            updateState { it.copy(isLoading = false) }
                            sendEvent(TransportAttEvent.ShowMessage(SnackbarMessage(error.errorMessage(), MessageType.ERROR)))
                        }
                }
        }
    }

    private fun onStudentAttendanceChanged(studentIndex: Int, status: Int) {
        val state = currentState() ?: return
        val students = state.studentList.toMutableList()
        if (studentIndex !in students.indices) return
        val student = students[studentIndex]

        if (state.tripType == TransportConstants.UP_TRIP) {
            if (state.freezPickup) return
            students[studentIndex] = student.copy(pickupStatus = status, isSelected = true)
        } else if (state.tripType == TransportConstants.DOWN_TRIP) {
            if (state.freezDrop) return
            if (status != TransportConstants.ABSENT && student.pickupStatus != TransportConstants.PRESENT) {
                sendEvent(TransportAttEvent.ShowMessage(SnackbarMessage("Absent student status cannot be changed", MessageType.ERROR)))
                return
            }
            students[studentIndex] = student.copy(dropStatus = status, isSelected = true)
        }

        updateState {
            it.copy(
                studentList = students,
                busCount = calculateBusCount(students, it.tripType),
            )
        }
    }

    private fun showConfirmDialog() {
        val state = currentState() ?: return
        var presentCount = 0
        var absentCount = 0
        var leaveCount = 0

        state.studentList.forEach { student ->
            if (state.tripType == TransportConstants.UP_TRIP) {
                when (student.pickupStatus) {
                    TransportConstants.PRESENT -> presentCount++
                    TransportConstants.ABSENT -> absentCount++
                }
            } else if (state.tripType == TransportConstants.DOWN_TRIP) {
                when (student.dropStatus) {
                    TransportConstants.PRESENT -> presentCount++
                    TransportConstants.ABSENT -> absentCount++
                    TransportConstants.OP -> leaveCount++
                }
            }
        }

        updateState {
            it.copy(
                isConfirmDialogVisible = true,
                presentCount = presentCount,
                absentCount = absentCount,
                leaveCount = leaveCount,
            )
        }
    }

    private fun dismissConfirmDialog() {
        updateState { it.copy(isConfirmDialogVisible = false) }
    }

    private fun saveAttendance() {
        val state = currentState() ?: return
        val route = state.selectedRoute ?: return

        val requestList = state.studentList.map { student ->
            StudentAttendanceRequest(
                stID = student.stID,
                status = if (state.tripType == TransportConstants.UP_TRIP) student.pickupStatus else student.dropStatus,
                stopID = student.stopID,
            )
        }

        viewModelScope.launch {
            transportRepository.postTransAttendance(
                attDate = state.selectedDateSystem,
                routeID = route.routeID,
                stopID = 0,
                stuAtt = requestList,
                trip = state.tripType,
            )
                .onStart {
                    updateState { it.copy(isConfirmDialogVisible = false, isLoading = true) }
                }
                .collect { result ->
                    result
                        .onSuccess {
                            updateState { it.copy(isLoading = false) }
                            sendEvent(TransportAttEvent.ShowMessage(SnackbarMessage("Attendance marked successfully", MessageType.SUCCESS)))
                            fetchStudents()
                        }
                        .onFailure { error ->
                            updateState { it.copy(isLoading = false) }
                            sendEvent(TransportAttEvent.ShowMessage(SnackbarMessage(error.errorMessage(), MessageType.ERROR)))
                        }
                }
        }
    }

    private fun onDropStudentClicked(studentIndex: Int) {
        val state = currentState() ?: return
        val students = state.studentList.toMutableList()
        if (studentIndex !in students.indices) return
        updateState { it.copy(droppingStudentIndex = studentIndex, isDropConfirmVisible = true) }
    }

    private fun confirmDropStudent(studentIndex: Int) {
        val state = currentState() ?: return
        val students = state.studentList
        if (studentIndex !in students.indices) return
        val student = students[studentIndex]

        viewModelScope.launch {
            transportRepository.dropToStudent(
                stID = student.stID,
                attDate = state.selectedDateSystem,
                hasDropped = true,
            )
                .onStart {
                    updateState { it.copy(isDropConfirmVisible = false, isLoading = true) }
                }
                .collect { result ->
                    result
                        .onSuccess {
                            val updatedStudents = state.studentList.toMutableList()
                            updatedStudents[studentIndex] = student.copy(isDropped = true)
                            updateState {
                                it.copy(
                                    isLoading = false,
                                    studentList = updatedStudents,
                                    droppingStudentIndex = null,
                                )
                            }
                            sendEvent(TransportAttEvent.ShowMessage(SnackbarMessage("Updated successfully", MessageType.SUCCESS)))
                        }
                        .onFailure { error ->
                            updateState { it.copy(isLoading = false, droppingStudentIndex = null) }
                            sendEvent(TransportAttEvent.ShowMessage(SnackbarMessage(error.errorMessage(), MessageType.ERROR)))
                        }
                }
        }
    }

    private fun cancelDropStudent(studentIndex: Int) {
        updateState { it.copy(droppingStudentIndex = null, isDropConfirmVisible = false) }
    }

    private fun dismissStudentList() {
        updateState { it.copy(showStudentList = false) }
    }

    private fun showTripTypeSheet() {
        updateState { it.copy(isTripTypeSheetVisible = true) }
    }

    private fun dismissTripTypeSheet() {
        updateState { it.copy(isTripTypeSheetVisible = false) }
    }

    private fun showRouteSheet() {
        val state = currentState() ?: return
        if (state.routeList.isEmpty()) {
            sendEvent(TransportAttEvent.ShowMessage(SnackbarMessage("No route data", MessageType.ERROR)))
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

    private fun calculateBusCount(students: List<TransportStudent>, tripType: Int): Int {
        var count = 0
        students.forEach { student ->
            if (student.pickupStatus == TransportConstants.PRESENT) count++
            if (tripType == TransportConstants.DOWN_TRIP) {
                if (student.dropStatus == TransportConstants.PRESENT || student.dropStatus == TransportConstants.OP) {
                    if (count > 0) count--
                }
            }
        }
        return count
    }

    private fun currentState(): TransportAttUiState? {
        return (_uiState.value as? UiState.Success)?.data
    }

    private fun updateState(update: (TransportAttUiState) -> TransportAttUiState) {
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

// ── UI State ──

@Immutable
data class TransportAttUiState(
    val selectedDate: String = "",
    val selectedDateSystem: String = "",
    val routeList: List<Route> = emptyList(),
    val stopList: List<Stop> = emptyList(),
    val studentList: List<TransportStudent> = emptyList(),
    val selectedRoute: Route? = null,
    val selectedStops: List<Stop> = emptyList(),
    val tripType: Int = 0,
    val routeSelected: Boolean = false,
    val stoppersSelected: Boolean = false,
    val selectAllStops: Boolean = false,
    val busCount: Int = 0,
    val freezDrop: Boolean = false,
    val freezPickup: Boolean = false,
    val isLoading: Boolean = false,
    val showStudentList: Boolean = false,
    val isDatePickerVisible: Boolean = false,
    val isTripTypeSheetVisible: Boolean = false,
    val isRouteSheetVisible: Boolean = false,
    val isConfirmDialogVisible: Boolean = false,
    val isDropConfirmVisible: Boolean = false,
    val droppingStudentIndex: Int? = null,
    val presentCount: Int = 0,
    val absentCount: Int = 0,
    val leaveCount: Int = 0,
)

// ── Intents ──

sealed interface TransportAttIntent {
    data object OnBackClicked : TransportAttIntent
    data class OnDateSelected(val dateMillis: Long) : TransportAttIntent
    data class OnTripTypeSelected(val tripType: Int) : TransportAttIntent
    data class OnRouteSelected(val route: Route) : TransportAttIntent
    data class OnStopToggled(val stop: Stop) : TransportAttIntent
    data object OnSelectAllStops : TransportAttIntent
    data object OnMarkAttendanceClicked : TransportAttIntent
    data class OnStudentAttendanceChanged(val studentIndex: Int, val status: Int) : TransportAttIntent
    data object OnSaveAttendanceClicked : TransportAttIntent
    data object OnConfirmSaveAttendance : TransportAttIntent
    data object OnDismissConfirmDialog : TransportAttIntent
    data class OnDropStudentClicked(val studentIndex: Int) : TransportAttIntent
    data class OnConfirmDropStudent(val studentIndex: Int) : TransportAttIntent
    data class OnCancelDropStudent(val studentIndex: Int) : TransportAttIntent
    data object OnDismissStudentList : TransportAttIntent
    data object OnViewAttendanceClicked : TransportAttIntent
    data object OnViewOutPassClicked : TransportAttIntent
    data object ShowTripTypeSheet : TransportAttIntent
    data object DismissTripTypeSheet : TransportAttIntent
    data object ShowRouteSheet : TransportAttIntent
    data object DismissRouteSheet : TransportAttIntent
    data object ShowDatePicker : TransportAttIntent
    data object DismissDatePicker : TransportAttIntent
}

// ── Events ──

sealed interface TransportAttEvent {
    data object NavigateBack : TransportAttEvent
    data object NavigateToViewAttendance : TransportAttEvent
    data object NavigateToOutPass : TransportAttEvent
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : TransportAttEvent
}

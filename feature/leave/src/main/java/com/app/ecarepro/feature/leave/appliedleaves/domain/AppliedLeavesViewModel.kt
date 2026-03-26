package com.app.ecarepro.feature.leave.appliedleaves.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.LeaveActionRequest
import com.app.ecarepro.core.domain.model.LeaveReportItem
import com.app.ecarepro.core.domain.repository.AppliedLeavesRepository
import com.app.ecarepro.feature.leave.appliedleaves.data.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonPrimitive
import javax.inject.Inject
import com.app.ecarepro.core.domain.model.LeaveAction as DomainLeaveAction

@HiltViewModel
class AppliedLeavesViewModel @Inject constructor(
    private val appliedLeavesRepository: AppliedLeavesRepository
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow<UIState>(UIState.Loading)
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    // Data
    private val _leaves = MutableStateFlow<List<Leave>>(emptyList())
    private val _cachedLeavesByStatus = mutableMapOf<LeaveStatus, List<Leave>>()

    // Presentation
    private val _presentation = MutableStateFlow<AppliedLeavesViewPresentation?>(null)
    val presentation: StateFlow<AppliedLeavesViewPresentation?> = _presentation.asStateFlow()

    // Screen Type
    private val _screenType = MutableStateFlow(AppliedLeavesScreenType.SELF_LEAVES)
    val screenType: StateFlow<AppliedLeavesScreenType> = _screenType.asStateFlow()

    // UI Controls
    private val _selectedTab = MutableStateFlow(LeaveStatus.PENDING)
    val selectedTab: StateFlow<LeaveStatus> = _selectedTab.asStateFlow()

    private val _selectedLeaveIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedLeaveIds: StateFlow<Set<String>> = _selectedLeaveIds.asStateFlow()

    private val _selectAll = MutableStateFlow(false)
    val selectAll: StateFlow<Boolean> = _selectAll.asStateFlow()

    private val _showAttendance = MutableStateFlow(false)
    val showAttendance: StateFlow<Boolean> = _showAttendance.asStateFlow()

    // Messages
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    fun initialize(screenType: AppliedLeavesScreenType) {
        _screenType.value = screenType
        _selectedTab.value = if (screenType.showTabs) {
            screenType.availableTabs.firstOrNull() ?: LeaveStatus.PENDING
        } else {
            LeaveStatus.PENDING
        }
        loadLeaves()
    }

    // Load leaves from API
    fun loadLeaves(status: Int? = null, forceRefresh: Boolean = false, silentRefresh: Boolean = false) {
        viewModelScope.launch {
            val statusCode = status ?: _selectedTab.value.statusCode
            val statusKey = LeaveStatus.fromStatusCode(statusCode)

            // For self leaves, don't cache by status
            if (_screenType.value == AppliedLeavesScreenType.SELF_LEAVES) {
                fetchLeavesFromServer(statusCode, silentRefresh = silentRefresh)
                return@launch
            }

            // Check cache
            if (!forceRefresh && _cachedLeavesByStatus[statusKey] != null) {
                _leaves.value = _cachedLeavesByStatus[statusKey]!!
                updatePresentation()
                _uiState.value = UIState.Loaded
                return@launch
            }

            fetchLeavesFromServer(statusCode, statusKey, silentRefresh)
        }
    }

    private fun fetchLeavesFromServer(
        statusCode: Int,
        cacheKey: LeaveStatus? = null,
        silentRefresh: Boolean = false
    ) {
        viewModelScope.launch {
            _uiState.value = if (silentRefresh || _leaves.value.isNotEmpty()) {
                UIState.SilentLoading
            } else {
                UIState.Loading
            }

            val applType = when (_screenType.value) {
                AppliedLeavesScreenType.SELF_LEAVES -> 1 // Default to student for self
                AppliedLeavesScreenType.STUDENT_LEAVES -> 1
                AppliedLeavesScreenType.STAFF_LEAVES -> 3
            }

            appliedLeavesRepository.getAppliedLeaves(
                status = statusCode,
                order = 2,
                applType = applType,
                page = 1,
                showAttendance = _showAttendance.value,
                duration = 0
            ).collect { result ->
                result
                    .onSuccess { response ->
                        val leaves = response.dtl.map { it.toFeatureLeave() }
                        _leaves.value = leaves

                        // Cache if key provided
                        cacheKey?.let { _cachedLeavesByStatus[it] = leaves }

                        updatePresentation()
                        _uiState.value = if (leaves.isEmpty()) UIState.NoResult else UIState.Loaded
                    }
                    .onFailure { error ->
                        _errorMessage.value = "Failed to load leaves: ${error.message}"
                        _uiState.value = UIState.Error
                    }
            }
        }
    }

    fun refreshLeaves() {
        loadLeaves(forceRefresh = true, silentRefresh = true)
    }

    private fun updatePresentation() {
        val groupedByStatus = _leaves.value.groupBy { it.leaveStatus }

        val mapToPresentations: (List<Leave>) -> List<LeaveCardPresentation> = { leaves ->
            leaves.map {
                LeaveCardPresentation.fromLeave(
                    it,
                    isSelected = _selectedLeaveIds.value.contains(it.id),
                    screenType = _screenType.value,
                    showAttendance = _showAttendance.value
                )
            }
        }

        _presentation.value = AppliedLeavesViewPresentation(
            allLeaves = mapToPresentations(_leaves.value),
            pendingLeaves = mapToPresentations(groupedByStatus[LeaveStatus.PENDING] ?: emptyList()),
            approvedLeaves = mapToPresentations(groupedByStatus[LeaveStatus.APPROVED] ?: emptyList()),
            rejectedLeaves = mapToPresentations(groupedByStatus[LeaveStatus.REJECTED] ?: emptyList()),
            cancelledLeaves = mapToPresentations(groupedByStatus[LeaveStatus.CANCELLED] ?: emptyList()),
            selectedTab = _selectedTab.value,
            screenType = _screenType.value
        )
    }

    fun onTabChange(newTab: LeaveStatus) {
        _selectedTab.value = newTab
        _selectedLeaveIds.value = emptySet()
        _selectAll.value = false
        loadLeaves()
    }

    fun toggleAttendance() {
        _showAttendance.value = !_showAttendance.value
        if (_showAttendance.value) {
            _cachedLeavesByStatus.clear()
            loadLeaves(forceRefresh = true, silentRefresh = true)
        } else {
            updatePresentation()
        }
    }

    fun toggleLeaveSelection(leaveId: String) {
        _selectedLeaveIds.value = if (_selectedLeaveIds.value.contains(leaveId)) {
            _selectedLeaveIds.value - leaveId
        } else {
            _selectedLeaveIds.value + leaveId
        }
        updateSelectAllState()
        updatePresentation()
    }

    fun toggleSelectAll() {
        _selectAll.value = !_selectAll.value
        val currentTabLeaves = filterLeavesByStatus(_selectedTab.value)

        _selectedLeaveIds.value = if (_selectAll.value) {
            currentTabLeaves.map { it.id }.toSet()
        } else {
            emptySet()
        }
        updatePresentation()
    }

    private fun updateSelectAllState() {
        val currentTabLeaves = filterLeavesByStatus(_selectedTab.value)
        val currentTabIds = currentTabLeaves.map { it.id }.toSet()
        _selectAll.value = currentTabIds.isNotEmpty() &&
                currentTabIds.all { _selectedLeaveIds.value.contains(it) }
    }

    private fun filterLeavesByStatus(status: LeaveStatus): List<Leave> {
        return _leaves.value.filter { it.leaveStatus == status }
    }

    // Leave Actions
    fun approveLeave(lvID: Int) {
        performLeaveAction(LeaveAction.APPROVE, listOf(lvID))
    }

    fun rejectLeave(lvID: Int) {
        performLeaveAction(LeaveAction.REJECT, listOf(lvID))
    }

    fun approveSelectedLeaves() {
        if (_selectedLeaveIds.value.isEmpty()) return
        val lvIDs = _selectedLeaveIds.value.mapNotNull { it.toIntOrNull() }
        performLeaveAction(LeaveAction.APPROVE, lvIDs)
        _selectedLeaveIds.value = emptySet()
        _selectAll.value = false
    }

    fun rejectSelectedLeaves() {
        if (_selectedLeaveIds.value.isEmpty()) return
        val lvIDs = _selectedLeaveIds.value.mapNotNull { it.toIntOrNull() }
        performLeaveAction(LeaveAction.REJECT, lvIDs)
        _selectedLeaveIds.value = emptySet()
        _selectAll.value = false
    }

    private fun performLeaveAction(
        action: LeaveAction,
        lvIDs: List<Int>,
        isPartialApproved: Boolean = false,
        partialFromDate: String = "",
        partialTillDate: String = "",
        forwardedTo: Int = 0
    ) {
        viewModelScope.launch {
            _uiState.value = UIState.SilentLoading

            val applType = when (_screenType.value) {
                AppliedLeavesScreenType.SELF_LEAVES -> 1
                AppliedLeavesScreenType.STUDENT_LEAVES -> 1
                AppliedLeavesScreenType.STAFF_LEAVES -> 3
            }

            val domainAction = when (action) {
                LeaveAction.APPROVE -> DomainLeaveAction.APPROVE
                LeaveAction.REJECT -> DomainLeaveAction.REJECT
                LeaveAction.CANCEL -> DomainLeaveAction.CANCEL
                LeaveAction.FORWARD -> DomainLeaveAction.FORWARD
            }

            // Process each leave ID
            lvIDs.forEach { lvID ->
                val request = LeaveActionRequest(
                    applType = applType,
                    lvID = lvID,
                    action = domainAction,
                    forwardedTo = forwardedTo,
                    isPartialApproved = isPartialApproved,
                    partialFromDate = partialFromDate.takeIf { it.isNotEmpty() },
                    partialTillDate = partialTillDate.takeIf { it.isNotEmpty() }
                )

                appliedLeavesRepository.performLeaveAction(request).collect { result ->
                    result
                        .onSuccess { response ->
                            // Update local state
                            updateLeaveStatus(lvID, action, isPartialApproved)
                            updatePresentation()
                            _uiState.value = UIState.Loaded
                            _successMessage.value = response.message.ifEmpty { "Leave has been ${action.displayText}" }
                        }
                        .onFailure { error ->
                            _errorMessage.value = "Failed to ${action.displayText} leave: ${error.message}"
                            _uiState.value = UIState.Loaded
                        }
                }
            }
        }
    }

    private fun updateLeaveStatus(lvID: Int, action: LeaveAction, isPartialApproved: Boolean) {
        val index = _leaves.value.indexOfFirst { it.lvID == lvID }
        if (index == -1) return

        val newStatus = when (action) {
            LeaveAction.APPROVE -> LeaveStatus.APPROVED
            LeaveAction.REJECT -> LeaveStatus.REJECTED
            LeaveAction.CANCEL -> LeaveStatus.CANCELLED
            LeaveAction.FORWARD -> _leaves.value[index].leaveStatus
        }

        val updatedLeaves = _leaves.value.toMutableList()
        updatedLeaves[index] = updatedLeaves[index].withUpdatedStatus(newStatus)
        _leaves.value = updatedLeaves

        // Remove from selection if moved to different tab
        if (_selectedTab.value != newStatus) {
            _selectedLeaveIds.value = _selectedLeaveIds.value - lvID.toString()
            updateSelectAllState()
        }
    }

    fun clearSuccessMessage() {
        _successMessage.value = null
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}

// Extension function to convert domain LeaveReportItem to feature Leave model
private fun LeaveReportItem.toFeatureLeave(): Leave {
    return Leave(
        lvID = lvID,
        fromDate = fromDate,
        tillDate = tillDate,
        submittedOn = submittedOn,
        duration = JsonPrimitive(durationStr),
        durationStr = durationStr,
        reason = reason,
        applicantName = applicantName,
        applicantPhoto = applicantPhoto.takeIf { it.isNotEmpty() },
        studentName = studentName,
        studentPhoto = studentPhoto.takeIf { it.isNotEmpty() },
        studentClass = studentClass,
        admissionNumber = null,
        classteacherName = null,
        status = status,
        actionOn = actionOn.takeIf { it.isNotEmpty() },
        sid = sid,
        teacherName = teacherName.takeIf { it.isNotEmpty() },
        designation = designation.takeIf { it.isNotEmpty() },
        photo = photo.takeIf { it.isNotEmpty() },
        attachment = attachment.takeIf { it.isNotEmpty() },
        rejectionReason = rejectionReason.takeIf { it.isNotEmpty() },
        isPartialApproved = false,
        partialFromDate = null,
        partialTillDate = null,
        partialDuration = null,
        attPer = attPer.takeIf { it.isNotEmpty() },
        isSelected = false,
        leaveType = leaveType.takeIf { it.isNotEmpty() },
        leaveAbbr = leaveAbbr.takeIf { it.isNotEmpty() },
        forwardedBy = forwardedBy.takeIf { it > 0 },
        forwardedByName = forwardedByName,
        showCancelButton = showCancelButton
    )
}

data class AppliedLeavesViewPresentation(
    val allLeaves: List<LeaveCardPresentation>,
    val pendingLeaves: List<LeaveCardPresentation>,
    val approvedLeaves: List<LeaveCardPresentation>,
    val rejectedLeaves: List<LeaveCardPresentation>,
    val cancelledLeaves: List<LeaveCardPresentation>,
    val selectedTab: LeaveStatus,
    val screenType: AppliedLeavesScreenType
) {
    fun leavesForTab(status: LeaveStatus): List<LeaveCardPresentation> {
        if (screenType == AppliedLeavesScreenType.SELF_LEAVES) return allLeaves

        return when (status) {
            LeaveStatus.PENDING -> pendingLeaves
            LeaveStatus.APPROVED -> approvedLeaves
            LeaveStatus.REJECTED -> rejectedLeaves
            LeaveStatus.CANCELLED -> cancelledLeaves
            LeaveStatus.UNKNOWN -> emptyList()
        }
    }
}

sealed class UIState {
    object NotInitialized : UIState()
    object Loading : UIState()
    object SilentLoading : UIState()
    object Loaded : UIState()
    object NoResult : UIState()
    object Error : UIState()

    val shouldShowLoading: Boolean
        get() = this is Loading
}

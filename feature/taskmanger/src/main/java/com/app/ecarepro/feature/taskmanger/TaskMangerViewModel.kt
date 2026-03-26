package com.app.ecarepro.feature.taskmanger

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.repository.TaskManagerRepository
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskMangerViewModel @Inject constructor(
    private val taskManagerRepository: TaskManagerRepository,
) : BaseViewModel<TaskMangerIntent, TaskMangerEvent>() {

    private val _uiState: MutableStateFlow<UiState<TaskMangerUiState>> =
        MutableStateFlow(UiState.Success(TaskMangerUiState()))
    val uiState = _uiState.asStateFlow()

    // All loaded tasks (presentation layer)
    private var allTasks: TaskManagementData = TaskManagementData()

    companion object {
        val FILTER_TABS = listOf(
            "My tasks",
            "All tasks",
            "Assigned to me",
            "Assigned by me",
            "Watch list",
            "Closed"
        )
    }

    init {
        fetchTasks()
    }

    private fun fetchTasks(isRefreshing: Boolean = false) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: TaskMangerUiState()
        val filterIndex = currentState.selectedFilterIndex

        viewModelScope.launch {
            _uiState.update { state ->
                if (isRefreshing) {
                    (state as? UiState.Success)?.let { successState ->
                        UiState.Success(successState.data.copy(isRefreshing = true))
                    } ?: state
                } else {
                    (state as? UiState.Success)?.let { successState ->
                        UiState.Success(successState.data.copy(isLoading = true))
                    } ?: UiState.Loading
                }
            }

            taskManagerRepository.getTaskOverview(filter = filterIndex).collect { result ->
                result.onSuccess { domainData ->
                    val data = domainData.toPresentation()
                    allTasks = data
                    val latestState = (_uiState.value as? UiState.Success)?.data ?: TaskMangerUiState()
                    _uiState.update {
                        UiState.Success(
                            latestState.copy(
                                isLoading = false,
                                isRefreshing = false,
                                filteredOverdueTasks = filterTasks(data.overdue, latestState.searchQuery),
                                filteredTodaysTasks = filterTasks(data.todays, latestState.searchQuery),
                                filteredUpcomingTasks = filterTasks(data.upcoming, latestState.searchQuery),
                                filteredClosedTasks = filterTasks(data.closed, latestState.searchQuery),
                            )
                        )
                    }
                }.onFailure { error ->
                    val latestState = (_uiState.value as? UiState.Success)?.data ?: TaskMangerUiState()
                    _uiState.update {
                        UiState.Success(
                            latestState.copy(
                                isLoading = false,
                                isRefreshing = false,
                            )
                        )
                    }
                    sendEvent(
                        TaskMangerEvent.ShowMessage(
                            SnackbarMessage(text = error.message ?: "Failed to load tasks")
                        )
                    )
                }
            }
        }
    }

    override fun handleIntent(intent: TaskMangerIntent) {
        when (intent) {
            is TaskMangerIntent.OnBackClicked -> viewModelScope.launch {
                sendEvent(TaskMangerEvent.NavigateBack)
            }
            is TaskMangerIntent.OnRefresh -> fetchTasks(isRefreshing = true)
            is TaskMangerIntent.OnAddTaskClicked -> viewModelScope.launch {
                sendEvent(TaskMangerEvent.NavigateToAddTask)
            }
            is TaskMangerIntent.OnTaskClicked -> viewModelScope.launch {
                sendEvent(TaskMangerEvent.NavigateToTaskDetail(intent.taskId))
            }
            is TaskMangerIntent.OnFilterSelected -> onFilterSelected(intent.index)
            is TaskMangerIntent.OnSearchQueryChanged -> onSearchQueryChanged(intent.query)
            is TaskMangerIntent.OnToggleOverdueSection -> toggleSection(SectionType.OVERDUE)
            is TaskMangerIntent.OnToggleTodaySection -> toggleSection(SectionType.TODAY)
            is TaskMangerIntent.OnToggleUpcomingSection -> toggleSection(SectionType.UPCOMING)
            is TaskMangerIntent.OnToggleClosedSection -> toggleSection(SectionType.CLOSED)
            is TaskMangerIntent.OnStatusClicked -> onStatusClicked(intent.taskId, intent.currentStatus)
            is TaskMangerIntent.OnStatusSelected -> onStatusSelected(intent.taskId, intent.newStatus)
            is TaskMangerIntent.OnStatusSheetDismissed -> onStatusSheetDismissed()
            is TaskMangerIntent.OnPriorityClicked -> onPriorityClicked(intent.taskId, intent.currentPriority)
            is TaskMangerIntent.OnPrioritySelected -> onPrioritySelected(intent.taskId, intent.newPriority)
            is TaskMangerIntent.OnPrioritySheetDismissed -> onPrioritySheetDismissed()
            is TaskMangerIntent.OnAssigneeClicked -> onAssigneeClicked(intent.taskId, intent.assignees)
            is TaskMangerIntent.OnAssigneeSheetDismissed -> onAssigneeSheetDismissed()
        }
    }

    private fun onFilterSelected(index: Int) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return

        _uiState.update {
            UiState.Success(currentState.copy(selectedFilterIndex = index))
        }

        fetchTasks()
    }

    private fun onSearchQueryChanged(query: String) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return

        _uiState.update {
            UiState.Success(
                currentState.copy(
                    searchQuery = query,
                    filteredOverdueTasks = filterTasks(allTasks.overdue, query),
                    filteredTodaysTasks = filterTasks(allTasks.todays, query),
                    filteredUpcomingTasks = filterTasks(allTasks.upcoming, query),
                    filteredClosedTasks = filterTasks(allTasks.closed, query),
                )
            )
        }
    }

    private fun toggleSection(section: SectionType) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return

        _uiState.update {
            UiState.Success(
                when (section) {
                    SectionType.OVERDUE -> currentState.copy(isOverdueExpanded = !currentState.isOverdueExpanded)
                    SectionType.TODAY -> currentState.copy(isTodayExpanded = !currentState.isTodayExpanded)
                    SectionType.UPCOMING -> currentState.copy(isUpcomingExpanded = !currentState.isUpcomingExpanded)
                    SectionType.CLOSED -> currentState.copy(isClosedExpanded = !currentState.isClosedExpanded)
                }
            )
        }
    }

    private fun onStatusClicked(taskId: String, currentStatus: TaskStatus) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.update {
            UiState.Success(
                currentState.copy(
                    isStatusSheetVisible = true,
                    statusSheetTaskId = taskId,
                    statusSheetCurrentStatus = currentStatus,
                )
            )
        }
    }

    private fun onStatusSelected(taskId: String, newStatus: TaskStatus) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        // Dismiss bottom sheet and show loading
        _uiState.update {
            UiState.Success(currentState.copy(isStatusSheetVisible = false, isLoading = true))
        }

        viewModelScope.launch {
            taskManagerRepository.updateTaskStatus(taskId, newStatus.value).collect { result ->
                result.onSuccess { message ->
                    sendEvent(
                        TaskMangerEvent.ShowMessage(
                            SnackbarMessage(text = message.ifEmpty { "Status updated to ${newStatus.displayName}" })
                        )
                    )
                    // Reload tasks while maintaining the current filter tab
                    fetchTasks()
                }.onFailure { error ->
                    val latestState = (_uiState.value as? UiState.Success)?.data ?: TaskMangerUiState()
                    _uiState.update {
                        UiState.Success(latestState.copy(isLoading = false))
                    }
                    sendEvent(
                        TaskMangerEvent.ShowMessage(
                            SnackbarMessage(text = error.message ?: "Failed to update status")
                        )
                    )
                }
            }
        }
    }

    private fun onStatusSheetDismissed() {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.update {
            UiState.Success(currentState.copy(isStatusSheetVisible = false))
        }
    }

    private fun onPriorityClicked(taskId: String, currentPriority: TaskPriority) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.update {
            UiState.Success(
                currentState.copy(
                    isPrioritySheetVisible = true,
                    prioritySheetTaskId = taskId,
                    prioritySheetCurrentPriority = currentPriority,
                )
            )
        }
    }

    private fun onPrioritySelected(taskId: String, newPriority: TaskPriority) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.update {
            UiState.Success(currentState.copy(isPrioritySheetVisible = false))
        }
        // TODO: Call API to update task priority when API is available
        sendEvent(
            TaskMangerEvent.ShowMessage(
                SnackbarMessage(text = "Priority updated to ${newPriority.displayName}")
            )
        )
    }

    private fun onPrioritySheetDismissed() {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.update {
            UiState.Success(currentState.copy(isPrioritySheetVisible = false))
        }
    }

    private fun onAssigneeClicked(taskId: String, assignees: List<TaskAssigneePresentation>) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.update {
            UiState.Success(
                currentState.copy(
                    isAssigneeSheetVisible = true,
                    assigneeSheetTaskId = taskId,
                    assigneeSheetAssignees = assignees,
                )
            )
        }
    }

    private fun onAssigneeSheetDismissed() {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.update {
            UiState.Success(currentState.copy(isAssigneeSheetVisible = false))
        }
    }

    private fun filterTasks(tasks: List<TaskPresentation>, searchQuery: String): List<TaskPresentation> {
        if (searchQuery.isBlank()) return tasks

        return tasks.filter { task ->
            task.taskTitle.contains(searchQuery, ignoreCase = true) ||
            task.taskList.contains(searchQuery, ignoreCase = true) ||
            task.description.contains(searchQuery, ignoreCase = true)
        }
    }

    private enum class SectionType {
        OVERDUE, TODAY, UPCOMING, CLOSED
    }
}

// Presentation data class to hold all task categories
data class TaskManagementData(
    val overdue: List<TaskPresentation> = emptyList(),
    val todays: List<TaskPresentation> = emptyList(),
    val upcoming: List<TaskPresentation> = emptyList(),
    val closed: List<TaskPresentation> = emptyList()
)

@Immutable
data class TaskMangerUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val searchQuery: String = "",
    val selectedFilterIndex: Int = 0,
    val filterTabs: List<String> = TaskMangerViewModel.FILTER_TABS,
    val filteredOverdueTasks: List<TaskPresentation> = emptyList(),
    val filteredTodaysTasks: List<TaskPresentation> = emptyList(),
    val filteredUpcomingTasks: List<TaskPresentation> = emptyList(),
    val filteredClosedTasks: List<TaskPresentation> = emptyList(),
    val isOverdueExpanded: Boolean = true,
    val isTodayExpanded: Boolean = true,
    val isUpcomingExpanded: Boolean = false,
    val isClosedExpanded: Boolean = false,
    val isStatusSheetVisible: Boolean = false,
    val statusSheetTaskId: String = "",
    val statusSheetCurrentStatus: TaskStatus = TaskStatus.OPEN,
    val isPrioritySheetVisible: Boolean = false,
    val prioritySheetTaskId: String = "",
    val prioritySheetCurrentPriority: TaskPriority = TaskPriority.NO_PRIORITY,
    val isAssigneeSheetVisible: Boolean = false,
    val assigneeSheetTaskId: String = "",
    val assigneeSheetAssignees: List<TaskAssigneePresentation> = emptyList(),
)

// Task Presentation Model for UI
@Immutable
data class TaskPresentation(
    val id: String,
    val taskTitle: String,
    val description: String = "",
    val taskList: String = "",
    val priority: TaskPriority = TaskPriority.NO_PRIORITY,
    val status: TaskStatus = TaskStatus.OPEN,
    val startDate: String = "",
    val dueDate: String = "",
    val assignees: List<TaskAssigneePresentation> = emptyList(),
    val attachment: String = ""
)

// Assignee Presentation Model for UI
@Immutable
data class TaskAssigneePresentation(
    val id: Int,
    val name: String,
    val designation: String = "",
    val photo: String = "",
    val status: Int = 0
)

// Task Priority Enum matching API values
// 0 = No Priority, 1 = Low, 2 = Medium, 3 = High
enum class TaskPriority(val value: Int, val displayName: String) {
    NO_PRIORITY(0, "No Priority"),
    LOW(1, "Low"),
    MEDIUM(2, "Medium"),
    HIGH(3, "High");

    companion object {
        fun fromValue(value: Int): TaskPriority =
            entries.firstOrNull { it.value == value } ?: NO_PRIORITY
    }
}

// Task Status Enum matching API values
// 0 = Open, 1 = In Progress, 2 = Hold, 3 = Closed
enum class TaskStatus(val value: Int, val displayName: String) {
    OPEN(0, "Open"),
    IN_PROGRESS(1, "In Progress"),
    HOLD(2, "Hold"),
    CLOSED(3, "Closed");

    companion object {
        fun fromValue(value: Int): TaskStatus =
            entries.firstOrNull { it.value == value } ?: OPEN
    }
}

sealed interface TaskMangerIntent {
    data object OnBackClicked : TaskMangerIntent
    data object OnRefresh : TaskMangerIntent
    data object OnAddTaskClicked : TaskMangerIntent
    data class OnTaskClicked(val taskId: String) : TaskMangerIntent
    data class OnFilterSelected(val index: Int) : TaskMangerIntent
    data class OnSearchQueryChanged(val query: String) : TaskMangerIntent
    data object OnToggleOverdueSection : TaskMangerIntent
    data object OnToggleTodaySection : TaskMangerIntent
    data object OnToggleUpcomingSection : TaskMangerIntent
    data object OnToggleClosedSection : TaskMangerIntent
    data class OnStatusClicked(val taskId: String, val currentStatus: TaskStatus) : TaskMangerIntent
    data class OnStatusSelected(val taskId: String, val newStatus: TaskStatus) : TaskMangerIntent
    data object OnStatusSheetDismissed : TaskMangerIntent
    data class OnPriorityClicked(val taskId: String, val currentPriority: TaskPriority) : TaskMangerIntent
    data class OnPrioritySelected(val taskId: String, val newPriority: TaskPriority) : TaskMangerIntent
    data object OnPrioritySheetDismissed : TaskMangerIntent
    data class OnAssigneeClicked(val taskId: String, val assignees: List<TaskAssigneePresentation>) : TaskMangerIntent
    data object OnAssigneeSheetDismissed : TaskMangerIntent
}

sealed interface TaskMangerEvent {
    data object NavigateBack : TaskMangerEvent
    data object NavigateToAddTask : TaskMangerEvent
    data class NavigateToTaskDetail(val taskId: String) : TaskMangerEvent
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : TaskMangerEvent
}

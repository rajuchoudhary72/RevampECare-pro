package com.app.ecarepro.feature.taskmanger.screens

import android.util.Base64
import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.TaskAssignee
import com.app.ecarepro.core.domain.model.TaskListItem
import com.app.ecarepro.core.domain.model.TaskWatcherDomain
import com.app.ecarepro.core.domain.repository.TaskManagerRepository
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.SelectedFileDetails
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.feature.taskmanger.TaskPriority
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TaskCreateViewModel @Inject constructor(
    private val taskManagerRepository: TaskManagerRepository,
) : BaseViewModel<TaskCreateIntent, TaskCreateEvent>() {

    private val _uiState = MutableStateFlow<UiState<TaskCreateUiState>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    init {
        loadCreateTaskData()
    }

    private fun loadCreateTaskData() {
        viewModelScope.launch {
            _uiState.update { UiState.Loading }
            taskManagerRepository.getCreateTaskData().collect { result ->
                result.onSuccess { data ->
                    _uiState.update {
                        UiState.Success(
                            TaskCreateUiState(
                                taskLists = data.taskLists,
                                allWatchers = data.watchers,
                            )
                        )
                    }
                }.onFailure { error ->
                    _uiState.update {
                        UiState.Error(error.message ?: "Failed to load data")
                    }
                }
            }
        }
    }

    override fun handleIntent(intent: TaskCreateIntent) {
        when (intent) {
            is TaskCreateIntent.OnBackClicked -> sendEvent(TaskCreateEvent.NavigateBack)
            is TaskCreateIntent.OnRetry -> loadCreateTaskData()

            // Form fields
            is TaskCreateIntent.OnTaskNameChanged -> updateState { it.copy(taskName = intent.name) }
            is TaskCreateIntent.OnDescriptionChanged -> updateState { it.copy(description = intent.description) }
            is TaskCreateIntent.OnRemindBeforeChanged -> updateState {
                it.copy(remindBeforeHours = intent.hours.coerceAtLeast(0))
            }
            is TaskCreateIntent.OnIsPublicChanged -> updateState {
                it.copy(isPublic = intent.isPublic)
            }

            // Start date
            is TaskCreateIntent.OnStartDateChanged -> updateState {
                it.copy(
                    startDateApi = intent.apiDate,
                    startDateDisplay = intent.displayDate,
                    startDateMillis = intent.millis,
                ).let { state ->
                    // Auto-correct due date if start date is after due date
                    if (state.dueDateApi.isNotEmpty()) {
                        try {
                            val startParsed = apiDateFormat.parse(intent.apiDate)
                            val dueParsed = apiDateFormat.parse(state.dueDateApi)
                            if (startParsed != null && dueParsed != null && startParsed.after(dueParsed)) {
                                return@let state.copy(
                                    dueDateApi = intent.apiDate,
                                    dueDateDisplay = intent.displayDate,
                                )
                            }
                        } catch (_: Exception) { }
                    }
                    state
                }
            }

            // Due date
            is TaskCreateIntent.OnEndDateChanged -> updateState {
                it.copy(
                    dueDateApi = intent.apiDate,
                    dueDateDisplay = intent.displayDate,
                )
            }

            // Priority
            is TaskCreateIntent.OnPriorityClicked -> updateState { it.copy(isPrioritySheetVisible = true) }
            is TaskCreateIntent.OnPrioritySelected -> updateState {
                it.copy(selectedPriority = intent.priority, isPrioritySheetVisible = false)
            }
            is TaskCreateIntent.OnPrioritySheetDismissed -> updateState { it.copy(isPrioritySheetVisible = false) }

            // Task list
            is TaskCreateIntent.OnTaskListClicked -> updateState { it.copy(isTaskListSheetVisible = true) }
            is TaskCreateIntent.OnTaskListSelected -> onTaskListSelected(intent.taskList)
            is TaskCreateIntent.OnTaskListSheetDismissed -> updateState { it.copy(isTaskListSheetVisible = false) }

            // Assignees
            is TaskCreateIntent.OnAssigneeClicked -> updateState { it.copy(isAssigneeSheetVisible = true) }
            is TaskCreateIntent.OnAssigneeSelectionChanged -> updateState { it.copy(selectedAssigneeIds = intent.selectedIds) }
            is TaskCreateIntent.OnAssigneeSheetDismissed -> updateState { it.copy(isAssigneeSheetVisible = false) }
            is TaskCreateIntent.OnClearAssignees -> updateState { it.copy(selectedAssigneeIds = emptyList()) }

            // Watchers
            is TaskCreateIntent.OnWatcherClicked -> updateState { it.copy(isWatcherSheetVisible = true) }
            is TaskCreateIntent.OnWatcherSelectionChanged -> updateState { it.copy(selectedWatcherIds = intent.selectedIds) }
            is TaskCreateIntent.OnWatcherSheetDismissed -> updateState { it.copy(isWatcherSheetVisible = false) }
            is TaskCreateIntent.OnClearWatchers -> updateState { it.copy(selectedWatcherIds = emptyList()) }

            // Attachment
            is TaskCreateIntent.OnAttachmentClicked -> updateState { it.copy(isFileUploadSheetVisible = true) }
            is TaskCreateIntent.OnFileSelected -> updateState { it.copy(selectedFile = intent.file, isFileUploadSheetVisible = false) }
            is TaskCreateIntent.OnRemoveAttachment -> updateState {
                it.copy(selectedFile = null)
            }
            is TaskCreateIntent.OnFileUploadSheetDismissed -> updateState { it.copy(isFileUploadSheetVisible = false) }

            // Submit
            is TaskCreateIntent.OnSubmitClicked -> onSubmitClicked()

            // Error display
            is TaskCreateIntent.OnShowError -> {
                sendEvent(TaskCreateEvent.ShowMessage(SnackbarMessage(text = intent.message)))
            }
        }
    }

    private fun onTaskListSelected(taskList: TaskListItem) {
        updateState {
            it.copy(
                selectedTaskList = taskList,
                isTaskListSheetVisible = false,
                availableAssignees = emptyList(),
                selectedAssigneeIds = emptyList(),
                isLoadingAssignees = true,
            )
        }
        // Fetch assignees for selected task list
        viewModelScope.launch {
            taskManagerRepository.getTaskListAssignees(taskList.id).collect { result ->
                result.onSuccess { assignees ->
                    updateState {
                        it.copy(
                            availableAssignees = assignees,
                            isLoadingAssignees = false,
                        )
                    }
                }.onFailure { error ->
                    updateState { it.copy(isLoadingAssignees = false) }
                    sendEvent(
                        TaskCreateEvent.ShowMessage(
                            SnackbarMessage(text = error.message ?: "Failed to load assignees")
                        )
                    )
                }
            }
        }
    }

    private fun onSubmitClicked() {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return

        if (currentState.selectedTaskList == null) {
            sendEvent(TaskCreateEvent.ShowMessage(SnackbarMessage(text = "Task list is required")))
            return
        }
        if (currentState.taskName.isBlank()) {
            sendEvent(TaskCreateEvent.ShowMessage(SnackbarMessage(text = "Task name is required")))
            return
        }
        if (currentState.selectedAssigneeIds.isEmpty()) {
            sendEvent(TaskCreateEvent.ShowMessage(SnackbarMessage(text = "At least one assignee is required")))
            return
        }
        if (currentState.description.isBlank()) {
            sendEvent(TaskCreateEvent.ShowMessage(SnackbarMessage(text = "Description is required")))
            return
        }
        if (currentState.startDateApi.isEmpty()) {
            sendEvent(TaskCreateEvent.ShowMessage(SnackbarMessage(text = "Start date is required")))
            return
        }
        if (currentState.dueDateApi.isEmpty()) {
            sendEvent(TaskCreateEvent.ShowMessage(SnackbarMessage(text = "Due date is required")))
            return
        }
        if (!currentState.isPublic && currentState.selectedWatcherIds.isEmpty()) {
            sendEvent(TaskCreateEvent.ShowMessage(SnackbarMessage(text = "Watchers required or make task public")))
            return
        }

        updateState { it.copy(isSaving = true) }

        viewModelScope.launch {
            // Process attachment if present
            var attachmentBase64: String? = null
            var attachmentFileExt: String? = null

            val file = currentState.selectedFile
            if (file != null) {
                try {
                    val bytes = withContext(Dispatchers.IO) { file.file.readBytes() }
                    attachmentBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
                    attachmentFileExt = file.name.substringAfterLast('.', "")
                } catch (_: Exception) { }
            }

            taskManagerRepository.saveTask(
                tlId = currentState.selectedTaskList!!.id,
                title = currentState.taskName.trim(),
                description = currentState.description.trim(),
                priority = currentState.selectedPriority.value,
                startDate = currentState.startDateApi,
                dueDate = currentState.dueDateApi,
                remindBefore = currentState.remindBeforeHours,
                isPublic = currentState.isPublic,
                assigneesIDs = currentState.selectedAssigneeIds.joinToString(","),
                watchersIDs = if (currentState.isPublic) "" else currentState.selectedWatcherIds.joinToString(","),
                attachmentBase64 = attachmentBase64,
                attachmentFileExt = attachmentFileExt,
            ).collect { result ->
                result.onSuccess { message ->
                    updateState { it.copy(isSaving = false) }
                    sendEvent(
                        TaskCreateEvent.ShowMessage(
                            SnackbarMessage(text = message.ifEmpty { "Task created successfully" })
                        )
                    )
                    sendEvent(TaskCreateEvent.TaskCreated)
                }.onFailure { error ->
                    updateState { it.copy(isSaving = false) }
                    sendEvent(
                        TaskCreateEvent.ShowMessage(
                            SnackbarMessage(text = error.message ?: "Failed to create task")
                        )
                    )
                }
            }
        }
    }

    private fun updateState(transform: (TaskCreateUiState) -> TaskCreateUiState) {
        _uiState.update { state ->
            when (state) {
                is UiState.Success -> UiState.Success(transform(state.data))
                else -> state
            }
        }
    }
}

// ============== UI State ==============

@Immutable
data class TaskCreateUiState(
    // Form fields
    val taskName: String = "",
    val description: String = "",
    val selectedPriority: TaskPriority = TaskPriority.LOW,
    val selectedTaskList: TaskListItem? = null,
    val startDateApi: String = "",
    val startDateDisplay: String = "",
    val startDateMillis: Long? = null,
    val dueDateApi: String = "",
    val dueDateDisplay: String = "",
    val remindBeforeHours: Int = 1,
    val isPublic: Boolean = false,

    // Data lists
    val taskLists: List<TaskListItem> = emptyList(),
    val allWatchers: List<TaskWatcherDomain> = emptyList(),
    val availableAssignees: List<TaskAssignee> = emptyList(),
    val isLoadingAssignees: Boolean = false,

    // Selections
    val selectedAssigneeIds: List<String> = emptyList(),
    val selectedWatcherIds: List<String> = emptyList(),

    // Attachment
    val selectedFile: SelectedFileDetails? = null,

    // Sheet visibility
    val isPrioritySheetVisible: Boolean = false,
    val isTaskListSheetVisible: Boolean = false,
    val isAssigneeSheetVisible: Boolean = false,
    val isWatcherSheetVisible: Boolean = false,
    val isFileUploadSheetVisible: Boolean = false,

    // Validation
    val isSaving: Boolean = false,
)

// ============== Intents ==============

sealed interface TaskCreateIntent {
    data object OnBackClicked : TaskCreateIntent
    data object OnRetry : TaskCreateIntent

    // Form fields
    data class OnTaskNameChanged(val name: String) : TaskCreateIntent
    data class OnDescriptionChanged(val description: String) : TaskCreateIntent
    data class OnRemindBeforeChanged(val hours: Int) : TaskCreateIntent
    data class OnIsPublicChanged(val isPublic: Boolean) : TaskCreateIntent

    // Dates
    data class OnStartDateChanged(val apiDate: String, val displayDate: String, val millis: Long) : TaskCreateIntent
    data class OnEndDateChanged(val apiDate: String, val displayDate: String, val millis: Long) : TaskCreateIntent

    // Priority
    data object OnPriorityClicked : TaskCreateIntent
    data class OnPrioritySelected(val priority: TaskPriority) : TaskCreateIntent
    data object OnPrioritySheetDismissed : TaskCreateIntent

    // Task list
    data object OnTaskListClicked : TaskCreateIntent
    data class OnTaskListSelected(val taskList: TaskListItem) : TaskCreateIntent
    data object OnTaskListSheetDismissed : TaskCreateIntent

    // Assignees
    data object OnAssigneeClicked : TaskCreateIntent
    data class OnAssigneeSelectionChanged(val selectedIds: List<String>) : TaskCreateIntent
    data object OnAssigneeSheetDismissed : TaskCreateIntent
    data object OnClearAssignees : TaskCreateIntent

    // Watchers
    data object OnWatcherClicked : TaskCreateIntent
    data class OnWatcherSelectionChanged(val selectedIds: List<String>) : TaskCreateIntent
    data object OnWatcherSheetDismissed : TaskCreateIntent
    data object OnClearWatchers : TaskCreateIntent

    // Attachment
    data object OnAttachmentClicked : TaskCreateIntent
    data class OnFileSelected(val file: SelectedFileDetails) : TaskCreateIntent
    data object OnRemoveAttachment : TaskCreateIntent
    data object OnFileUploadSheetDismissed : TaskCreateIntent

    // Submit
    data object OnSubmitClicked : TaskCreateIntent

    // Error
    data class OnShowError(val message: String) : TaskCreateIntent
}

// ============== Events ==============

sealed interface TaskCreateEvent {
    data object NavigateBack : TaskCreateEvent
    data object TaskCreated : TaskCreateEvent
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : TaskCreateEvent
}

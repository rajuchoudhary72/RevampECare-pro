package com.app.ecarepro.feature.taskmanger.screens

import android.util.Base64
import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.TaskActivityDomain
import com.app.ecarepro.core.domain.model.TaskCommentDomain
import com.app.ecarepro.core.domain.model.TaskDetailsDomain
import com.app.ecarepro.core.domain.model.TaskWatcherDomain
import com.app.ecarepro.core.domain.repository.TaskManagerRepository
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.SelectedFileDetails
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.feature.taskmanger.TaskAssigneePresentation
import com.app.ecarepro.feature.taskmanger.TaskPriority
import com.app.ecarepro.feature.taskmanger.TaskStatus
import com.app.ecarepro.feature.taskmanger.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TaskMangerDetailsViewModel @Inject constructor(
    private val taskManagerRepository: TaskManagerRepository,
) : BaseViewModel<TaskMangerDetailsIntent, TaskMangerDetailsEvent>() {

    private var taskId: String = ""

    private val _uiState = MutableStateFlow<UiState<TaskMangerDetailsUiState>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun loadTaskDetails(taskId: String) {
        this.taskId = taskId
        // Skip if already loaded and not refreshing
        if (_uiState.value is UiState.Success) return
        fetchTaskDetails()
    }

    private fun fetchTaskDetails(isRefreshing: Boolean = false) {
        viewModelScope.launch {
            if (!isRefreshing) {
                _uiState.update { UiState.Loading }
            } else {
                updateSuccessState { it.copy(isRefreshing = true) }
            }

            taskManagerRepository.getTaskDetails(taskId).collect { result ->
                result.onSuccess { domain ->
                    val presentation = domain.toPresentation()
                    _uiState.update {
                        UiState.Success(presentation.copy(isRefreshing = false))
                    }
                }.onFailure { error ->
                    if (isRefreshing) {
                        updateSuccessState { it.copy(isRefreshing = false) }
                        sendEvent(
                            TaskMangerDetailsEvent.ShowMessage(
                                SnackbarMessage(text = error.message ?: "Failed to load task details")
                            )
                        )
                    } else {
                        _uiState.update {
                            UiState.Error(error.message ?: "Failed to load task details")
                        }
                    }
                }
            }
        }
    }

    override fun handleIntent(intent: TaskMangerDetailsIntent) {
        when (intent) {
            is TaskMangerDetailsIntent.OnBackClicked -> {
                sendEvent(TaskMangerDetailsEvent.NavigateBack)
            }

            is TaskMangerDetailsIntent.OnRefresh -> fetchTaskDetails(isRefreshing = true)

            // Tab switching
            is TaskMangerDetailsIntent.OnTabSelected -> {
                updateSuccessState { it.copy(selectedTabIndex = intent.index) }
            }

            // Comment
            is TaskMangerDetailsIntent.OnCommentTextChanged -> {
                updateSuccessState { it.copy(commentText = intent.text) }
            }

            is TaskMangerDetailsIntent.OnSendComment -> sendComment()

            // Edit Title
            is TaskMangerDetailsIntent.OnEditTitleClicked -> {
                updateSuccessState {
                    it.copy(
                        isEditDialogVisible = true,
                        editDialogTitle = "Edit Title",
                        editDialogCurrentValue = it.title,
                        editDialogFieldName = TaskFieldName.TASK_TITLE,
                    )
                }
            }

            // Edit Description
            is TaskMangerDetailsIntent.OnEditDescriptionClicked -> {
                updateSuccessState {
                    it.copy(
                        isEditDialogVisible = true,
                        editDialogTitle = "Edit Description",
                        editDialogCurrentValue = it.description,
                        editDialogFieldName = TaskFieldName.DESCRIPTION,
                    )
                }
            }

            is TaskMangerDetailsIntent.OnEditDialogValueChanged -> {
                updateSuccessState { it.copy(editDialogCurrentValue = intent.value) }
            }

            is TaskMangerDetailsIntent.OnEditDialogConfirmed -> onEditDialogConfirmed()
            is TaskMangerDetailsIntent.OnEditDialogDismissed -> {
                updateSuccessState { it.copy(isEditDialogVisible = false) }
            }

            // Date editing
            is TaskMangerDetailsIntent.OnEditStartDateClicked -> {
                sendEvent(TaskMangerDetailsEvent.ShowDatePicker(TaskFieldName.START_DATE))
            }

            is TaskMangerDetailsIntent.OnEditDueDateClicked -> {
                sendEvent(TaskMangerDetailsEvent.ShowDatePicker(TaskFieldName.DUE_DATE))
            }

            is TaskMangerDetailsIntent.OnDateSelected -> onDateSelected(intent.fieldName, intent.date)

            // Priority
            is TaskMangerDetailsIntent.OnPriorityClicked -> {
                val currentState = (_uiState.value as? UiState.Success)?.data ?: return
                updateSuccessState {
                    it.copy(
                        isPrioritySheetVisible = true,
                        currentPriority = currentState.priority,
                    )
                }
            }

            is TaskMangerDetailsIntent.OnPrioritySelected -> onPrioritySelected(intent.priority)
            is TaskMangerDetailsIntent.OnPrioritySheetDismissed -> {
                updateSuccessState { it.copy(isPrioritySheetVisible = false) }
            }

            // Status
            is TaskMangerDetailsIntent.OnStatusClicked -> {
                val currentState = (_uiState.value as? UiState.Success)?.data ?: return
                if (currentState.canChangeStatus && currentState.imOwner) {
                    updateSuccessState {
                        it.copy(
                            isStatusSheetVisible = true,
                            currentStatus = currentState.status,
                        )
                    }
                } else {
                    sendEvent(
                        TaskMangerDetailsEvent.ShowMessage(
                            SnackbarMessage(text = "You don't have permission to change status")
                        )
                    )
                }
            }

            is TaskMangerDetailsIntent.OnStatusSelected -> onStatusSelected(intent.status)
            is TaskMangerDetailsIntent.OnStatusSheetDismissed -> {
                updateSuccessState { it.copy(isStatusSheetVisible = false) }
            }

            // Assignee
            is TaskMangerDetailsIntent.OnAssigneeClicked -> {
                val currentState = (_uiState.value as? UiState.Success)?.data ?: return
                updateSuccessState {
                    it.copy(
                        isAssigneeSheetVisible = true,
                        assignees = currentState.assignees,
                    )
                }
            }

            is TaskMangerDetailsIntent.OnAssigneeSheetDismissed -> {
                updateSuccessState { it.copy(isAssigneeSheetVisible = false) }
            }

            // Attachment
            is TaskMangerDetailsIntent.OnUploadAttachmentClicked -> {
                updateSuccessState { it.copy(isFileUploadSheetVisible = true) }
            }

            is TaskMangerDetailsIntent.OnDismissFileUploadSheet -> {
                updateSuccessState { it.copy(isFileUploadSheetVisible = false) }
            }

            is TaskMangerDetailsIntent.OnFileSelected -> {
                onFileSelected(intent.file)
            }

            is TaskMangerDetailsIntent.OnShowError -> {
                sendEvent(
                    TaskMangerDetailsEvent.ShowMessage(
                        SnackbarMessage(text = intent.message)
                    )
                )
            }
        }
    }

    private fun onEditDialogConfirmed() {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        val fieldName = currentState.editDialogFieldName ?: return
        val newValue = currentState.editDialogCurrentValue
        val oldValue = when (fieldName) {
            TaskFieldName.TASK_TITLE -> currentState.title
            TaskFieldName.DESCRIPTION -> currentState.description
            else -> ""
        }

        if (newValue.isBlank()) {
            sendEvent(
                TaskMangerDetailsEvent.ShowMessage(
                    SnackbarMessage(text = "Value cannot be empty")
                )
            )
            return
        }

        updateSuccessState { it.copy(isEditDialogVisible = false, isActionLoading = true) }

        viewModelScope.launch {
            taskManagerRepository.updateTask(
                fieldName = fieldName.value,
                id = taskId,
                newValue = newValue,
                oldValue = oldValue,
            ).collect { result ->
                result.onSuccess { message ->
                    sendEvent(
                        TaskMangerDetailsEvent.ShowMessage(
                            SnackbarMessage(text = message.ifEmpty { "${fieldName.displayName} updated" })
                        )
                    )
                    fetchTaskDetails(isRefreshing = true)
                }.onFailure { error ->
                    updateSuccessState { it.copy(isActionLoading = false) }
                    sendEvent(
                        TaskMangerDetailsEvent.ShowMessage(
                            SnackbarMessage(text = error.message ?: "Update failed")
                        )
                    )
                }
            }
        }
    }

    private fun onDateSelected(fieldName: TaskFieldName, date: String) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        val oldValue = when (fieldName) {
            TaskFieldName.START_DATE -> currentState.startDate
            TaskFieldName.DUE_DATE -> currentState.dueDate
            else -> ""
        }

        updateSuccessState { it.copy(isActionLoading = true) }

        viewModelScope.launch {
            taskManagerRepository.updateTask(
                fieldName = fieldName.value,
                id = taskId,
                newValue = date,
                oldValue = oldValue,
            ).collect { result ->
                result.onSuccess { message ->
                    sendEvent(
                        TaskMangerDetailsEvent.ShowMessage(
                            SnackbarMessage(text = message.ifEmpty { "${fieldName.displayName} updated" })
                        )
                    )
                    fetchTaskDetails(isRefreshing = true)
                }.onFailure { error ->
                    updateSuccessState { it.copy(isActionLoading = false) }
                    sendEvent(
                        TaskMangerDetailsEvent.ShowMessage(
                            SnackbarMessage(text = error.message ?: "Update failed")
                        )
                    )
                }
            }
        }
    }

    private fun onPrioritySelected(priority: TaskPriority) {
        updateSuccessState { it.copy(isPrioritySheetVisible = false, isActionLoading = true) }

        val currentState = (_uiState.value as? UiState.Success)?.data ?: return

        viewModelScope.launch {
            taskManagerRepository.updateTask(
                fieldName = TaskFieldName.PRIORITY.value,
                id = taskId,
                newValue = priority.value.toString(),
                oldValue = currentState.priority.value.toString(),
            ).collect { result ->
                result.onSuccess { message ->
                    sendEvent(
                        TaskMangerDetailsEvent.ShowMessage(
                            SnackbarMessage(text = message.ifEmpty { "Priority updated to ${priority.displayName}" })
                        )
                    )
                    fetchTaskDetails(isRefreshing = true)
                }.onFailure { error ->
                    updateSuccessState { it.copy(isActionLoading = false) }
                    sendEvent(
                        TaskMangerDetailsEvent.ShowMessage(
                            SnackbarMessage(text = error.message ?: "Failed to update priority")
                        )
                    )
                }
            }
        }
    }

    private fun onStatusSelected(status: TaskStatus) {
        updateSuccessState { it.copy(isStatusSheetVisible = false, isActionLoading = true) }

        viewModelScope.launch {
            taskManagerRepository.updateTaskStatus(taskId, status.value).collect { result ->
                result.onSuccess { message ->
                    sendEvent(
                        TaskMangerDetailsEvent.ShowMessage(
                            SnackbarMessage(text = message.ifEmpty { "Status updated to ${status.displayName}" })
                        )
                    )
                    fetchTaskDetails(isRefreshing = true)
                }.onFailure { error ->
                    updateSuccessState { it.copy(isActionLoading = false) }
                    sendEvent(
                        TaskMangerDetailsEvent.ShowMessage(
                            SnackbarMessage(text = error.message ?: "Failed to update status")
                        )
                    )
                }
            }
        }
    }

    private fun sendComment() {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        val comment = currentState.commentText.trim()

        if (comment.isBlank()) {
            sendEvent(
                TaskMangerDetailsEvent.ShowMessage(
                    SnackbarMessage(text = "Please enter a comment")
                )
            )
            return
        }

        updateSuccessState { it.copy(isCommentSending = true) }

        viewModelScope.launch {
            taskManagerRepository.sendComment(taskId, comment).collect { result ->
                result.onSuccess { message ->
                    updateSuccessState { it.copy(commentText = "", isCommentSending = false) }
                    sendEvent(
                        TaskMangerDetailsEvent.ShowMessage(
                            SnackbarMessage(text = message.ifEmpty { "Comment added" })
                        )
                    )
                    fetchTaskDetails(isRefreshing = true)
                }.onFailure { error ->
                    updateSuccessState { it.copy(isCommentSending = false) }
                    sendEvent(
                        TaskMangerDetailsEvent.ShowMessage(
                            SnackbarMessage(text = error.message ?: "Failed to send comment")
                        )
                    )
                }
            }
        }
    }

    private fun onFileSelected(file: SelectedFileDetails) {
        updateSuccessState { it.copy(isFileUploadSheetVisible = false, isActionLoading = true) }

        viewModelScope.launch {
            val base64 = withContext(Dispatchers.IO) {
                val bytes = file.file.readBytes()
                Base64.encodeToString(bytes, Base64.NO_WRAP)
            }
            val fileExt = file.name.substringAfterLast('.', "")

            taskManagerRepository.updateTaskAttachment(taskId, base64, fileExt).collect { result ->
                result.onSuccess { message ->
                    sendEvent(
                        TaskMangerDetailsEvent.ShowMessage(
                            SnackbarMessage(text = message.ifEmpty { "Attachment uploaded" })
                        )
                    )
                    fetchTaskDetails(isRefreshing = true)
                }.onFailure { error ->
                    updateSuccessState { it.copy(isActionLoading = false) }
                    sendEvent(
                        TaskMangerDetailsEvent.ShowMessage(
                            SnackbarMessage(text = error.message ?: "Failed to upload attachment")
                        )
                    )
                }
            }
        }
    }

    private fun updateSuccessState(transform: (TaskMangerDetailsUiState) -> TaskMangerDetailsUiState) {
        _uiState.update { state ->
            when (state) {
                is UiState.Success -> UiState.Success(transform(state.data))
                else -> state
            }
        }
    }
}

// ============== Presentation Mapper ==============

private fun TaskDetailsDomain.toPresentation(): TaskMangerDetailsUiState {
    val taskInfo = task ?: return TaskMangerDetailsUiState()
    return TaskMangerDetailsUiState(
        taskId = taskInfo.id,
        title = taskInfo.taskTitle,
        description = taskInfo.description,
        assignBy = taskInfo.assignBy,
        startDate = taskInfo.startDate,
        dueDate = taskInfo.dueDate,
        priority = TaskPriority.fromValue(taskInfo.priority),
        status = TaskStatus.fromValue(taskInfo.status),
        attachmentUrl = taskInfo.attachment,
        imOwner = taskInfo.imOwner,
        canChangeStatus = taskInfo.canChangeStatus,
        taskList = taskInfo.taskList,
        assignees = taskInfo.assignTo.map { it.toPresentation() },
        watchers = taskInfo.watchers.map { it.toPresentation() },
        comments = comments.map { it.toPresentation() },
        activities = activities.map { it.toPresentation() },
        myID = myID,
    )
}

private fun TaskWatcherDomain.toPresentation(): WatcherPresentation {
    return WatcherPresentation(
        userID = userID,
        name = name,
        designation = designation,
        photo = photo,
    )
}

private fun TaskCommentDomain.toPresentation(): CommentPresentation {
    return CommentPresentation(
        comment = comment,
        commentBy = commentBy,
        commentOn = commentOn,
        name = name,
        designation = designation,
        photo = photo,
    )
}

private fun TaskActivityDomain.toPresentation(): ActivityPresentation {
    return ActivityPresentation(
        activity = activity,
        actionOn = actionOn,
        actorID = actorID,
        name = name,
        designation = designation,
        photo = photo,
    )
}

// ============== UI State ==============

@Immutable
data class TaskMangerDetailsUiState(
    val isRefreshing: Boolean = false,
    val isActionLoading: Boolean = false,
    val isCommentSending: Boolean = false,
    val taskId: String = "",
    val title: String = "",
    val description: String = "",
    val assignBy: String = "",
    val startDate: String = "",
    val dueDate: String = "",
    val priority: TaskPriority = TaskPriority.NO_PRIORITY,
    val status: TaskStatus = TaskStatus.OPEN,
    val attachmentUrl: String = "",
    val imOwner: Boolean = false,
    val canChangeStatus: Boolean = false,
    val taskList: String = "",
    val myID: Int = 0,
    val assignees: List<TaskAssigneePresentation> = emptyList(),
    val watchers: List<WatcherPresentation> = emptyList(),
    val comments: List<CommentPresentation> = emptyList(),
    val activities: List<ActivityPresentation> = emptyList(),
    // Tab state
    val selectedTabIndex: Int = 0,
    // Comment input
    val commentText: String = "",
    // Edit dialog state
    val isEditDialogVisible: Boolean = false,
    val editDialogTitle: String = "",
    val editDialogCurrentValue: String = "",
    val editDialogFieldName: TaskFieldName? = null,
    // Priority sheet state
    val isPrioritySheetVisible: Boolean = false,
    val currentPriority: TaskPriority = TaskPriority.NO_PRIORITY,
    // Status sheet state
    val isStatusSheetVisible: Boolean = false,
    val currentStatus: TaskStatus = TaskStatus.OPEN,
    // Assignee sheet state
    val isAssigneeSheetVisible: Boolean = false,
    // File upload sheet state
    val isFileUploadSheetVisible: Boolean = false,
)

// ============== Presentation Models ==============

@Immutable
data class CommentPresentation(
    val comment: String = "",
    val commentBy: Int = 0,
    val commentOn: String = "",
    val name: String = "",
    val designation: String = "",
    val photo: String = "",
)

@Immutable
data class ActivityPresentation(
    val activity: String = "",
    val actionOn: String = "",
    val actorID: Int = 0,
    val name: String = "",
    val designation: String = "",
    val photo: String = "",
)

@Immutable
data class WatcherPresentation(
    val userID: Int = 0,
    val name: String = "",
    val designation: String = "",
    val photo: String = "",
)

// ============== Enums ==============

enum class TaskFieldName(val value: String, val displayName: String) {
    TASK_TITLE("taskTitle", "Title"),
    START_DATE("startDate", "Start date"),
    DUE_DATE("dueDate", "Due date"),
    PRIORITY("priority", "Priority"),
    DESCRIPTION("description", "Description"),
}

// ============== Intents ==============

sealed interface TaskMangerDetailsIntent {
    data object OnBackClicked : TaskMangerDetailsIntent
    data object OnRefresh : TaskMangerDetailsIntent

    // Tab
    data class OnTabSelected(val index: Int) : TaskMangerDetailsIntent

    // Comment
    data class OnCommentTextChanged(val text: String) : TaskMangerDetailsIntent
    data object OnSendComment : TaskMangerDetailsIntent

    // Edit text fields (title, description)
    data object OnEditTitleClicked : TaskMangerDetailsIntent
    data object OnEditDescriptionClicked : TaskMangerDetailsIntent
    data class OnEditDialogValueChanged(val value: String) : TaskMangerDetailsIntent
    data object OnEditDialogConfirmed : TaskMangerDetailsIntent
    data object OnEditDialogDismissed : TaskMangerDetailsIntent

    // Date editing
    data object OnEditStartDateClicked : TaskMangerDetailsIntent
    data object OnEditDueDateClicked : TaskMangerDetailsIntent
    data class OnDateSelected(val fieldName: TaskFieldName, val date: String) : TaskMangerDetailsIntent

    // Priority
    data object OnPriorityClicked : TaskMangerDetailsIntent
    data class OnPrioritySelected(val priority: TaskPriority) : TaskMangerDetailsIntent
    data object OnPrioritySheetDismissed : TaskMangerDetailsIntent

    // Status
    data object OnStatusClicked : TaskMangerDetailsIntent
    data class OnStatusSelected(val status: TaskStatus) : TaskMangerDetailsIntent
    data object OnStatusSheetDismissed : TaskMangerDetailsIntent

    // Assignees
    data object OnAssigneeClicked : TaskMangerDetailsIntent
    data object OnAssigneeSheetDismissed : TaskMangerDetailsIntent

    // Attachment
    data object OnUploadAttachmentClicked : TaskMangerDetailsIntent
    data object OnDismissFileUploadSheet : TaskMangerDetailsIntent
    data class OnFileSelected(val file: SelectedFileDetails) : TaskMangerDetailsIntent
    data class OnShowError(val message: String) : TaskMangerDetailsIntent
}

// ============== Events ==============

sealed interface TaskMangerDetailsEvent {
    data object NavigateBack : TaskMangerDetailsEvent
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : TaskMangerDetailsEvent
    data class ShowDatePicker(val fieldName: TaskFieldName) : TaskMangerDetailsEvent
}

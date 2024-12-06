package com.app.ecarepro.ui.taskmanager.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.TaskFiledName
import com.app.ecarepro.data.network.model.UpdateTaskDto
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.model.Attachment
import com.app.ecarepro.model.TaskDetails
import com.app.ecarepro.model.UpdateTaskAttachmentDto
import com.app.ecarepro.ui.message.sent.UNKNOWN_ERROR_MESSAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailsViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {


    private val taskId = savedStateHandle.getLiveData<String>("taskId", "")
    val taskTitle = savedStateHandle.getStateFlow("taskTitle", "")

    val uiState =
        taskId.asFlow().flatMapLatest { taskId ->
            schoolRepository.getTaskDetails(taskId)
        }.map { result ->
            if (result.isSuccess) {
                TaskDetailsUiState.Success(
                    result.getOrNull(),
                )

            } else {
                val error = result.exceptionOrNull() ?: IllegalArgumentException(
                    UNKNOWN_ERROR_MESSAGE
                )
                TaskDetailsUiState.Error(
                    error
                )
            }
        }.stateIn(
            initialValue = TaskDetailsUiState.Loading,
            started = SharingStarted.WhileSubscribed(300),
            scope = viewModelScope
        )
    fun updateTask(statusId: Int, func: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            schoolRepository
                .updateTaskStatus(taskId.value, statusId)
                .collectLatest { result ->
                    func(result.isSuccess, result.getOrNull()?:result.exceptionOrNull()?.message?: UNKNOWN_ERROR_MESSAGE)
                    if (result.isSuccess) {
                        refresh()
                    }
                }
        }
    }

    fun updateTask(
        taskTitle: TaskFiledName,
        old: String?,
        newValue: String,
        function: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            schoolRepository.updateTask(
                UpdateTaskDto(
                    fieldName = taskTitle.value,
                    id = taskId.value,
                    newValue = newValue,
                    oldValue = old
                )
            ).collectLatest { result ->
                function(result.isSuccess, result.getOrNull() ?: result.exceptionOrNull()?.message)
                if (result.isSuccess) {
                    taskId.value = taskId.value
                }
            }
        }
    }

    fun updateAttachment(imageString: String, imageExt: String, function: (String) -> Unit) {
        viewModelScope.launch {
            schoolRepository.updateTaskImage(
                UpdateTaskAttachmentDto(
                    id = taskId.value,
                    action = 1,
                    attachment = Attachment(
                        imageString,
                        imageExt
                    )
                )
            ).collectLatest {
                function(it.getOrNull() ?: it.exceptionOrNull()?.message ?: UNKNOWN_ERROR_MESSAGE)
                if (it.isSuccess) {
                    taskId.value = taskId.value
                }
            }
        }
    }
    fun sendComment(comment: String, func: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            schoolRepository.sendComment(
                taskId.value!!,
                comment
            ).collectLatest {
                func(
                    it.isSuccess,
                    it.getOrNull() ?: it.exceptionOrNull()?.message ?: UNKNOWN_ERROR_MESSAGE
                )
            }
        }
    }
    fun refresh(){
        viewModelScope.launch {
            taskId.value = taskId.value
        }
    }
}

sealed interface TaskDetailsUiState {
    object Loading : TaskDetailsUiState

    object EmptyInbox : TaskDetailsUiState

    data class Success(
        val taskDetails: TaskDetails?
    ) : TaskDetailsUiState

    data class Error(
        val error: Throwable,
    ) : TaskDetailsUiState

    fun isLoading() = this == Loading

    fun getErrorOrNull() = if (this is Error) this.error else null
}
package com.app.ecarepro.ui.taskmanager.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.model.TaskDetails
import com.app.ecarepro.ui.message.sent.UNKNOWN_ERROR_MESSAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TaskDetailsViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val taskId = savedStateHandle.getStateFlow("taskId", "")
    val taskTitle = savedStateHandle.getStateFlow("taskTitle", "")

    val uiState =
        taskId.flatMapLatest { taskId ->
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
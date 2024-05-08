package com.app.ecarepro.ui.taskmanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.model.TasksDto
import com.app.ecarepro.ui.message.sent.UNKNOWN_ERROR_MESSAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TaskManagerViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository
) : ViewModel() {
    val filter = MutableStateFlow(TaskFilter.MY_TASK.value)
    val uiState =
        filter.flatMapLatest {
            schoolRepository.getTaskList(it)
        }
            .map { result ->
                if (result.isSuccess) {
                    TaskManagerUiState.Success(
                        tasksDto = result.getOrNull()
                    )

                } else {
                    val error = result.exceptionOrNull() ?: IllegalArgumentException(
                        UNKNOWN_ERROR_MESSAGE
                    )
                    TaskManagerUiState.Error(
                        error
                    )
                }
            }
            .stateIn(
                initialValue = TaskManagerUiState.Loading,
                started = SharingStarted.WhileSubscribed(300),
                scope = viewModelScope
            )

    fun setFilter(key: Int) {
        filter.update { key }
    }
}

sealed interface TaskManagerUiState {
    object Loading : TaskManagerUiState

    object EmptyInbox : TaskManagerUiState

    data class Success(
        val tasksDto: TasksDto?
    ) : TaskManagerUiState

    data class Error(
        val error: Throwable,
    ) : TaskManagerUiState

    fun isLoading() = this == Loading

    fun getErrorOrNull() = if (this is Error) this.error else null
}

enum class TaskFilter(val key: String, val value: Int) {
    MY_TASK("My Task", 0),
    ALL_TASK("All Task", 1),
    ASSIGN_TO_ME("Assign to me", 2),
    ASSIGN_BY_ME("Assign by me", 3),
    WATCH_LIST("watch list", 0),
    CLOSED_TASK("Closed Task", 0);
}
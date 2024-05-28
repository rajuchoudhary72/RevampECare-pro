package com.app.ecarepro.ui.taskmanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.model.Task
import com.app.ecarepro.model.TasksDto
import com.app.ecarepro.ui.message.sent.UNKNOWN_ERROR_MESSAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskManagerViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository
) : ViewModel() {
    val filter = MutableStateFlow(TaskFilter.MY_TASK.value)
    val showSearchView = MutableStateFlow(false)
    val searchQuery = MutableStateFlow("")
    val uiState =
        combine(searchQuery, filter) { query, filter ->
            Pair(query, filter)
        }.flatMapLatest { (query, filter) ->
            schoolRepository.getTaskList(filter)
        }.map { result ->
            if (result.isSuccess) {
                TaskManagerUiState.Success(
                    tasksDto = result.getOrNull(),
                    searchQuery = searchQuery.value
                )

            } else {
                val error = result.exceptionOrNull() ?: IllegalArgumentException(
                    UNKNOWN_ERROR_MESSAGE
                )
                TaskManagerUiState.Error(
                    error
                )
            }
        }.stateIn(
            initialValue = TaskManagerUiState.Loading,
            started = SharingStarted.WhileSubscribed(300),
            scope = viewModelScope
        )

    fun setFilter(key: Int) {
        filter.update { key }
    }

    fun showSearchBar() {
        showSearchView.update { true }
    }

    fun clearSearchQuery() {
        if (searchQuery.value.isEmpty()) {
            showSearchView.update { false }
        } else
            searchQuery.update {
                ""
            }
    }

    fun updateTask(task: Task, statusId: Int, func: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            schoolRepository
                .updateTaskStatus(task.id, statusId)
                .collectLatest { result ->
                    func(result.isSuccess, result.getOrNull()?:result.exceptionOrNull()?.message?: UNKNOWN_ERROR_MESSAGE)
                    if(result.isSuccess){
                        searchQuery.update { it }
                    }
                }
        }
    }
}

sealed interface TaskManagerUiState {
    object Loading : TaskManagerUiState

    object EmptyInbox : TaskManagerUiState

    data class Success(
        val tasksDto: TasksDto?,
        val searchQuery: String = ""
    ) : TaskManagerUiState

    data class Error(
        val error: Throwable,
    ) : TaskManagerUiState

    fun isLoading() = this == Loading

    fun getErrorOrNull() = if (this is Error) this.error else null
}

enum class TaskFilter(val key: String, val value: Int) {
    MY_TASK("My Task", 0), ALL_TASK("All Task", 1), ASSIGN_TO_ME(
        "Assign to me",
        2
    ),
    ASSIGN_BY_ME("Assign by me", 3), WATCH_LIST("watch list", 4), CLOSED_TASK("Closed Task", 5);
}
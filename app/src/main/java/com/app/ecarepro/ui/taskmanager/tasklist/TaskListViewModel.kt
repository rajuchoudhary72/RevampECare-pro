package com.app.ecarepro.ui.taskmanager.tasklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.model.Title
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository
) : ViewModel() {

    val uiState =
        schoolRepository
            .getTasks()
            .map {
                if (it.isSuccess) {
                    val tasks = it.getOrNull() ?: emptyList()
                    if (tasks.isEmpty()) {
                        TaskListUiState.Empty
                    } else {
                        TaskListUiState.Success(tasks)
                    }

                } else {
                    TaskListUiState.Error(it.exceptionOrNull() ?: Throwable())
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                TaskListUiState.Loading
            )


}

sealed interface TaskListUiState {
    object Loading : TaskListUiState

    object Empty : TaskListUiState

    data class Success(
        val tasks: List<Title>
    ) : TaskListUiState

    data class Error(
        val error: Throwable,
    ) : TaskListUiState

    fun isLoading() = this == Loading

    fun getErrorOrNull() = if (this is Error) this.error else null
}
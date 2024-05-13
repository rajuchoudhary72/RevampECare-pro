package com.app.ecarepro.ui.taskmanager.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.model.AddTaskDto
import com.app.ecarepro.model.Attachment
import com.app.ecarepro.model.Title
import com.app.ecarepro.model.Watcher
import com.app.ecarepro.ui.message.sent.UNKNOWN_ERROR_MESSAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository
) : ViewModel() {

    val title = MutableStateFlow("")
    val description = MutableStateFlow("")
    val remindBefore = MutableStateFlow("0")
    val makePublic = MutableStateFlow(false)


    var priority: Int? = null
    var startDate: String? = null
    var endDate: String? = null
    var attachment: Pair<String, String>? = null
    var selectedTitle = MutableStateFlow<Title?>(null)

    val watchers = mutableListOf<Watcher>()

    val uiState =
        combine(
            flow = schoolRepository.getTasks(),
            flow2 = schoolRepository.getWatchers()
        ) { tasks, watchers ->
            Pair(tasks, watchers)
        }.map { (tasks, watchers) ->
            if (tasks.isSuccess && watchers.isSuccess) {
                watchers.getOrNull()?.let {
                    this.watchers.clear()
                    this.watchers.addAll(it)
                }
                AddTaskUiState.Success(
                    title = tasks.getOrNull() ?: emptyList(),
                    watchers = watchers.getOrNull() ?: emptyList()
                )
            } else {
                val error = tasks.exceptionOrNull() ?: watchers.exceptionOrNull()
                ?: IllegalArgumentException(
                    UNKNOWN_ERROR_MESSAGE
                )
                AddTaskUiState.Error(
                    error
                )
            }
        }.stateIn(
            initialValue = AddTaskUiState.Loading,
            started = SharingStarted.WhileSubscribed(300),
            scope = viewModelScope
        )

    fun addTask(response: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            schoolRepository
                .addTask(
                    AddTaskDto(
                        assigneesIDs = selectedTitle.value?.assignees?.filter { it.isSelected }
                            ?.map { it.userID }?.joinToString(),
                        attachment = Attachment(attachment?.first, attachment?.second),
                        description = description.value,
                        dueDate = endDate,
                        isPublic = makePublic.value,
                        priority = priority,
                        remindBefore = remindBefore.value.toInt(),
                        title = title.value,
                        tlId = selectedTitle.value?.tlId,
                        tskID = 1,
                        startDate = startDate,
                        repeatedBy = 0,
                        watchersIDs = if(makePublic.value) watchers.filter { it.isSelected }.map { it.userID }
                            .joinToString() else null
                    )
                )
                .collectLatest { result ->
                    if (result.isSuccess) {
                        response.invoke(true, result.getOrNull() ?: "Saved")
                    } else {
                        response.invoke(
                            false,
                            result.exceptionOrNull()?.message ?: UNKNOWN_ERROR_MESSAGE
                        )
                    }
                }
        }
    }
}

sealed interface AddTaskUiState {
    object Loading : AddTaskUiState

    object EmptyInbox : AddTaskUiState

    data class Success(
        val title: List<Title>,
        val watchers: List<Watcher>,
    ) : AddTaskUiState

    data class Error(
        val error: Throwable,
    ) : AddTaskUiState

    fun isLoading() = this == Loading

    fun getErrorOrNull() = if (this is Error) this.error else null
}
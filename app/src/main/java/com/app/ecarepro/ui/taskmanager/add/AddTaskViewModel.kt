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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
    fun getAssignee(selectedTitle: Title, func: (Boolean) -> Unit) {
        viewModelScope.launch {
            schoolRepository
                .getTaskAssignee(selectedTitle.tlId!!)
                .collectLatest { result ->
                    if (result.isSuccess) {
                        this@AddTaskViewModel.selectedTitle.update {
                            selectedTitle.copy(
                                assignees = result.getOrNull()
                            )
                        }
                        func.invoke(true)
                    } else {
                        func.invoke(false)
                    }
                }
        }
    }

    val uiState =
        schoolRepository
            .getWatchers()
            .map { watchers ->
                if (watchers.isSuccess) {
                    val result = watchers.getOrNull()
                    result?.watchers?.let {
                        this.watchers.clear()
                        this.watchers.addAll(it)
                    }
                    AddTaskUiState.Success(
                        title = result?.taskList ?: emptyList(),
                        watchers = result?.watchers ?: emptyList()
                    )
                } else {
                    val error = watchers.exceptionOrNull()
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
                        attachment = if (attachment?.first != null) Attachment(
                            attachment?.first,
                            attachment?.second
                        ) else null,
                        description = description.value,
                        dueDate = endDate,
                        isPublic = makePublic.value,
                        priority = priority,
                        remindBefore = remindBefore.value.toInt(),
                        title = title.value,
                        tlId = selectedTitle.value?.tlId,
                        tskID = 0,
                        startDate = startDate,
                        repeatedBy = 0,
                        watchersIDs = if (makePublic.value.not()) watchers.filter { it.isSelected }
                            .map { it.userID }
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
package com.app.ecarepro.ui.taskmanager.tasklist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.StaffType
import com.app.ecarepro.data.repository.MessageRepository
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.model.AddTaskListDto
import com.app.ecarepro.model.Assignee
import com.app.ecarepro.model.Title
import com.app.ecarepro.ui.message.sent.UNKNOWN_ERROR_MESSAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTaskListViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository,
    private val messageRepository: MessageRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val task: Title? = savedStateHandle.get<Title>("task")

    val loading = MutableSharedFlow<Boolean>()
    val error = MutableSharedFlow<String>()

    val name = MutableStateFlow<String>("")
    val staffTypes = MutableStateFlow<List<StaffType>>(emptyList())
    val assignees = MutableStateFlow<List<Assignee>>(emptyList())

    var selectedStaffTypes: List<StaffType>? = null
    var selectedAssignees: List<Assignee> = emptyList()

    init {
        getStaffTypes {
            if (task != null) {
                name.value= task.title?:""
            }
        }
    }


    fun getStaffTypes(onSuccess: () -> Unit) {
        viewModelScope.launch {
            loading.emit(true)
            messageRepository.getStaffTypes()
                .collect {
                    loading.emit(false)
                    it
                        .onSuccess {
                            staffTypes.value = it
                            onSuccess()
                        }.onFailure {
                            error.emit(it.message ?: UNKNOWN_ERROR_MESSAGE)
                        }

                }
        }
    }

    fun getStaffAssignee() {
        viewModelScope.launch {
            loading.emit(true)
            schoolRepository.getTaskAssignee(
                null,
                selectedStaffTypes?.joinToString(separator = ",") { it.staffTypeID.toString() })
                .collect {
                    loading.emit(false)
                    it
                        .onSuccess { assignees ->
                            this@AddTaskListViewModel.assignees.value = assignees
                        }.onFailure {
                            error.emit(it.message ?: UNKNOWN_ERROR_MESSAGE)
                        }
                }
        }
    }

    fun saveTaskList(onSuccess: () -> Unit) {
        viewModelScope.launch {
            loading.emit(true)
            schoolRepository.saveTaskList(
                AddTaskListDto(
                    title = name.value,
                    id = task?.tlId?.toString(),
                    assignee = selectedAssignees.joinToString(separator = ",") { it.userID.toString() },
                )
            )
                .collect {
                    loading.emit(false)
                    it
                        .onSuccess {
                            onSuccess()
                        }.onFailure {
                            error.emit(it.message ?: UNKNOWN_ERROR_MESSAGE)
                        }
                }
        }
    }


}
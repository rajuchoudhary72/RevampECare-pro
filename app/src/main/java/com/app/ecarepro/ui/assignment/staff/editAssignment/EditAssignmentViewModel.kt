package com.app.ecarepro.ui.assignment.staff.editAssignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkViewAssignment
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditAssignmentViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository,
    private val userRepository: UserRepository
) : ViewModel() {


    private val createAssignmentMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val createAssignmentStateFlow: StateFlow<NetworkResult<CommonResponse>> =
        createAssignmentMutableStateFlow

    private val viewAssignmentMutableStateFlow: MutableStateFlow<NetworkResult<NetworkViewAssignment>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val viewAssignmentStateFlow: StateFlow<NetworkResult<NetworkViewAssignment>> =
        viewAssignmentMutableStateFlow


    fun viewAssignment(iD: String) = viewModelScope.launch {
        runCatching {
            viewAssignmentMutableStateFlow.value = NetworkResult.Loading()
            userRepository.viewAssignment(iD)
        }.onSuccess {
            viewAssignmentMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            viewAssignmentMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }


    fun createAssignment(
        asgDate: String,
        asgID: Int,
        attachment: String,
        fileExt: String,
        fileURL: String,
        classID: Int,
        classIDs: String,
        `data`: String,
        `file`: String,
        id: String,
        isActive: Boolean,
        isFileRemoved: Boolean,
        multipleSubmission: Boolean,

        subjectID: Int,
        submitDate: String,
        title: String

    ) = viewModelScope.launch {
        runCatching {
            createAssignmentMutableStateFlow.value = NetworkResult.Loading()
            userRepository.createAssignment(
                asgDate,
                asgID,
                attachment,
                fileExt,
                fileURL,
                classID,
                classIDs,
                data,
                file,
                id,
                isActive,
                isFileRemoved,
                multipleSubmission,
                subjectID,
                submitDate,
                title
            )
        }.onSuccess {
            createAssignmentMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            createAssignmentMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }


}
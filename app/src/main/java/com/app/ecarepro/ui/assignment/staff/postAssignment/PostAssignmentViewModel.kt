package com.app.ecarepro.ui.assignment.staff.postAssignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkMyClass
import com.app.ecarepro.data.network.model.NetworkMySubjects
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostAssignmentViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository,
    private val userRepository: UserRepository
) : ViewModel() {


    private val myClassMutableStateFlow: MutableStateFlow<NetworkResult<NetworkMyClass>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val myClassStateFlow: StateFlow<NetworkResult<NetworkMyClass>> = myClassMutableStateFlow

    private val subjectsMutableStateFlow: MutableStateFlow<NetworkResult<NetworkMySubjects>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val subjectsStateFlow: StateFlow<NetworkResult<NetworkMySubjects>> = subjectsMutableStateFlow

    private val createAssignmentMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val createAssignmentStateFlow: StateFlow<NetworkResult<CommonResponse>> =
        createAssignmentMutableStateFlow


    fun getMyClass(subID: Int, iD: Int) = viewModelScope.launch {
        runCatching {
            myClassMutableStateFlow.value = NetworkResult.Loading()
            userRepository.staffMyClass(subID, iD)
        }.onSuccess {
            myClassMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            myClassMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }

    fun mySubjects() = viewModelScope.launch {
        runCatching {
            subjectsMutableStateFlow.value = NetworkResult.Loading()
            userRepository.mySubjects()
        }.onSuccess {
            subjectsMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            subjectsMutableStateFlow.value = NetworkResult.Error(it.message)
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
package com.app.ecarepro.ui.assignment.staff.postAssignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkMyClass
import com.app.ecarepro.data.network.model.NetworkMySubjects
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkStudentParentComms
import com.app.ecarepro.data.network.model.NetworkViewAssignment
import com.app.ecarepro.data.network.model.post_question.Attachment
import com.app.ecarepro.data.repository.MessageRepository
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.model.ClassID_StID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class PostAssignmentViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val  userRepository: UserRepository
) : ViewModel() {


    private val myClassMutableStateFlow: MutableStateFlow<NetworkResult<NetworkMyClass>> = MutableStateFlow(
        NetworkResult.Loading())
    val myClassStateFlow: StateFlow<NetworkResult<NetworkMyClass>> = myClassMutableStateFlow

    private val subjectsMutableStateFlow: MutableStateFlow<NetworkResult<NetworkMySubjects>> = MutableStateFlow(
        NetworkResult.Loading())
    val subjectsStateFlow: StateFlow<NetworkResult<NetworkMySubjects>> = subjectsMutableStateFlow

    private val createAssignmentMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> = MutableStateFlow(
        NetworkResult.Loading())
    val createAssignmentStateFlow: StateFlow<NetworkResult<CommonResponse>> = createAssignmentMutableStateFlow

    private val studentParentCommsMutableStateFlow: MutableStateFlow<NetworkResult<NetworkStudentParentComms>> = MutableStateFlow(
        NetworkResult.Loading())
    val studentParentCommsStateFlow: StateFlow<NetworkResult<NetworkStudentParentComms>> = studentParentCommsMutableStateFlow

    private val viewAssignmentMutableStateFlow: MutableStateFlow<NetworkResult<NetworkViewAssignment>> = MutableStateFlow(
        NetworkResult.Loading())
    val viewAssignmentStateFlow: StateFlow<NetworkResult<NetworkViewAssignment>> = viewAssignmentMutableStateFlow


    fun getMyClass(subID: Int, iD: Int  )=viewModelScope.launch {
          runCatching {
              myClassMutableStateFlow.value =NetworkResult.Loading( )
              userRepository.staffMyClass(subID, iD)
          }.onSuccess {
              myClassMutableStateFlow.value =NetworkResult.Success(it)
          }.onFailure {
              myClassMutableStateFlow.value = NetworkResult.Error(it.message)
          }
      }

    fun mySubjects(classID :Int )=viewModelScope.launch {
        runCatching {
            subjectsMutableStateFlow.value =NetworkResult.Loading( )
            userRepository.mySubjects(classID )
        }.onSuccess {
            subjectsMutableStateFlow.value =NetworkResult.Success(it)
        }.onFailure {
            subjectsMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }

    fun studentParentComms(
        recipientType: Int,
        classIDs: String,
        scholarType: Int,
        byRollNo: Boolean,
    )=viewModelScope.launch {
        runCatching {
            studentParentCommsMutableStateFlow.value =NetworkResult.Loading( )
            messageRepository.studentParentComms(recipientType, classIDs, scholarType, byRollNo )
        }.onSuccess {
            studentParentCommsMutableStateFlow.value =NetworkResult.Success(it)
        }.onFailure {
            studentParentCommsMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }



    fun createAssignment(
        asgDate: String,
        asgID: Int,

        classID: Int,
        classIDs: String,
        data: String,
        file: String,
        id: String,
        isActive: Boolean,
        isFileRemoved: Boolean,
        multipleSubmission: Boolean,

        subjectID: Int,
        submitDate: String,
        title: String,
        lateSubmission: Boolean,
        attachments: List<Attachment>,
        classID_StID: List<ClassID_StID>,
        stIDs: String?


    )=viewModelScope.launch {
        runCatching {
            createAssignmentMutableStateFlow.value =NetworkResult.Loading( )
            userRepository.createAssignment( asgDate, asgID,   classID, classIDs,
                data, file, id, isActive, isFileRemoved, multipleSubmission, subjectID, submitDate, title,lateSubmission,attachments,classID_StID,stIDs)
        }.onSuccess {
            createAssignmentMutableStateFlow.value =NetworkResult.Success(it)
        }.onFailure {
            createAssignmentMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }





    fun viewAssignment(  iD: String )=viewModelScope.launch {
        runCatching {
            viewAssignmentMutableStateFlow.value = NetworkResult.Loading()
            userRepository.viewAssignment(iD )
        }.onSuccess {
            viewAssignmentMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            viewAssignmentMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }


}
package com.app.ecarepro.ui.markAttendence

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkMarkAttendance
import com.app.ecarepro.data.network.model.NetworkMySubjects
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkStudentListToMarkAtt
import com.app.ecarepro.data.network.model.post_mark_attedance.StudentAtt
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StuMarkAttendanceViewModel  @Inject constructor(
    private val  userRepository: UserRepository
) : ViewModel() {

    private val myClassMutableStateFlow: MutableStateFlow<NetworkResult<NetworkMarkAttendance>> = MutableStateFlow(
        NetworkResult.Loading())
    val myClassStateFlow: StateFlow<NetworkResult<NetworkMarkAttendance>> = myClassMutableStateFlow

    private val stuListToMarkAttMutableStateFlow: MutableStateFlow<NetworkResult<NetworkStudentListToMarkAtt>> = MutableStateFlow(
        NetworkResult.Loading())
    val stuListToMarkAttStateFlow: StateFlow<NetworkResult<NetworkStudentListToMarkAtt>> = stuListToMarkAttMutableStateFlow


    private val subjectsMutableStateFlow: MutableStateFlow<NetworkResult<NetworkMySubjects>> = MutableStateFlow(
        NetworkResult.Loading())
    val subjectsStateFlow: StateFlow<NetworkResult<NetworkMySubjects>> = subjectsMutableStateFlow

    private val postMarkAttendanceMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> = MutableStateFlow(
        NetworkResult.Loading())
    val postMarkAttendanceStateFlow: StateFlow<NetworkResult<CommonResponse>> = postMarkAttendanceMutableStateFlow


    fun getMarkAttendance( )=viewModelScope.launch {
        runCatching {
            myClassMutableStateFlow.value =NetworkResult.Loading( )
            userRepository.markAttendance( )
        }.onSuccess {
            myClassMutableStateFlow.value =NetworkResult.Success(it)
        }.onFailure {
            myClassMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }

    fun mySubjects( classID :Int)=viewModelScope.launch {
        runCatching {
            subjectsMutableStateFlow.value =NetworkResult.Loading( )
            userRepository.mySubjects(classID)
        }.onSuccess {
            subjectsMutableStateFlow.value =NetworkResult.Success(it)
        }.onFailure {
            subjectsMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }

    fun getStudentListToMarkAtt(
        classID: Int,
        subID: Int,
        attDate: String
    )=viewModelScope.launch {
        runCatching {
            stuListToMarkAttMutableStateFlow.value =NetworkResult.Loading( )
            userRepository.getStudentListToMarkAtt(classID, subID, attDate)
        }.onSuccess {
            stuListToMarkAttMutableStateFlow.value =NetworkResult.Success(it)
        }.onFailure {
            stuListToMarkAttMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }

    fun  postMarkAttendance(
        classID:Int,
        subID:Int,
        mode:Int,
        attDate:String,
        stuList:List<StudentAtt>
    )=viewModelScope.launch {
        runCatching {
            postMarkAttendanceMutableStateFlow.value =NetworkResult.Loading( )
            userRepository.postMarkAttedance(classID, subID, mode, attDate, stuList)
        }.onSuccess {
            postMarkAttendanceMutableStateFlow.value =NetworkResult.Success(it)
        }.onFailure {
            postMarkAttendanceMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }



}
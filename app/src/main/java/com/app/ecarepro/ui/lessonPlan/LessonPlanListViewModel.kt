package com.app.ecarepro.ui.lessonPlan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkLessonPlanList
import com.app.ecarepro.data.network.model.NetworkMarkAttendance
import com.app.ecarepro.data.network.model.NetworkMyClass
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
class LessonPlanListViewModel  @Inject constructor(
    private val  userRepository: UserRepository
) : ViewModel() {


    private val lessonPlanListMutableStateFlow: MutableStateFlow<NetworkResult<NetworkLessonPlanList>> = MutableStateFlow(
        NetworkResult.Loading())
    val lessonPlanListStateFlow: StateFlow<NetworkResult<NetworkLessonPlanList>> = lessonPlanListMutableStateFlow

    private val lessonActionMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> = MutableStateFlow(
        NetworkResult.Loading())
    val lessonActionStateFlow: StateFlow<NetworkResult<CommonResponse>> = lessonActionMutableStateFlow

    private val myClassMutableStateFlow: MutableStateFlow<NetworkResult<NetworkMyClass>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val myClassStateFlow: StateFlow<NetworkResult<NetworkMyClass>> = myClassMutableStateFlow

    private val subjectsMutableStateFlow: MutableStateFlow<NetworkResult<NetworkMySubjects>> = MutableStateFlow(
        NetworkResult.Loading())
    val subjectsStateFlow: StateFlow<NetworkResult<NetworkMySubjects>> = subjectsMutableStateFlow



    fun getLessonPlanList(page:Int ,id: String)=viewModelScope.launch {
        runCatching {
            lessonPlanListMutableStateFlow.value =NetworkResult.Loading( )
            userRepository.getLessonPlanList( page,id)
        }.onSuccess {
            lessonPlanListMutableStateFlow.value =NetworkResult.Success(it)
        }.onFailure {
            lessonPlanListMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }

    fun lessonPlanAction(
        lPlnID: Int,
        action: Int,
        rejectionComments: String,
    )=viewModelScope.launch {
        runCatching {
            lessonActionMutableStateFlow.value =NetworkResult.Loading( )
            userRepository.lessonPlanAction(lPlnID, action, rejectionComments )
        }.onSuccess {
            lessonActionMutableStateFlow.value =NetworkResult.Success(it)
        }.onFailure {
            lessonActionMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }

    fun getLessonPlanFilter(
        filter: String,
        from: String,
        till: String,
        classIds: String,
        subIds: String,
        status: Int,


        )=viewModelScope.launch {
        runCatching {
            lessonPlanListMutableStateFlow.value =NetworkResult.Loading( )
            userRepository.getLessonPlanFilter(filter, from, till, classIds, subIds, status )
        }.onSuccess {
            lessonPlanListMutableStateFlow.value =NetworkResult.Success(it)
        }.onFailure {
            lessonPlanListMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }


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






}
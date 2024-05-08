package com.app.ecarepro.ui.lessonPlan.add_lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkCreateLesson
import com.app.ecarepro.data.network.model.NetworkLessonPlanDTL
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
class AddLessonViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {


    private val postLessonMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val postLessonStateFlow: StateFlow<NetworkResult<CommonResponse>> = postLessonMutableStateFlow

 private val createLessonMutableStateFlow: MutableStateFlow<NetworkResult<NetworkCreateLesson>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val createLessonStateFlow: StateFlow<NetworkResult<NetworkCreateLesson>> = createLessonMutableStateFlow

    private val myClassMutableStateFlow: MutableStateFlow<NetworkResult<NetworkMyClass>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val myClassStateFlow: StateFlow<NetworkResult<NetworkMyClass>> = myClassMutableStateFlow



    private val viewLessonPlanMutableStateFlow: MutableStateFlow<NetworkResult<NetworkLessonPlanDTL>> = MutableStateFlow(
        NetworkResult.Loading())
    val viewLessonPlanStateFlow: StateFlow<NetworkResult<NetworkLessonPlanDTL>> = viewLessonPlanMutableStateFlow


    fun postLessonPlan(
        attachment: String,
        fileExt: String,
        fileURL: String,
        auditory: String,
        classIds: String,
        closure: String,
        extensionTopic: String,
        fileName: String,
        fromDate: String,
        introduction: String,
        kinestheticActivity: String,
        lPlnID: Int,
        learningOutcomes: String,
        objective: String,
        otherResources: String,
        resources: String,
        showToStudent: Boolean,
        subID: Int,
        tillDate: String,
        topic: String,
        youtubeLinks: String
    ) = viewModelScope.launch {
        runCatching {
            postLessonMutableStateFlow.value = NetworkResult.Loading()
            userRepository.postLessonPlan(
                attachment,
                fileExt,
                fileURL,
                auditory,
                classIds,
                closure,
                extensionTopic,
                fileName,
                fromDate,
                introduction,
                kinestheticActivity,
                lPlnID,
                learningOutcomes,
                objective,
                otherResources,
                resources,
                showToStudent,
                subID,
                tillDate,
                topic,
                youtubeLinks
            )
        }.onSuccess {
            postLessonMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            postLessonMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }

    fun createLessonPlan(  ) = viewModelScope.launch {
        runCatching {
            createLessonMutableStateFlow.value = NetworkResult.Loading()
            userRepository.createLessonPlan( )
        }.onSuccess {
            createLessonMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            createLessonMutableStateFlow.value = NetworkResult.Error(it.message)
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


    fun getLessonPlanDTL(
        id: String,
        teacherID: Int
    )=viewModelScope.launch {
        runCatching {
            viewLessonPlanMutableStateFlow.value =NetworkResult.Loading( )
            userRepository.getLessonPlanDTL( id, teacherID)
        }.onSuccess {
            viewLessonPlanMutableStateFlow.value =NetworkResult.Success(it)
        }.onFailure {
            viewLessonPlanMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }





    




}
package com.app.ecarepro.ui.lessonPlan.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkLessonPlanDTL
import com.app.ecarepro.data.network.model.NetworkLessonPlanList
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
class ViewLessonPlanViewModel  @Inject constructor(
    private val  userRepository: UserRepository
) : ViewModel() {


    private val viewLessonPlanMutableStateFlow: MutableStateFlow<NetworkResult<NetworkLessonPlanDTL>> = MutableStateFlow(
        NetworkResult.Loading())
    private val viewLessonPlanActionMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> = MutableStateFlow(
        NetworkResult.Loading())
    val viewLessonPlanStateFlow: StateFlow<NetworkResult<NetworkLessonPlanDTL>> = viewLessonPlanMutableStateFlow

    fun lessonPlanAction(
        lPlnID: Int,
        action: Int,
        rejectionComments: String,
    )=viewModelScope.launch {
        runCatching {
            viewLessonPlanActionMutableStateFlow.value =NetworkResult.Loading( )
            userRepository.lessonPlanAction(lPlnID, action, rejectionComments )
        }.onSuccess {
            viewLessonPlanActionMutableStateFlow.value =NetworkResult.Success(it)
        }.onFailure {
            viewLessonPlanActionMutableStateFlow.value = NetworkResult.Error(it.message)
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
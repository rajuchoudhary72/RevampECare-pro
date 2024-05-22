package com.app.ecarepro.ui.survey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SurveyViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val surveyListMutableStateFlow: MutableStateFlow<NetworkResult<SurveyListResponse>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val surveyListStateFlow: StateFlow<NetworkResult<SurveyListResponse>> =
        surveyListMutableStateFlow

    fun surveyList(pg: Int, isReport: Boolean) = viewModelScope.launch {
        runCatching {
            surveyListMutableStateFlow.value = NetworkResult.Loading()
            userRepository.surveyList(pg, isReport)
        }.onSuccess {
            surveyListMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            surveyListMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

   /* private val attendanceMutableStateFlow: MutableStateFlow<NetworkResult<AttendanceResponse>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val attendanceStateFlow: StateFlow<NetworkResult<AttendanceResponse>> =
        attendanceMutableStateFlow

    fun getAttendance(
        from: String,
        till: String,
        yrID: String,
    ) = viewModelScope.launch {
        runCatching {
            attendanceMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getAttendance(from, till, yrID)
        }.onSuccess {
            attendanceMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            attendanceMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }*/


}
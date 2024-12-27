package com.app.ecarepro.ui.studentProfile.academic_performance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkAcademicPerformance
import com.app.ecarepro.data.network.model.NetworkAcademicYear
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsConstants
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AcademicPerViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val analyticsManager: AnalyticsManager
) : ViewModel() {

    private val studentProfileMutableStateFlow: MutableStateFlow<NetworkResult<NetworkAcademicPerformance>> = MutableStateFlow(
        NetworkResult.Loading())
    val studentProfileStateFlow: StateFlow<NetworkResult<NetworkAcademicPerformance>> = studentProfileMutableStateFlow

    private val academicYearMutableStateFlow: MutableStateFlow<NetworkResult<NetworkAcademicYear>> = MutableStateFlow(
        NetworkResult.Loading())
    val academicYearStateFlow: StateFlow<NetworkResult<NetworkAcademicYear>> = academicYearMutableStateFlow

    fun sendScreenEvent(){
        analyticsManager.trackScreen(AnalyticsConstants.Screens.STUDENT_ACADEMIC_SCREEN)
    }
    fun getAcademicPerformance(
        sId: Int,
        yrID: Int
    )=viewModelScope.launch {
        runCatching {
            studentProfileMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getAcademicPerformance(sId, yrID)
        }.onSuccess {
            studentProfileMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            studentProfileMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }


    fun academicYears()=viewModelScope.launch {
        runCatching {
            academicYearMutableStateFlow.value = NetworkResult.Loading()
            userRepository.academicYears()
        }.onSuccess {
            academicYearMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            academicYearMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }



}


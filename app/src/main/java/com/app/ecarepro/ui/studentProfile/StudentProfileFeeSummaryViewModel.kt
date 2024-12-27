package com.app.ecarepro.ui.studentProfile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkProfileAttendanceDTL
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkStaffProfile
import com.app.ecarepro.data.network.model.NetworkStudentProfile
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.model.FeeSummery
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsConstants
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentProfileFeeSummaryViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val analyticsManager: AnalyticsManager
) : ViewModel() {

    private val studentProfileFeeSummeryMutableStateFlow: MutableStateFlow<NetworkResult<FeeSummery>> = MutableStateFlow(
        NetworkResult.Loading())
    val feeSummeryStateFlow: StateFlow<NetworkResult<FeeSummery>> = studentProfileFeeSummeryMutableStateFlow

    fun  getFeeSummaryYrID(
        sId: Int,
        yrID: Int
    )=viewModelScope.launch {
        runCatching {
            studentProfileFeeSummeryMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getFeeSummaryYrID(sId, yrID)
        }.onSuccess {
            studentProfileFeeSummeryMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            studentProfileFeeSummeryMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }
    fun sendScreenEvent(){
        analyticsManager.trackScreen(AnalyticsConstants.Screens.STUDENT_FEE_SCREEN)
    }
}


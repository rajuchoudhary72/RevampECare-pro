package com.app.ecarepro.ui.studentProfile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkProfileAttendanceDTL
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkStaffProfile
import com.app.ecarepro.data.network.model.NetworkStudentProfile
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsConstants
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentProfileAttendanceViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val analyticsManager: AnalyticsManager
) : ViewModel() {

    private val studentProfileMutableStateFlow: MutableStateFlow<NetworkResult<NetworkProfileAttendanceDTL>> = MutableStateFlow(
        NetworkResult.Loading())
    val studentProfileStateFlow: StateFlow<NetworkResult<NetworkProfileAttendanceDTL>> = studentProfileMutableStateFlow

    fun  getSAttendanceYrID(
        sId: Int,
        yrID: Int
    )=viewModelScope.launch {
        runCatching {
            studentProfileMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getSAttendanceYrID(sId, yrID)
        }.onSuccess {
            studentProfileMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            studentProfileMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

    fun sendScreenEvent(){
        analyticsManager.trackScreen(AnalyticsConstants.Screens.STUDENT_ATTENDANCE_SCREEN)
    }
}


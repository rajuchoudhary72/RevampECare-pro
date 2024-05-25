package com.app.ecarepro.ui.transport_attendance.out_pass

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkActivityCalender
import com.app.ecarepro.data.network.model.NetworkAssignments
import com.app.ecarepro.data.network.model.NetworkOutPassReport
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkRouteList
import com.app.ecarepro.data.network.model.NetworkStoppage
import com.app.ecarepro.data.network.model.NetworkStudentToMarkTransAttendane
import com.app.ecarepro.data.network.model.NetworkTeachersTimetable
import com.app.ecarepro.data.network.model.NetworkTransAttendanceReport
import com.app.ecarepro.data.network.model.post_trans_att.StuAtt
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OutPassReportViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {



    private val outPassReportMutableStateFlow: MutableStateFlow<NetworkResult<NetworkOutPassReport>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val outPassReportStateFlow: StateFlow<NetworkResult<NetworkOutPassReport>> =
        outPassReportMutableStateFlow





    fun getOutPassReport(
        attDate: String,
    ) = viewModelScope.launch {
        runCatching {
            outPassReportMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getOutPassReport(  attDate)
        }.onSuccess {
            outPassReportMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            outPassReportMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }





}
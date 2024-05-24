package com.app.ecarepro.ui.transport_attendance.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkActivityCalender
import com.app.ecarepro.data.network.model.NetworkAssignments
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
class TransportAttReportViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val routesListMutableStateFlow: MutableStateFlow<NetworkResult<NetworkRouteList>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val routesListStateFlow: StateFlow<NetworkResult<NetworkRouteList>> = routesListMutableStateFlow

    private val stoppageListMutableStateFlow: MutableStateFlow<NetworkResult<NetworkStoppage>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val stoppageListStateFlow: StateFlow<NetworkResult<NetworkStoppage>> =
        stoppageListMutableStateFlow

    private val transAttendanceReportMutableStateFlow: MutableStateFlow<NetworkResult<NetworkTransAttendanceReport>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val transAttendanceReportStateFlow: StateFlow<NetworkResult<NetworkTransAttendanceReport>> =
        transAttendanceReportMutableStateFlow



    fun getRoutesList() = viewModelScope.launch {
        runCatching {
            routesListMutableStateFlow.value = NetworkResult.Loading()
            userRepository.routesList()
        }.onSuccess {
            routesListMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            routesListMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

    fun getStoppageList(
        routeIDs: String,
        trip: Int
    ) = viewModelScope.launch {
        runCatching {
            stoppageListMutableStateFlow.value = NetworkResult.Loading()
            userRepository.stoppageList(routeIDs, trip)
        }.onSuccess {
            stoppageListMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            stoppageListMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

    fun getTransAttendanceReport(
        routeID : Int,
        stopIDs: String,
        attDate: String,
    ) = viewModelScope.launch {
        runCatching {
            transAttendanceReportMutableStateFlow.value = NetworkResult.Loading()
            userRepository.transAttendanceReport(routeID, stopIDs, attDate)
        }.onSuccess {
            transAttendanceReportMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            transAttendanceReportMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }





}
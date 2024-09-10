package com.app.ecarepro.ui.transport_attendance

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
import com.app.ecarepro.data.network.model.post_trans_att.StuAtt
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransportAttendanceViewModel @Inject constructor(
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

    private val studentToMarkTransAttendanceMutableStateFlow: MutableStateFlow<NetworkResult<NetworkStudentToMarkTransAttendane>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val studentToMarkTransAttendanceStateFlow: StateFlow<NetworkResult<NetworkStudentToMarkTransAttendane>> =
        studentToMarkTransAttendanceMutableStateFlow

    private val postTransAttendanceMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val postTransAttendanceStateFlow: StateFlow<NetworkResult<CommonResponse>> = postTransAttendanceMutableStateFlow

    private val postDropToStudentMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val postDropToStudentStateFlow: StateFlow<NetworkResult<CommonResponse>> = postDropToStudentMutableStateFlow


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

    fun getStudentToMarkTransAttendance(
        routeIDs: String,
        stopID: Int,
        trip: Int,
        attDate: String,
        stopIDs: String
    ) = viewModelScope.launch {
        runCatching {
            studentToMarkTransAttendanceMutableStateFlow.value = NetworkResult.Loading()
            userRepository.studentToMarkTransAttendane(routeIDs, stopID, trip, attDate, stopIDs)
        }.onSuccess {
            studentToMarkTransAttendanceMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            studentToMarkTransAttendanceMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

    fun  postTransAttendance(
        attDate: String,
        routeID: Int,
        stopID: Int,
        stuAtt: List<StuAtt>,
        trip: Int
    ) = viewModelScope.launch {
        runCatching {
            postTransAttendanceMutableStateFlow.value = NetworkResult.Loading()
            userRepository.postTransAttendance(attDate, routeID, stopID, stuAtt, trip)
        }.onSuccess {
            postTransAttendanceMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            postTransAttendanceMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

    fun  getStudentToDrop(
        routeID : Int,
        stopID: Int,
        attDate: String,
    ) = viewModelScope.launch {
        runCatching {
            studentToMarkTransAttendanceMutableStateFlow.value = NetworkResult.Loading()
            userRepository.studentToDrop(routeID , stopID, attDate)
        }.onSuccess {
            studentToMarkTransAttendanceMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            studentToMarkTransAttendanceMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

    fun  dropToStudent(
        stID : Int,
        attDate: String,
        hasDropped: Boolean,
    ) = viewModelScope.launch {
        runCatching {
            postDropToStudentMutableStateFlow.value = NetworkResult.Loading()
            userRepository.dropToStudent(stID, attDate, hasDropped)
        }.onSuccess {
            postDropToStudentMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            postDropToStudentMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

}
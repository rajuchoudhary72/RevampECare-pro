package com.app.ecarepro.ui.staffAttendence

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkClassSyllabus
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkStaffAttendence
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.ui.attendance_section.AttendanceResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val staffAttendenceMutableStateFlow: MutableStateFlow<NetworkResult<NetworkStaffAttendence>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val staffAttendenceStateFlow: StateFlow<NetworkResult<NetworkStaffAttendence>> =
        staffAttendenceMutableStateFlow

    fun staffAttendance(
        month: Int,
        year: Int,
    ) = viewModelScope.launch {
        runCatching {
            staffAttendenceMutableStateFlow.value = NetworkResult.Loading()
            userRepository.staffAttendance(month, year)
        }.onSuccess {
            staffAttendenceMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            staffAttendenceMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

    private val attendanceMutableStateFlow: MutableStateFlow<NetworkResult<AttendanceResponse>> =
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

    }


}
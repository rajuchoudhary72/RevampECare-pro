package com.app.ecarepro.ui.staffAttendence

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkStaffAttendence
import com.app.ecarepro.data.repository.UserRepository
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

}
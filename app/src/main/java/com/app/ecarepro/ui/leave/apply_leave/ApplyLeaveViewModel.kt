package com.app.ecarepro.ui.leave.apply_leave

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkLeaveListStatus
import com.app.ecarepro.data.network.model.NetworkLeaveSetting
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.post_leave_request.FileAttachment
import com.app.ecarepro.data.network.model.post_leave_request.HalfdayDTL
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApplyLeaveViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val leaveApplyMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> = MutableStateFlow(
        NetworkResult.Loading())
    val leaveApplyStateFlow: StateFlow<NetworkResult<CommonResponse>> = leaveApplyMutableStateFlow


    private val leaveSettingMutableStateFlow: MutableStateFlow<NetworkResult<NetworkLeaveSetting>> = MutableStateFlow(
        NetworkResult.Loading())
    val leaveSettingStateFlow: StateFlow<NetworkResult<NetworkLeaveSetting>> = leaveSettingMutableStateFlow

    private val leaveHistoryMutableStateFlow: MutableStateFlow<NetworkResult<NetworkLeaveListStatus>> = MutableStateFlow(
        NetworkResult.Loading())
    val leaveHistoryStateFlow: StateFlow<NetworkResult<NetworkLeaveListStatus>> = leaveHistoryMutableStateFlow



    fun leaveApply(
        leaveID: Int,
        fromDate: String,
        tillDate:String,
        duration:Double,
        halfdayDTL: List<HalfdayDTL>?,
        reason:String,
        fileAttachment: FileAttachment? )=viewModelScope.launch {
        runCatching {
            leaveApplyMutableStateFlow.value = NetworkResult.Loading( )
            userRepository.leaveApply(leaveID, fromDate, tillDate, duration, halfdayDTL, reason,fileAttachment )
        }.onSuccess {
            leaveApplyMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            leaveApplyMutableStateFlow .value = NetworkResult.Error(it.message)
        }
    }


    fun leaveSetting(  )=viewModelScope.launch {
        runCatching {
            leaveSettingMutableStateFlow.value = NetworkResult.Loading( )
            userRepository.leaveSetting(  )
        }.onSuccess {
            leaveSettingMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            leaveSettingMutableStateFlow .value = NetworkResult.Error(it.message)
        }
    }


    fun leaveHistory(  )=viewModelScope.launch {
        runCatching {
            leaveHistoryMutableStateFlow.value = NetworkResult.Loading( )
            userRepository.leaveListStatus( )
        }.onSuccess {
            leaveHistoryMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            leaveHistoryMutableStateFlow .value = NetworkResult.Error(it.message)
        }
    }



}
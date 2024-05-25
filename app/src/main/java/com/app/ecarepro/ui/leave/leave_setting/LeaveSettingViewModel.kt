package com.app.ecarepro.ui.leave.leave_setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkLeaveSetting
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaveSettingViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val leaveSettingMutableStateFlow: MutableStateFlow<NetworkResult<NetworkLeaveSetting>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val leaveSettingStateFlow: StateFlow<NetworkResult<NetworkLeaveSetting>> =
        leaveSettingMutableStateFlow


    fun leaveSetting() = viewModelScope.launch {
        runCatching {
            leaveSettingMutableStateFlow.value = NetworkResult.Loading()
            userRepository.leaveSetting()
        }.onSuccess {
            leaveSettingMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            leaveSettingMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }

}
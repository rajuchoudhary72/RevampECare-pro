package com.app.ecarepro.ui.leave

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkLeaveListStatus
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaveHistoryViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val leaveHistoryMutableStateFlow: MutableStateFlow<NetworkResult<NetworkLeaveListStatus>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val leaveHistoryStateFlow: StateFlow<NetworkResult<NetworkLeaveListStatus>> =
        leaveHistoryMutableStateFlow

    private val leaveDeleteMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val leaveDeleteStateFlow: StateFlow<NetworkResult<CommonResponse>> = leaveDeleteMutableStateFlow

    fun leaveHistory() = viewModelScope.launch {
        runCatching {
            leaveHistoryMutableStateFlow.value = NetworkResult.Loading()
            userRepository.leaveListStatus()
        }.onSuccess {
            leaveHistoryMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            leaveHistoryMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }

    fun leaveDelete(lvID: Int) = viewModelScope.launch {
        runCatching {
            leaveDeleteMutableStateFlow.value = NetworkResult.Loading()
            userRepository.leaveDelete(lvID)
        }.onSuccess {
            leaveDeleteMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            leaveDeleteMutableStateFlow.value = NetworkResult.Error(it.message)
        }
    }

}
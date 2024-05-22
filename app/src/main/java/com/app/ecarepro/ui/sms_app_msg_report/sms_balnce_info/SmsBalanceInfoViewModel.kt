package com.app.ecarepro.ui.sms_app_msg_report.sms_balnce_info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkSMSBalnceInfo
import com.app.ecarepro.data.network.model.NetworkSMSConsumption
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SmsBalanceInfoViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val smsBalanceInfoMutableStateFlow: MutableStateFlow<NetworkResult<NetworkSMSBalnceInfo>> = MutableStateFlow(
        NetworkResult.Loading())
    val smsBalanceInfoStateFlow: StateFlow<NetworkResult<NetworkSMSBalnceInfo>> = smsBalanceInfoMutableStateFlow






    fun  getSMSBalanceInfo()=viewModelScope.launch {
        runCatching {
            smsBalanceInfoMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getSMSBalnceInfo(  )
        }.onSuccess {
            smsBalanceInfoMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            smsBalanceInfoMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

}


package com.app.ecarepro.ui.sms_app_msg_report.recharge_log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkRechargeLog
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
class RechargeLogViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val rechargeLogMutableStateFlow: MutableStateFlow<NetworkResult<NetworkRechargeLog>> = MutableStateFlow(
        NetworkResult.Loading())
    val rechargeLogStateFlow: StateFlow<NetworkResult<NetworkRechargeLog>> = rechargeLogMutableStateFlow






    fun  getRechargeLog(
        fromDate : String,
        toDate: String
    )=viewModelScope.launch {
        runCatching {
            rechargeLogMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getRechargeLog( fromDate, toDate )
        }.onSuccess {
            rechargeLogMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            rechargeLogMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

}


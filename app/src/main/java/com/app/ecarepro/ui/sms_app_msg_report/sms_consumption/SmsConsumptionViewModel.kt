package com.app.ecarepro.ui.sms_app_msg_report.sms_consumption

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkSMSConsumption
import com.app.ecarepro.data.network.model.NetworkSmsMsgReport
import com.app.ecarepro.data.network.model.NetworkStaffList
import com.app.ecarepro.data.network.model.NetworkStudentList
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SmsConsumptionViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val sMSConsumptionMutableStateFlow: MutableStateFlow<NetworkResult<NetworkSMSConsumption>> = MutableStateFlow(
        NetworkResult.Loading())
    val sMSConsumptionStateFlow: StateFlow<NetworkResult<NetworkSMSConsumption>> = sMSConsumptionMutableStateFlow






    fun  getSMSConsumption(
        fromDate : String,
        toDate: String
    )=viewModelScope.launch {
        runCatching {
            sMSConsumptionMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getSMSConsumption( fromDate, toDate )
        }.onSuccess {
            sMSConsumptionMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            sMSConsumptionMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

}


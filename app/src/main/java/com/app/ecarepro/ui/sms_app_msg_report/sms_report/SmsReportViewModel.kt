package com.app.ecarepro.ui.sms_app_msg_report.sms_report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkSmsMsgReport
import com.app.ecarepro.data.network.model.NetworkSmsReportDetails
import com.app.ecarepro.data.network.model.NetworkSmsReportModel
import com.app.ecarepro.data.network.model.NetworkStaffList
import com.app.ecarepro.data.network.model.NetworkStudentList
import com.app.ecarepro.data.network.model.SmsType
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SmsReportViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {


    private val smsTypeMutableStateFlow: MutableStateFlow<NetworkResult<NetworkSmsReportModel>> = MutableStateFlow(
        NetworkResult.Loading())
    val smsTypeStateFlow: StateFlow<NetworkResult<NetworkSmsReportModel>> = smsTypeMutableStateFlow

    private val smsReportMutableStateFlow: MutableStateFlow<NetworkResult<NetworkSmsReportDetails>> = MutableStateFlow(
        NetworkResult.Loading())
    val smsReportStateFlow: StateFlow<NetworkResult<NetworkSmsReportDetails>> = smsReportMutableStateFlow





    fun  getSMSType(  )=viewModelScope.launch {
        runCatching {
            smsTypeMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getSMSType( )
        }.onSuccess {
            smsTypeMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            smsTypeMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

    fun  getSMSReport ( fromDate : String,
                        toDate: String,
                        sMSType : Int,
                        page: Int  )=viewModelScope.launch {
        runCatching {
            smsReportMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getSMSReport( fromDate,toDate,sMSType,page)
        }.onSuccess {
            smsReportMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            smsReportMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

}


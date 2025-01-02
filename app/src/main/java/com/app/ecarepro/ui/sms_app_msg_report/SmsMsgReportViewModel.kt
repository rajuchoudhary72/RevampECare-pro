package com.app.ecarepro.ui.sms_app_msg_report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkSmsMsgReport
import com.app.ecarepro.data.network.model.NetworkStaffList
import com.app.ecarepro.data.network.model.NetworkStudentList
import com.app.ecarepro.data.network.model.NetworkWingReport
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SmsMsgReportViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

//    private val staffListMutableStateFlow: MutableStateFlow<NetworkResult<NetworkSmsMsgReport>> = MutableStateFlow(
//        NetworkResult.Loading())
//    val staffListStateFlow: StateFlow<NetworkResult<NetworkSmsMsgReport>> = staffListMutableStateFlow

    private val smsMsgReportMutableStateFlow: MutableStateFlow<NetworkResult<NetworkSmsMsgReport>> = MutableStateFlow(
        NetworkResult.Loading())
    val smsMsgReportStateFlow: StateFlow<NetworkResult<NetworkSmsMsgReport>> = smsMsgReportMutableStateFlow

    private val wingReportMutableStateFlow: MutableStateFlow<NetworkResult<NetworkWingReport>> = MutableStateFlow(
        NetworkResult.Loading())
    val  wingReportStateFlow: StateFlow<NetworkResult<NetworkWingReport>> = wingReportMutableStateFlow





//    fun  getAppMsgUsesForStaff(
//        fromDate : String,
//        toDate: String,
//        iD: String,
//    )=viewModelScope.launch {
//        runCatching {
//            staffListMutableStateFlow.value = NetworkResult.Loading()
//            userRepository.getAppMsgUses( fromDate, toDate, iD)
//        }.onSuccess {
//            staffListMutableStateFlow.value = NetworkResult.Success(it)
//        }.onFailure {
//            staffListMutableStateFlow.value = NetworkResult.Error(it.message)
//        }
//
//    }

    fun  getAppMsgUses(
        fromDate : String,
        toDate: String,
        iD: String,
    )=viewModelScope.launch {
        runCatching {
            smsMsgReportMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getAppMsgUses( fromDate, toDate, iD)
        }.onSuccess {
            smsMsgReportMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            smsMsgReportMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

    fun  wingsList()=viewModelScope.launch {
        runCatching {
            wingReportMutableStateFlow.value = NetworkResult.Loading()
            userRepository.wingsList( )
        }.onSuccess {
            wingReportMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            wingReportMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

    fun  getSMSUses(
        fromDate : String,
        toDate: String,
        iD: String,
    )=viewModelScope.launch {
        runCatching {
            smsMsgReportMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getSMSUses( fromDate, toDate, iD)
        }.onSuccess {
            smsMsgReportMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            smsMsgReportMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }



}


package com.app.ecarepro.ui.leave.leave_report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkLeaveListStatus
import com.app.ecarepro.data.network.model.NetworkLeaveReport
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaveReportViewModel @Inject constructor(
    private val userDataStore: UserDataStore,

    private val userRepository: UserRepository
) : ViewModel() {


    private val leaveReportMutableStateFlow: MutableStateFlow<NetworkResult<NetworkLeaveReport>> = MutableStateFlow(
        NetworkResult.Loading())
    val leaveReportStateFlow: StateFlow<NetworkResult<NetworkLeaveReport>> = leaveReportMutableStateFlow

    private val leaveActionMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> = MutableStateFlow(
        NetworkResult.Loading())
    val leaveActionStateFlow: StateFlow<NetworkResult<CommonResponse>> = leaveActionMutableStateFlow

    val user = userDataStore.getUserAsFlow()

    fun leaveReport(
        status: Int,
        ord: Int,
        applType: Int,
        pg: Int
    )=viewModelScope.launch {
        runCatching {
            leaveReportMutableStateFlow.value = NetworkResult.Loading( )
            userRepository.leaveReport( status, ord, applType, pg)
        }.onSuccess {
            leaveReportMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            leaveReportMutableStateFlow .value = NetworkResult.Error(it.message)
        }
    }

    fun leaveAction(
        applType:Int,
        lvID:Int?,
        lvIDs: String?,
        action:Int,
        forwardedTo:Int,
        rejectionReason: String,
         isPartialApproved: Boolean?,
         partialFromDate: String?,
         partialTillDate: String?,
    )=viewModelScope.launch {
        runCatching {
            leaveActionMutableStateFlow.value = NetworkResult.Loading( )
            userRepository.leaveAction( applType, lvID, lvIDs, action, forwardedTo,rejectionReason , isPartialApproved, partialFromDate, partialTillDate)
        }.onSuccess {
            leaveActionMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            leaveActionMutableStateFlow .value = NetworkResult.Error(it.message)
        }
    }

}
package com.app.ecarepro.ui.assignment.staff.viewAssignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkAssignments
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkSubmitAssignReport
import com.app.ecarepro.data.network.model.NetworkViewAssignment
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewAssignmentViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val viewAssignmentMutableStateFlow: MutableStateFlow<NetworkResult<NetworkViewAssignment>> = MutableStateFlow(
        NetworkResult.Loading())
    val viewAssignmentStateFlow: StateFlow<NetworkResult<NetworkViewAssignment>> = viewAssignmentMutableStateFlow

    private val assigSubRPTMutableStateFlow: MutableStateFlow<NetworkResult<NetworkSubmitAssignReport>> = MutableStateFlow(
        NetworkResult.Loading())
    val assigSubRPTStateFlow: StateFlow<NetworkResult<NetworkSubmitAssignReport>> = assigSubRPTMutableStateFlow

    private val offlineSubmitedMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> = MutableStateFlow(
        NetworkResult.Loading())
    val offlineSubmitedStateFlow: StateFlow<NetworkResult<CommonResponse>> = offlineSubmitedMutableStateFlow


    fun viewAssignment(  iD: String )=viewModelScope.launch {
        runCatching {
            viewAssignmentMutableStateFlow.value = NetworkResult.Loading()
            userRepository.viewAssignment(iD )
        }.onSuccess {
            viewAssignmentMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            viewAssignmentMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

    fun assignmnetSubmissionRPT(
        iD: String,
        notSubmitted: Boolean,
    )=viewModelScope.launch {
        runCatching {
            assigSubRPTMutableStateFlow.value = NetworkResult.Loading()
            userRepository.assignmnetSubmissionRPT(iD, notSubmitted )
        }.onSuccess {
            assigSubRPTMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            assigSubRPTMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

    fun  offlineSubmited(
        iD: String,
        stID: Int,
        submissitedOn: String
    )=viewModelScope.launch {
        runCatching {
            offlineSubmitedMutableStateFlow.value = NetworkResult.Loading()
            userRepository.offlineSubmited(iD, stID, submissitedOn )
        }.onSuccess {
            offlineSubmitedMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            offlineSubmitedMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

}

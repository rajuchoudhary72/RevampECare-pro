package com.app.ecarepro.ui.assignment.staff

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkTeacherAssignment
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeacherAssignmentViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val teacAssignmentMutableStateFlow: MutableStateFlow<NetworkResult<NetworkTeacherAssignment>> = MutableStateFlow(
        NetworkResult.Loading())
    val teacAssignmentStateFlow: StateFlow<NetworkResult<NetworkTeacherAssignment>> = teacAssignmentMutableStateFlow

    private val deleteAssignmentMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> = MutableStateFlow(
        NetworkResult.Loading())
    val deleteAssignmentStateFlow: StateFlow<NetworkResult<CommonResponse>> = deleteAssignmentMutableStateFlow


    fun teachersAssignment( iD: String)=viewModelScope.launch {
        runCatching {
            teacAssignmentMutableStateFlow.value = NetworkResult.Loading()
            userRepository.teachersAssignment(iD )
        }.onSuccess {
            teacAssignmentMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            teacAssignmentMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

    fun deleteAssignment(  iD: String  )=viewModelScope.launch {
        runCatching {
            deleteAssignmentMutableStateFlow.value = NetworkResult.Loading()
            userRepository.deleteAssignment(iD )
        }.onSuccess {
            deleteAssignmentMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            deleteAssignmentMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

}
package com.app.ecarepro.ui.assignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkActivityCalender
import com.app.ecarepro.data.network.model.NetworkAssignments
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssignmentNavHostViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val assignmentMutableStateFlow: MutableStateFlow<NetworkResult<NetworkAssignments>> = MutableStateFlow(
        NetworkResult.Loading())
    val assignmentStateFlow: StateFlow<NetworkResult<NetworkAssignments>> = assignmentMutableStateFlow

    fun getAssignment( )=viewModelScope.launch {
        runCatching {
            assignmentMutableStateFlow.value = NetworkResult.Loading()
            userRepository.assignment( )
        }.onSuccess {
            assignmentMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            assignmentMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

}
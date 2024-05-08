package com.app.ecarepro.ui.staffList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkStaffList
import com.app.ecarepro.data.network.model.NetworkStudentList
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StaffListViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val staffListMutableStateFlow: MutableStateFlow<NetworkResult<NetworkStaffList>> = MutableStateFlow(
        NetworkResult.Loading())
    val staffListStateFlow: StateFlow<NetworkResult<NetworkStaffList>> = staffListMutableStateFlow

    fun  getStaffList(

    )=viewModelScope.launch {
        runCatching {
            staffListMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getStaffList( )
        }.onSuccess {
            staffListMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            staffListMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

}


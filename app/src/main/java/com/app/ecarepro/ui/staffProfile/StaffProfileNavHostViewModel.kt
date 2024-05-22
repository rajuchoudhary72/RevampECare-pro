package com.app.ecarepro.ui.staffProfile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkStaffProfile
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StaffProfileNavHostViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val staffProfileMutableStateFlow: MutableStateFlow<NetworkResult<NetworkStaffProfile>> = MutableStateFlow(
        NetworkResult.Loading())
    val staffProfileStateFlow: StateFlow<NetworkResult<NetworkStaffProfile>> = staffProfileMutableStateFlow

    fun  getStaffProfile( sId: Int )=viewModelScope.launch {
        runCatching {
            staffProfileMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getStaffProfile(sId)
        }.onSuccess {
            staffProfileMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            staffProfileMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

}


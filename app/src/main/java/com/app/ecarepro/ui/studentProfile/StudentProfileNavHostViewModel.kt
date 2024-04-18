package com.app.ecarepro.ui.studentProfile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkStaffProfile
import com.app.ecarepro.data.network.model.NetworkStudentProfile
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentProfileNavHostViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val studentProfileMutableStateFlow: MutableStateFlow<NetworkResult<NetworkStudentProfile>> = MutableStateFlow(
        NetworkResult.Loading())
    val studentProfileStateFlow: StateFlow<NetworkResult<NetworkStudentProfile>> = studentProfileMutableStateFlow

    fun   getStudentProfile(  sId: Int  )=viewModelScope.launch {
        runCatching {
            studentProfileMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getStudentProfile(sId)
        }.onSuccess {
            studentProfileMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            studentProfileMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

}


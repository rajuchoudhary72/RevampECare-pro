package com.app.ecarepro.ui.studentProfile.academic_performance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkAcademicPerformance
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AcademicPerViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val studentProfileMutableStateFlow: MutableStateFlow<NetworkResult<NetworkAcademicPerformance>> = MutableStateFlow(
        NetworkResult.Loading())
    val studentProfileStateFlow: StateFlow<NetworkResult<NetworkAcademicPerformance>> = studentProfileMutableStateFlow

    fun getAcademicPerformance(
        sId: Int,
        yrID: Int
    )=viewModelScope.launch {
        runCatching {
            studentProfileMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getAcademicPerformance(sId, yrID)
        }.onSuccess {
            studentProfileMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            studentProfileMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }



}


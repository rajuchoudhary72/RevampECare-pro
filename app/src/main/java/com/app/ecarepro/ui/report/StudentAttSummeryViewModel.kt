package com.app.ecarepro.ui.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkAttedanceSummary
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentAttSummeryViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val attSummeryMutableStateFlow: MutableStateFlow<NetworkResult<NetworkAttedanceSummary>> = MutableStateFlow(
        NetworkResult.Loading())
    val attSummeryStateFlow: StateFlow<NetworkResult<NetworkAttedanceSummary>> = attSummeryMutableStateFlow


    fun  getAttendanceSummary(
        attDate: String
    )=viewModelScope.launch {
        runCatching {
            attSummeryMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getAttendanceSummary(attDate )
        }.onSuccess {
            attSummeryMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            attSummeryMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

}


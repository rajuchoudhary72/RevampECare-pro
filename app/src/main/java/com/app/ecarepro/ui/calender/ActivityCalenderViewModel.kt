package com.app.ecarepro.ui.calender

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkActivityCalender
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityCalenderViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val calenderStateFlow: MutableStateFlow<NetworkResult<NetworkActivityCalender>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val _calenderStateFlow: StateFlow<NetworkResult<NetworkActivityCalender>> = calenderStateFlow

    fun getActivityCaledar() = viewModelScope.launch {
        runCatching {
            calenderStateFlow.value = NetworkResult.Loading()
            userRepository.getActivityCalender()
        }.onSuccess {
            calenderStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            calenderStateFlow.value = NetworkResult.Error(it.message)
        }

    }

}
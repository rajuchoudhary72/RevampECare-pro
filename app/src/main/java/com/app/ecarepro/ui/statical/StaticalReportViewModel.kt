package com.app.ecarepro.ui.statical

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StaticalReportViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val staticGraphResponseMutableStateFlow: MutableStateFlow<NetworkResult<StaticGraphResponse>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val staticGraphResponseStateFlow: StateFlow<NetworkResult<StaticGraphResponse>> =
        staticGraphResponseMutableStateFlow

    fun statistical() = viewModelScope.launch {
        runCatching {
            staticGraphResponseMutableStateFlow.value = NetworkResult.Loading()
            userRepository.statistical()
        }.onSuccess {
            staticGraphResponseMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            staticGraphResponseMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }




}
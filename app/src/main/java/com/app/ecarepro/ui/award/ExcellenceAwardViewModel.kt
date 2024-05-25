package com.app.ecarepro.ui.award

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
class ExcellenceAwardViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val awadStateFlow: MutableStateFlow<NetworkResult<ExcellenceAwardResponse>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val _awadStateFlow: StateFlow<NetworkResult<ExcellenceAwardResponse>> = awadStateFlow

    fun getAwardData() = viewModelScope.launch {
        runCatching {
            awadStateFlow.value = NetworkResult.Loading()
            userRepository.excellenceAward()
        }.onSuccess {
            awadStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            awadStateFlow.value = NetworkResult.Error(it.message)
        }
    }


}
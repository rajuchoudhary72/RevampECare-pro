package com.app.ecarepro.ui.discipline_log.infraction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkAddInfraction
import com.app.ecarepro.data.network.model.NetworkInfractions
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InfractionListViewModel @Inject constructor(
    private   val userRepository: UserRepository
) : ViewModel() {


    private val addInfractionMutableStateFlow: MutableStateFlow<NetworkResult<NetworkInfractions>> = MutableStateFlow(
        NetworkResult.Loading())
    val addInfractionStateFlow: StateFlow<NetworkResult<NetworkInfractions>> = addInfractionMutableStateFlow

    fun  getInfractions( stID: Int  )=viewModelScope.launch {
        runCatching {
            addInfractionMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getInfractions(stID)
        }.onSuccess {
            addInfractionMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            addInfractionMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

}
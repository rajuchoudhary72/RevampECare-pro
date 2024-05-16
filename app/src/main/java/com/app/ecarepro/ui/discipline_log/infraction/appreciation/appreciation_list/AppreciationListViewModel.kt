package com.app.ecarepro.ui.discipline_log.infraction.appreciation.appreciation_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkAddAppreciation
import com.app.ecarepro.data.network.model.NetworkAddInfraction
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppreciationListViewModel @Inject constructor(
    private   val userRepository: UserRepository
) : ViewModel() {


    private val addAppreciationMutableStateFlow: MutableStateFlow<NetworkResult<NetworkAddAppreciation>> = MutableStateFlow(
        NetworkResult.Loading())
    val addAppreciationStateFlow: StateFlow<NetworkResult<NetworkAddAppreciation>> = addAppreciationMutableStateFlow

    fun  addAppreciation( stID: Int  )=viewModelScope.launch {
        runCatching {
            addAppreciationMutableStateFlow.value = NetworkResult.Loading()
            userRepository.addAppreciation(stID)
        }.onSuccess {
            addAppreciationMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            addAppreciationMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

}
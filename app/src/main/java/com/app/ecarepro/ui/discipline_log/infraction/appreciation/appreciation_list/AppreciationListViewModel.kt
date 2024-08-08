package com.app.ecarepro.ui.discipline_log.infraction.appreciation.appreciation_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkAddAppreciation
import com.app.ecarepro.data.network.model.NetworkAddInfraction
import com.app.ecarepro.data.network.model.NetworkAppreciations
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


    private val addAppreciationMutableStateFlow: MutableStateFlow<NetworkResult<NetworkAppreciations>> = MutableStateFlow(
        NetworkResult.Loading())
    val addAppreciationStateFlow: StateFlow<NetworkResult<NetworkAppreciations>> = addAppreciationMutableStateFlow

    private val deleteLogMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> = MutableStateFlow(
        NetworkResult.Loading())
    val deleteLogStateFlow: StateFlow<NetworkResult<CommonResponse>> = deleteLogMutableStateFlow



    fun  getAppreciations( stID: Int  )=viewModelScope.launch {
        runCatching {
            addAppreciationMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getAppreciations(stID)
        }.onSuccess {
            addAppreciationMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            addAppreciationMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

    fun  disciplineLogDeleteLog(
        id: String,
        type: Int
    )=viewModelScope.launch {
        runCatching {
            deleteLogMutableStateFlow.value = NetworkResult.Loading()
            userRepository.disciplineLogDeleteLog(id, type)
        }.onSuccess {
            deleteLogMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            deleteLogMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

}
package com.app.ecarepro.ui.discipline_log.infraction.appreciation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkAddAppreciation
import com.app.ecarepro.data.network.model.NetworkAppreciationInstance
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkSubAppreciationTypes
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddAppreciationViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {


    private val appreciationInstanceMutableStateFlow: MutableStateFlow<NetworkResult<NetworkAppreciationInstance>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val appreciationInstanceStateFlow: StateFlow<NetworkResult<NetworkAppreciationInstance>> =
        appreciationInstanceMutableStateFlow


    private val addAppreciationMutableStateFlow: MutableStateFlow<NetworkResult<NetworkAddAppreciation>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val addAppreciationStateFlow: StateFlow<NetworkResult<NetworkAddAppreciation>> =
        addAppreciationMutableStateFlow

    private val subAppreciationTypesMutableStateFlow: MutableStateFlow<NetworkResult<NetworkSubAppreciationTypes>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val subAppreciationTypesStateFlow: StateFlow<NetworkResult<NetworkSubAppreciationTypes>> =
        subAppreciationTypesMutableStateFlow

    private val saveAppreciationMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val saveAppreciationStateFlow: StateFlow<NetworkResult<CommonResponse>> =
        saveAppreciationMutableStateFlow


    fun addAppreciation(stID: Int) = viewModelScope.launch {
        runCatching {
            addAppreciationMutableStateFlow.value = NetworkResult.Loading()
            userRepository.addAppreciation(stID)
        }.onSuccess {
            addAppreciationMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            addAppreciationMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

    fun subAppreciationTypes(aprID: Int) = viewModelScope.launch {
        runCatching {
            subAppreciationTypesMutableStateFlow.value = NetworkResult.Loading()
            userRepository.subAppreciationTypes(aprID)
        }.onSuccess {
            subAppreciationTypesMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            subAppreciationTypesMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

    fun appreciationInstance(
        aprSubID: Int,
        stID: Int,
    ) = viewModelScope.launch {
        runCatching {
            appreciationInstanceMutableStateFlow.value = NetworkResult.Loading()
            userRepository.appreciationInstance(aprSubID, stID)
        }.onSuccess {
            appreciationInstanceMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            appreciationInstanceMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

    fun saveAppreciation(
        action: Int,
        stID: Int,
        aprSubID: Int,
        rwdID: Int,
        instance: Int,
        appreciationOn: String,
        remark: String,

        ) = viewModelScope.launch {
        runCatching {
            saveAppreciationMutableStateFlow.value = NetworkResult.Loading()
            userRepository.saveAppreciation(
                action,
                stID,
                aprSubID,
                rwdID,
                instance,
                appreciationOn,
                remark
            )
        }.onSuccess {
            saveAppreciationMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            saveAppreciationMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

}
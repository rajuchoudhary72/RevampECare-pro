package com.app.ecarepro.ui.discipline_log.infraction.add_infraction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkAddInfraction
import com.app.ecarepro.data.network.model.NetworkInfractionInstance
import com.app.ecarepro.data.network.model.NetworkInfractionTypes
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkSubInfractionTypes
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddInfractionViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {


    private val infractionTypesMutableStateFlow: MutableStateFlow<NetworkResult<NetworkInfractionTypes>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val infractionTypesStateFlow: StateFlow<NetworkResult<NetworkInfractionTypes>> =
        infractionTypesMutableStateFlow


    private val infractionInstanceMutableStateFlow: MutableStateFlow<NetworkResult<NetworkInfractionInstance>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val infractionInstanceStateFlow: StateFlow<NetworkResult<NetworkInfractionInstance>> =
        infractionInstanceMutableStateFlow


    private val addInfractionMutableStateFlow: MutableStateFlow<NetworkResult<NetworkAddInfraction>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val addInfractionStateFlow: StateFlow<NetworkResult<NetworkAddInfraction>> =
        addInfractionMutableStateFlow

    private val subInfractionTypesMutableStateFlow: MutableStateFlow<NetworkResult<NetworkSubInfractionTypes>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val subInfractionTypesStateFlow: StateFlow<NetworkResult<NetworkSubInfractionTypes>> =
        subInfractionTypesMutableStateFlow

    private val saveInfractionMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val saveInfractionStateFlow: StateFlow<NetworkResult<CommonResponse>> =
        saveInfractionMutableStateFlow


    fun getInfractionTypes() = viewModelScope.launch {
        runCatching {
            infractionTypesMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getInfractionTypes()
        }.onSuccess {
            infractionTypesMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            infractionTypesMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

    fun addInfraction(stID: Int) = viewModelScope.launch {
        runCatching {
            addInfractionMutableStateFlow.value = NetworkResult.Loading()
            userRepository.addInfraction(stID)
        }.onSuccess {
            addInfractionMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            addInfractionMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

    fun getSubInfractionTypes(infrTypeID: Int) = viewModelScope.launch {
        runCatching {
            subInfractionTypesMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getSubInfractionTypes(infrTypeID)
        }.onSuccess {
            subInfractionTypesMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            subInfractionTypesMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

    fun getinfractionInstance(
        infrTypeID: Int,
        InfrSubTypeID: Int,
        StID: Int
    ) = viewModelScope.launch {
        runCatching {
            infractionInstanceMutableStateFlow.value = NetworkResult.Loading()
            userRepository.infractionInstance(infrTypeID, InfrSubTypeID, StID)
        }.onSuccess {
            infractionInstanceMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            infractionInstanceMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

    fun saveInfraction(
        action: Int,
        stID: Int,
        infrSubTypeID: Int,
        consID: Int,
        instance: Int,
        infractionOn: String,
        correctiveAction: String,

        ) = viewModelScope.launch {
        runCatching {
            saveInfractionMutableStateFlow.value = NetworkResult.Loading()
            userRepository.saveInfraction(
                action,
                stID,
                infrSubTypeID,
                consID,
                instance,
                infractionOn,
                correctiveAction
            )
        }.onSuccess {
            saveInfractionMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            saveInfractionMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

}
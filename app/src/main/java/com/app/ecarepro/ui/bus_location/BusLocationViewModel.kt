package com.app.ecarepro.ui.bus_location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkBusLocation
import com.app.ecarepro.data.network.model.NetworkCircular
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkVehicleNumber
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BusLocationViewModel  @Inject constructor(
    private val userRepository: UserRepository ,
 ) : ViewModel() {

    private val vehicleNumberMutableStateFlow: MutableStateFlow<NetworkResult<NetworkVehicleNumber>> = MutableStateFlow(
        NetworkResult.Loading())
    val vehicleNumberStateFlowStateFlow: StateFlow<NetworkResult<NetworkVehicleNumber>> = vehicleNumberMutableStateFlow

    private val busLocationMutableStateFlow: MutableStateFlow<NetworkResult<NetworkBusLocation>> = MutableStateFlow(
        NetworkResult.Loading())
    val busLocationStateFlowStateFlow: StateFlow<NetworkResult<NetworkBusLocation>> = busLocationMutableStateFlow

    init {
        getVehicleNumber()
    }
    fun getVehicleNumber( )=viewModelScope.launch {
       runCatching {
           vehicleNumberMutableStateFlow.value =NetworkResult.Loading()
           userRepository.getVehicleNumber( )
       }.onSuccess {
           vehicleNumberMutableStateFlow.value =NetworkResult.Success(it)
       }.onFailure {
           vehicleNumberMutableStateFlow.value = NetworkResult.Error(it.message)
       }

    }

    fun busLocation(
        vehicleNumber: String
    )=viewModelScope.launch {
        runCatching {
            busLocationMutableStateFlow.value =NetworkResult.Loading()
            userRepository.busLocation(vehicleNumber )
        }.onSuccess {
            busLocationMutableStateFlow.value =NetworkResult.Success(it)
        }.onFailure {
            busLocationMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }


}
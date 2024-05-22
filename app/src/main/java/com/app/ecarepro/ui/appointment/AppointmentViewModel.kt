package com.app.ecarepro.ui.appointment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.database.databases.UserDatabase
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkActivityCalender
import com.app.ecarepro.data.network.model.NetworkAppointments
import com.app.ecarepro.data.network.model.NetworkAssignments
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppointmentViewModel @Inject constructor(
    private val userRepository: UserRepository
    ) : ViewModel() {



    private val appointmentsMutableStateFlow: MutableStateFlow<NetworkResult<NetworkAppointments>> = MutableStateFlow(
        NetworkResult.Loading())
    val appointmentsStateFlow: StateFlow<NetworkResult<NetworkAppointments>> = appointmentsMutableStateFlow


    private val appointmentsActionMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> = MutableStateFlow(
        NetworkResult.Loading())
    val appointmentsActionStateFlow: StateFlow<NetworkResult<CommonResponse>> = appointmentsActionMutableStateFlow


    fun appointmentOverview(
        appDate: String,
        tillDate: String,
        all: Boolean,
    )=viewModelScope.launch {
        runCatching {
            appointmentsMutableStateFlow.value = NetworkResult.Loading()
            userRepository.appointmentOverview(appDate, tillDate, all )
        }.onSuccess {
            appointmentsMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            appointmentsMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

    fun appointmentAction(
        act: Int,
        appId: Int
    )=viewModelScope.launch {
        runCatching {
            appointmentsActionMutableStateFlow.value = NetworkResult.Loading()
            userRepository.appointmentAction(act, appId )
        }.onSuccess {
            appointmentsActionMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            appointmentsActionMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }



}
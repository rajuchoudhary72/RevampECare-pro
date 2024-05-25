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
import com.app.ecarepro.data.repository.FomApiRepository
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.utils.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppointmentSubViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val fomApiRepository: FomApiRepository,
    private val userDataStore: UserDataStore,
    ) : ViewModel() {




    private val approveAppointmentMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> = MutableStateFlow(
        NetworkResult.Loading())
    val approveAppointmentStateFlow: StateFlow<NetworkResult<CommonResponse>> = approveAppointmentMutableStateFlow


    private val appointmentsActionMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> = MutableStateFlow(
        NetworkResult.Loading())
    val appointmentsActionStateFlow: StateFlow<NetworkResult<CommonResponse>> = appointmentsActionMutableStateFlow




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

    fun approveAppointment(
        url: String,
        // token: String,
    )=viewModelScope.launch {
        runCatching {
            approveAppointmentMutableStateFlow.value = NetworkResult.Loading()
            fomApiRepository.approveAppointment(
                Constant.APPOINTMENT_BASEURL+
                    Constant.APPROVE_APPOINTMENT_URL+
                        userDataStore.getSchoolData()?.schoolCode+ "/"+
                url

            )
        }.onSuccess {
            approveAppointmentMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            approveAppointmentMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }



}
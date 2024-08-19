package com.app.ecarepro.ui.fom_guard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkSchool
import com.app.ecarepro.data.network.model.NetworkUserDetailsDto
import com.app.ecarepro.data.repository.FomApiRepository
import com.app.ecarepro.ui.fom_guard.model.FomGuardAppointments
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FomGuardAppointmentsViewModel @Inject constructor(
    private val fomApiRepository: FomApiRepository,
    private val userDataStore: UserDataStore,
    ) : ViewModel() {



    var feePaymentURL : String = ""
    lateinit var schoolDetails : NetworkSchool
    lateinit var userDetails : NetworkUserDetailsDto

    init {


        viewModelScope.launch {
            schoolDetails = userDataStore.getSchoolData()!!
         }
        viewModelScope.launch {
            userDetails = userDataStore.getUser()!!
        }

        viewModelScope.launch {
            feePaymentURL = userDataStore.getSchoolData()?.feePayemtURL.toString()
        }



    }


    private val fomGuardAppointmentsMutableStateFlow: MutableStateFlow<NetworkResult<FomGuardAppointments>> = MutableStateFlow(
        NetworkResult.Loading())
    val fomGuardAppointmentsStateFlow: StateFlow<NetworkResult<FomGuardAppointments>> = fomGuardAppointmentsMutableStateFlow

    private val updateappointmentcheckoutMutableStateFlow: MutableStateFlow<NetworkResult<CommonResponse>> = MutableStateFlow(
        NetworkResult.Loading())
    val updateappointmentcheckoutStateFlow: StateFlow<NetworkResult<CommonResponse>> = updateappointmentcheckoutMutableStateFlow



    fun getFomGuardAppointments(date: String?) =viewModelScope.launch {
        runCatching {
            fomGuardAppointmentsMutableStateFlow.value = NetworkResult.Loading()
            fomApiRepository.getFomGuardAppointments(
                  userDataStore.getSchoolData()?.feePayemtURL!!.replace("mlogin.aspx", "")+"api/certificate?senderid="+userDataStore.getSchoolData()!!.schoolCode,
                 )
        }.onSuccess {
            fomGuardAppointmentsMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            fomGuardAppointmentsMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

    fun updateappointmentcheckout(id: String?) =viewModelScope.launch {
        runCatching {
            updateappointmentcheckoutMutableStateFlow.value = NetworkResult.Loading()
            fomApiRepository.updateappointmentcheckout(
                userDataStore.getSchoolData()?.feePayemtURL!!.replace("mlogin.aspx", "")+"api/certificate?senderid="+userDataStore.getSchoolData()!!.schoolCode,
            )
        }.onSuccess {
            updateappointmentcheckoutMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            updateappointmentcheckoutMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }






}
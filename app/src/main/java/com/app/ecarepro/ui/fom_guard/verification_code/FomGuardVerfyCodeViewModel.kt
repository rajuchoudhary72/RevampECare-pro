package com.app.ecarepro.ui.fom_guard.verification_code

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkSchool
import com.app.ecarepro.data.network.model.NetworkUserDetailsDto
import com.app.ecarepro.data.repository.FomApiRepository
import com.app.ecarepro.ui.fom_guard.model.verify_code.NetworkVerifyCode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FomGuardVerfyCodeViewModel @Inject constructor(
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



    private val updateappointmentCheckInTimeMutableStateFlow: MutableStateFlow<NetworkResult<NetworkVerifyCode>> = MutableStateFlow(
        NetworkResult.Loading())
    val updateappointmentCheckInTimeStateFlow: StateFlow<NetworkResult<NetworkVerifyCode>> = updateappointmentCheckInTimeMutableStateFlow





    fun updateappointmentCheckInTime(code: String?) =viewModelScope.launch {
        runCatching {
            updateappointmentCheckInTimeMutableStateFlow.value = NetworkResult.Loading()
            fomApiRepository.updateappointmentCheckInTime(
                userDataStore.getSchoolData()?.feePayemtURL!!.replace("mlogin.aspx", "")+"api/certificate?senderid="+userDataStore.getSchoolData()!!.schoolCode,
            )
        }.onSuccess {
            updateappointmentCheckInTimeMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            updateappointmentCheckInTimeMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }






}
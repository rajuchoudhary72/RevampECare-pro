package com.app.ecarepro.ui.fom_guard.verifying_phone

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkSchool
import com.app.ecarepro.data.network.model.NetworkUserDetailsDto
import com.app.ecarepro.data.network.model.VisitorDetailsDto
import com.app.ecarepro.data.repository.FomApiRepository
import com.app.ecarepro.ui.fom_guard.model.verifiy_number.VerifyPhone
import com.app.ecarepro.ui.fom_guard.model.verify_code.Appdetails
import com.app.ecarepro.ui.fom_guard.model.verify_code.NetworkVerifyCode
import com.app.ecarepro.utils.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerfyPhoneViewModel @Inject constructor(
    private val fomApiRepository: FomApiRepository,
    private val userDataStore: UserDataStore,
    ) : ViewModel() {



     lateinit var schoolDetails : NetworkSchool

    init {


        viewModelScope.launch {
            schoolDetails = userDataStore.getSchoolData()!!
         }




    }


    val appointmentData = MutableLiveData<Appdetails>()

    fun setAppointmentData(data: Appdetails) {
        appointmentData.value = data
    }


    private val  userdetailsfrommobileMutableStateFlow: MutableStateFlow<NetworkResult<VisitorDetailsDto>> = MutableStateFlow(
        NetworkResult.Loading())
    val  userdetailsfrommobiletateFlow: StateFlow<NetworkResult<VisitorDetailsDto>> = userdetailsfrommobileMutableStateFlow





    fun getuserdetailsfrommobile(number: String?) =viewModelScope.launch {
        runCatching {
            userdetailsfrommobileMutableStateFlow.value = NetworkResult.Loading()
            fomApiRepository.getuserdetailsfrommobile(
                Constant.APPOINTMENT_BASEURL+"getuserdetailsfrommobile/"+userDataStore.getSchoolData()!!.schoolCode+"/"+number+""
            )
        }.onSuccess {
            userdetailsfrommobileMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            userdetailsfrommobileMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }






}
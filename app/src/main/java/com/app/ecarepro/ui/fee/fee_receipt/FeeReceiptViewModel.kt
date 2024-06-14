package com.app.ecarepro.ui.fee.fee_receipt

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.NetworkFeeCollection
import com.app.ecarepro.data.network.model.NetworkFeeReceipt
import com.app.ecarepro.data.network.model.NetworkGenerateTokenFeePay
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkSchool
import com.app.ecarepro.data.network.model.NetworkUserDetailsDto
import com.app.ecarepro.data.network.model.create_fee_request.FeeReceiptRequest
import com.app.ecarepro.data.repository.FomApiRepository
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeeReceiptViewModel @Inject constructor(
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


    private val feeReceiptMutableStateFlow: MutableStateFlow<NetworkResult<NetworkFeeReceipt>> = MutableStateFlow(
        NetworkResult.Loading())
    val feeReceiptStateFlow: StateFlow<NetworkResult<NetworkFeeReceipt>> = feeReceiptMutableStateFlow



    fun getFeeReceipt(
        url: String,
        sessionid:Int

    )=viewModelScope.launch {
        runCatching {
            feeReceiptMutableStateFlow.value = NetworkResult.Loading()
            fomApiRepository.getFeeReceipt(
                userDataStore.getSchoolData()?.feePayemtURL!!.replace("mlogin.aspx", "")+"api/feereceipt",
                FeeReceiptRequest(
                userDataStore.getSchoolData()!!.schoolCode,
                    userDataStore.getUserNameID().toString(),
                "",
                "",
                sessionid
            ))
        }.onSuccess {
            feeReceiptMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            feeReceiptMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }





}
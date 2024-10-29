package com.app.ecarepro.ui.fee_certificate

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.NetworkFeeCerDownload
import com.app.ecarepro.data.network.model.NetworkFeeCollection
import com.app.ecarepro.data.network.model.NetworkFeeReceipt
import com.app.ecarepro.data.network.model.NetworkGenerateTokenFeePay
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkSchool
import com.app.ecarepro.data.network.model.NetworkUserDetailsDto
import com.app.ecarepro.data.network.model.PostCertf.PostDataFeeCertificate
import com.app.ecarepro.data.network.model.create_fee_request.FeeReceiptRequest
import com.app.ecarepro.data.repository.FomApiRepository
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.model.FeeCertificateList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class FeeCertificateViewModel @Inject constructor(
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


    private val feeCertificateMutableStateFlow: MutableStateFlow<NetworkResult<FeeCertificateList>> = MutableStateFlow(
        NetworkResult.Loading())
    val feeCertificateStateFlow: StateFlow<NetworkResult<FeeCertificateList>> = feeCertificateMutableStateFlow

    private val feeCertificateDownloadMutableStateFlow: MutableStateFlow<NetworkResult<NetworkFeeCerDownload>> = MutableStateFlow(
        NetworkResult.Loading())
    val feeCertificateDownloadStateFlow: StateFlow<NetworkResult<NetworkFeeCerDownload>> = feeCertificateDownloadMutableStateFlow


    fun getFeeCertificate( )=viewModelScope.launch {
        runCatching {
            feeCertificateMutableStateFlow.value = NetworkResult.Loading()
            fomApiRepository.getFeeCertificate(
                //"https://payment.agnelgreaternoida.org/api/certificate?senderid=AGNLGN"
                 userDataStore.getSchoolData()?.feePayemtURL!!.replace("mlogin.aspx", "")+"api/certificate?senderid="+userDataStore.getSchoolData()!!.schoolCode,
                /*FeeReceiptRequest(
                userDataStore.getSchoolData()!!.schoolCode,
                    userDataStore.getUserNameID().toString(),
                "",
                "",
                sessionid
            )*/)
        }.onSuccess {
            feeCertificateMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            feeCertificateMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

    fun getFeeCertificateDownload(sessionId : Int,sessionName: String )=viewModelScope.launch {
        runCatching {
            feeCertificateDownloadMutableStateFlow.value = NetworkResult.Loading()
            fomApiRepository.getFeeCertificateDownload(
                //"https://payment.agnelgreaternoida.org/api/certificate",
                userDataStore.getSchoolData()?.feePayemtURL!!.replace("mlogin.aspx", "")+"api/certificate" ,
                PostDataFeeCertificate(
                    "",
                    userDataStore.getUser()?.userId.toString(),
                     userDataStore.getSchoolData()!!.schoolCode,
                    //"PR1572",
                    //"AGNLGN",
                    sessionId,
                    sessionName,

            ) )
        }.onSuccess {
            feeCertificateDownloadMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            feeCertificateDownloadMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }





}
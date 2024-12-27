package com.app.ecarepro.ui.fee.fee_book

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.FeeBookDownloadRequestModel
import com.app.ecarepro.data.network.model.FeeBookModel
import com.app.ecarepro.data.network.model.NetworkFeeBook
import com.app.ecarepro.data.network.model.NetworkFeeCerDownload
import com.app.ecarepro.data.network.model.NetworkFeeReceipt
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkSchool
import com.app.ecarepro.data.network.model.NetworkUserDetailsDto
import com.app.ecarepro.data.network.model.create_fee_request.FeeReceiptDownloadRequest
import com.app.ecarepro.data.network.model.create_fee_request.FeeReceiptRequest
import com.app.ecarepro.data.repository.FomApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeeBookViewModel @Inject constructor(
    private val fomApiRepository: FomApiRepository,
    private val userDataStore: UserDataStore,
    ) : ViewModel() {

        lateinit var schoolDetails : NetworkSchool
        lateinit var userDetails : NetworkUserDetailsDto

    init {


        viewModelScope.launch {
            schoolDetails = userDataStore.getSchoolData()!!
         }
        viewModelScope.launch {
            userDetails = userDataStore.getUser()!!
        }

    }
    private val feeCertificateDownloadMutableStateFlow: MutableStateFlow<NetworkResult<NetworkFeeCerDownload>> = MutableStateFlow(
        NetworkResult.Loading())
    val feeCertificateDownloadStateFlow: StateFlow<NetworkResult<NetworkFeeCerDownload>> = feeCertificateDownloadMutableStateFlow


    private val feeBookMutableStateFlow: MutableStateFlow<NetworkResult<NetworkFeeBook>> = MutableStateFlow(
        NetworkResult.Loading())
    val feeBookStateFlow: StateFlow<NetworkResult<NetworkFeeBook>> = feeBookMutableStateFlow



    fun getFeeBookReportList()=viewModelScope.launch {
        runCatching {
            feeBookMutableStateFlow.value = NetworkResult.Loading()
            fomApiRepository.getFeeBookReportList(
                url = userDataStore.getSchoolData()?.feePayemtURL!!.replace("mlogin.aspx", "")+"api/billbook",
               ParentName = "",
                stid = userDataStore.getUser()?.userId.toString(),
                schoolcode =   userDataStore.getSchoolData()!!.schoolCode)
        }.onSuccess {
            feeBookMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            feeBookMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }


    fun getFeeReceiptDownload(feeBookModel: FeeBookModel)=viewModelScope.launch {
        runCatching {
            feeCertificateDownloadMutableStateFlow.value = NetworkResult.Loading()
            fomApiRepository.getFeeBookDownload(
                //"https://payment.agnelgreaternoida.org/api/certificate",
                userDataStore.getSchoolData()?.feePayemtURL!!.replace("mlogin.aspx", "")+"api/billbookdownload" ,
                schoolcode =    userDataStore.getSchoolData()!!.schoolCode,
                Billsetting = feeBookModel.billtype.toString(),
                installid = feeBookModel.InstallId.toString(),
                stid = feeBookModel.stid.toString(),
                yrid = feeBookModel.yrid.toString(),
            )
        }.onSuccess {
            feeCertificateDownloadMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            feeCertificateDownloadMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }


}
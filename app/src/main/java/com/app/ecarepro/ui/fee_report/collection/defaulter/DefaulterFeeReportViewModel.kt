package com.app.ecarepro.ui.fee_report.collection.defaulter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.DefaulterDataList
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.FomApiRepository
import com.app.ecarepro.data.network.model.DefaulterFilters
import com.app.ecarepro.utils.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DefaulterFeeReportViewModel @Inject constructor(
    private val fomApiRepository: FomApiRepository,
    private val userDataStore: UserDataStore,
    ) : ViewModel() {



    private val defaultFilterMutableStateFlow: MutableStateFlow<NetworkResult<DefaulterFilters>> =
        MutableStateFlow(
        NetworkResult.Loading())
    val defaultFilterStateFlow: StateFlow<NetworkResult<DefaulterFilters>> =
        defaultFilterMutableStateFlow

    private val defaultersDataMutableStateFlow: MutableStateFlow<NetworkResult<List<DefaulterDataList>>> =
        MutableStateFlow(NetworkResult.Loading())
    val defaultersDataStateFlow:
            StateFlow<NetworkResult<List<DefaulterDataList>>> = defaultersDataMutableStateFlow



    fun defaulterFilters(
    )=viewModelScope.launch {
        runCatching {
            defaultFilterMutableStateFlow.value = NetworkResult.Loading()
            fomApiRepository.defaulterFilters(
               // userDataStore.getSchoolData()?.feePayemtURL!!.replace("mlogin.aspx", "")+"api/defaulter?senderid="+userDataStore.getSchoolData()?.schoolCode)
                Constant.REPORT_BASE_URL+"api/defaulter?senderid="+userDataStore.getSchoolData()?.schoolCode)
        }.onSuccess {
            defaultFilterMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            defaultFilterMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }


    fun getDefaulterReport(
        DateFrom : String,
        DateTo : String,
        schoolid : String,
        feetypeid : String,
        classid : String,
        sectionid : String,
        installid : String,
    )=viewModelScope.launch {
        runCatching {
            defaultersDataMutableStateFlow.value = NetworkResult.Loading()
            fomApiRepository.getDefaulterReport(
                Constant.REPORT_BASE_URL+"api/defaulter",
               // userDataStore.getSchoolData()?.feePayemtURL!!.replace("mlogin.aspx", "")+"api/defaulter",
                userDataStore.getSchoolData()!!.schoolCode,
                DateFrom, DateTo, schoolid, feetypeid, classid, sectionid, installid )
        }.onSuccess {
            defaultersDataMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            defaultersDataMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

}
package com.app.ecarepro.ui.fee_report.collection.estimate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.DefaulterDataList
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.FomApiRepository
import com.app.ecarepro.data.network.model.DefaulterFilters
import com.app.ecarepro.data.network.model.EstimateModule
import com.app.ecarepro.utils.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EstimateFeeReportViewModel @Inject constructor(
    private val fomApiRepository: FomApiRepository,
    private val userDataStore: UserDataStore,
    ) : ViewModel() {

    private val defaultFilterMutableStateFlow: MutableStateFlow<NetworkResult<DefaulterFilters>> =
        MutableStateFlow(
        NetworkResult.Loading())
    val defaultFilterStateFlow: StateFlow<NetworkResult<DefaulterFilters>> = defaultFilterMutableStateFlow


    private val estimateDataMutableStateFlow: MutableStateFlow<NetworkResult<List<EstimateModule>>> = MutableStateFlow(NetworkResult.Loading())
    val estimateDataStateFlow: StateFlow<NetworkResult<List<EstimateModule>>> = estimateDataMutableStateFlow

    fun defaulterFilters(
    )=viewModelScope.launch {
        runCatching {
            defaultFilterMutableStateFlow.value = NetworkResult.Loading()
            fomApiRepository.defaulterFilters(
                Constant.REPORT_BASE_URL+"api/estimated?senderid="+userDataStore.getSchoolData()?.schoolCode)
              //  userDataStore.getSchoolData()?.feePayemtURL!!.replace("mlogin.aspx", "")+"api/estimated?senderid="+userDataStore.getSchoolData()?.schoolCode)
        }.onSuccess {
            defaultFilterMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            defaultFilterMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }




    fun getEstimateReport(
        DateFrom : String,
        DateTo : String,
        schoolid : String,
        feetypeid : String,
        classid : String,
        sectionid : String,
        installid : String,
    )=viewModelScope.launch {
        runCatching {
            estimateDataMutableStateFlow.value = NetworkResult.Loading()
            fomApiRepository.getEstimateReport(
               // userDataStore.getSchoolData()?.feePayemtURL!!.replace("mlogin.aspx", "")+"api/estimated",
                "https://pay.franciscanwebsolutions.com/api/estimated",
                userDataStore.getSchoolData()!!.schoolCode,
                DateFrom, DateTo, schoolid, feetypeid, classid, sectionid, installid )
        }.onSuccess {
            estimateDataMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            estimateDataMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }

}
package com.app.ecarepro.ui.fee_report.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.NetworkFeeCollection
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.FomApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionFeeReportViewModel @Inject constructor(
    private val fomApiRepository: FomApiRepository,
    private val userDataStore: UserDataStore,
    ) : ViewModel() {




    private val feeCollectionMutableStateFlow: MutableStateFlow<NetworkResult<NetworkFeeCollection>> = MutableStateFlow(
        NetworkResult.Loading())
    val feeCollectionStateFlow: StateFlow<NetworkResult<NetworkFeeCollection>> = feeCollectionMutableStateFlow



    fun feeCollectionReport(
        dateFrom : String,
        dateTo : String,
    )=viewModelScope.launch {
        runCatching {
            feeCollectionMutableStateFlow.value = NetworkResult.Loading()
            fomApiRepository.feeCollectionReport(
                userDataStore.getSchoolData()?.feePayemtURL!!.replace("mlogin.aspx", "")+"api/Collection",
                userDataStore.getSchoolData()!!.schoolCode,
                dateFrom,
                dateTo)
        }.onSuccess {
            feeCollectionMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            feeCollectionMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }



}
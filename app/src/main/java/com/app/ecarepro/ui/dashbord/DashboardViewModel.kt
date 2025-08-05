package com.app.ecarepro.ui.dashbord

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.FeeCollection
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.app.ecarepro.data.network.model.CollectionModeWise
import com.app.ecarepro.data.network.model.NetworkFeeDefaulter
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.ui.dashbord.model.ModeWiseCollection
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsConstants
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsManager
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HiltViewModel
class DashboardViewModel @Inject constructor(
    userDataStore: UserDataStore,
    private val userRepository: UserRepository,
    private val analyticsManager: AnalyticsManager
) : ViewModel() {
    val feeCollection = MutableStateFlow<FeeCollection?>(null)
    val feeDefaulter = MutableStateFlow<NetworkFeeDefaulter?>(null)
    private val modelWiseCollection = MutableStateFlow<ModeWiseCollection?>(null)

    val dashboard = combine(
        flow = userDataStore.getDashboardData(),
        flow2 = userDataStore.getFeeds(),
        flow3 = feeCollection,
        flow4 = modelWiseCollection,
        flow5 = feeDefaulter
    ) { dashboardData, feeds, feeCollection, modelWiseColl,feeDefaulter ->

        var data = dashboardData

        if (feeCollection != null) {
            Log.e("Dashboard Fee 1", data?.feeCollection.toString())
            if (data?.feeCollection!=null){
                data = dashboardData?.copy(
                    feeCollection = feeCollection.copy()
                )
                Log.e("Dashboard Fee 2", data?.feeCollection.toString())
            }

        }
        if (feeDefaulter != null) {
            Log.e("Dashboard Fee 3", data?.feeDafaulter.toString())
            if (data?.feeDafaulter!=null)
            data = dashboardData?.copy(
                feeDafaulter = feeDefaulter.copy()
            )
            Log.e("Dashboard Fee 4", data?.feeCollection.toString())
        }


        if (modelWiseColl != null) {
            if (modelWiseColl.transactionDetails!=null){
                Log.e("Dashboard Data 1", data?.collectionModeWise.toString())
                data = dashboardData?.copy(collectionModeWise = CollectionModeWise(modelWiseColl.transactionDetails))
            }

        }
        Log.e("Dashboard Data", data?.feeCollection.toString())

        data
    }
    init {
        getFeeCollection()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        getTodayModeWiseCollection(sdf.format(Date())) { _, _ ->
        }
        getFeeDefaulters(0, 0.toString())
    }



    fun getFeeDefaulters(
        feeTypeId: Int?,
        installIds: String?,
        onResponse: ((Boolean, String?) -> Unit)? = null
    ) {
        viewModelScope.launch {
            userRepository.getFeeDefaultersDas(feeTypeId, installIds).collectLatest { result ->
                if (result.isSuccess) {
                    feeDefaulter.update {
                        result.getOrNull()
                    }
                }
                onResponse?.invoke(result.isSuccess, result.exceptionOrNull()?.message)
            }
        }
    }

    fun getFeeCollection(
        feeTypeId: Int? = null,
        fromDate: String? = null,
        tillDate: String? = null,
        onResponse: ((Boolean, String?) -> Unit)? = null
    ) {
        viewModelScope.launch {
            userRepository.feeCollection(feeTypeId, fromDate, tillDate).collectLatest {
                if (it.isSuccess) {
                    feeCollection.value = it.getOrNull()
                }
                onResponse?.invoke(it.isSuccess, it.exceptionOrNull()?.message)
            }
        }
    }

    fun getTodayModeWiseCollection(
        date: String,
        onResponse: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            userRepository.todayModeWiseCollection(date).collectLatest {
                if(it.isSuccess){
                    modelWiseCollection.value = it.getOrNull()
                }
                onResponse.invoke(it.isSuccess, it.exceptionOrNull()?.message)
            }
        }
    }

    fun sendScreenEvent(){
        analyticsManager.trackScreen(AnalyticsConstants.Screens.DASH_BOARD_SCREEN)
    }
    fun sendAnalyticEvent(
        event: String,
        attributes: Map<String, String>
    ) {
        analyticsManager.trackEvent(
            event,
            attributes
        )
    }
}
package com.app.ecarepro.ui.dashbord

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
import com.app.ecarepro.ui.dashbord.model.ModeWiseCollection
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsConstants
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsManager
import kotlinx.coroutines.flow.update

@HiltViewModel
class DashboardViewModel @Inject constructor(
    userDataStore: UserDataStore,
    private val userRepository: UserRepository,
    private val analyticsManager: AnalyticsManager
) : ViewModel() {
    private val feeCollection = MutableStateFlow<FeeCollection?>(null)
    private val modelWiseCollection = MutableStateFlow<ModeWiseCollection?>(null)

    val dashboard = combine(
        flow = userDataStore.getDashboardData(),
        flow2 = userDataStore.getFeeds(),
        flow3 = feeCollection,
        flow4 = modelWiseCollection
    ) { dashboardData, feeds, feeCollection, modelWiseColl ->

        var data = dashboardData

        if (feeCollection != null) {
            data = dashboardData?.copy(feeCollection = feeCollection)
        }

        if (modelWiseColl != null) {
            data = dashboardData?.copy(collectionModeWise = CollectionModeWise(modelWiseColl.transactionDetails))
        }


        data
    }
    fun getFeeCollection(
        feeTypeId: Int,
        fromDate: String,
        tillDate: String,
        onResponse: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            userRepository.feeCollection(feeTypeId, fromDate, tillDate).collectLatest {
                if (it.isSuccess) {
                    feeCollection.value = it.getOrNull()
                }
                onResponse.invoke(it.isSuccess, it.exceptionOrNull()?.message)
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
}
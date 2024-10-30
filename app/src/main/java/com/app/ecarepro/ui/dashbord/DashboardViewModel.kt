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

@HiltViewModel
class DashboardViewModel @Inject constructor(
    userDataStore: UserDataStore,
    private val userRepository: UserRepository
) : ViewModel() {
    private val feeCollection = MutableStateFlow<FeeCollection?>(null)
    val dashboard = combine(
        flow = userDataStore.getDashboardData(),
        flow2 = userDataStore.getFeeds(),
        flow3 = feeCollection
    ) { dashboardData, feeds, feeCollection ->
        Triple(dashboardData, feeds, feeCollection)
    }
    fun getFeeCollection(
        feeTypeId: Int,
        fromDate: String,
        tillDate: String,
        onResponse: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            userRepository.feeCollection(feeTypeId, fromDate, tillDate).collectLatest {
                onResponse.invoke(it.isSuccess, it.exceptionOrNull()?.message)
            }
        }
    }
}
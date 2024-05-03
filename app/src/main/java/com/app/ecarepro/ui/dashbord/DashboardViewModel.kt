package com.app.ecarepro.ui.dashbord

import androidx.lifecycle.ViewModel
import com.app.ecarepro.data.datastore.UserDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.combine

@HiltViewModel
class DashboardViewModel @Inject constructor(
    userDataStore: UserDataStore
) : ViewModel() {
    val dashboard = combine(
        flow = userDataStore.getDashboardData(),
        flow2 = userDataStore.getFeeds()
    ) { dashboardData, feeds ->
        Pair(dashboardData, feeds)
    }
}
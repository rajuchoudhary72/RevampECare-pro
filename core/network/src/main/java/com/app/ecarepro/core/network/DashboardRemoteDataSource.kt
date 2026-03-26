package com.app.ecarepro.core.network

import com.app.ecarepro.core.network.model.dashboard.NetworkDashFeedResponse
import com.app.ecarepro.core.network.model.dashboard.NetworkDashboardResponse
import com.app.ecarepro.core.network.model.dashboard.NetworkFeeCollectionResponse
import com.app.ecarepro.core.network.model.dashboard.NetworkModeWiseCollectionResponse

interface DashboardRemoteDataSource {
    suspend fun getDashboard(): NetworkDashboardResponse
    suspend fun getFeeCollection(feeTypeId: Int, fromDate: String, tillDate: String): NetworkFeeCollectionResponse
    suspend fun getModeWiseCollection(collectionDate: String): NetworkModeWiseCollectionResponse
    suspend fun getFeed(): NetworkDashFeedResponse
}

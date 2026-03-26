package com.app.ecarepro.core.network.datasource

import com.app.ecarepro.core.network.DashboardRemoteDataSource
import com.app.ecarepro.core.network.model.dashboard.NetworkDashFeedResponse
import com.app.ecarepro.core.network.model.dashboard.NetworkDashboardResponse
import com.app.ecarepro.core.network.model.dashboard.NetworkFeeCollectionResponse
import com.app.ecarepro.core.network.model.dashboard.NetworkModeWiseCollectionResponse
import com.app.ecarepro.core.network.retrofit.service.DashboardService
import javax.inject.Inject

internal class DashboardRemoteDataSourceImpl @Inject constructor(
    private val service: DashboardService,
) : DashboardRemoteDataSource {
    override suspend fun getDashboard(): NetworkDashboardResponse = service.getDashboard()
    override suspend fun getFeeCollection(feeTypeId: Int, fromDate: String, tillDate: String): NetworkFeeCollectionResponse =
        service.getFeeCollection(feeTypeId, fromDate, tillDate)
    override suspend fun getModeWiseCollection(collectionDate: String): NetworkModeWiseCollectionResponse =
        service.getModeWiseCollection(collectionDate)
    override suspend fun getFeed(): NetworkDashFeedResponse = service.getFeed()
}

package com.app.ecarepro.core.network.retrofit.service

import com.app.ecarepro.core.network.model.dashboard.NetworkDashFeedResponse
import com.app.ecarepro.core.network.model.dashboard.NetworkDashboardResponse
import com.app.ecarepro.core.network.model.dashboard.NetworkFeeCollectionResponse
import com.app.ecarepro.core.network.model.dashboard.NetworkModeWiseCollectionResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DashboardService {
    @GET("User/AppDashboard")
    suspend fun getDashboard(): NetworkDashboardResponse

    @GET("Report/FeeCollection")
    suspend fun getFeeCollection(
        @Query("FeeTypeId") feeTypeId: Int,
        @Query("FromDate") fromDate: String,
        @Query("TillDate") tillDate: String,
    ): NetworkFeeCollectionResponse

    @GET("Report/ModeWiseCollection")
    suspend fun getModeWiseCollection(
        @Query("CollectionDate") collectionDate: String,
    ): NetworkModeWiseCollectionResponse

    @GET("School/Feed")
    suspend fun getFeed(): NetworkDashFeedResponse
}

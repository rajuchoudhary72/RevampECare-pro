package com.app.ecarepro.data.network.service

import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.DefaulterDataList
import com.app.ecarepro.data.network.model.NetworkFeeCollection
import com.app.ecarepro.data.network.model.post_fee_collection.FeeCollectionBody
import com.app.ecarepro.data.network.model.DefaulterFilters
import com.app.ecarepro.data.network.model.NetworkFeeReceipt
import com.app.ecarepro.data.network.model.create_fee_request.FeeReceiptRequest
import com.app.ecarepro.data.network.model.post_default_report.DefaultReportBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface FomApiService {

    @GET
    suspend fun approveAppointment(
        @Url url: String
     ): CommonResponse

    @POST
    suspend fun feeCollectionReport(
        @Url url: String,
        @Body request: FeeCollectionBody
    ): NetworkFeeCollection

    @GET
    suspend fun defaulterFilters(
        @Url url: String
    ): DefaulterFilters

    @GET
    suspend fun getDefaulterReport(
        @Url url: String,
        @Body request: DefaultReportBody
    ): List<DefaulterDataList>

    @GET
    suspend fun getFeeReceipt(
        @Url url: String,
        @Body request: FeeReceiptRequest
    ): NetworkFeeReceipt




}
package com.app.ecarepro.core.network.retrofit.service

import com.app.ecarepro.core.network.model.fee.FeeCertificateRequest
import com.app.ecarepro.core.network.model.fee.NetworkDefaulterItem
import com.app.ecarepro.core.network.model.fee.NetworkEstimateItem
import com.app.ecarepro.core.network.model.fee.NetworkFeeCertificateResponse
import com.app.ecarepro.core.network.model.fee.NetworkFeeCollectionItem
import com.app.ecarepro.core.network.model.fee.NetworkFeeCollectionRequest
import com.app.ecarepro.core.network.model.fee.NetworkFeeReceiptRequest
import com.app.ecarepro.core.network.model.fee.NetworkFeeReceiptResponse
import com.app.ecarepro.core.network.model.fee.NetworkFeeReportFilterResponse
import com.app.ecarepro.core.network.model.fee.NetworkFeeReportRequest
import com.app.ecarepro.core.network.model.fee.NetworkReceiptDownloadRequest
import com.app.ecarepro.core.network.model.fee.NetworkReceiptDownloadResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface FeeService {

    @POST("api/feereceipt")
    suspend fun getReceipts(@Body request: NetworkFeeReceiptRequest): NetworkFeeReceiptResponse

    @POST("api/receiptdownload")
    suspend fun downloadReceipt(@Body request: NetworkReceiptDownloadRequest): NetworkReceiptDownloadResponse

    @POST("api/Collection")
    suspend fun getCollections(@Body request: NetworkFeeCollectionRequest): List<NetworkFeeCollectionItem>

    @GET("api/defaulter")
    suspend fun getReportFilters(@Query("senderid") senderId: String): NetworkFeeReportFilterResponse

    @POST("api/defaulter")
    suspend fun getDefaulterReport(@Body request: NetworkFeeReportRequest): List<NetworkDefaulterItem>

    @POST("api/estimated")
    suspend fun getEstimateReport(@Body request: NetworkFeeReportRequest): List<NetworkEstimateItem>

    @POST("api/certificate")
    suspend fun getCertificate(@Body request: FeeCertificateRequest): NetworkFeeCertificateResponse
}

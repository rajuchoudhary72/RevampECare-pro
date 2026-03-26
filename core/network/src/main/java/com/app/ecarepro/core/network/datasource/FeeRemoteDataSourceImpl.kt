package com.app.ecarepro.core.network.datasource

import com.app.ecarepro.core.network.FeeRemoteDataSource
import com.app.ecarepro.core.network.model.fee.FeeCertificateRequest
import com.app.ecarepro.core.network.model.fee.NetworkDefaulterItem
import com.app.ecarepro.core.network.model.fee.NetworkEstimateItem
import com.app.ecarepro.core.network.model.fee.NetworkFeeCollectionItem
import com.app.ecarepro.core.network.model.fee.NetworkFeeCollectionRequest
import com.app.ecarepro.core.network.model.fee.NetworkFeeReceiptRequest
import com.app.ecarepro.core.network.model.fee.NetworkFeeReceiptResponse
import com.app.ecarepro.core.network.model.fee.NetworkFeeReportFilterResponse
import com.app.ecarepro.core.network.model.fee.NetworkFeeReportRequest
import com.app.ecarepro.core.network.model.fee.NetworkReceiptDownloadRequest
import com.app.ecarepro.core.network.model.fee.NetworkReceiptDownloadResponse
import com.app.ecarepro.core.network.retrofit.service.FeeService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class FeeRemoteDataSourceImpl @Inject constructor(
    private val networkJson: Json,
) : FeeRemoteDataSource {

    private val serviceCache = mutableMapOf<String, FeeService>()

    private fun getService(baseUrl: String): FeeService {
        return serviceCache.getOrPut(baseUrl) {
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(
                    HttpLoggingInterceptor().apply {
                        setLevel(HttpLoggingInterceptor.Level.BODY)
                    }
                )
                .build()
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(networkJson.asConverterFactory("application/json".toMediaType()))
                .client(okHttpClient)
                .build()
                .create(FeeService::class.java)
        }
    }

    override suspend fun getReceipts(
        baseUrl: String,
        senderId: String,
        username: String,
        sessionId: Int,
    ): NetworkFeeReceiptResponse {
        return getService(baseUrl).getReceipts(
            NetworkFeeReceiptRequest(
                senderid = senderId,
                username = username,
                sessionid = sessionId,
            )
        )
    }

    override suspend fun downloadReceipt(
        baseUrl: String,
        senderId: String,
        recId: String,
        sessionId: Int,
        stId: Int,
    ): NetworkReceiptDownloadResponse {
        return getService(baseUrl).downloadReceipt(
            NetworkReceiptDownloadRequest(
                senderid = senderId,
                recid = recId,
                sessionid = sessionId,
                stid = stId,
            )
        )
    }

    override suspend fun getCollections(
        baseUrl: String,
        dateFrom: String,
        dateTo: String,
        senderId: String,
    ): List<NetworkFeeCollectionItem> {
        return getService(baseUrl).getCollections(
            NetworkFeeCollectionRequest(
                dateFrom = dateFrom,
                dateTo = dateTo,
                senderid = senderId,
            )
        )
    }

    override suspend fun getReportFilters(
        baseUrl: String,
        senderId: String,
    ): NetworkFeeReportFilterResponse {
        return getService(baseUrl).getReportFilters(senderId)
    }

    override suspend fun getDefaulterReport(
        baseUrl: String,
        request: NetworkFeeReportRequest,
    ): List<NetworkDefaulterItem> {
        return getService(baseUrl).getDefaulterReport(request)
    }

    override suspend fun getEstimateReport(
        baseUrl: String,
        request: NetworkFeeReportRequest,
    ): List<NetworkEstimateItem> {
        return getService(baseUrl).getEstimateReport(request)
    }

    override suspend fun getCertificate(
        baseUrl: String,
        senderId: String,
        sessionId: Int,
        parentName: String,
        stid: String,
        sessionName: String,
    ): String? {
        return getService(baseUrl).getCertificate(
            FeeCertificateRequest(
                senderId = senderId,
                sessionId = sessionId,
                parentName = parentName,
                stid = stid,
                sessionName = sessionName,
            )
        ).bytedata
    }
}

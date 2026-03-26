package com.app.ecarepro.core.network

import com.app.ecarepro.core.network.model.fee.NetworkDefaulterItem
import com.app.ecarepro.core.network.model.fee.NetworkEstimateItem
import com.app.ecarepro.core.network.model.fee.NetworkFeeCollectionItem
import com.app.ecarepro.core.network.model.fee.NetworkFeeReceiptResponse
import com.app.ecarepro.core.network.model.fee.NetworkFeeReportFilterResponse
import com.app.ecarepro.core.network.model.fee.NetworkFeeReportRequest
import com.app.ecarepro.core.network.model.fee.NetworkReceiptDownloadResponse

interface FeeRemoteDataSource {
    suspend fun getReceipts(
        baseUrl: String,
        senderId: String,
        username: String,
        sessionId: Int,
    ): NetworkFeeReceiptResponse

    suspend fun downloadReceipt(
        baseUrl: String,
        senderId: String,
        recId: String,
        sessionId: Int,
        stId: Int,
    ): NetworkReceiptDownloadResponse

    suspend fun getCollections(
        baseUrl: String,
        dateFrom: String,
        dateTo: String,
        senderId: String,
    ): List<NetworkFeeCollectionItem>

    suspend fun getReportFilters(
        baseUrl: String,
        senderId: String,
    ): NetworkFeeReportFilterResponse

    suspend fun getDefaulterReport(
        baseUrl: String,
        request: NetworkFeeReportRequest,
    ): List<NetworkDefaulterItem>

    suspend fun getEstimateReport(
        baseUrl: String,
        request: NetworkFeeReportRequest,
    ): List<NetworkEstimateItem>

    suspend fun getCertificate(
        baseUrl: String,
        senderId: String,
        sessionId: Int,
        parentName: String,
        stid: String,
        sessionName: String,
    ): String?
}

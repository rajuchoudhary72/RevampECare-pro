package com.app.ecarepro.data

import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.DefaulterDataList
import com.app.ecarepro.data.network.model.NetworkFeeCollection
import com.app.ecarepro.data.network.model.post_fee_collection.FeeCollectionBody
import com.app.ecarepro.data.network.service.FomApiService
import com.app.ecarepro.data.repository.FomApiRepository
import com.app.ecarepro.data.network.model.DefaulterFilters
import com.app.ecarepro.data.network.model.NetworkFeeReceipt
import com.app.ecarepro.data.network.model.create_fee_request.FeeReceiptRequest
import com.app.ecarepro.data.network.model.post_default_report.DefaultReportBody
import javax.inject.Inject

class FomApiRepositoryImpl @Inject constructor(
    private val fomApiService: FomApiService
) : FomApiRepository  {
    override suspend fun approveAppointment(url: String): CommonResponse {
        return fomApiService.approveAppointment(url)
    }

    override suspend fun feeCollectionReport(
        url: String,
        senderId: String,
        dateFrom: String,
        dateTo: String
    ): NetworkFeeCollection {
        return fomApiService.feeCollectionReport(url, FeeCollectionBody(senderId,dateFrom,dateTo))
    }

    override suspend fun defaulterFilters(url: String): DefaulterFilters {
        return fomApiService.defaulterFilters(url)
    }

    override suspend fun getDefaulterReport(
        url: String,
        senderid: String,
        DateFrom: String,
        DateTo: String,
        schoolid: String,
        feetypeid: String,
        classid: String,
        sectionid: String,
        installid: String
    ): List<DefaulterDataList> {
        return fomApiService.getDefaulterReport(url, DefaultReportBody(senderid, DateFrom, DateTo, schoolid, feetypeid, classid, sectionid, installid))
    }

    override suspend fun getFeeReceipt(url: String,request: FeeReceiptRequest): NetworkFeeReceipt {
        return fomApiService.getFeeReceipt(url,request)
    }


}
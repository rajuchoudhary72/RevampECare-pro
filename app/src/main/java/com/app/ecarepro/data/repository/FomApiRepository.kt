package com.app.ecarepro.data.repository

import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.DefaulterDataList
import com.app.ecarepro.data.network.model.NetworkFeeCollection
import com.app.ecarepro.data.network.model.DefaulterFilters
import com.app.ecarepro.data.network.model.NetworkFeeReceipt
import com.app.ecarepro.data.network.model.create_fee_request.FeeReceiptRequest
import com.app.ecarepro.data.network.model.post_default_report.DefaultReportBody
import retrofit2.http.Body
import retrofit2.http.Url

interface FomApiRepository {
    suspend   fun approveAppointment(
          url: String
    ): CommonResponse

    suspend fun feeCollectionReport(
         url: String,
         senderId : String,
         dateFrom : String,
         dateTo : String,
    ): NetworkFeeCollection

    suspend fun defaulterFilters(
         url: String
    ): DefaulterFilters

    suspend fun getDefaulterReport(
        url: String,
        senderid : String,
        DateFrom : String,
        DateTo : String,
        schoolid : String,
        feetypeid : String,
        classid : String,
        sectionid : String,
        installid : String,
    ): List<DefaulterDataList>

    suspend fun getFeeReceipt(
          url: String,
          request: FeeReceiptRequest
    ): NetworkFeeReceipt

}
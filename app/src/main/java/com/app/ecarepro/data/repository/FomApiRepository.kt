package com.app.ecarepro.data.repository

import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.DefaulterDataList
import com.app.ecarepro.data.network.model.NetworkFeeCollection
import com.app.ecarepro.data.network.model.DefaulterFilters
import com.app.ecarepro.data.network.model.EstimateModule
import com.app.ecarepro.data.network.model.FeeBookDownloadRequestModel
import com.app.ecarepro.data.network.model.FeeBookModel
import com.app.ecarepro.data.network.model.NetworkFeeBook
import com.app.ecarepro.data.network.model.NetworkFeeCerDownload
import com.app.ecarepro.data.network.model.NetworkFeeCerfResponse
import com.app.ecarepro.data.network.model.NetworkFeeReceipt
import com.app.ecarepro.data.network.model.PostCertf.PostDataFeeCertificate
import com.app.ecarepro.data.network.model.create_fee_request.FeeReceiptDownloadRequest
import com.app.ecarepro.data.network.model.create_fee_request.FeeReceiptRequest
import com.app.ecarepro.data.network.model.post_default_report.DefaultReportBody
import com.app.ecarepro.model.FeeCertificateList
import com.app.ecarepro.ui.fom_guard.model.FomGuardAppointments
import com.app.ecarepro.ui.fom_guard.model.verifiy_number.VerifyPhone
import com.app.ecarepro.ui.fom_guard.model.verify_code.NetworkVerifyCode
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Url
import com.app.ecarepro.model.CollectionReport
import retrofit2.http.Field

interface FomApiRepository {
    suspend   fun approveAppointment(
          url: String
    ): CommonResponse

    suspend fun feeCollectionReport(
         url: String,
         senderId : String,
         dateFrom : String,
         dateTo : String,
    ): List<CollectionReport>


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

    suspend fun getEstimateReport(
        url: String,
        senderid : String,
        DateFrom : String,
        DateTo : String,
        schoolid : String,
        feetypeid : String,
        classid : String,
        sectionid : String,
        installid : String,
    ): List<EstimateModule>


    suspend fun getFeeReceipt(
          url: String,
          request: FeeReceiptRequest
    ): NetworkFeeReceipt

    suspend fun getFeeCertificate(
      url: String
    ): FeeCertificateList

    suspend fun getFeeReceiptDownload (
         url: String,
          request: FeeReceiptDownloadRequest
    ): NetworkFeeCerDownload

    suspend fun getFeeCertificateDownload (
          url: String,
          request: PostDataFeeCertificate
    ): NetworkFeeCerDownload

    suspend fun getFomGuardAppointments(
         url: String
    ): FomGuardAppointments

    suspend fun updateappointmentcheckout (
          url: String
    ): CommonResponse

    suspend fun updateappointmentCheckInTime (
          url: String
    ): NetworkVerifyCode

    suspend fun getuserdetailsfrommobile (
          url: String
    ): VerifyPhone

    suspend fun getFeeBookReportList(
        url: String,
         ParentName: String?,
        stid: String?,
         schoolcode: String
    ): NetworkFeeBook

    suspend fun getFeeBookDownload (
        url: String,
        Billsetting: String?,
         schoolcode: String,
         installid: String,
     stid: String,
        yrid: String,
    ): NetworkFeeCerDownload

}
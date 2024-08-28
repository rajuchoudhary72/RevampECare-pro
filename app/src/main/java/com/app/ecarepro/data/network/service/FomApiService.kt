package com.app.ecarepro.data.network.service

import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.DefaulterDataList
import com.app.ecarepro.data.network.model.NetworkFeeCollection
import com.app.ecarepro.data.network.model.post_fee_collection.FeeCollectionBody
import com.app.ecarepro.data.network.model.DefaulterFilters
import com.app.ecarepro.data.network.model.NetworkFeeCerDownload
import com.app.ecarepro.data.network.model.NetworkFeeCerfResponse
import com.app.ecarepro.data.network.model.NetworkFeeReceipt
import com.app.ecarepro.data.network.model.PostCertf.PostDataFeeCertificate
import com.app.ecarepro.data.network.model.create_fee_request.FeeReceiptRequest
import com.app.ecarepro.data.network.model.post_default_report.DefaultReportBody
import com.app.ecarepro.model.FeeCertificateList
import com.app.ecarepro.ui.fom_guard.model.FomGuardAppointments
import com.app.ecarepro.ui.fom_guard.model.verifiy_number.VerifyPhone
import com.app.ecarepro.ui.fom_guard.model.verify_code.NetworkVerifyCode
import okhttp3.ResponseBody
import retrofit2.Call
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

    @POST
    suspend fun getFeeReceipt(
        @Url url: String,
        @Body request: FeeReceiptRequest
    ): NetworkFeeReceipt

    @GET
    suspend fun getFeeCertificate(
        @Url url: String
    ): FeeCertificateList

    @POST
    suspend fun getFeeCertificateDownload (
        @Url url: String,
        @Body request: PostDataFeeCertificate
    ): NetworkFeeCerDownload


    @GET
    suspend fun getFomGuardAppointments(
        @Url url: String
    ): FomGuardAppointments

    @POST
    suspend fun updateappointmentcheckout (
        @Url url: String
    ): CommonResponse

    @POST
    suspend fun updateappointmentCheckInTime (
        @Url url: String
    ): NetworkVerifyCode

    @POST
    suspend fun getuserdetailsfrommobile (
        @Url url: String
    ): VerifyPhone


}
package com.app.ecarepro.core.network.retrofit.service

import com.app.ecarepro.core.network.model.sms.NetworkSMSConsumptionResponse
import com.app.ecarepro.core.network.model.sms.NetworkSmsStudents
import retrofit2.http.GET
import retrofit2.http.Query

interface SmsService {
    @GET("SMS/StudentParent_Comms")
    suspend fun getStudents(
        @Query("RecipientType") teacherId: String = "1",
        @Query("ClassIDs") classId: String,
        @Query("ScholarType") scholarType: String = "2",
    ): NetworkSmsStudents

    @GET("School/SMSConsumption")
    suspend fun getSMSConsumption(
        @Query("FromDate") fromDate: String,
        @Query("ToDate") toDate: String,
    ): NetworkSMSConsumptionResponse
}
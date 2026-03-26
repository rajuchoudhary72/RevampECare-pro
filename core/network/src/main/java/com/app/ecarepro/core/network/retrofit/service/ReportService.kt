package com.app.ecarepro.core.network.retrofit.service

import com.app.ecarepro.core.network.model.birthday.NetworkBirthdayResponse
import com.app.ecarepro.core.network.model.classteacher.NetworkClassTeacherResponse
import com.app.ecarepro.core.network.model.smsreport.NetworkSMSReportResponse
import com.app.ecarepro.core.network.model.smsreport.NetworkSMSTypeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ReportService {
    @GET("Report/SMSType")
    suspend fun getSMSTypes(): NetworkSMSTypeResponse

    @GET("Report/SMSReport")
    suspend fun getSMSReport(
        @Query("FromDate") fromDate: String,
        @Query("TillDate") tillDate: String,
        @Query("SMSType") smsType: Int,
    ): NetworkSMSReportResponse

    @GET("Report/Birthday")
    suspend fun getBirthdays(
        @Query("UserType") userType: String,
        @Query("RptType") rptType: Int,
        @Query("MonthNo") monthNo: Int,
        @Query("Date") date: String? = null,
    ): NetworkBirthdayResponse

    @GET("Report/Classteacher")
    suspend fun getClassTeachers(): NetworkClassTeacherResponse
}

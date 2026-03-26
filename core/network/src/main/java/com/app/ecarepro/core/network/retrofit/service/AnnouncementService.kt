package com.app.ecarepro.core.network.retrofit.service

import com.app.ecarepro.core.network.model.announcement.NetworkCreateCircularResponse
import com.app.ecarepro.core.network.model.announcement.NetworkGetCircularDetail
import com.app.ecarepro.core.network.model.announcement.NetworkGetCirculars
import com.app.ecarepro.core.network.model.announcement.NetworkGetNoticeDetail
import com.app.ecarepro.core.network.model.announcement.NetworkGetNotices
import com.app.ecarepro.core.network.model.announcement.NetworkSaveCircularRequest
import com.app.ecarepro.core.network.model.announcement.NetworkSaveCircularResponse
import com.app.ecarepro.core.network.model.announcement.NetworkStaffContactResponse
import com.app.ecarepro.core.network.model.announcement.NetworkStudentParentContactResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AnnouncementService {

    @GET("School/Notices")
    suspend fun getNotices(
        @Query("pg") page: Int = 1,
        @Query("isStaffNotice") isStaffNotice: Boolean? = null,
        @Query("ClassID") classId: Int? = null,
    ): NetworkGetNotices

    @GET("School/NoticeDTL")
    suspend fun getNoticeDetail(
        @Query("ID") id: String,
    ): NetworkGetNoticeDetail

    @GET("School/Circulars")
    suspend fun getCirculars(
        @Query("pg") page: Int = 1,
        @Query("title") title: String = "",
        @Query("date") date: String = "",
        @Query("YrID") yrId: Int = 0,
    ): NetworkGetCirculars

    @GET("School/CircularDTL")
    suspend fun getCircularDetail(
        @Query("ID") id: String,
    ): NetworkGetCircularDetail

    @GET("Admin/CreateCircular")
    suspend fun getCreateCircularData(): NetworkCreateCircularResponse

    @GET("Message/StaffContact")
    suspend fun getStaffContacts(
        @Query("StaffTypeIDs") staffTypeIds: String,
    ): NetworkStaffContactResponse

    @GET("Message/StudentParentContact")
    suspend fun getStudentParentContacts(
        @Query("OfUserType") ofUserType: Int = 1,
        @Query("ClassIDs") classIds: String,
        @Query("ScholarType") scholarType: Int,
    ): NetworkStudentParentContactResponse

    @POST("Admin/SaveCircular")
    suspend fun saveCircular(
        @Body request: NetworkSaveCircularRequest,
    ): NetworkSaveCircularResponse
}

package com.app.ecarepro.data.network.service

import com.app.ecarepro.data.network.model.NetworkCircular
import com.app.ecarepro.data.network.model.NetworkCircularDetails
import com.app.ecarepro.data.network.model.NetworkNoticDetails
import com.app.ecarepro.data.network.model.NetworkNotice
import com.app.ecarepro.data.network.model.NetworkSchool
import com.app.ecarepro.data.network.model.NetworkSchoolsDto
import com.app.ecarepro.data.network.model.NetworkWalkThrough
import com.app.ecarepro.model.AppResponse
import com.app.ecarepro.model.ClassPromotionModel
import com.app.ecarepro.model.PromotionModel
import com.app.ecarepro.model.RequestClassPromotion
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface SchoolService {
    @GET("School/WalkThrough")
    suspend fun getWalkThroughData(): NetworkWalkThrough

    @GET("School/DTL")
    suspend fun validateSchoolCode(@Query("SchCode") schoolCode: String): NetworkSchool

    @GET("School/DTL")
    suspend fun getSchoolDetails(@Query("SchCode") schoolCode: String): NetworkSchool

    @GET("School/List")
    suspend fun getSchools(): NetworkSchoolsDto

    @GET("School/Notices")
    suspend fun getNotices(
        @Query("pg") pg: Int,
        @Query("ClassID") classID: Int,
    ): NetworkNotice

    @GET("School/Circulars")
    suspend fun getCirculars(
        @Query("pg") pg: Int,
        @Query("YrID") yrID: Int,
        @Query("title") title: String
    ): NetworkCircular

    @GET("School/NoticeDTL")
    suspend fun getNoticeDTL(
        @Query("NtID") ntID: Int,
        @Query("ID") iD: Int,
    ): NetworkNoticDetails

    @GET("School/CircularDTL")
    suspend fun getCircularDTL(
        @Query("CirID") cirID: Int,
        @Query("ID") iD: Int,
    ): NetworkCircularDetails


    @GET("Staff/ClassTeacherOf")
    suspend fun getClassTeacherOf(): ClassPromotionModel

    @GET("Student/ClassPromotion")
    suspend fun getClassPromotion(@Query("ClassId") classId: String): PromotionModel


    @POST("Student/SaveClassPromotion")
    suspend fun saveClassPromotion(@Body body: RequestClassPromotion): AppResponse
}
package com.app.ecarepro.data.network.service

import com.app.ecarepro.data.network.model.NetworkNoticDetails
import com.app.ecarepro.data.network.model.NetworkNotice
import com.app.ecarepro.data.network.model.NetworkSchool
import com.app.ecarepro.data.network.model.NetworkSchoolsDto
import com.app.ecarepro.data.network.model.NetworkWalkThrough
import retrofit2.http.GET
import retrofit2.http.Query

interface SchoolService {
    @GET("School/WalkThrough")
    suspend fun getWalkThroughData(): NetworkWalkThrough

    @GET("School/DTL")
    suspend fun validateSchoolCode(@Query("SchCode") schoolCode: String): NetworkSchool

    @GET("School/List")
    suspend fun getSchools(): NetworkSchoolsDto

    @GET("School/Notices")
    suspend fun getNotices(
        @Query("pg") pg: Int,
        @Query("ClassID") classID: Int,
    ): NetworkNotice

    @GET("School/NoticeDTL")
    suspend fun getNoticeDTL(
        @Query("NtID") ntID: Int,
        @Query("ID")  iD: Int,
    ): NetworkNoticDetails

}
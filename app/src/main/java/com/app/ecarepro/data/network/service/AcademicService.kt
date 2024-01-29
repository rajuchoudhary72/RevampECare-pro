package com.app.ecarepro.data.network.service

import com.app.ecarepro.data.network.model.NetworkActivityCalender
import com.app.ecarepro.data.network.model.NetworkClassSyllabus
import retrofit2.http.GET
import retrofit2.http.Headers

interface AcademicService {

    @Headers("Accept: application/json")
    @GET("Academic/ClassSyllabus")
    suspend fun getClassSyllabus(  ): NetworkClassSyllabus

    @Headers("Accept: application/json")
    @GET("Academic/ActivityCaledar")
    suspend fun getActivityCaledar(  ): NetworkActivityCalender



}
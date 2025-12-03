package com.app.ecarepro.core.network.retrofit.service

import com.app.ecarepro.core.network.model.staff.NetworkClassSection
import com.app.ecarepro.core.network.model.staff.NetworkClassSubjects
import com.app.ecarepro.core.network.model.staff.NetworkClasses
import retrofit2.http.GET
import retrofit2.http.Query

interface StaffService {

    @GET("Staff/MyClass")
    suspend fun getClasses(
        @Query("SubID") subId: Int = 0,
        @Query("OnlyClass") onlyClass: Boolean = true,
    ): NetworkClasses

    @GET("Staff/Sections")
    suspend fun getSections(
        @Query("ClassSTD") classSTD: String,
    ): NetworkClassSection

    @GET("Staff/Subjects")
    suspend fun getSubjects(
        @Query("ClassSTD") classSTD: String,
    ): NetworkClassSubjects

}
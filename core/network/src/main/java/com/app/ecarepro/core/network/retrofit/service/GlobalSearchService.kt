package com.app.ecarepro.core.network.retrofit.service

import com.app.ecarepro.core.network.model.globalsearch.NetworkSearchStaffResponse
import com.app.ecarepro.core.network.model.globalsearch.NetworkSearchStudentResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GlobalSearchService {
    @GET("Report/StudentList")
    suspend fun getStudentList(
        @Query("ScholarType") scholarType: Int = 2,
        @Query("showAll") showAll: Boolean = false,
    ): NetworkSearchStudentResponse

    @GET("Report/StaffList")
    suspend fun getStaffList(): NetworkSearchStaffResponse
}

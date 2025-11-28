package com.app.ecarepro.core.network.retrofit.service

import com.app.ecarepro.core.network.model.CommonNetworkResponse
import com.app.ecarepro.core.network.model.academic.NetworkTeacherTimetable
import com.app.ecarepro.core.network.model.admin.NetworkDeleteSyllabus
import com.app.ecarepro.core.network.model.admin.NetworkGetSyllabuses
import com.app.ecarepro.core.network.model.admin.NetworkSaveSyllabus
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AdminService {

    @GET("Admin/Syllabuses")
    suspend fun getSyllabus(): NetworkGetSyllabuses

    @GET("Admin/DeleteSyllabus")
    suspend fun deleteSyllabus(
        @Query("ID") teacherId: String? = null,
    ): NetworkDeleteSyllabus

    @POST("Admin/SaveSyllabus")
    suspend fun saveSyllabus(
        @Body request: NetworkSaveSyllabus,
    ): CommonNetworkResponse

}
package com.app.ecarepro.core.network.retrofit.service

import com.app.ecarepro.core.network.model.transport.NetworkDropStudentResponse
import com.app.ecarepro.core.network.model.transport.NetworkOutPassReportResponse
import com.app.ecarepro.core.network.model.transport.NetworkPostTransAttRequest
import com.app.ecarepro.core.network.model.transport.NetworkRouteListResponse
import com.app.ecarepro.core.network.model.transport.NetworkStoppageResponse
import com.app.ecarepro.core.network.model.transport.NetworkStudentToMarkTransAttResponse
import com.app.ecarepro.core.network.model.transport.NetworkTransAttReportResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TransportService {

    @GET("Transport/Routes")
    suspend fun getRoutesList(): NetworkRouteListResponse

    @GET("Transport/Stoppage")
    suspend fun getStoppageList(
        @Query("RouteIDs") routeIDs: String,
        @Query("Trip") trip: Int,
    ): NetworkStoppageResponse

    @GET("Transport/StudentToMarkTransAttendane")
    suspend fun getStudentToMarkTransAttendance(
        @Query("RouteID") routeIDs: String,
        @Query("StopID") stopID: Int,
        @Query("Trip") trip: Int,
        @Query("AttDate") attDate: String,
        @Query("StopIDs", encoded = true) stopIDs: String,
    ): NetworkStudentToMarkTransAttResponse

    @GET("Transport/StudentToDrop")
    suspend fun getStudentToDrop(
        @Query("RouteID") routeID: Int,
        @Query("StopID") stopID: Int,
        @Query("AttDate") attDate: String,
    ): NetworkStudentToMarkTransAttResponse

    @POST("Transport/PostTransAttendance")
    suspend fun postTransAttendance(
        @Body request: NetworkPostTransAttRequest,
    ): NetworkDropStudentResponse

    @GET("Transport/DropToStudent")
    suspend fun dropToStudent(
        @Query("StID") stID: Int,
        @Query("AttDate") attDate: String,
        @Query("HasDropped") hasDropped: Boolean,
    ): NetworkDropStudentResponse

    @GET("Transport/TransAttendanceReport")
    suspend fun getTransAttendanceReport(
        @Query("RouteID") routeID: Int,
        @Query("StopIDs", encoded = true) stopIDs: String,
        @Query("AttDate") attDate: String,
    ): NetworkTransAttReportResponse

    @GET("Transport/OutPassReport")
    suspend fun getOutPassReport(
        @Query("AttDate") attDate: String,
    ): NetworkOutPassReportResponse
}

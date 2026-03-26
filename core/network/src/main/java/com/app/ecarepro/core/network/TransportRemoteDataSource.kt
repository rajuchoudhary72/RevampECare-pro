package com.app.ecarepro.core.network

import com.app.ecarepro.core.network.model.transport.NetworkRoute
import com.app.ecarepro.core.network.model.transport.NetworkStop
import com.app.ecarepro.core.network.model.transport.NetworkStuAtt
import com.app.ecarepro.core.network.model.transport.NetworkStudentMarkAttResult
import com.app.ecarepro.core.network.model.transport.NetworkTransportStudent

interface TransportRemoteDataSource {

    suspend fun getRoutesList(): List<NetworkRoute>

    suspend fun getStoppageList(routeIDs: String, trip: Int): List<NetworkStop>

    suspend fun getStudentToMarkTransAttendance(
        routeIDs: String,
        stopID: Int,
        trip: Int,
        attDate: String,
        stopIDs: String,
    ): NetworkStudentMarkAttResult

    suspend fun getStudentToDrop(
        routeID: Int,
        stopID: Int,
        attDate: String,
    ): NetworkStudentMarkAttResult

    suspend fun postTransAttendance(
        attDate: String,
        routeID: Int,
        stopID: Int,
        stuAtt: List<NetworkStuAtt>,
        trip: Int,
    ): String

    suspend fun dropToStudent(
        stID: Int,
        attDate: String,
        hasDropped: Boolean,
    ): String

    suspend fun getTransAttendanceReport(
        routeID: Int,
        stopIDs: String,
        attDate: String,
    ): List<NetworkStop>

    suspend fun getOutPassReport(attDate: String): List<NetworkTransportStudent>
}

package com.app.ecarepro.core.network.datasource

import com.app.ecarepro.core.network.TransportRemoteDataSource
import com.app.ecarepro.core.network.model.transport.NetworkPostTransAttRequest
import com.app.ecarepro.core.network.model.transport.NetworkRoute
import com.app.ecarepro.core.network.model.transport.NetworkStop
import com.app.ecarepro.core.network.model.transport.NetworkStuAtt
import com.app.ecarepro.core.network.model.transport.NetworkStudentMarkAttResult
import com.app.ecarepro.core.network.model.transport.NetworkTransportStudent
import com.app.ecarepro.core.network.model.unwrapPayload
import com.app.ecarepro.core.network.retrofit.service.TransportService
import javax.inject.Inject

internal class TransportRemoteDataSourceImpl @Inject constructor(
    private val transportService: TransportService,
) : TransportRemoteDataSource {

    override suspend fun getRoutesList(): List<NetworkRoute> {
        return transportService.getRoutesList().unwrapPayload { routeLST ?: emptyList() }
    }

    override suspend fun getStoppageList(routeIDs: String, trip: Int): List<NetworkStop> {
        return transportService.getStoppageList(routeIDs, trip)
            .unwrapPayload { stopLST ?: emptyList() }
    }

    override suspend fun getStudentToMarkTransAttendance(
        routeIDs: String,
        stopID: Int,
        trip: Int,
        attDate: String,
        stopIDs: String,
    ): NetworkStudentMarkAttResult {
        val response = transportService.getStudentToMarkTransAttendance(
            routeIDs, stopID, trip, attDate, stopIDs
        )
        return response.unwrapPayload {
            NetworkStudentMarkAttResult(
                students = stuLst ?: emptyList(),
                freezDrop = freezDrop,
                freezPickup = freezPickup,
            )
        }
    }

    override suspend fun getStudentToDrop(
        routeID: Int,
        stopID: Int,
        attDate: String,
    ): NetworkStudentMarkAttResult {
        val response = transportService.getStudentToDrop(routeID, stopID, attDate)
        return response.unwrapPayload {
            NetworkStudentMarkAttResult(
                students = stuLst ?: emptyList(),
                freezDrop = freezDrop,
                freezPickup = freezPickup,
            )
        }
    }

    override suspend fun postTransAttendance(
        attDate: String,
        routeID: Int,
        stopID: Int,
        stuAtt: List<NetworkStuAtt>,
        trip: Int,
    ): String {
        return transportService.postTransAttendance(
            NetworkPostTransAttRequest(
                attDate = attDate,
                routeID = routeID,
                stopID = stopID,
                stuAtt = stuAtt,
                trip = trip,
            )
        ).unwrapPayload { message }
    }

    override suspend fun dropToStudent(
        stID: Int,
        attDate: String,
        hasDropped: Boolean,
    ): String {
        return transportService.dropToStudent(stID, attDate, hasDropped)
            .unwrapPayload { message }
    }

    override suspend fun getTransAttendanceReport(
        routeID: Int,
        stopIDs: String,
        attDate: String,
    ): List<NetworkStop> {
        return transportService.getTransAttendanceReport(routeID, stopIDs, attDate)
            .unwrapPayload { stopLST ?: emptyList() }
    }

    override suspend fun getOutPassReport(attDate: String): List<NetworkTransportStudent> {
        return transportService.getOutPassReport(attDate)
            .unwrapPayload { stuLst ?: emptyList() }
    }
}

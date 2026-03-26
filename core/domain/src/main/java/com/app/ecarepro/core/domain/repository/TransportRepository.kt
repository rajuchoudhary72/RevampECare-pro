package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.transport.MarkTransportAttendanceResult
import com.app.ecarepro.core.domain.model.transport.Route
import com.app.ecarepro.core.domain.model.transport.Stop
import com.app.ecarepro.core.domain.model.transport.StudentAttendanceRequest
import com.app.ecarepro.core.domain.model.transport.TransportStudent
import kotlinx.coroutines.flow.Flow

interface TransportRepository {

    fun getRoutesList(): Flow<Result<List<Route>>>

    fun getStoppageList(routeIDs: String, trip: Int): Flow<Result<List<Stop>>>

    fun getStudentToMarkTransAttendance(
        routeIDs: String,
        stopID: Int,
        trip: Int,
        attDate: String,
        stopIDs: String,
    ): Flow<Result<MarkTransportAttendanceResult>>

    fun getStudentToDrop(
        routeID: Int,
        stopID: Int,
        attDate: String,
    ): Flow<Result<MarkTransportAttendanceResult>>

    fun postTransAttendance(
        attDate: String,
        routeID: Int,
        stopID: Int,
        stuAtt: List<StudentAttendanceRequest>,
        trip: Int,
    ): Flow<Result<String>>

    fun dropToStudent(
        stID: Int,
        attDate: String,
        hasDropped: Boolean,
    ): Flow<Result<String>>

    fun getTransAttendanceReport(
        routeID: Int,
        stopIDs: String,
        attDate: String,
    ): Flow<Result<List<Stop>>>

    fun getOutPassReport(attDate: String): Flow<Result<List<TransportStudent>>>
}

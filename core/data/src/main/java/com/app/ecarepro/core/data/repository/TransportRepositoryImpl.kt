package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.transport.MarkTransportAttendanceResult
import com.app.ecarepro.core.domain.model.transport.Route
import com.app.ecarepro.core.domain.model.transport.Stop
import com.app.ecarepro.core.domain.model.transport.StudentAttendanceRequest
import com.app.ecarepro.core.domain.model.transport.TransportStudent
import com.app.ecarepro.core.domain.repository.TransportRepository
import com.app.ecarepro.core.network.TransportRemoteDataSource
import com.app.ecarepro.core.network.model.transport.toDomainModel
import com.app.ecarepro.core.network.model.transport.toDomainResult
import com.app.ecarepro.core.network.model.transport.toNetworkModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class TransportRepositoryImpl @Inject constructor(
    private val transportRemoteDataSource: TransportRemoteDataSource,
) : TransportRepository {

    override fun getRoutesList(): Flow<Result<List<Route>>> {
        return asResultFlow {
            transportRemoteDataSource.getRoutesList().map { it.toDomainModel() }
        }
    }

    override fun getStoppageList(routeIDs: String, trip: Int): Flow<Result<List<Stop>>> {
        return asResultFlow {
            transportRemoteDataSource.getStoppageList(routeIDs, trip)
                .map { it.toDomainModel() }
        }
    }

    override fun getStudentToMarkTransAttendance(
        routeIDs: String,
        stopID: Int,
        trip: Int,
        attDate: String,
        stopIDs: String,
    ): Flow<Result<MarkTransportAttendanceResult>> {
        return asResultFlow {
            transportRemoteDataSource.getStudentToMarkTransAttendance(
                routeIDs, stopID, trip, attDate, stopIDs
            ).toDomainResult()
        }
    }

    override fun getStudentToDrop(
        routeID: Int,
        stopID: Int,
        attDate: String,
    ): Flow<Result<MarkTransportAttendanceResult>> {
        return asResultFlow {
            transportRemoteDataSource.getStudentToDrop(routeID, stopID, attDate)
                .toDomainResult()
        }
    }

    override fun postTransAttendance(
        attDate: String,
        routeID: Int,
        stopID: Int,
        stuAtt: List<StudentAttendanceRequest>,
        trip: Int,
    ): Flow<Result<String>> {
        return asResultFlow {
            transportRemoteDataSource.postTransAttendance(
                attDate = attDate,
                routeID = routeID,
                stopID = stopID,
                stuAtt = stuAtt.map { it.toNetworkModel() },
                trip = trip,
            )
        }
    }

    override fun dropToStudent(
        stID: Int,
        attDate: String,
        hasDropped: Boolean,
    ): Flow<Result<String>> {
        return asResultFlow {
            transportRemoteDataSource.dropToStudent(stID, attDate, hasDropped)
        }
    }

    override fun getTransAttendanceReport(
        routeID: Int,
        stopIDs: String,
        attDate: String,
    ): Flow<Result<List<Stop>>> {
        return asResultFlow {
            transportRemoteDataSource.getTransAttendanceReport(routeID, stopIDs, attDate)
                .map { it.toDomainModel() }
        }
    }

    override fun getOutPassReport(attDate: String): Flow<Result<List<TransportStudent>>> {
        return asResultFlow {
            transportRemoteDataSource.getOutPassReport(attDate)
                .map { it.toDomainModel() }
        }
    }
}

package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.ApplyLeaveRequest
import com.app.ecarepro.core.domain.model.ApplyLeaveResponse
import com.app.ecarepro.core.domain.model.LeaveActionRequest
import com.app.ecarepro.core.domain.model.LeaveActionResponse
import com.app.ecarepro.core.domain.model.LeaveReportResponse
import com.app.ecarepro.core.domain.repository.AppliedLeavesRepository
import com.app.ecarepro.core.network.UserRemoteDataSource
import com.app.ecarepro.core.network.model.leave.toDomainModel
import com.app.ecarepro.core.network.model.leave.toNetworkModel
import com.app.ecarepro.core.domain.model.LeaveSettingResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class AppliedLeavesRepositoryImpl @Inject constructor(
    private val userRemoteDataSource: UserRemoteDataSource,
) : AppliedLeavesRepository {

    override fun getAppliedLeaves(
        status: Int,
        order: Int,
        applType: Int,
        page: Int,
        showAttendance: Boolean,
        duration: Int
    ): Flow<Result<LeaveReportResponse>> {
        return asResultFlow {
            userRemoteDataSource.getLeaveReport(
                status = status,
                order = order,
                applType = applType,
                page = page,
                showAttendance = showAttendance,
                duration = duration
            ).toDomainModel()
        }
    }
    override fun performLeaveAction(request: LeaveActionRequest): Flow<Result<LeaveActionResponse>> {
        return asResultFlow {
            userRemoteDataSource.leaveAction(request.toNetworkModel()).toDomainModel()
        }
    }

    override fun applyLeave(request: ApplyLeaveRequest): Flow<Result<ApplyLeaveResponse>> {
        return asResultFlow {
            userRemoteDataSource.applyLeave(request.toNetworkModel()).toDomainModel()
        }
    }

    override fun getLeaveSettings(): Flow<Result<LeaveSettingResponse>> {
        return asResultFlow {
            userRemoteDataSource.getLeaveSettings().toDomainModel()
        }
    }
}

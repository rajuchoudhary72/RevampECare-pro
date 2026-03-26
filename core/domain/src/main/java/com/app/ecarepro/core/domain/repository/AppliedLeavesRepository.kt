package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.ApplyLeaveRequest
import com.app.ecarepro.core.domain.model.ApplyLeaveResponse
import com.app.ecarepro.core.domain.model.LeaveActionRequest
import com.app.ecarepro.core.domain.model.LeaveActionResponse
import com.app.ecarepro.core.domain.model.LeaveReportResponse
import com.app.ecarepro.core.domain.model.LeaveSettingResponse
import kotlinx.coroutines.flow.Flow

interface AppliedLeavesRepository {
    /**
     * Get applied leaves report from the server
     * @param status Leave status filter (0=Pending, 1=Approved, 2=Rejected, 3=Cancelled)
     * @param order Sort order (2=default)
     * @param applType Application type (1=Student, 3=Staff)
     * @param page Page number for pagination
     * @param showAttendance Whether to show attendance percentage
     * @param duration Duration filter
     * @return Flow of Result containing LeaveReportResponse
     */
    fun getAppliedLeaves(
        status: Int,
        order: Int = 2,
        applType: Int,
        page: Int = 1,
        showAttendance: Boolean = false,
        duration: Int = 0
    ): Flow<Result<LeaveReportResponse>>

    /**
     * Perform action on leave (approve/reject/cancel)
     * @param request Leave action request
     * @return Flow of Result containing LeaveActionResponse
     */
    fun performLeaveAction(request: LeaveActionRequest): Flow<Result<LeaveActionResponse>>

    /**
     * Apply for a new leave
     * @param request Apply leave request containing leave details
     * @return Flow of Result containing ApplyLeaveResponse
     */
    fun applyLeave(request: ApplyLeaveRequest): Flow<Result<ApplyLeaveResponse>>

    /**
     * Get leave settings including leave types, terms, holidays
     * @return Flow of Result containing LeaveSettingResponse
     */
    fun getLeaveSettings(): Flow<Result<LeaveSettingResponse>>
}

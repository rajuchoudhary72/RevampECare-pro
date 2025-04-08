package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.HolidayList
import com.app.ecarepro.model.LeaveDetail
import com.app.ecarepro.model.LeaveTerms
import com.app.ecarepro.model.LeaveTypes
import com.app.ecarepro.model.TermCondition

data class NetworkLeaveSetting(
    val errorCode: Int?,
    val holidayList: HolidayList?,
    val leaveDetails: List<LeaveDetail?>,
    val leaveTerms: LeaveTerms?,
    val leaveTypes: List<LeaveTypes?>,
    val message: String?,
    val serverDate: String?,
    val status: String?,
    val shortLeaveValue: Double,
    val termCondition: TermCondition?
)
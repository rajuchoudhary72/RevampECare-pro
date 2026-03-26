package com.app.ecarepro.core.network.model.leave

import com.app.ecarepro.core.domain.model.Holiday
import com.app.ecarepro.core.domain.model.HolidayList
import com.app.ecarepro.core.domain.model.LeaveDetail
import com.app.ecarepro.core.domain.model.LeaveSettingResponse
import com.app.ecarepro.core.domain.model.LeaveTerms
import com.app.ecarepro.core.domain.model.TermCondition
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@InternalSerializationApi
@Serializable
data class NetworkLeaveSettingResponse(
    @SerialName("errorCode")
    val errorCode: Int = 0,
     @SerialName("status")
    val status: String? = null,
    @SerialName("message")
    val message: String? = null,
    @SerialName("serverDate")
    val serverDate: String? = null,
    @SerialName("shortLeaveValue")
    val shortLeaveValue: Double? = null,
    @SerialName("leaveTerms")
    val leaveTerms: NetworkLeaveTerms? = null,
    @SerialName("leaveTypes")
    val leaveTypes: List<String>? = null,
    @SerialName("termCondition")
    val termCondition: NetworkTermCondition? = null,
    @SerialName("holidayList")
    val holidayList: NetworkHolidayList? = null,
    @SerialName("leaveDetails")
    val leaveDetails: List<NetworkLeaveDetail>? = null
)

@InternalSerializationApi
@Serializable
data class NetworkLeaveTerms(
    @SerialName("daysLimit")
    val daysLimit: Int? = null,
    @SerialName("isPrevDatesAllow")
    val isPrevDatesAllow: Boolean? = null,
    @SerialName("forwardDays")
    val forwardDays: Int? = null,
    @SerialName("backwardDays")
    val backwardDays: Int? = null,
    @SerialName("weekOff")
    val weekOff: List<String>? = null
)

@InternalSerializationApi
@Serializable
data class NetworkTermCondition(
    @SerialName("tc")
    val tc: String? = null,
    @SerialName("notes")
    val notes: String? = null,
    @SerialName("rules")
    val rules: String? = null
)
@InternalSerializationApi
@Serializable
data class NetworkHolidayList(
    @SerialName("holiday")
    val holiday: List<NetworkHoliday>? = null
)

@InternalSerializationApi
@Serializable
data class NetworkHoliday(
    @SerialName("title")
    val title: String? = null,
    @SerialName("fromDate")
    val fromDate: String? = null,
    @SerialName("tillDate")
    val tillDate: String? = null,
    @SerialName("duration")
    val duration: Int? = null
)

@InternalSerializationApi
@Serializable
data class NetworkLeaveDetail(
    @SerialName("leaveID")
    val leaveID: Int? = null,
    @SerialName("leaveType")
    val leaveType: String? = null,
    @SerialName("leaveAbbr")
    val leaveAbbr: String? = null,
    @SerialName("total")
    val total: Double? = null,
    @SerialName("taken")
    val taken: Double? = null,
    @SerialName("inCurMonth")
    val inCurMonth: Double? = null,
    @SerialName("minAcceptableLimit")
    val minAcceptableLimit: Double? = null,
    @SerialName("maxAcceptableLimit")
    val maxAcceptableLimit: Double? = null,
    @SerialName("applyBeforeHours")
    val applyBeforeHours: Int? = null,
    @SerialName("minimumLimit")
    val minimumLimit: Int? = null,
    @SerialName("attachmentMandatory")
    val attachmentMandatory: Boolean? = null,
    @SerialName("sandwichEnable")
    val sandwichEnable: Boolean? = null
)

fun NetworkLeaveSettingResponse.toDomainModel(): LeaveSettingResponse {
    return LeaveSettingResponse(
        errorCode = errorCode,
        status = status,
        message = message,
        serverDate = serverDate,
        shortLeaveValue = shortLeaveValue,
        leaveTerms = leaveTerms?.toDomainModel(),
        leaveTypes = leaveTypes,
        termCondition = termCondition?.toDomainModel(),
        holidayList = holidayList?.toDomainModel(),
        leaveDetails = leaveDetails?.map { it.toDomainModel() }
    )
}

fun NetworkLeaveTerms.toDomainModel(): LeaveTerms {
    return LeaveTerms(
        daysLimit = daysLimit,
        isPrevDatesAllow = isPrevDatesAllow,
        forwardDays = forwardDays,
        backwardDays = backwardDays,
        weekOff = weekOff
    )
}

fun NetworkTermCondition.toDomainModel(): TermCondition {
    return TermCondition(
        tc = tc,
        notes = notes,
        rules = rules
    )
}

fun NetworkHolidayList.toDomainModel(): HolidayList {
    return HolidayList(
        holiday = holiday?.map { it.toDomainModel() }
    )
}

fun NetworkHoliday.toDomainModel(): Holiday {
    return Holiday(
        title = title,
        fromDate = fromDate,
        tillDate = tillDate,
        duration = duration
    )
}

fun NetworkLeaveDetail.toDomainModel(): LeaveDetail {
    return LeaveDetail(
        leaveID = leaveID,
        leaveType = leaveType,
        leaveAbbr = leaveAbbr,
        total = total,
        taken = taken,
        inCurMonth = inCurMonth,
        minAcceptableLimit = minAcceptableLimit,
        maxAcceptableLimit = maxAcceptableLimit,
        applyBeforeHours = applyBeforeHours,
        minimumLimit = minimumLimit,
        attachmentMandatory = attachmentMandatory,
        sandwichEnable = sandwichEnable
    )
}

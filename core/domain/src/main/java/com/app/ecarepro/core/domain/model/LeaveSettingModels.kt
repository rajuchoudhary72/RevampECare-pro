package com.app.ecarepro.core.domain.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class LeaveSettingResponse(
    val errorCode: Int = 0,
    val status: String? = null,
    val message: String? = null,
    val serverDate: String? = null,
    val shortLeaveValue: Double? = null,
    val leaveTerms: LeaveTerms? = null,
    val leaveTypes: List<String>? = null,
    val termCondition: TermCondition? = null,
    val holidayList: HolidayList? = null,
    val leaveDetails: List<LeaveDetail>? = null
)

data class LeaveTerms(
    val daysLimit: Int? = null,
    val isPrevDatesAllow: Boolean? = null,
    val forwardDays: Int? = null,
    val backwardDays: Int? = null,
    val weekOff: List<String>? = null
) {
    val weekOffDays: Set<String> get() = weekOff?.toSet() ?: emptySet()
}

data class TermCondition(
    val tc: String? = null,
    val notes: String? = null,
    val rules: String? = null
)

data class HolidayList(
    val holiday: List<Holiday>? = null
)

data class Holiday(
    val title: String? = null,
    val fromDate: String? = null,
    val tillDate: String? = null,
    val duration: Int? = null
) {
    fun containsDate(date: LocalDate): Boolean {
        val from = fromDate?.parseToLocalDate() ?: return false
        val till = tillDate?.parseToLocalDate() ?: return false
        return date in from..till
    }

    private fun String.parseToLocalDate(): LocalDate? {
        return try {
            LocalDate.parse(this, DateTimeFormatter.ISO_DATE)
        } catch (e: Exception) {
            null
        }
    }
}

data class LeaveDetail(
    val leaveID: Int? = null,
    val leaveType: String? = null,
    val leaveAbbr: String? = null,
    val total: Double? = null,
    val taken: Double? = null,
    val inCurMonth: Double? = null,
    val minAcceptableLimit: Double? = null,
    val maxAcceptableLimit: Double? = null,
    val applyBeforeHours: Int? = null,
    val minimumLimit: Int? = null,
    val attachmentMandatory: Boolean? = null,
    val sandwichEnable: Boolean? = null
) {
    val availableBalance: Double get() = (total ?: 0.0) - (taken ?: 0.0)
    val balancePercentage: Double
        get() = if ((total ?: 0.0) > 0) (taken ?: 0.0) / (total ?: 1.0) else 0.0
}

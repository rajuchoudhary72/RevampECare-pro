package com.app.ecarepro.model

data class LeaveDetail(
    val applyBeforeHours: Int,
    val attachmentMandatory: Boolean,
    val inCurMonth: Double,
    val leaveAbbr: String,
    val leaveID: Int,
    val leaveType: String,
    val maxAcceptableLimit: Double,
    val minAcceptableLimit: Double,
    val sandwichEnable: Boolean,
    val taken: Double,
    val total: Double,
    val minimumLimit: Int
)
package com.app.ecarepro.model

data class Appointment(
    val appDate: String,
    val appId: Int,
    val appTime: String,
    val appToID: Int,
    val appointeeDesig: String,
    val appointeeName: String,
    val checkInTime: String,
    val checkOutTime: String,
    val isNotMine: Boolean,
    val isRescheduled: Boolean,
    val mobile: String,
    val purpose: String,
    val status: String,
    val statusID: Int,
    val visitorID: Int,
    val visitorName: String,
    val visitorPhoto: String,
    val visitorType: String
)
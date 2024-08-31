package com.app.ecarepro.model

data class Dtl(
    val actionOn: String,
    val applicantName: String,
    val applicantPhoto: String,
    val attachment: String,
    val designation: String,
    val duration: Double,
    val forwardedBy: Int,
    val forwardedByName: Any,
    val fromDate: String,
    val studentName: String,
    val studentPhoto: String,

    val halfdayDTL: Any,
    val leaveAbbr: String,
    val leaveType: String,
    val lvID: Int,
    val photo: String,
    val reason: String,
    val sid: Int,
    val status: String,
    val submittedOn: String,
    val teacherID: Int,
    val teacherName: String,
    val tillDate: String
)
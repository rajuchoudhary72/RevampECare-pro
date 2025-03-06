package com.app.ecarepro.model

data class ProfileAttendanceDTL(
    val absent: Int,
    val isLateEnabled: Boolean,
    val late: Int,
    val leave: Int,
    val present: Int,
    val wh: Int,
    val summaryAttendance: List<SummaryAttendance>,
    val working: Int
)
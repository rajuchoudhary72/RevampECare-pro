package com.app.ecarepro.model

data class AttendanceDTL(
    val earlyOutsCount: Int,
    val fromDate: String,
    val latInCount: Int,
    val totalAbsent: Int,
    val totalPresent: Int
)
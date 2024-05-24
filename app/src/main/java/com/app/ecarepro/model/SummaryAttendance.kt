package com.app.ecarepro.model

data class SummaryAttendance(
    val absent: Int,
    val endDate: String,
    val late: Int,
    val leave: Int,
    val month: String,
    val monthID: Int,
    val present: Int,
    val startDate: String,
    val working: Int,
    val year: Int
)
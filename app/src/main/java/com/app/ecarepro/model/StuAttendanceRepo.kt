package com.app.ecarepro.model

data class StuAttendanceRepo(
    val attDate: String,
    val dayName: String,
    val isLate: Boolean,
    val status: Int
)
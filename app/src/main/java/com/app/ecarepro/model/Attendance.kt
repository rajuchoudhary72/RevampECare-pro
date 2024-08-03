package com.app.ecarepro.model

data class Attendance(
    val attDate: String,
    val inTime: String,
    val name: String,
    val isPrasent: Boolean,
    val lateBy: String,
    val outTime: String,
    val status: String,
    val totalWorking: String
)
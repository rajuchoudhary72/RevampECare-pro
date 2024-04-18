package com.app.ecarepro.model

data class AttReport(
    val admissionNumber: String,
    val attDate: String,
    val className: String,
    val id: String,
    val isLate: Boolean,
    val markedTime: Any,
    val name: String,
    val photo: String,
    val rfTagIn: Any,
    val rfTagOut: Any,
    val rollNumber: String,
    val status: Int,
    val temp: Any
)
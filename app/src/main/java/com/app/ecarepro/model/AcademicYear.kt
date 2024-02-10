package com.app.ecarepro.model

data class AcademicYear(
    val endDate: String,
    val isCur: Boolean,
    val session: String,
    val startDate: String,
    val yrID: Int
)
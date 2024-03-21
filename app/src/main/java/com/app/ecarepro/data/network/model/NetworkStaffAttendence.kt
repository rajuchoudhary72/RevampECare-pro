package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.Attendance

data class NetworkStaffAttendence(
    val attendance: List<Attendance>,
    val errorCode: Int,
    val message: String,
    val startMonth: Int,
    val startYear: Int,
    val status: String
)
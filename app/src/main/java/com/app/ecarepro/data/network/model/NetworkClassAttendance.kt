package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.AttReport

data class NetworkClassAttendance(
    val attReport: List<AttReport>,
    val className: String,
    val errorCode: Int,
    val message: String,
    val status: String
)
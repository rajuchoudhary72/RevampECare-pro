package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.StopLST

data class NetworkTransAttendanceReport(
    val errorCode: Int,
    val message: String,
    val status: String,
    val stopLST: List<StopLST>
)
package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.Dtl

data class NetworkLeaveReport(
    val dtl: List<Dtl>,
    val errorCode: Int,
    val message: String,
    val status: String,
    val canTalkeAction: Boolean
)
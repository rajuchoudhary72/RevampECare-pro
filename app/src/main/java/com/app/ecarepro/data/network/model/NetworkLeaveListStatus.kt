package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.Dtl

data class NetworkLeaveListStatus(
    val dtl: List<Dtl>,
    val errorCode: Int,
    val message: String,
    val myReporting: Any,
    val status: String
)
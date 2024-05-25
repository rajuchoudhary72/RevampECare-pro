package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.DateWise

data class NetworkSMSConsumption(
    val dateWise: List<DateWise>,
    val errorCode: Int,
    val message: String,
    val rechrgeLog: Any,
    val status: String
)
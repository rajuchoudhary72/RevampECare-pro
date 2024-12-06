package com.app.ecarepro.data.network.model

data class NetworkSmsReportDetails(
    val errorCode: Int,
    val message: String,
    val smSs: List<SmS>,
    val status: String
)
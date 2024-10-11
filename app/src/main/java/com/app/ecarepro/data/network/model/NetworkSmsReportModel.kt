package com.app.ecarepro.data.network.model

data class NetworkSmsReportModel(
    val errorCode: Int,
    val message: String,
    val smsType:  List<SmsType>,
    val status: String
)
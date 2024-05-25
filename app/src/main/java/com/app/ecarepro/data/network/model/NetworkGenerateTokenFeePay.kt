package com.app.ecarepro.data.network.model

data class NetworkGenerateTokenFeePay(
    val errorCode: Int,
    val message: String,
    val status: String,
    val tokenKey: String
)
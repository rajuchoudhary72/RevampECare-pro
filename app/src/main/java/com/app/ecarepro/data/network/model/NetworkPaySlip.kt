package com.app.ecarepro.data.network.model

data class NetworkPaySlip(
    val errorCode: Int,
    val message: String,
    val status: String,
    val years: List<Year>
)
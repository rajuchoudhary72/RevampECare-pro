package com.app.ecarepro.data.network.model

data class NetworkBusLocation(
    val `data`: BusData,
    val errorCode: Int,
    val googleKey: Any,
    val message: String,
    val status: String
)
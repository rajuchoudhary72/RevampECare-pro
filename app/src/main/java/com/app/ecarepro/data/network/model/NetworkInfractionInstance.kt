package com.app.ecarepro.data.network.model

data class NetworkInfractionInstance(
    val errorCode: Int,
    val instance: Int,
    val message: String,
    val status: String
)
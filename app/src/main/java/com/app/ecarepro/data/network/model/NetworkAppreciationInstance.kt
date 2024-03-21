package com.app.ecarepro.data.network.model

data class NetworkAppreciationInstance(
    val errorCode: Int,
    val instance: Int,
    val message: String,
    val status: String
)
package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.Circular

data class NetworkCircularDetails(
    val circuler: Circular,
    val errorCode: Int,
    val message: String,
    val status: String
)
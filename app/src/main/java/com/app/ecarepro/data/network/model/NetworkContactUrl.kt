package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.Circular

data class NetworkContactUrl(
    val supprtURL: String,
    val errorCode: Int,
    val message: String,
    val status: String
)
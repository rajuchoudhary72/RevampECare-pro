package com.app.ecarepro.data.network.model

import com.app.ecarepro.data.network.model.Year

data class NetworkPaySlip(
    val errorCode: Int,
    val message: String,
    val status: String,
    val years: List<Year>
)
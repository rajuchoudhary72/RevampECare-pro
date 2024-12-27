package com.app.ecarepro.data.network.model

data class NetworkWingReport(
    val errorCode: Int,
    val message: String,
    val status: String,
    val wingLST: List<WingLST>
)
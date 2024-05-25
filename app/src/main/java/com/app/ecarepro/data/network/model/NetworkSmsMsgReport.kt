package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.UsesRPT

data class NetworkSmsMsgReport(
    val errorCode: Int,
    val message: String,
    val status: String,
    val usesRPT: List<UsesRPT>
)
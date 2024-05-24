package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.StuLst

data class NetworkOutPassReport(
    val errorCode: Int,
    val freezDrop: Boolean,
    val freezPickup: Boolean,
    val message: String,
    val status: String,
    val stuLst: List<StuLst>
)
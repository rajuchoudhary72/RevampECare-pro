package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.RechargeLog

data class NetworkRechargeLog(
    val errorCode : String,
    val message : String,
    val rechargeLog : List<RechargeLog>,
)

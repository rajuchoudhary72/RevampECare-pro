package com.app.ecarepro.model

data class RechargeLog(

    val date: String,
    val prevBalance: String,
    val rechargedVol: String,
    val newBalance: String,
    val remarks: String,
    val amountPaid: String,
    val rechargeType: Int,

    )

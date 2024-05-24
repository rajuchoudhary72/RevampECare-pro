package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.AlertNotification
import com.app.ecarepro.model.SmsPriceDTL

data class NetworkSMSBalnceInfo(
    val alertNotification: AlertNotification,
    val balanceOn: String,
    val errorCode: Int,
    val message: String,
    val payURL: Any,
    val smsBalance: Int,
    val smsCommitment: String,
    val smsPriceDTL: SmsPriceDTL,
    val status: String
)
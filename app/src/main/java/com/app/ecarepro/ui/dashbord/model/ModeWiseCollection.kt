package com.app.ecarepro.ui.dashbord.model

import com.app.ecarepro.data.network.model.TransactionDetail
import com.google.gson.annotations.SerializedName


data class ModeWiseCollection(
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("totalCollection")
    val totalCollection: Int?,
    @SerializedName("transactionDetails")
    val transactionDetails: List<TransactionDetail>?
)
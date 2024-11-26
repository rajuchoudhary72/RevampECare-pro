package com.app.ecarepro.data.network.model
import com.google.gson.annotations.SerializedName


data class ValidateOtpRequest(
    @SerializedName("oTPAuthKey")
    val oTPAuthKey: String?,
    @SerializedName("otp")
    val otp: String? = null,
    @SerializedName("schCode")
    val schCode: String?
)
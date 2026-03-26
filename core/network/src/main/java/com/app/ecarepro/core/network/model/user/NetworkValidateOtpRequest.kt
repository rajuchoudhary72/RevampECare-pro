package com.app.ecarepro.core.network.model.user

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@InternalSerializationApi
@Serializable
data class NetworkValidateOtpRequest(
    @SerialName("oTPAuthKey")
    val otpAuthKey: String,
    @SerialName("otp")
    val otp: String,
    @SerialName("schCode")
    val schoolCode: String,
)
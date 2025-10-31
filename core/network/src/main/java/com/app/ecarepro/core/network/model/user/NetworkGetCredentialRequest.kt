package com.app.ecarepro.core.network.model.user

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@InternalSerializationApi
@Serializable
data class NetworkGetCredentialRequest(
    @SerialName("email")
    val email: String? = null,
    @SerialName("mobile")
    val mobile: String? = null,
    @SerialName("rcvOn")
    val receivedOn: String,
    @SerialName("schCode")
    val schoolCode: String,
    @SerialName("userType")
    val userType: Int,
)
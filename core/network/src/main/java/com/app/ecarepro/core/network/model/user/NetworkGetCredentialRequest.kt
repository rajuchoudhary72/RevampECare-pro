package com.app.ecarepro.core.network.model.user

import com.app.ecarepro.core.domain.model.GetCredential
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

fun GetCredential.toNetworkGetCredentialRequest() = NetworkGetCredentialRequest(
    email = email,
    mobile = mobile,
    receivedOn = receivedOn,
    schoolCode = schoolCode,
    userType = userType
)
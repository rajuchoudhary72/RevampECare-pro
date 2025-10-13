package com.app.ecarepro.core.network.model.user

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@InternalSerializationApi
@Serializable
data class NetworkLoginRequest(
    @SerialName("password")
    val password: String?,
    @SerialName("schCode")
    val schoolCode: String?,
    @SerialName("username")
    val userName: String?,
    @SerialName("deviceinfo")
    val deviceInfo: NetworkDeviceInfo? = null,
)
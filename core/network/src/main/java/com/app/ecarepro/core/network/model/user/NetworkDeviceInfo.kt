package com.app.ecarepro.core.network.model.user

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@InternalSerializationApi
@Serializable
data class NetworkDeviceInfo(
    @SerialName("ipAddress")
    val ipAddress: String,
    @SerialName("locationCity")
    val locationCity: String,
    @SerialName("appVersion")
    val appVersion: String,
    @SerialName("deviceType")
    val deviceType: Int,
    @SerialName("model")
    val model: String,
    @SerialName("osVersion")
    val osVersion: String
)
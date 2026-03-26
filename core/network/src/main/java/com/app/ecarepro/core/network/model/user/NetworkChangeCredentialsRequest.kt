package com.app.ecarepro.core.network.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkChangeCredentialsRequest(
    @SerialName("currentUsername") val currentUsername: String,
    @SerialName("newUsername") val newUsername: String,
    @SerialName("newPassword") val newPassword: String,
)

package com.app.ecarepro.core.network.model.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkUpdateParentProfileRequest(
    @SerialName("contactMobile") val contactMobile: String,
    @SerialName("contactEmailID") val contactEmailID: String,
    @SerialName("address") val address: String,
)

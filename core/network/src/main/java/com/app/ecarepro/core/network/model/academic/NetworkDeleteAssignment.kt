package com.app.ecarepro.core.network.model.academic

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@InternalSerializationApi
data class NetworkDeleteAssignment(
    @SerialName("ID")
    val id: String,
)

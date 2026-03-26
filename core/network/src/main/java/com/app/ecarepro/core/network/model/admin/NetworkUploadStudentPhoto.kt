package com.app.ecarepro.core.network.model.admin

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkUploadStudentPhotoRequest(
    @SerialName("stID") val stID: Int,
    @SerialName("photo") val photo: String,
    @SerialName("photoExt") val photoExt: String,
)

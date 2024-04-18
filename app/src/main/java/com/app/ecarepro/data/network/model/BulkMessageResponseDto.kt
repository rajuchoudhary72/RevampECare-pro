package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName


data class BulkMessageResponseDto(
    @SerializedName("ErrorCode")
    val errorCode: Int?,
    @SerializedName("Message")
    val message: String?,
    @SerializedName("Status")
    val status: String?
)
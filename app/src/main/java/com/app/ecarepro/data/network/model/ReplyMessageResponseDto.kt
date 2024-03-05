package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName

data class ReplyMessageResponseDto(
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: String?
)
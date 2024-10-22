package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName
data class SendCommentDto(
    @SerializedName("comment")
    val comment: String?,
    @SerializedName("id")
    val id: String?
)
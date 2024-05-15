package com.app.ecarepro.model

import com.google.gson.annotations.SerializedName


data class UpdateTaskAttachmentDto(
    @SerializedName("action")
    val action: Int?,
    @SerializedName("attachment")
    val attachment: Attachment?,
    @SerializedName("id")
    val id: String?
)

package com.app.ecarepro.data.network

import com.google.gson.annotations.SerializedName

data class SaveSkillResponse(
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: String?
)



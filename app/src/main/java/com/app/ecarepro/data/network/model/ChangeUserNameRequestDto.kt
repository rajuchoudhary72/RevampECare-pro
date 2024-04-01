package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName


data class ChangeUserNameRequestDto(
    @SerializedName("currentUsername")
    val currentUsername: String?,
    @SerializedName("newPassword")
    val newPassword: String?,
    @SerializedName("newUsername")
    val newUsername: String?
)
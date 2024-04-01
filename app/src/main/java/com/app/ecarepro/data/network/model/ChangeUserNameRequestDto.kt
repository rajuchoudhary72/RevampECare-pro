package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName


data class ChangeUserNameRequestDto(
    @SerializedName("currentUsername")
    val currentUsername: String? = null,
    @SerializedName("newPassword")
    val newPassword: String? = null,
    @SerializedName("newUsername")
    val newUsername: String? = null
)
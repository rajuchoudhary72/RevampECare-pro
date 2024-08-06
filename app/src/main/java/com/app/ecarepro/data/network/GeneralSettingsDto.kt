package com.app.ecarepro.data.network
import com.google.gson.annotations.SerializedName


data class GeneralSettingsDto(
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("settings")
    val settings: List<Setting>?,
    @SerializedName("status")
    val status: String?
)

data class Setting(
    @SerializedName("description")
    val description: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("isEnabled")
    val isEnabled: Boolean?,
    @SerializedName("settingName")
    val settingName: String?
)
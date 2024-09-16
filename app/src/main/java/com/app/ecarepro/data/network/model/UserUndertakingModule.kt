package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName


data class UserUndertakingModule(
    @SerializedName("UtID")
    val UtID: String?,
    @SerializedName("deviceModel")
    val deviceModel: String?,
    @SerializedName("device")
    val deviceType: Int?,
    @SerializedName("deviceID")
    val deviceID: String?,
    @SerializedName("geoCoordinate")
    val geoCoordinate: String?
)
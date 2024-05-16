package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName


data class RegisterDevice(
    @SerializedName("deviceID")
    val deviceID: String?,
    @SerializedName("deviceModel")
    val deviceModel: String?,
    @SerializedName("deviceType")
    val deviceType: Int?,
    @SerializedName("fcmToken")
    val fcmToken: String?,
    @SerializedName("imeI_1")
    val imeI1: String?,
    @SerializedName("imeI_2")
    val imeI2: String?,
    @SerializedName("osVersion")
    val osVersion: String?
)
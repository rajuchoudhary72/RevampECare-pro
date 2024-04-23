package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName


data class BulkMessageRequestDto(
    @SerializedName("Data")
    val data: List<Data>?,
    @SerializedName("Device")
    val device: Int? = 1,
    @SerializedName("GeoCoordinate")
    val geoCoordinate: String? = "26.9332265,75.7440641",
    @SerializedName("IPAddress")
    val iPAddress: String?,
    @SerializedName("isBulk")
    val isBulk: Int?,
    @SerializedName("isUnicode")
    val isUnicode: Int? = 0,
    @SerializedName("SMSType")
    val sMSType: Int?,
    @SerializedName("SchCode")
    val schCode: String?,
    @SerializedName("UID")
    val uID: Int?,
    @SerializedName("UType")
    val uType: Int?
)

data class Data(
    @SerializedName("Mobile")
    val mobile: String?,
    @SerializedName("RCPTID")
    val rCPTID: String?,
    @SerializedName("RCPTType")
    val rCPTType: Int?,
    @SerializedName("SMS")
    val sMS: String?,
    @SerializedName("TemplateID")
    val templateID: String?
)
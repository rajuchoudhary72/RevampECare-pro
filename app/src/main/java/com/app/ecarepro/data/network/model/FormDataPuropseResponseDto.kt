package com.app.ecarepro.data.network.model
import com.google.gson.annotations.SerializedName


data class FormDataPurposeResponseDto(
    @SerializedName("data")
    val `data`: List<Purpose>?,
    @SerializedName("status")
    val status: Boolean?
)

data class Purpose(
    @SerializedName("enabled")
    val enabled: Boolean?,
    @SerializedName("purposeID")
    val purposeID: Int?,
    @SerializedName("purposeName")
    val purposeName: String?
)
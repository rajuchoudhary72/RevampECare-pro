package com.app.ecarepro.data.network.model
import com.google.gson.annotations.SerializedName


data class FormDataDesignationResponseDto(
    @SerializedName("data")
    val `data`: List<Designation>?,
    @SerializedName("status")
    val status: Boolean?
)

data class Designation(
    @SerializedName("designationID")
    val designationID: Int?,
    @SerializedName("designationName")
    val designationName: String?
)
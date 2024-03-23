package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName


data class StaffTypeDto(
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("staffType")
    val staffType: List<StaffType>?,
    @SerializedName("status")
    val status: String?
)

data class StaffType(
    @SerializedName("staff_Type")
    val staffType: String?,
    @SerializedName("staffTypeID")
    val staffTypeID: Int?
)
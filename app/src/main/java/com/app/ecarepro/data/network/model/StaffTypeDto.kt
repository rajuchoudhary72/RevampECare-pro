package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class StaffTypeDto(
    @SerializedName("errorCode")
    val errorCode: Int? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("staffType")
    val staffType: List<StaffType>?,
    val selectedStaffType: List<StaffType>?,
    @SerializedName("status")
    val status: String? = null
) : Serializable

data class StaffType(
    @SerializedName("staff_Type")
    val staffType: String?,
    @SerializedName("staffTypeID")
    val staffTypeID: Int
) : Serializable
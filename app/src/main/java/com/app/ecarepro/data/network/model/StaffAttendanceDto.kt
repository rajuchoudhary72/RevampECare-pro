package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName


data class StaffAttendanceDto(
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("dtl")
    val dtl: List<StaffAttendanceDetails>?
)

data class StaffAttendanceDetails(
    @SerializedName("designation")
    val designation: String,
    @SerializedName("empCode")
    val empCode: String?,
    @SerializedName("empID")
    val empID: Int?,
    @SerializedName("isAbsent")
    val isAbsent: Boolean?,
    @SerializedName("isHoliday")
    val isHoliday: Boolean?,
    @SerializedName("isPrasent")
    val isPrasent: Boolean?,
    @SerializedName("lateBy")
    val lateBy: String?,
    @SerializedName("markOn")
    val markOn: List<MarkOn>?,
    @SerializedName("name")
    val name: String,
    @SerializedName("photo")
    val photo: String?,
    @SerializedName("status")
    val status: Any?
)
data class MarkOn(
    @SerializedName("markOn")
    val markOn: String?
)


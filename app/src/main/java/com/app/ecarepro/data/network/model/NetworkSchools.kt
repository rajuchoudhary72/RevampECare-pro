package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.School
import com.google.gson.annotations.SerializedName


data class NetworkSchoolsDto(
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("list")
    val list: List<NetworkSchoolItem>?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: String?
)

data class NetworkSchoolItem(
    @SerializedName("address")
    val address: String?,
    @SerializedName("city")
    val city: String?,
    @SerializedName("logo")
    val logo: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("schoolCode")
    val schoolCode: String?,
    @SerializedName("state")
    val state: String?
)

fun NetworkSchoolItem.asExternalModel() = School(
    address, city, logo, name, schoolCode, state
)
package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

data class VisitorDetailsDto(
    @SerializedName("data")
    val data: VisitorDetails?,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Boolean?
)
@Parcelize
data class VisitorDetails(
    @SerializedName("address")
    val address: String?,
    @SerializedName("company")
    val company: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("mobile")
    val mobile: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("userid")
    val userid: String?,
    @SerializedName("visitorPhoto")
    val visitorPhoto: String?,
    @SerializedName("visitorType")
    val visitorType: Int?
) : Parcelable

package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName


data class GetCredentialsRequest(
    @SerializedName("email")
    val email: String?,
    @SerializedName("mobile")
    val mobile: String?,
    @SerializedName("rcvOn")
    val rcvOn: String?,
    @SerializedName("schCode")
    val schCode: String?,
    @SerializedName("userType")
    val userType: Int?
)
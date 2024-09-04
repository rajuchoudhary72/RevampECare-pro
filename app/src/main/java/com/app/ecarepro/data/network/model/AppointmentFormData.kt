package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName


data class AppointmentFormData(
    @SerializedName("data")
    val data: List<Form>?,
    @SerializedName("status")
    val status: Boolean?
)

data class Form(
    @SerializedName("active")
    val active: Boolean?,
    @SerializedName("appFormTypeID")
    val appFormTypeID: Int?,
    @SerializedName("columnDisplayName")
    val columnDisplayName: String?,
    @SerializedName("columnID")
    val columnID: Int?,
    @SerializedName("columnName")
    val columnName: String,
    @SerializedName("isrequired")
    val isrequired: Boolean?,
    @SerializedName("mustInclude")
    val mustInclude: Boolean?,
    @SerializedName("orderid")
    val orderid: Int?,
    val value:String? = null,
    val base64Image:String? = "",
)
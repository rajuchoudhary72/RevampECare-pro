package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName


data class SmsTemplatesDto(
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("smsType")
    val smsType: List<SmsType>?,
    @SerializedName("status")
    val status: String?
)

data class SmsType(
    @SerializedName("subject")
    val subject: String?,
    @SerializedName("templates")
    val templates: List<Template>?,
    @SerializedName("typeID")
    val  typeID: Int?
)

data class Template(
    @SerializedName("smsType")
    val smsType: Int?,
    @SerializedName("template")
    val template: String?,
    @SerializedName("templateID")
    val templateID: String?
)
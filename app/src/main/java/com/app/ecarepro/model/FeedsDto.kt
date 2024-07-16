package com.app.ecarepro.model

import com.google.gson.annotations.SerializedName


data class FeedsDto(
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("serverDateTime")
    val serverDateTime: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("total")
    val total: Int,
    @SerializedName("updates")
    val updates: List<Feed>?
)

data class Feed(
    @SerializedName("caption")
    val caption: String?,
    @SerializedName("galleryUpdate")
    val galleryUpdate: Any?,
    @SerializedName("hasAttachment")
    val hasAttachment: Boolean?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("menuID")
    val menuID: Int,
    @SerializedName("chMenuID")
    val chMenuID: Int,
    @SerializedName("sbChMenuID")
    val sbChMenuID: Int,
    @SerializedName("mdlID")
    val mdlID: Int?,
    @SerializedName("module")
    val module: String?,
    @SerializedName("msgDTL")
    val msgDTL: MsgDTL?,
    @SerializedName("updtedOn")
    val updtedOn: String?,
    @SerializedName("webLink")
    val webLink: String?
)

data class MsgDTL(
    @SerializedName("filePath")
    val filePath: Any?,
    @SerializedName("msgType")
    val msgType: Int?,
    @SerializedName("senderDTL")
    val senderDTL: SenderDTL?,
    @SerializedName("subject")
    val subject: Any?
)

data class SenderDTL(
    @SerializedName("childName")
    val childName: String?,
    @SerializedName("className")
    val className: String?,
    @SerializedName("designation")
    val designation: Any?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("photo")
    val photo: String?,
    @SerializedName("senderID")
    val senderID: Int?,
    @SerializedName("senderType")
    val senderType: Int?
)
package com.app.ecarepro.data.network.model

import com.google.gson.annotations.SerializedName


data class NotificationsDto(
    @SerializedName("count")
    val count: Int?,
    @SerializedName("errorCode")
    val errorCode: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("recentNotifications")
    val recentNotifications: List<Notification>?,
    @SerializedName("status")
    val status: String?
)

data class Notification(
    @SerializedName("body")
    val body: String?,
    @SerializedName("hasSeen")
    val hasSeen: Boolean?,
    @SerializedName("icon")
    val icon: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("link")
    val link: String?,
    @SerializedName("moduleID")
    val moduleID: Int?,
    @SerializedName("sentOn")
    val sentOn: String?,
    @SerializedName("title")
    val title: String?
)
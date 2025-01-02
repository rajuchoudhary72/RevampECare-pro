package com.app.ecarepro.data.network.model

data class NetworkPushNotificationRequest(
    val ClassSTD: Int?,
    val chMenuID: Int?,
    val classIDs: String?,
    val menuID: Int?,
    val message: String?,
    val recipientType: Int?,
    val title: String?
)
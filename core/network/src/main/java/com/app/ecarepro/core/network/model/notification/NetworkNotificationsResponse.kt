package com.app.ecarepro.core.network.model.notification

import com.app.ecarepro.core.domain.model.NotificationItem
import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkNotificationsResponse(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("status") override val status: String,
    @SerialName("message") override val message: String,
    @SerialName("count") val count: Int? = null,
    @SerialName("recentNotifications") val recentNotifications: List<NetworkNotificationItem>? = null,
) : NetworkResponse

@Serializable
data class NetworkNotificationItem(
    @SerialName("id") val id: String? = null,
    @SerialName("moduleID") val moduleID: Int? = null,
    @SerialName("chMenuID") val chMenuID: Int? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("body") val body: String? = null,
    @SerialName("hasSeen") val hasSeen: Boolean? = null,
    @SerialName("sentOn") val sentOn: String? = null,
    @SerialName("icon") val icon: String? = null,
    @SerialName("link") val link: String? = null,
    @SerialName("refID") val refID: String? = null,
)

fun NetworkNotificationItem.toDomainModel() = NotificationItem(
    id = id.orEmpty(),
    moduleID = moduleID ?: 0,
    chMenuID = chMenuID ?: 0,
    title = title.orEmpty(),
    body = body.orEmpty(),
    hasSeen = hasSeen ?: false,
    sentOn = sentOn.orEmpty(),
    icon = icon,
    link = link,
    refID = refID,
)

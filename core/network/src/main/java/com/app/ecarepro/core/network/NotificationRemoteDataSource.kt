package com.app.ecarepro.core.network

import com.app.ecarepro.core.network.model.notification.NetworkNotificationItem

interface NotificationRemoteDataSource {
    suspend fun getNotifications(page: Int): List<NetworkNotificationItem>
    suspend fun markNotificationSeen(id: String)
}

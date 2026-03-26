package com.app.ecarepro.core.network.datasource

import com.app.ecarepro.core.network.NotificationRemoteDataSource
import com.app.ecarepro.core.network.model.notification.NetworkNotificationItem
import com.app.ecarepro.core.network.model.unwrapPayload
import com.app.ecarepro.core.network.retrofit.service.AppService
import javax.inject.Inject

internal class NotificationRemoteDataSourceImpl @Inject constructor(
    private val service: AppService,
) : NotificationRemoteDataSource {

    override suspend fun getNotifications(page: Int): List<NetworkNotificationItem> =
        service.getNotifications(page).unwrapPayload { recentNotifications.orEmpty() }

    override suspend fun markNotificationSeen(id: String) {
        val response = service.markNotificationSeen(id)
        if (response.status != "ok") throw Exception(response.message)
    }
}

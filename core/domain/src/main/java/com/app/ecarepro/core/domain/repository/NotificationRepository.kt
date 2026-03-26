package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.NotificationItem
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getNotifications(page: Int): Flow<Result<List<NotificationItem>>>
    fun markNotificationSeen(id: String): Flow<Result<Unit>>
}

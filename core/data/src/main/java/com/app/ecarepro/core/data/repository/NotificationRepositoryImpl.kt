package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.NotificationItem
import com.app.ecarepro.core.domain.repository.NotificationRepository
import com.app.ecarepro.core.network.NotificationRemoteDataSource
import com.app.ecarepro.core.network.model.notification.toDomainModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class NotificationRepositoryImpl @Inject constructor(
    private val dataSource: NotificationRemoteDataSource,
) : NotificationRepository {

    override fun getNotifications(page: Int): Flow<Result<List<NotificationItem>>> = asResultFlow {
        dataSource.getNotifications(page).map { it.toDomainModel() }
    }

    override fun markNotificationSeen(id: String): Flow<Result<Unit>> = asResultFlow {
        dataSource.markNotificationSeen(id)
    }
}

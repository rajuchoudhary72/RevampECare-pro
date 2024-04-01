package com.app.ecarepro.data.repository

import com.app.ecarepro.data.network.model.AppLayoutDto
import com.app.ecarepro.data.network.model.Notification
import kotlinx.coroutines.flow.Flow

interface AppRepository {
    fun getAppLayout(): Flow<Result<AppLayoutDto>>

    fun getNotifications(): Flow<Result<List<Notification>>>
}
package com.app.ecarepro.data.repository

import com.app.ecarepro.data.network.model.AppLayoutDto
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.Notification
import com.app.ecarepro.data.network.model.RegisterDevice
import kotlinx.coroutines.flow.Flow
import com.app.ecarepro.data.network.model.Favourites
import com.app.ecarepro.data.network.model.SyncData

interface AppRepository {
    fun getAppLayout(): Flow<Result<AppLayoutDto>>
    fun getNotifications(refresh: Boolean): Flow<Result<List<Notification>>>
    fun registerDevice(registerDevice: RegisterDevice): Flow<Result<String>>
    fun getFavourites(): Flow<Result<List<Favourites>>>
    fun updateFavourites(items:List<Favourites>): Flow<Result<String>>
    suspend fun notificationSeen(  id: String ): CommonResponse
    fun syncData(): Flow<Result<SyncData>>
}
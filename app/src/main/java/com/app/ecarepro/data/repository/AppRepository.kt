package com.app.ecarepro.data.repository

import com.app.ecarepro.data.network.model.AppLayoutDto
import com.app.ecarepro.data.network.model.Favourites
import com.app.ecarepro.data.network.model.FavouritesUpdateDto
import com.app.ecarepro.data.network.model.Notification
import com.app.ecarepro.data.network.model.RegisterDevice
import kotlinx.coroutines.flow.Flow

interface AppRepository {
    fun getAppLayout(): Flow<Result<AppLayoutDto>>
    fun getNotifications(): Flow<Result<List<Notification>>>
    fun registerDevice(registerDevice: RegisterDevice): Flow<Result<String>>
    fun getFavourites(): Flow<Result<List<Favourites>>>
    fun updateFavourites(items:List<FavouritesUpdateDto>): Flow<Result<String>>
}
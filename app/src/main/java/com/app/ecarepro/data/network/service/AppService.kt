package com.app.ecarepro.data.network.service

import com.app.ecarepro.data.network.model.AppLayoutDto
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.Favourites
import com.app.ecarepro.data.network.model.NotificationsDto
import com.app.ecarepro.data.network.model.RegisterDevice
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import com.app.ecarepro.data.network.model.FavouritesDto
import com.app.ecarepro.data.network.model.FavouritesUpdateDto
import com.app.ecarepro.data.network.model.SyncDataDto

interface AppService {
    @GET("App/Layout")
    suspend fun getAppLayout(
        @Query("Device") device: Int = 1,
    ): AppLayoutDto


    @GET("App/Notifications")
    suspend fun getNotifications(): NotificationsDto

    @POST("App/RegisterDevice")
    suspend fun registerFirebaseToken(
        @Body registerDevice: RegisterDevice
    ): CommonResponse

    @GET("App/FavoriteMenus")
    suspend fun getFavourites(
        @Query("Device") device: Int = 1,
    ): FavouritesDto

    @POST("App/UpdateFavoriteMenus")
    suspend fun updateFavourites(
        @Body request: List<Favourites>
    ): CommonResponse

    @GET("App/NotificationSeen")
    suspend fun notificationSeen(
        @Query("ID") id: String
    ): CommonResponse
    @GET("App/Sync")
    suspend fun syncData(): SyncDataDto
}
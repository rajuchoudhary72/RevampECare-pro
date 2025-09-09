package com.app.ecarepro.data.network.service

import com.app.ecarepro.data.network.model.AppLayoutDto
import com.app.ecarepro.data.network.model.BadgeCountResponse
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
import com.app.ecarepro.ui.language.model.SheetResponseDto
import retrofit2.http.Path

interface AppService {
    @GET("App/Layout")
    suspend fun getAppLayout(
        @Query("Device") device: Int = 1,
        @Query("language") language: String ,
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
        @Query("language") language: String ,
    ): FavouritesDto

    @POST("App/UpdateFavoriteMenus")
    suspend fun updateFavourites(
        @Body request: List<Favourites>
    ): CommonResponse

    @GET("App/NotificationSeen")
    suspend fun notificationSeen(
        @Query("ID") id: String
    ): CommonResponse


    @GET("User/NotificationCount")
    suspend fun getNotificationCount():BadgeCountResponse

    @GET("App/Sync")
    suspend fun syncData(): SyncDataDto



    @GET("https://sheets.googleapis.com/v4/spreadsheets/{spreadsheetId}/values/{range}")
    suspend fun getSheetValues(
        @Path("spreadsheetId") spreadsheetId: String,
        @Path("range") range: String,
        @Query("key") apiKey: String
    ): SheetResponseDto
    
    @GET("User/UpdateLastActiveSession")
    suspend fun updateLastSession(
        @Path("SessionID") sessionID: String
    ): CommonResponse
}
package com.app.ecarepro.core.network.retrofit.service

import com.app.ecarepro.core.network.model.CommonNetworkResponse
import com.app.ecarepro.core.network.model.menu.NetworkMenuResponse
import com.app.ecarepro.core.network.model.notification.NetworkNotificationsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AppService {
    @GET("App/Menu")
    suspend fun getMenu(
        @Query("Platform") platform: Int = 1,
        @Query("language") language: String,
    ): NetworkMenuResponse

    @GET("App/Notifications")
    suspend fun getNotifications(@Query("pg") page: Int): NetworkNotificationsResponse

    @GET("App/NotificationSeen")
    suspend fun markNotificationSeen(@Query("ID") id: String): CommonNetworkResponse
}

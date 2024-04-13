package com.app.ecarepro.data.network.service

import com.app.ecarepro.data.network.model.AppLayoutDto
import com.app.ecarepro.data.network.model.NotificationsDto
import retrofit2.http.GET
import retrofit2.http.Query

interface AppService {
    @GET("App/Layout")
    suspend fun getAppLayout(
        @Query("Device") device: Int = 1,
    ): AppLayoutDto
    @GET("App/Notifications")
    suspend fun getNotifications(): NotificationsDto
}
package com.app.ecarepro.data.network.service

import com.app.ecarepro.data.network.model.AppLayoutDto
import retrofit2.http.GET
import retrofit2.http.Query

interface AppService {
    @GET("App/Layout")
    suspend fun getAppLayout(
        @Query("Device") device: Int = 1,
    ): AppLayoutDto
}
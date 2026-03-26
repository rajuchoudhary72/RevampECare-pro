package com.app.ecarepro.core.network.datasource

import com.app.ecarepro.core.network.MenuRemoteDataSource
import com.app.ecarepro.core.network.model.menu.NetworkMenuCategory
import com.app.ecarepro.core.network.model.unwrapPayload
import com.app.ecarepro.core.network.retrofit.service.AppService
import javax.inject.Inject

internal class MenuRemoteDataSourceImpl @Inject constructor(
    private val appService: AppService,
) : MenuRemoteDataSource {
    override suspend fun fetchMenu(language: String): List<NetworkMenuCategory> =
        appService.getMenu(language = language).unwrapPayload { categories }
}

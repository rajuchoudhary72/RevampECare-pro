package com.app.ecarepro.core.network

import com.app.ecarepro.core.network.model.menu.NetworkMenuCategory

interface MenuRemoteDataSource {
    suspend fun fetchMenu(language: String): List<NetworkMenuCategory>
}

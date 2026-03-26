package com.app.ecarepro.core.network

import com.app.ecarepro.core.network.model.CommonNetworkResponse

interface SettingsRemoteDataSource {
    suspend fun changeUsername(currentUsername: String, newUsername: String): CommonNetworkResponse
    suspend fun changePassword(currentUsername: String, newPassword: String): CommonNetworkResponse
}

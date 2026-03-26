package com.app.ecarepro.core.network.datasource

import com.app.ecarepro.core.network.SettingsRemoteDataSource
import com.app.ecarepro.core.network.model.CommonNetworkResponse
import com.app.ecarepro.core.network.model.user.NetworkChangeCredentialsRequest
import com.app.ecarepro.core.network.retrofit.service.UserService
import javax.inject.Inject

internal class SettingsRemoteDataSourceImpl @Inject constructor(
    private val userService: UserService,
) : SettingsRemoteDataSource {

    override suspend fun changeUsername(
        currentUsername: String,
        newUsername: String,
    ): CommonNetworkResponse = userService.changeUsername(
        NetworkChangeCredentialsRequest(
            currentUsername = currentUsername,
            newUsername = newUsername,
            newPassword = newUsername, // intentional: newPassword = newUsername per API contract
        )
    )

    override suspend fun changePassword(
        currentUsername: String,
        newPassword: String,
    ): CommonNetworkResponse = userService.changePassword(
        NetworkChangeCredentialsRequest(
            currentUsername = currentUsername,
            newUsername = currentUsername, // intentional: newUsername = currentUsername per API contract
            newPassword = newPassword,
        )
    )
}

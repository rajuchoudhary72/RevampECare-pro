package com.app.ecarepro.core.network.datasource

import com.app.ecarepro.core.network.MyProfileRemoteDataSource
import com.app.ecarepro.core.network.model.CommonNetworkResponse
import com.app.ecarepro.core.network.model.profile.NetworkMyProfileResponse
import com.app.ecarepro.core.network.model.profile.NetworkStaffProfileRequest
import com.app.ecarepro.core.network.model.profile.NetworkUpdateParentProfileRequest
import com.app.ecarepro.core.network.retrofit.service.UserService
import javax.inject.Inject

internal class MyProfileRemoteDataSourceImpl @Inject constructor(
    private val service: UserService,
) : MyProfileRemoteDataSource {

    override suspend fun getProfile(edit: String): NetworkMyProfileResponse =
        service.getMyProfile(edit)

    override suspend fun sendStaffProfileRequest(request: NetworkStaffProfileRequest): CommonNetworkResponse {
        val response = service.sendStaffProfileRequest(request)
        if (response.errorCode != 0) throw Exception(response.message)
        return response
    }

    override suspend fun updateParentProfile(request: NetworkUpdateParentProfileRequest): CommonNetworkResponse {
        val response = service.updateParentProfile(request)
        if (response.errorCode != 0) throw Exception(response.message)
        return response
    }
}

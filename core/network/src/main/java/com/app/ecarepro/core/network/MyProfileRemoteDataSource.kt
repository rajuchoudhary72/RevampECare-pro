package com.app.ecarepro.core.network

import com.app.ecarepro.core.network.model.CommonNetworkResponse
import com.app.ecarepro.core.network.model.profile.NetworkMyProfileResponse
import com.app.ecarepro.core.network.model.profile.NetworkStaffProfileRequest
import com.app.ecarepro.core.network.model.profile.NetworkUpdateParentProfileRequest

interface MyProfileRemoteDataSource {
    suspend fun getProfile(edit: String): NetworkMyProfileResponse
    suspend fun sendStaffProfileRequest(request: NetworkStaffProfileRequest): CommonNetworkResponse
    suspend fun updateParentProfile(request: NetworkUpdateParentProfileRequest): CommonNetworkResponse
}

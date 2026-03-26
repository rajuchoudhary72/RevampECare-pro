package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.profile.MyProfileData
import com.app.ecarepro.core.domain.model.profile.StaffProfileUpdateRequest
import kotlinx.coroutines.flow.Flow

interface MyProfileRepository {
    fun getProfile(): Flow<Result<MyProfileData>>
    fun sendStaffProfileRequest(request: StaffProfileUpdateRequest): Flow<Result<Unit>>
    fun updateParentProfile(mobile: String, email: String, address: String): Flow<Result<Unit>>
}

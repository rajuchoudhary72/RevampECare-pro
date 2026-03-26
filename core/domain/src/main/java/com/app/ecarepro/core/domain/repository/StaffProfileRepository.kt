package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.StaffProfile
import com.app.ecarepro.core.domain.model.StaffProfileDetailsResponse
import kotlinx.coroutines.flow.Flow

interface StaffProfileRepository {
    /**
     * Get staff profiles from the server
     * @return Flow of Result containing list of StaffProfile
     */
    fun getStaffProfiles(): Flow<Result<List<StaffProfile>>>

    /**
     * Get staff profile details from the server
     * @param staffId Staff ID (SID)
     * @return StaffProfileDetailsResponse containing detailed profile information
     */
    suspend fun getStaffProfileDetails(staffId: Int): StaffProfileDetailsResponse
}

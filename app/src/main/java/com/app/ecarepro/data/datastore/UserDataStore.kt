package com.app.ecarepro.data.datastore

import com.app.ecarepro.data.network.model.LoginResponseDto
import com.app.ecarepro.data.network.model.NetworkSchool
import com.app.ecarepro.data.network.model.NetworkUserDetailsDto
import com.app.ecarepro.data.network.model.UserDashboardDto
import com.app.ecarepro.model.Feed
import com.app.ecarepro.model.FeedsDto
import com.app.ecarepro.model.Slide
import kotlinx.coroutines.flow.Flow
import com.app.ecarepro.data.network.Setting

interface UserDataStore {
    suspend fun saveUser(user: NetworkUserDetailsDto)
    suspend fun saveUserDetails(user: LoginResponseDto, schoolCode: String)
    suspend fun getUser(): NetworkUserDetailsDto?
    fun getUsersFlow(): Flow<List<NetworkUserDetailsDto>>
    suspend fun setCurrentUserId(userId: Int)
    suspend fun getCurrentUserId(): Int?
    fun getCurrentUserIdAsFlow(): Flow<Int?>
    suspend fun setCurrentSchoolCode(schoolCode: String)
    suspend fun getCurrentSchoolCode(): String?
    fun getCurrentSchoolCodeAsFlow(): Flow<String?>
    fun getUserAsFlow(): Flow<NetworkUserDetailsDto?>
    suspend fun saveSchoolData(school: NetworkSchool)
    suspend fun getSchoolData(): NetworkSchool?
    fun getSchoolAsFlow(): Flow<NetworkSchool?>
    suspend fun saveFeeds(feeds: FeedsDto)
    fun getFeeds(): Flow<List<Feed>>
    suspend fun saveGeneralSettings(settings: List<Setting>)
    suspend fun isGeneralSettingEnabled(key: String): Boolean
    suspend fun saveDashboardData(school: UserDashboardDto)
    fun getDashboardData(): Flow<UserDashboardDto?>
    suspend fun saveAuthToken(token: String)

    suspend fun getRoleName( ): String?

    suspend fun saveRoleName(roleName: String)
    suspend fun saveUserNameID(userNameId: String)

    suspend fun getUserNameID( ): String?

    suspend fun saveUserType(userType: Int)

    suspend fun getUserType(): Int?


    suspend fun setAsUserAuthenticated(isAuthenticated: Boolean)
    suspend fun isUserAuthenticated(): Boolean
    suspend fun getAuthToken(): String?
    suspend fun saveSlides(sliders: List<Slide>)
    fun getSlides(): Flow<List<Slide>>
    suspend fun clear()
}
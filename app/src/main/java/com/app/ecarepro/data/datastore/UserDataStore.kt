package com.app.ecarepro.data.datastore

import com.app.ecarepro.data.network.model.NetworkSchool
import com.app.ecarepro.data.network.model.NetworkUserDetailsDto
import com.app.ecarepro.data.network.model.UserDashboardDto
import com.app.ecarepro.model.Slide
import kotlinx.coroutines.flow.Flow
import com.app.ecarepro.model.Feed
import com.app.ecarepro.model.FeedsDto
interface UserDataStore {
    suspend fun saveUser(user: NetworkUserDetailsDto)
    suspend fun getUser(): NetworkUserDetailsDto
    fun getUserAsFlow(): Flow<NetworkUserDetailsDto>
    suspend fun saveSchoolData(school: NetworkSchool)
    suspend fun getSchoolData(): NetworkSchool?
    suspend fun saveDashboardData(school: UserDashboardDto)
    fun getDashboardData(): Flow<UserDashboardDto?>
    suspend fun saveFeeds(feeds: FeedsDto)
    fun getFeeds(): Flow<List<Feed>>
    suspend fun saveAuthToken(token: String)
    suspend fun setAsUserAuthenticated(isAuthenticated: Boolean)
    suspend fun isUserAuthenticated(): Boolean
    suspend fun getAuthToken(): String?
    suspend fun saveSlides(sliders: List<Slide>)
    fun getSlides(): Flow<List<Slide>>
    suspend fun clear()
}
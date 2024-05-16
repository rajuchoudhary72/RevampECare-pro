package com.app.ecarepro.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.app.ecarepro.data.database.ECareProDatabase
import com.app.ecarepro.data.database.databases.SchoolDatabase
import com.app.ecarepro.data.database.databases.UserDatabase
import com.app.ecarepro.data.database.model.asNetworkSchool
import com.app.ecarepro.data.database.model.asNetworkUserDetailsDto
import com.app.ecarepro.data.network.model.LoginResponseDto
import com.app.ecarepro.data.network.model.NetworkSchool
import com.app.ecarepro.data.network.model.NetworkUserDetailsDto
import com.app.ecarepro.data.network.model.UserDashboardDto
import com.app.ecarepro.data.network.model.asNetworkSchool
import com.app.ecarepro.data.network.model.asUserEntity
import com.app.ecarepro.model.Feed
import com.app.ecarepro.model.FeedsDto
import com.app.ecarepro.model.Slide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_datastore")

@Suppress("IMPLICIT_NOTHING_TYPE_ARGUMENT_AGAINST_NOT_NOTHING_EXPECTED_TYPE")
class UserDataStoreImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userDatabase: UserDatabase,
    private val schoolDatabase: SchoolDatabase,
    private val eCareProDatabase: ECareProDatabase,
    private val gson: Gson
) : UserDataStore {

    override suspend fun saveUser(user: NetworkUserDetailsDto) {
        //userDatabase.insertUser(user.asUserEntity())
    }

    override suspend fun saveUserDetails(user: LoginResponseDto, schoolCode: String) {
        userDatabase.insertUser(user.asUserEntity().copy(schoolCode = schoolCode))
        val userId = getCurrentUserId()
        if (userId == null || userId == 0)
            setCurrentUserId(userId = user.userID)

    }

    override suspend fun getUser(): NetworkUserDetailsDto? {
        val userId = getCurrentUserId()
        if (userId == null || userId == 0) return null
        return userDatabase.getUser(userId).asNetworkUserDetailsDto()
    }

    override fun getUsersFlow(): Flow<List<NetworkUserDetailsDto>> {
        return userDatabase
            .getUsersFlow()
            .map {
                it.map {
                    it.asNetworkUserDetailsDto().copy(
                        school = schoolDatabase.getSchool(it.schoolCode ?: "").asNetworkSchool()
                    )
                }
            }
    }

    override suspend fun setCurrentUserId(userId: Int) {
        context.dataStore.edit { preferences ->
            preferences[currentUserId] = userId
        }
    }

    override suspend fun getCurrentUserId(): Int? {
        return context.dataStore.data.map { preferences ->
            preferences[currentUserId]
        }.first()
    }

    override fun getCurrentUserIdAsFlow(): Flow<Int?> {
        return context.dataStore.data.map { preferences ->
            preferences[currentUserId]
        }
    }

    override suspend fun setCurrentSchoolCode(schoolCode: String) {
        context.dataStore.edit { preferences ->
            preferences[currentSchoolCode] = schoolCode
        }
    }

    override suspend fun getCurrentSchoolCode(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[currentSchoolCode]
        }.first()
    }

    override fun getCurrentSchoolCodeAsFlow(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[currentSchoolCode]
        }
    }

    override fun getUserAsFlow(): Flow<NetworkUserDetailsDto?> {
        return runBlocking {
            if (getCurrentUserId() == null || getCurrentUserId() == 0) flow {
                emit(
                    null
                )
            }
            else userDatabase.getUserFlow(getCurrentUserId()!!).map { it.asNetworkUserDetailsDto() }
        }
    }

    override suspend fun saveSchoolData(school: NetworkSchool) {
        schoolDatabase.insertSchool(school.asNetworkSchool())
        val schoolCode = getCurrentSchoolCode()
        if (schoolCode.isNullOrEmpty())
            setCurrentSchoolCode(school.schoolCode)
    }

    override suspend fun getSchoolData(): NetworkSchool? {
        val user = getUser()
        if (user == null || user.schoolCode.isNullOrEmpty())
            return null
        return schoolDatabase.getSchool(user.schoolCode).asNetworkSchool()
    }

    override fun getSchoolAsFlow(): Flow<NetworkSchool?> {
        val user = runBlocking { getUser() }
        if (user == null || user.schoolCode.isNullOrEmpty())
            return flowOf(null)
        return schoolDatabase.getSchoolFlow(user.schoolCode).map { it.asNetworkSchool() }
    }

    override suspend fun saveFeeds(feeds: FeedsDto) {
        context.dataStore.edit { preferences ->
            preferences[feedsKey] = gson.toJson(feeds)
        }
    }

    override fun getFeeds(): Flow<List<Feed>> {
        return context.dataStore.data.map { preferences ->
            val json = preferences[feedsKey]
            if (json == null) {
                null
            } else gson.fromJson(json, FeedsDto::class.java)
        }.map { it?.updates ?: emptyList() }
    }

    override suspend fun saveDashboardData(school: UserDashboardDto) {
        context.dataStore.edit { preferences ->
            preferences[dashboardData] = gson.toJson(school)
        }
    }

    override fun getDashboardData(): Flow<UserDashboardDto?> {
        return context.dataStore.data.map { preferences ->
            val json = preferences[dashboardData]
            if (json == null) {
                null
            } else gson.fromJson(json, UserDashboardDto::class.java)
        }
    }


    override suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[authTokenKey] = token
        }
    }

    override suspend fun setAsUserAuthenticated(isAuthenticated: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[isAuthenticatedKey] = isAuthenticated
        }
    }

    override suspend fun isUserAuthenticated(): Boolean {
        return context.dataStore.data.map { preferences ->
            preferences[isAuthenticatedKey] ?: false
        }.first()
    }

    override suspend fun getAuthToken(): String? {
        val userId = getCurrentUserId()
        if (userId == null || userId == 0) return null
        return userDatabase.getUser(userId).authToken
    }

    override suspend fun saveSlides(sliders: List<Slide>) {

        context.dataStore.edit { preferences ->
            preferences[slidesKey] = gson.toJson(sliders)
        }
    }

    override fun getSlides(): Flow<List<Slide>> {
        return context.dataStore.data.map { preferences ->
            val itemType = object : TypeToken<List<Slide>>() {}.type
            gson.fromJson(preferences[slidesKey], itemType)
        }
    }

    override suspend fun clear() {
        context.dataStore.edit { it.clear() }
        eCareProDatabase.clearAllTables()
    }


    companion object {
        private val currentUserId = intPreferencesKey("currentUserId")
        private val currentSchoolCode = stringPreferencesKey("currentSchoolCode")
        private val schoolDataKey = stringPreferencesKey("schoolData")
        private val feedsKey = stringPreferencesKey("feeds")
        private val dashboardData = stringPreferencesKey("dashboardData")
        private val userPreferenceKey = stringPreferencesKey("user")
        private val authTokenKey = stringPreferencesKey("auth_token")
        private val slidesKey = stringPreferencesKey("slides")
        private val isAuthenticatedKey = booleanPreferencesKey("isAuthenticated")
    }
}
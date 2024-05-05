package com.app.ecarepro.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.app.ecarepro.data.database.databases.UserDatabase
import com.app.ecarepro.data.database.model.asNetworkUserDetailsDto
import com.app.ecarepro.data.network.model.LoginResponseDto
import com.app.ecarepro.data.network.model.NetworkSchool
import com.app.ecarepro.data.network.model.NetworkUserDetailsDto
import com.app.ecarepro.data.network.model.UserDashboardDto
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_datastore")

@Suppress("IMPLICIT_NOTHING_TYPE_ARGUMENT_AGAINST_NOT_NOTHING_EXPECTED_TYPE")
class UserDataStoreImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userDatabase: UserDatabase,
    private val gson: Gson
) : UserDataStore {

    override suspend fun saveUser(user: NetworkUserDetailsDto) {
        //userDatabase.insertUser(user.asUserEntity())
    }

    override suspend fun saveUserDetails(user: LoginResponseDto) {
        userDatabase.insertUser(user.asUserEntity())
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
        return userDatabase.getUsersFlow().map { it.map { it.asNetworkUserDetailsDto() } }
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
        context.dataStore.edit { preferences ->
            preferences[schoolDataKey] = gson.toJson(school)
        }
    }

    override suspend fun getSchoolData(): NetworkSchool? {
        return context.dataStore.data.map { preferences ->
            val json = preferences[schoolDataKey]
            if (json == null) {
                null
            } else gson.fromJson(json, NetworkSchool::class.java)
        }.first()
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
            gson.fromJson<List<Slide>>(preferences[slidesKey], itemType)
        }
    }

    override suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }


    companion object {
        private val currentUserId = intPreferencesKey("currentUserId")
        private val schoolDataKey = stringPreferencesKey("schoolData")
        private val feedsKey = stringPreferencesKey("feeds")
        private val dashboardData = stringPreferencesKey("dashboardData")
        private val userPreferenceKey = stringPreferencesKey("user")
        private val authTokenKey = stringPreferencesKey("auth_token")
        private val slidesKey = stringPreferencesKey("slides")
        private val isAuthenticatedKey = booleanPreferencesKey("isAuthenticated")
    }
}
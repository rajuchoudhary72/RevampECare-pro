package com.app.ecarepro.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.app.ecarepro.data.network.model.NetworkSchool
import com.app.ecarepro.model.Slide
import com.app.ecarepro.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_datastore")

class UserDataStoreImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) : UserDataStore {

    override suspend fun saveUser(user: User) {
        context.dataStore.edit { preferences ->
            preferences[userPreferenceKey] = gson.toJson(user)
        }
    }

    override suspend fun getUser(): User {
        return context.dataStore.data.map { preferences ->
            gson.fromJson(preferences[userPreferenceKey], User::class.java)
        }.first()
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
            } else
                gson.fromJson(json, NetworkSchool::class.java)
        }.first()
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
        return context.dataStore.data.map { preferences ->
            preferences[authTokenKey]
        }.first()
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


    companion object {
        private val schoolDataKey = stringPreferencesKey("schoolData")
        private val userPreferenceKey = stringPreferencesKey("user")
        private val authTokenKey = stringPreferencesKey("auth_token")
        private val slidesKey = stringPreferencesKey("slides")
        private val isAuthenticatedKey = booleanPreferencesKey("isAuthenticated")
    }
}
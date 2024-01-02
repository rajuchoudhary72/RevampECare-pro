package com.app.ecarepro.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
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
        private val userPreferenceKey = stringPreferencesKey("user")
        private val authTokenKey = stringPreferencesKey("auth_token")
        private val slidesKey = stringPreferencesKey("slides")
    }
}
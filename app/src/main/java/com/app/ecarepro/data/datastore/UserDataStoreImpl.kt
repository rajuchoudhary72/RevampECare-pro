package com.app.ecarepro.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.app.ecarepro.model.User
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserDataStoreImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson,
) : UserDataStore {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_datastore")

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


    companion object {
        val userPreferenceKey = stringPreferencesKey("user")
    }
}
package com.app.ecarepro.ui.language.dynamic_language.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalizationPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val SELECTED_LANGUAGE = stringPreferencesKey("selected_language")
        const val LANGUAGE_ENGLISH = "en"
        const val LANGUAGE_HINDI = "hi"
    }

    val selectedLanguage: Flow<String> = dataStore.data.map { preferences ->
        preferences[SELECTED_LANGUAGE] ?: LANGUAGE_ENGLISH
    }

    suspend fun setSelectedLanguage(language: String) {
        dataStore.edit { preferences ->
            preferences[SELECTED_LANGUAGE] = language
        }
    }
}

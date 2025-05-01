package com.app.ecarepro.ui.language

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

    private val _selectedLanguage = MutableStateFlow(getSavedLanguage())
    val selectedLanguage: Flow<String> = _selectedLanguage

    // Save language to SharedPreferences
    fun saveLanguage(languageCode: String) {
        sharedPreferences.edit().putString("app_language", languageCode).apply()
        _selectedLanguage.update { languageCode }
    }

    // Get saved language, default to English
     fun getSavedLanguage(): String {
        return sharedPreferences.getString("app_language", "en") ?: "en"
    }
}

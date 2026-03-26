package com.app.ecarepro.feature.setting

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val PREFS_LANGUAGE = "language_prefs"
private const val KEY_LANGUAGE_CODE = "language_code"
private const val DEFAULT_LANGUAGE = "en"

@Singleton
class LanguageManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val prefs by lazy { context.getSharedPreferences(PREFS_LANGUAGE, Context.MODE_PRIVATE) }

    fun getCurrentLanguage(): String = prefs.getString(KEY_LANGUAGE_CODE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE

    fun setLanguage(code: String) {
        prefs.edit().putString(KEY_LANGUAGE_CODE, code).apply()
    }
}

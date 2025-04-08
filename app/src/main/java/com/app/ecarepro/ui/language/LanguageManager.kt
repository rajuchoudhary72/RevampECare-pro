package com.app.ecarepro.ui.language

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.*

object LanguageManager {

    private const val PREF_NAME = "app_locale_pref"
    private const val SELECTED_LANGUAGE = "selected_language"

    fun setLocale(context: Context): Context {
        return updateResources(context, getLanguage(context))
    }

    fun setNewLocale(context: Context, language: String) {
        persistLanguage(context, language)
    }

    fun getLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(SELECTED_LANGUAGE, Locale.getDefault().language) ?: "en"
    }

    private fun persistLanguage(context: Context, language: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(SELECTED_LANGUAGE, language).apply()
    }

    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            context
        }
    }
}

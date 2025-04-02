package com.app.ecarepro.ui.language


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import com.app.ecarepro.ui.MainActivity
import java.util.Locale

object LanguageManager {

    private const val LANGUAGE_KEY = "app_language"

    // Save selected language in SharedPreferences
    fun saveLanguage(context: Context, languageCode: String) {
        val sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(LANGUAGE_KEY, languageCode).apply()
    }

    // Get saved language, default to English
    fun getSavedLanguage(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString(LANGUAGE_KEY, "en") ?: "en"
    }

    // Apply language and restart app
    fun setLanguage(activity: Activity, languageCode: String) {
        saveLanguage(activity, languageCode)

        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        activity.baseContext.resources.updateConfiguration(
            config,
            activity.baseContext.resources.displayMetrics
        )

    }

    fun applyLanguage(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }



    // Restart the app
     fun languageSetAndRestartApp(activity: Activity, languageCode: String) {
        setLanguage(activity, languageCode)
        val intent = Intent(activity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(intent)
        Runtime.getRuntime().exit(0) // Force restart
    }
}

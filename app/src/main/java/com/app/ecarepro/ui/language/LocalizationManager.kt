package com.app.ecarepro.ui.language

import android.app.Activity
import com.app.ecarepro.data.repository.TranslationRepository
import com.app.ecarepro.ui.language.model.TranslationItem
import java.lang.ref.WeakReference

object LocalizationManager {
    private lateinit var repository: TranslationRepository
    private var isInitialized = false
    private val activities = mutableListOf<WeakReference<Activity>>()

    // Call this in Application.onCreate()
    fun initialize(repository: TranslationRepository) {
        this.repository = repository
        isInitialized = true
    }

    // Language toggle (connect to your existing language selection)

    // Get translation for a key
    fun getString(key: String): String {
        if (!isInitialized) return key
        return repository.getTranslation(key)
    }

    // Load translations from Google Sheets
    suspend fun loadTranslations(
        spreadsheetId: String,
        range: String,
        apiKey: String
    ): Result<List<TranslationItem>> {
        if (!isInitialized) return Result.failure(IllegalStateException("LocalizationManager not initialized"))
        return repository.fetchTranslations(spreadsheetId, range, apiKey)
    }


    fun registerActivity(activity: Activity) {
        activities.add(WeakReference(activity))
        // Clean up any null references
        activities.removeAll { it.get() == null }
    }

    fun notifyTranslationsChanged() {
        // Tell activities to recreate themselves
        activities.forEach { ref ->
            ref.get()?.runOnUiThread {
                ref.get()?.recreate()
            }
        }
    }
}
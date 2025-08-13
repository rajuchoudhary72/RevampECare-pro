package com.app.ecarepro.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.app.ecarepro.data.network.service.AppService
import com.app.ecarepro.data.repository.TranslationRepository
import com.app.ecarepro.ui.language.LanguageManager
import com.app.ecarepro.ui.language.model.TranslationItem
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TranslationRepositoryImpl @Inject constructor(
    private val googleSheetsService: AppService,
    private val sharedPreferences: SharedPreferences,
     private val context: Context
) : TranslationRepository {

    override suspend fun fetchTranslations(
        spreadsheetId: String,
        range: String,
        apiKey: String
    ): Result<List<TranslationItem>> {
        return try {
            val response = googleSheetsService.getSheetValues(spreadsheetId, range, apiKey)
            Log.e("TranslationRepo", "fetch data from google sheet "+response.values)

            val translations = parseTranslations(response.values)
            saveTranslations(translations)
            Result.success(translations)
        } catch (error: Throwable) {
            Log.e("TranslationRepo", "Error fetching translations", error)
            Result.failure(error)
        }
    }

    override suspend fun saveTranslations(translations: List<TranslationItem>) {
        val jsonTranslations = Gson().toJson(translations)
        sharedPreferences.edit().putString(TRANSLATIONS_KEY, jsonTranslations).apply()
        Log.e("TranslationRepo", "Google sheet data saved")

    }

    override suspend fun getTranslations(): List<TranslationItem> {
        val jsonTranslations = sharedPreferences.getString(TRANSLATIONS_KEY, null)
        return if (jsonTranslations != null) {
            try {
                val type = object : TypeToken<List<TranslationItem>>() {}.type
                Gson().fromJson(jsonTranslations, type)
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    override fun getTranslation(key: String): String {

         val isHindi =LanguageManager.getLanguage(context) == "hi"

        val jsonTranslations = sharedPreferences.getString(TRANSLATIONS_KEY, null) ?: return key

        try {
            val type = object : TypeToken<List<TranslationItem>>() {}.type
            val translations: List<TranslationItem> = Gson().fromJson(jsonTranslations, type)

            val item = translations.find { it.key == key }
            return if (isHindi) item?.hindi ?: key else item?.english ?: key
        } catch (e: Exception) {
            return key
        }
    }

    private fun parseTranslations(values: List<List<String>>): List<TranslationItem> {
        // Skip header row if present
        val dataRows = if (values.firstOrNull()?.getOrNull(0) == "key") {
            values.drop(1)
        } else {
            values
        }

        return dataRows.mapNotNull { row ->
            if (row.size >= 3) {
                TranslationItem(
                    key = row[0],
                    english = row[1],
                    hindi = row[2]
                )
            } else null
        }
    }

    companion object {
        private const val TRANSLATIONS_KEY = "app_translations"
    }
}
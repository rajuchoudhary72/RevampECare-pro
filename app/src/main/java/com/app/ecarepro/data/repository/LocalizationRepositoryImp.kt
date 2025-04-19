package com.app.ecarepro.data.repository

import android.util.Log
import com.app.ecarepro.data.database.dao.LocalizationDao
import com.app.ecarepro.data.database.model.LocalizationEntity
import com.app.ecarepro.data.network.service.GoogleSheetsApiService
import com.app.ecarepro.ui.language.dynamic_language.SheetsApiResponseModel
import com.app.ecarepro.ui.language.dynamic_language.preferences.LocalizationPreferences
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class LocalizationRepositoryImp @Inject constructor(
    private val googleSheetsApiService: GoogleSheetsApiService,
    private val localizationDao: LocalizationDao,
    private val localizationPreferences: LocalizationPreferences
):LocalizationRepository {
    // Constants
    private val spreadsheetId = "1bpl7AGvrRotwsyMjFRm-h10pB5WZ31PJ5Y0naCr4atk"
    private val sheetRange = "Localization!A:C"
    private val apiKey = "884613644316-e20mupijchj2voq34jus046aj9knbj5i.apps.googleusercontent.com"

    // Get current selected language
    val selectedLanguage = localizationPreferences.selectedLanguage

    // Get all localizations from database
    val allLocalizations = localizationDao.getAllLocalizations()

    // Set selected language
    override suspend fun setLanguage(language: String) {
        localizationPreferences.setSelectedLanguage(language)
    }

    // Fetch localizations from Google Sheets and store in Room
    override suspend fun refreshLocalizations() {
        try {
            val response = googleSheetsApiService.getSheetValues(
                spreadsheetId = spreadsheetId,
                range = sheetRange,
                apiKey = apiKey
            )

            // Process and convert data
            val localizations = processSheetData(response)

            // Store in database
            localizationDao.refreshLocalizations(localizations)
        } catch (e: Exception) {
            // Handle errors
            Log.e("LocalizationRepo", "Error fetching localizations", e)
            throw e
        }
    }

    override suspend fun processSheetData(response: SheetsApiResponseModel): List<LocalizationEntity> {
        val localizations = mutableListOf<LocalizationEntity>()

        // Skip header row if it exists
        val dataRows = if (response.values.isNotEmpty()) {
            response.values.drop(1)
        } else {
            emptyList()
        }

        // Process each row (assuming: column A=key, B=English, C=Hindi)
        for (row in dataRows) {
            if (row.size >= 3) {
                val key = row[0]
                val englishValue = row[1]
                val hindiValue = row[2]

                localizations.add(
                    LocalizationEntity(
                        key = key,
                        englishValue = englishValue,
                        hindiValue = hindiValue
                    )
                )
            }
        }

        return localizations
    }



    // Get string based on current language
        override   suspend fun getString(key: String): String {
            val localization = localizationDao.getLocalizationByKey(key)
            val language = localizationPreferences.selectedLanguage.first()

            return when {
                localization == null -> key  // Fallback to key if not found
                language == LocalizationPreferences.LANGUAGE_HINDI -> localization.hindiValue
                else -> localization.englishValue
            }
        }
    }

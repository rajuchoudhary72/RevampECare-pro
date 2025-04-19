package com.app.ecarepro.data.repository

import com.app.ecarepro.data.database.model.LocalizationEntity
import com.app.ecarepro.ui.language.dynamic_language.SheetsApiResponseModel

interface LocalizationRepository {
    suspend fun setLanguage(language: String)
    suspend fun refreshLocalizations()
    suspend fun processSheetData(response: SheetsApiResponseModel):List<LocalizationEntity>
    suspend fun getString(key: String):String
}
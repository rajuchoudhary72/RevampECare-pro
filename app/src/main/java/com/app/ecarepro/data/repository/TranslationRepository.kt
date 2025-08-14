package com.app.ecarepro.data.repository

import com.app.ecarepro.ui.language.model.TranslationItem

interface TranslationRepository {

    suspend fun fetchTranslations(
        spreadsheetId: String,
        range: String,
        apiKey: String
    ): Result<List<TranslationItem>>

    suspend fun saveTranslations(translations: List<TranslationItem>)

    suspend fun getTranslations(): List<TranslationItem>

    fun getTranslation(key: String): String

}
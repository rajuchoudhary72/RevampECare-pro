package com.app.ecarepro.ui.language.dynamic_language.manager

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.app.ecarepro.ui.language.dynamic_language.LocalizationViewModel
import javax.inject.Inject

class LocalizationManager @Inject constructor(
    private val viewModel: LocalizationViewModel
) {
    // Get localized string
    suspend fun getString(key: String): String {
        return viewModel.getString(key)
    }

    // Extension function for context
    suspend fun Context.getLocalizedString(key: String): String {
        // Use ViewModelProvider to get the ViewModel instance
        val localizationViewModel = ViewModelProvider(this as ComponentActivity)[LocalizationViewModel::class.java]
        return localizationViewModel.getString(key)
    }
}
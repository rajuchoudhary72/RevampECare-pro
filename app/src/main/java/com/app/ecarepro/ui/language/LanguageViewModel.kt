package com.app.ecarepro.ui.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val languageRepository: LanguageRepository
) : ViewModel() {

    // Observe selected language
    val selectedLanguage: StateFlow<String> =
        languageRepository.selectedLanguage.stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            "en" // Default to English
        )

    // Change language and save it
    fun changeLanguage(languageCode: String) {
        viewModelScope.launch {
            languageRepository.saveLanguage(languageCode)
        }
    }
}

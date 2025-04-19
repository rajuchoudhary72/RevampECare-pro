package com.app.ecarepro.ui.language.dynamic_language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.repository.LocalizationRepository
import com.app.ecarepro.data.repository.LocalizationRepositoryImp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalizationViewModel @Inject constructor(
    private val localizationRepository: LocalizationRepositoryImp
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Currently selected language
    val selectedLanguage = localizationRepository.selectedLanguage

    // All localizations as mapped by key
    val localizations = localizationRepository.allLocalizations
        .map { list ->
            // Convert the list to a map for easier access
            list.associateBy { it.key }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    // Refresh localizations from Google Sheets
    fun refreshLocalizations() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                localizationRepository.refreshLocalizations()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to refresh localizations"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Change language
    fun setLanguage(language: String) {
        viewModelScope.launch {
            localizationRepository.setLanguage(language)
        }
    }

    // Get a localized string by key
    suspend fun getString(key: String): String {
        return localizationRepository.getString(key)
    }
}
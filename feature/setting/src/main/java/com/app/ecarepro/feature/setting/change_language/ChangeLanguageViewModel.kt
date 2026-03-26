package com.app.ecarepro.feature.setting.change_language

import androidx.compose.runtime.Immutable
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.feature.setting.LanguageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ChangeLanguageViewModel @Inject constructor(
    private val languageManager: LanguageManager,
) : BaseViewModel<ChangeLanguageIntent, ChangeLanguageEvent>() {

    private val _uiState = MutableStateFlow(
        ChangeLanguageUiState(selectedLanguageCode = languageManager.getCurrentLanguage())
    )
    val uiState = _uiState.asStateFlow()

    override fun handleIntent(intent: ChangeLanguageIntent) {
        when (intent) {
            is ChangeLanguageIntent.OnLanguageSelected -> _uiState.update { it.copy(selectedLanguageCode = intent.code) }
            is ChangeLanguageIntent.OnConfirm -> {
                languageManager.setLanguage(_uiState.value.selectedLanguageCode)
                sendEvent(ChangeLanguageEvent.RestartApp)
            }
        }
    }
}

@Immutable
data class ChangeLanguageUiState(
    val selectedLanguageCode: String = "en",
    val languages: List<LanguageOption> = listOf(
        LanguageOption("English", "en"),
        LanguageOption("Hindi", "hi"),
        LanguageOption("Gujrati", "gu"),
    ),
)

data class LanguageOption(val name: String, val code: String)

sealed interface ChangeLanguageIntent {
    data class OnLanguageSelected(val code: String) : ChangeLanguageIntent
    data object OnConfirm : ChangeLanguageIntent
}

sealed interface ChangeLanguageEvent {
    data object NavigateBack : ChangeLanguageEvent
    data object RestartApp : ChangeLanguageEvent
}

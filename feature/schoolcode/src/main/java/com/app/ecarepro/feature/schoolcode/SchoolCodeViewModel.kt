package com.app.ecarepro.feature.schoolcode

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.InvalidSchoolCodeException
import com.app.ecarepro.core.domain.repository.SchoolRepository
import com.app.ecarepro.core.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SchoolCodeViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository,
) : BaseViewModel<SchoolCodeIntent, SchoolCodeEvent>() {
    private val _uiState = MutableStateFlow(SchoolCodeUiState())
    val uiState: StateFlow<SchoolCodeUiState> = _uiState.asStateFlow()

    override fun handleIntent(intent: SchoolCodeIntent) {
        when (intent) {
            is SchoolCodeIntent.OnCodeChanged -> {
                updateCode(intent.code)
            }

            SchoolCodeIntent.OnNextClicked -> {
                verifySchoolCode()
            }

            SchoolCodeIntent.OnFindCodeClicked -> {
                sendEvent(SchoolCodeEvent.NavigateToSearchSchoolScreen)
            }

            SchoolCodeIntent.OnErrorShown -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun updateCode(code: String) {
        _uiState.update { it.copy(schoolCode = code) }
    }

    private fun verifySchoolCode() {
        viewModelScope.launch {
            val schoolCode = uiState.value.schoolCode ?: return@launch
            schoolRepository
                .getSchoolDetails(schoolCode)
                .onStart {
                    _uiState.update { it.copy(isLoading = true) }
                }
                .collect { result ->
                    _uiState.update { it.copy(isLoading = false) }
                    if (result.isSuccess) {
                        sendEvent(SchoolCodeEvent.NavigateToNextScreen)
                    } else {
                        val exception = result.exceptionOrNull()
                        val message = if (exception is InvalidSchoolCodeException) {
                            SnackbarMessage(
                                text = exception.message ?: "An unknown error occurred",
                                type = MessageType.WARNING,
                            )
                        } else {
                            val errorMessage =
                                result.exceptionOrNull()?.message ?: "An unknown error occurred"
                            SnackbarMessage(errorMessage, type = MessageType.ERROR)
                        }
                        _uiState.update { it.copy(errorMessage = message) }
                    }
                }
        }
    }
}

sealed interface SchoolCodeIntent {
    data class OnCodeChanged(val code: String) : SchoolCodeIntent
    data object OnNextClicked : SchoolCodeIntent
    data object OnFindCodeClicked : SchoolCodeIntent
    data object OnErrorShown : SchoolCodeIntent
}

// One-time navigation events from ViewModel to UI
sealed interface SchoolCodeEvent {
    data object NavigateToNextScreen : SchoolCodeEvent
    data object NavigateToSearchSchoolScreen : SchoolCodeEvent
}

@Immutable
data class SchoolCodeUiState(
    val schoolCode: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: SnackbarMessage? = null,
) {
    val isCodeEntered = schoolCode.isNullOrEmpty().not()
}
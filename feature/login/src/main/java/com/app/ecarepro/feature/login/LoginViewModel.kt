package com.app.ecarepro.feature.login

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.SchoolDetail
import com.app.ecarepro.core.domain.repository.SchoolRepository
import com.app.ecarepro.core.ui.BaseViewModel
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository,
) : BaseViewModel<LoginIntent, LoginEvent>() {
    private val schoolCode = MutableStateFlow("")
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    override fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.OnUsernameChanged -> {
                _uiState.update { it.copy(username = intent.username) }
            }

            is LoginIntent.OnPasswordChanged -> {
                _uiState.update { it.copy(password = intent.password) }
            }

            LoginIntent.OnLoginClicked -> {
                // For now, let's assume it's successful and navigate
                sendEvent(LoginEvent.NavigateToMainScreen)
            }

            LoginIntent.OnForgotPasswordClicked -> {
                sendEvent(LoginEvent.NavigateToForgotPasswordScreen)
            }

            LoginIntent.OnChangeSchoolClicked -> {
                sendEvent(LoginEvent.NavigateBackToSchoolCode)
            }

            LoginIntent.OnHelpClicked -> {
                sendEvent(LoginEvent.NavigateToHelpScreen)
            }

            LoginIntent.OnErrorShown -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }

    fun setSchoolCode(code: String) {
        schoolCode.value = code
        viewModelScope.launch {
            _uiState.update { it.copy(schoolDetails = schoolRepository.getSchoolDetail(code).first()) }
        }
    }
}

// Intents from the UI to the ViewModel
sealed interface LoginIntent {
    data class OnUsernameChanged(val username: String) : LoginIntent
    data class OnPasswordChanged(val password: String) : LoginIntent
    data object OnLoginClicked : LoginIntent
    data object OnForgotPasswordClicked : LoginIntent
    data object OnChangeSchoolClicked : LoginIntent
    data object OnHelpClicked : LoginIntent
    data object OnErrorShown : LoginIntent
}

// One-time events from the ViewModel to the UI for navigation or showing Snackbars
sealed interface LoginEvent {
    data object NavigateToMainScreen : LoginEvent
    data object NavigateToForgotPasswordScreen : LoginEvent
    data object NavigateBackToSchoolCode : LoginEvent
    data object NavigateToHelpScreen : LoginEvent
}

// Represents the state of the LoginScreen
@Immutable
data class LoginUiState(
    val schoolDetails: SchoolDetail? = null,
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: SnackbarMessage? = null,
) {
    val areCredentialsEntered: Boolean = username.isNotBlank() && password.isNotBlank()
}
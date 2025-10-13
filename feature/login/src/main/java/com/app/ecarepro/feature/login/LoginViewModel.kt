package com.app.ecarepro.feature.login

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.SchoolDetail
import com.app.ecarepro.core.domain.repository.SchoolRepository
import com.app.ecarepro.core.ui.viewmodel.AssistedViewModelFactory
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.feature.login.navigation.LoginNavigationGraph
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel(assistedFactory = LoginViewModel.Factory::class)
class LoginViewModel @AssistedInject constructor(
    @Assisted val navKey: LoginNavigationGraph.Login,
    private val schoolRepository: SchoolRepository,
) : BaseViewModel<LoginIntent, LoginEvent>() {
    private val schoolCode = navKey.schoolCode
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        fetchSchool(schoolCode)
    }

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

    private fun fetchSchool(schoolCode: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(schoolDetails = schoolRepository.getSchoolDetail(schoolCode).first()) }
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<LoginNavigationGraph.Login, LoginViewModel> {
        override fun create(param: LoginNavigationGraph.Login): LoginViewModel
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
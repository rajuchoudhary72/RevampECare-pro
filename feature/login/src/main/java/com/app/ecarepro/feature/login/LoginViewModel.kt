package com.app.ecarepro.feature.login

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.SchoolDetail
import com.app.ecarepro.core.domain.model.User
import com.app.ecarepro.core.domain.repository.SchoolRepository
import com.app.ecarepro.core.domain.repository.UserRepository
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
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = LoginViewModel.Factory::class)
class LoginViewModel @AssistedInject constructor(
    @Assisted val navKey: LoginNavigationGraph.Login,
    private val schoolRepository: SchoolRepository,
    private val userRepository: UserRepository,
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
                _uiState.update { it.copy(username = intent.username, errorMessage = null) }
            }

            is LoginIntent.OnPasswordChanged -> {
                _uiState.update { it.copy(password = intent.password, errorMessage = null) }
            }

            LoginIntent.OnLoginClicked -> {
                login()
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

    private fun login() {
        viewModelScope.launch {
            userRepository.login(
                userName = uiState.value.username,
                password = uiState.value.password,
                schoolCode = schoolCode
            )
                .onStart {
                    _uiState.update { it.copy(isLoading = true) }
                }
                .collect { result ->
                    result
                        .onSuccess { user ->
                            _uiState.update { it.copy(isLoading = false) }
                            sendEvent(LoginEvent.NavigateToMainScreen(user))
                        }
                        .onFailure { error: Throwable ->
                            val errorMessage = SnackbarMessage(error.errorMessage())
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = errorMessage
                                )
                            }
                            sendEvent(LoginEvent.ShowMessage(errorMessage))
                        }

                }
        }
    }

    private fun fetchSchool(schoolCode: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    schoolDetails = schoolRepository.getSchoolDetail(
                        schoolCode
                    ).first()
                )
            }
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

sealed interface LoginEvent {
    data class NavigateToMainScreen(val user: User) : LoginEvent
    data object NavigateToForgotPasswordScreen : LoginEvent
    data object NavigateBackToSchoolCode : LoginEvent
    data object NavigateToHelpScreen : LoginEvent

    data class ShowMessage(val message: SnackbarMessage) : LoginEvent
}

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
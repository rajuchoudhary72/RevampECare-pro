package com.app.ecarepro.feature.setting.change_password

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.repository.SettingsRepository
import com.app.ecarepro.core.domain.repository.UserRepository
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val repository: SettingsRepository,
    private val userRepository: UserRepository,
) : BaseViewModel<ChangePasswordIntent, ChangePasswordEvent>() {

    private val username: String = runBlocking { userRepository.getActiveUser()?.name ?: "" }

    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState = _uiState.asStateFlow()

    override fun handleIntent(intent: ChangePasswordIntent) {
        when (intent) {
            is ChangePasswordIntent.OnCurrentPasswordChanged -> _uiState.update { it.copy(currentPassword = intent.value) }
            is ChangePasswordIntent.OnNewPasswordChanged -> _uiState.update { it.copy(newPassword = intent.value) }
            is ChangePasswordIntent.OnConfirmPasswordChanged -> _uiState.update { it.copy(confirmPassword = intent.value) }
            is ChangePasswordIntent.ToggleCurrentPasswordVisibility -> _uiState.update { it.copy(showCurrentPassword = !it.showCurrentPassword) }
            is ChangePasswordIntent.ToggleNewPasswordVisibility -> _uiState.update { it.copy(showNewPassword = !it.showNewPassword) }
            is ChangePasswordIntent.ToggleConfirmPasswordVisibility -> _uiState.update { it.copy(showConfirmPassword = !it.showConfirmPassword) }
            is ChangePasswordIntent.OnSubmit -> submit()
        }
    }

    private fun submit() {
        _uiState.update { it.copy(showFieldErrors = true) }
        if (_uiState.value.hasAnyError) return
        if (_uiState.value.isLoading) return

        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            repository.changePassword(username, _uiState.value.newPassword).collect { result ->
                result.onSuccess { message ->
                    _uiState.update { it.copy(isLoading = false) }
                    userRepository.clearSession()
                    sendEvent(ChangePasswordEvent.ShowMessage(SnackbarMessage(message, MessageType.SUCCESS)))
                    sendEvent(ChangePasswordEvent.RedirectToLogin)
                }
                result.onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    sendEvent(ChangePasswordEvent.ShowMessage(
                        SnackbarMessage(error.message ?: "Something went wrong", MessageType.ERROR)
                    ))
                }
            }
        }
    }
}

@Immutable
data class ChangePasswordUiState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val showCurrentPassword: Boolean = false,
    val showNewPassword: Boolean = false,
    val showConfirmPassword: Boolean = false,
    val isLoading: Boolean = false,
    val showFieldErrors: Boolean = false,
) {
    val currentPasswordError: String?
        get() = if (showFieldErrors && currentPassword.isEmpty()) "Current password is required" else null

    val newPasswordError: String?
        get() {
            if (!showFieldErrors) return null
            if (newPassword.isEmpty()) return "New password is required"
            if (newPassword.contains("#") || newPassword.contains("&")) return "Password must not contain # or &"
            if (!isValidPasswordFormat(newPassword)) return "Password must be 5-10 characters with at least 1 number or special character"
            if (currentPassword.isNotEmpty() && newPassword == currentPassword) return "New password must be different from current password"
            return null
        }

    val confirmPasswordError: String?
        get() = if (showFieldErrors && newPassword != confirmPassword) "Passwords do not match" else null

    val hasAnyError: Boolean
        get() = currentPasswordError != null || newPasswordError != null || confirmPasswordError != null

    private fun isValidPasswordFormat(password: String): Boolean {
        if (password.length !in 5..10) return false
        return password.any { char -> char.isDigit() || (!char.isLetterOrDigit() && char != '#' && char != '&') }
    }
}

sealed interface ChangePasswordIntent {
    data class OnCurrentPasswordChanged(val value: String) : ChangePasswordIntent
    data class OnNewPasswordChanged(val value: String) : ChangePasswordIntent
    data class OnConfirmPasswordChanged(val value: String) : ChangePasswordIntent
    data object ToggleCurrentPasswordVisibility : ChangePasswordIntent
    data object ToggleNewPasswordVisibility : ChangePasswordIntent
    data object ToggleConfirmPasswordVisibility : ChangePasswordIntent
    data object OnSubmit : ChangePasswordIntent
}

sealed interface ChangePasswordEvent {
    data class ShowMessage(val message: SnackbarMessage) : ChangePasswordEvent
    data object RedirectToLogin : ChangePasswordEvent
}

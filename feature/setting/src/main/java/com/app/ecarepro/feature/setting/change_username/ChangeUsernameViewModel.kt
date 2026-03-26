package com.app.ecarepro.feature.setting.change_username

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
class ChangeUsernameViewModel @Inject constructor(
    private val repository: SettingsRepository,
    private val userRepository: UserRepository,
) : BaseViewModel<ChangeUsernameIntent, ChangeUsernameEvent>() {

    private val _uiState = MutableStateFlow(
        ChangeUsernameUiState(
            existingUsername = runBlocking { userRepository.getActiveUser()?.name ?: "" }
        )
    )
    val uiState = _uiState.asStateFlow()

    override fun handleIntent(intent: ChangeUsernameIntent) {
        when (intent) {
            is ChangeUsernameIntent.OnNewUsernameChanged -> _uiState.update { it.copy(newUsername = intent.value) }
            is ChangeUsernameIntent.OnSubmit -> submit()
        }
    }

    private fun submit() {
        _uiState.update { it.copy(showFieldErrors = true) }
        val newUsername = _uiState.value.newUsername.trim()
        if (newUsername.isEmpty()) return
        if (_uiState.value.isLoading) return

        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val existingUsername = _uiState.value.existingUsername
            repository.changeUsername(existingUsername, newUsername).collect { result ->
                result.onSuccess { message ->
                    _uiState.update { it.copy(isLoading = false) }
                    userRepository.clearSession()
                    sendEvent(ChangeUsernameEvent.ShowMessage(SnackbarMessage(message, MessageType.SUCCESS)))
                    sendEvent(ChangeUsernameEvent.RedirectToLogin)
                }
                result.onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    sendEvent(ChangeUsernameEvent.ShowMessage(
                        SnackbarMessage(error.message ?: "Something went wrong", MessageType.ERROR)
                    ))
                }
            }
        }
    }
}

@Immutable
data class ChangeUsernameUiState(
    val existingUsername: String = "",
    val newUsername: String = "",
    val isLoading: Boolean = false,
    val showFieldErrors: Boolean = false,
) {
    val newUsernameError: String?
        get() = if (showFieldErrors && newUsername.trim().isEmpty()) "New username is required" else null
}

sealed interface ChangeUsernameIntent {
    data class OnNewUsernameChanged(val value: String) : ChangeUsernameIntent
    data object OnSubmit : ChangeUsernameIntent
}

sealed interface ChangeUsernameEvent {
    data class ShowMessage(val message: SnackbarMessage) : ChangeUsernameEvent
    data object RedirectToLogin : ChangeUsernameEvent
}

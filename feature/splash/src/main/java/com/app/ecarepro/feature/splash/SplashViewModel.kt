package com.app.ecarepro.feature.splash

import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.User
import com.app.ecarepro.core.domain.repository.SchoolRepository
import com.app.ecarepro.core.domain.repository.UserRepository
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository,
    private val userRepository: UserRepository,
) : BaseViewModel<SplashIntent, SplashEvent>() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        initializeAndNavigate()
    }

    private fun initializeAndNavigate() {
        viewModelScope.launch {
            try {
                val activeUser = userRepository.getActiveUser()
                val schoolLogo = activeUser?.let { user ->
                    schoolRepository.getSchoolDetail(user.schoolCode).firstOrNull()?.logo
                }

                _uiState.update { it.copy(logo = schoolLogo) }

                // Keep the splash visible for a minimum duration
                delay(2.seconds)

                if (activeUser == null) {
                    sendEvent(SplashEvent.NavigateToLogin)
                } else {
                    sendEvent(SplashEvent.NavigateToDashboard(activeUser))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                sendEvent(SplashEvent.NavigateToLogin)
            }
        }
    }

    override fun handleIntent(intent: SplashIntent) {
        // No intents to handle for now
    }
}

data class SplashUiState(
    val logo: String? = null,
    val placeholder: Int = R.drawable.e_care_logo,
)

sealed interface SplashIntent
sealed interface SplashEvent {
    data object NavigateToLogin : SplashEvent
    data class NavigateToDashboard(val user: User) : SplashEvent
}
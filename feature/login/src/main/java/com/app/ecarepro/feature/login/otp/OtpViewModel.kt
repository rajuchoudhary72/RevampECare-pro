package com.app.ecarepro.feature.login.otp

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.LoginResult
import com.app.ecarepro.core.domain.model.User
import com.app.ecarepro.core.domain.repository.UserRepository
import com.app.ecarepro.core.ui.viewmodel.AssistedViewModelFactory
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.feature.login.navigation.LoginNavigationGraph
import com.app.ecarepro.feature.login.otp.OtpViewModel.Companion.RESEND_TIME_INTERVAL_SEC
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = OtpViewModel.Factory::class)
class OtpViewModel @AssistedInject constructor(
    @Assisted val navKey: LoginNavigationGraph.OtpVerification,
    private val userRepository: UserRepository,
) : BaseViewModel<OtpIntent, OtpEvent>() {

    companion object {
        const val RESEND_TIME_INTERVAL_SEC = 15
        const val EXPECTED_OTP_LENGTH = 4
    }

    private var otpAuthKey = navKey.otpAuthKey

    private val _uiState = MutableStateFlow(OtpUiState(message = navKey.message))
    val uiState: StateFlow<OtpUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null

    init {
        startResendCountdown()
    }

    override fun handleIntent(intent: OtpIntent) {
        when (intent) {
            is OtpIntent.OnOtpChanged -> _uiState.update { it.copy(otp = intent.otp, errorMessage = null) }
            OtpIntent.OnVerifyClicked -> verifyOtp()
            OtpIntent.OnResendClicked -> resendOtp()
            OtpIntent.OnBackClicked -> sendEvent(OtpEvent.NavigateBack)
        }
    }

    private fun verifyOtp() {
        val otp = uiState.value.otp
        if (otp.isNullOrEmpty() && otp?.length != EXPECTED_OTP_LENGTH) {
            val errorMessage = SnackbarMessage("Please enter a valid OTP.", MessageType.WARNING)
            _uiState.update { it.copy(errorMessage = errorMessage) }
            sendEvent(OtpEvent.ShowMessage(errorMessage))
            return
        }
        val resultFlow = userRepository
            .validateOtp(
                schoolCode = navKey.schoolCode,
                otpAuthKey = otpAuthKey,
                otp = otp
            )

        processOtpResult(
            resultFlow = resultFlow,
            onSuccess = { loginResult ->
                val userDetail = loginResult.userDetail
                if (userDetail == null) {
                    sendEvent(OtpEvent.ShowMessage(SnackbarMessage(loginResult.message)))
                } else {
                    sendEvent(OtpEvent.OnOtpVerificationComplete(userDetail))
                }
            }
        )
    }

    private fun resendOtp() {
        val resultFlow = userRepository
            .resendOtp(
                schoolCode = navKey.schoolCode,
                otpAuthKey = otpAuthKey
            )
        processOtpResult(
            resultFlow = resultFlow,
            onSuccess = { loginResult ->
                startResendCountdown()
                otpAuthKey = loginResult.otpAuthKey.orEmpty()
                _uiState.update {
                    it.copy(message = loginResult.message)
                }
                sendEvent(OtpEvent.ShowMessage(SnackbarMessage(text = loginResult.message, type = MessageType.SUCCESS)))
            }
        )
    }

    private fun processOtpResult(
        resultFlow: Flow<Result<LoginResult>>,
        onSuccess: (LoginResult) -> Unit,
    ) {
        viewModelScope.launch {
            resultFlow
                .onStart {
                    _uiState.update { it.copy(isLoading = true) }
                }
                .collect { result ->
                    _uiState.update { it.copy(isLoading = false) }
                    result.onSuccess { loginResult ->
                        onSuccess(loginResult)
                    }.onFailure { error ->
                        sendEvent(OtpEvent.ShowMessage(SnackbarMessage(error.errorMessage())))
                    }
                }
        }
    }

    private fun startResendCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            _uiState.update { it.copy(isResendEnabled = false) }

            (RESEND_TIME_INTERVAL_SEC downTo 1).asFlow()
                .onEach { delay(1000) }
                .onStart {
                    _uiState.update { it.copy(resendCountdown = RESEND_TIME_INTERVAL_SEC) }
                }
                .collect { remainingTime ->
                    _uiState.update { it.copy(resendCountdown = remainingTime) }
                }
            _uiState.update { it.copy(isResendEnabled = true) }
        }
    }


    @AssistedFactory
    interface Factory :
        AssistedViewModelFactory<LoginNavigationGraph.OtpVerification, OtpViewModel> {
        override fun create(param: LoginNavigationGraph.OtpVerification): OtpViewModel
    }
}

sealed interface OtpIntent {
    data class OnOtpChanged(val otp: String) : OtpIntent
    data object OnVerifyClicked : OtpIntent
    data object OnResendClicked : OtpIntent
    data object OnBackClicked : OtpIntent
}

sealed interface OtpEvent {
    data object NavigateBack : OtpEvent
    data class ShowMessage(val snackbarMessage: SnackbarMessage) : OtpEvent
    data class OnOtpVerificationComplete(val user: User) : OtpEvent
}

@Immutable
data class OtpUiState(
    val otp: String? = null,
    val message: String,
    val isLoading: Boolean = false,
    val errorMessage: SnackbarMessage? = null,
    val isResendEnabled: Boolean = false,
    val resendCountdown: Int = RESEND_TIME_INTERVAL_SEC,
) {
    val hasError = errorMessage != null
}
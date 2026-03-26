package com.app.ecarepro.feature.login.screens.forgotpassword

import android.os.CountDownTimer
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.exception.errorMessage
import com.app.ecarepro.core.domain.model.GetCredential
import com.app.ecarepro.core.domain.model.UserType
import com.app.ecarepro.core.domain.model.Ward
import com.app.ecarepro.core.domain.repository.UserRepository
import com.app.ecarepro.core.ui.viewmodel.AssistedViewModelFactory
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.feature.login.R
import com.app.ecarepro.feature.login.navigation.LoginNavigationGraph
import com.app.ecarepro.feature.login.screens.forgotpassword.ForgotPasswordViewModel.Companion.RESEND_TIME_INTERVAL_SEC
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ForgotPasswordViewModel.Factory::class)
class ForgotPasswordViewModel @AssistedInject constructor(
    @Assisted private val navKey: LoginNavigationGraph.ForgotPassword,
    private val userRepository: UserRepository,
) : BaseViewModel<ForgotPasswordIntent, ForgotPasswordEvent>() {

    companion object {
        const val RESEND_TIME_INTERVAL_SEC = 15
        const val EXPECTED_OTP_LENGTH = 4
    }

    private var resendTimer: CountDownTimer? = null

    private val schoolCode = navKey.schoolCode

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    override fun handleIntent(intent: ForgotPasswordIntent) = when (intent) {
        is ForgotPasswordIntent.SelectUserType -> _uiState.update { it.copy(selectedUserType = intent.type) }
        is ForgotPasswordIntent.NavigateBack -> sendEvent(ForgotPasswordEvent.NavigateBack)
        is ForgotPasswordIntent.SelectNextStep -> selectNextStep()
        ForgotPasswordIntent.GoToPreviousStep -> goToPreviousStep()
        is ForgotPasswordIntent.SelectWard -> _uiState.update { it.copy(selectedWard = intent.ward) }
        is ForgotPasswordIntent.SelectRecoveryMethod -> {
            _uiState.update {
                it.copy(
                    selectedRecoveryMethod = intent.method,
                    recoveryInput = "",
                    recoveryInputError = false
                )
            }
        }

        is ForgotPasswordIntent.OnRecoveryInputChanged -> {
            _uiState.update {
                it.copy(
                    recoveryInput = intent.input,
                    recoveryInputError = false
                )
            }
        }

        is ForgotPasswordIntent.OnOtpChanged -> {
            _uiState.update { it.copy(otpCode = intent.otp, isOtpError = false) }
            // When the OTP is fully entered, verify it
            if (intent.otp.length == EXPECTED_OTP_LENGTH) {
                verifyOtp(intent.otp)
            } else {

            }
        }

        ForgotPasswordIntent.OnOtpSubmitted -> {
            verifyOtp(_uiState.value.otpCode)
        }

        ForgotPasswordIntent.ResendOtpClicked -> {
            resendOtp()
        }

        ForgotPasswordIntent.OpenEmailAppClicked -> {
            sendEvent(ForgotPasswordEvent.OpenEmailApp)
        }
    }

    private fun selectNextStep() {
        val currentState = _uiState.value
        when (currentState.currentStep) {
            ForgotPasswordStep.SelectUserType -> {
                _uiState.update { it.copy(currentStep = ForgotPasswordStep.SelectRecoveryMethod) }
            }

            ForgotPasswordStep.SelectRecoveryMethod -> {
                fetchWards(
                    onSuccess = { wards ->
                        if (wards.isEmpty()) {
                            selectFinalStep(currentState)
                        } else {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    availableWards = wards,
                                    selectedWard = wards.firstOrNull(),
                                    currentStep = ForgotPasswordStep.SelectWard
                                )
                            }
                        }
                    }
                )
            }

            ForgotPasswordStep.SelectWard -> {
                sendPasswordDetails(
                    onSuccess = {
                        selectFinalStep(currentState)
                    }
                )
            }

            else -> {
                // Handle other steps or do nothing if it's a final step.
            }
        }
    }

    private fun selectFinalStep(currentState: ForgotPasswordUiState) {
        if (currentState.selectedRecoveryMethod == RecoveryMethod.MOBILE) {
            _uiState.update {
                it.copy(
                    isLoading = false
                )
            }
            sendEvent(ForgotPasswordEvent.NavigateBack)
        } else {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    currentStep = ForgotPasswordStep.EmailSentConfirmation,
                    headlineTitle = R.string.feature_login_sent_you_an_email,
                    description = R.string.feature_login_forgot_password_email_description,
                    descriptionArgs = listOf(currentState.recoveryInput)
                )
            }
        }
    }

    private fun fetchWards(
        onSuccess: (List<Ward>) -> Unit,
    ) {
        viewModelScope.launch {
            val currentState = _uiState.value
            userRepository
                .getCredential(
                    GetCredential(
                        schoolCode = schoolCode,
                        userType = currentState.selectedUserType.id,
                        receivedOn = currentState.selectedRecoveryMethod.key,
                        mobile = if (currentState.selectedRecoveryMethod == RecoveryMethod.MOBILE) currentState.recoveryInput else null,
                        email = if (currentState.selectedRecoveryMethod == RecoveryMethod.EMAIL) currentState.recoveryInput else null
                    )
                )
                .onStart {
                    _uiState.update { it.copy(isLoading = true) }
                }
                .collect { result ->
                    result
                        .onSuccess { (message, wards) ->
                            sendEvent(ForgotPasswordEvent.ShowMessage(SnackbarMessage(text = message, MessageType.SUCCESS)))
                            onSuccess(wards)
                        }
                        .onFailure { error ->
                            _uiState.update { it.copy(isLoading = false) }
                            sendEvent(
                                ForgotPasswordEvent.ShowMessage(
                                    SnackbarMessage(
                                        text = error.errorMessage()
                                    )
                                )
                            )
                        }
                }
        }
    }

    private fun sendPasswordDetails(
        onSuccess: () -> Unit,
    ) {
        val currentState = _uiState.value
        val selectedWard = currentState.selectedWard ?: return
        viewModelScope.launch {
            userRepository
                .getUsernameByUID(
                    schoolCode = schoolCode,
                    userID = selectedWard.userID,
                    userType = selectedWard.userType,
                    receivedOn = currentState.selectedRecoveryMethod.key
                )
                .onStart {
                    _uiState.update { it.copy(isLoading = true) }
                }
                .collect { result ->
                    result
                        .onSuccess { message ->
                            onSuccess()
                            sendEvent(
                                ForgotPasswordEvent.ShowMessage(
                                    SnackbarMessage(
                                        text = message,
                                        type = MessageType.SUCCESS
                                    )
                                )
                            )
                        }
                        .onFailure { error ->
                            _uiState.update { it.copy(isLoading = false) }
                            sendEvent(
                                ForgotPasswordEvent.ShowMessage(
                                    SnackbarMessage(
                                        text = error.errorMessage()
                                    )
                                )
                            )
                        }
                }
        }
    }

    private fun goToPreviousStep() {
        val currentState = _uiState.value
        when (currentState.currentStep) {
            ForgotPasswordStep.EmailSentConfirmation,
            ForgotPasswordStep.OtpVerification,
                -> {
                // If the user type is Parent, the previous step is SelectWard. Otherwise, it's SelectRecoveryMethod.
                val previousStep = if (currentState.selectedUserType == UserType.PARENT) {
                    ForgotPasswordStep.SelectWard
                } else {
                    ForgotPasswordStep.SelectRecoveryMethod
                }
                _uiState.update {
                    it.copy(
                        currentStep = previousStep,
                        headlineTitle = R.string.feature_login_forgot_password_title,
                        description = R.string.feature_login_forgot_password_description,
                        descriptionArgs = emptyList() // Clear any arguments
                    )
                }
            }

            ForgotPasswordStep.SelectWard -> {
                // From ward selection, always go back to recovery method selection.
                _uiState.update { it.copy(currentStep = ForgotPasswordStep.SelectRecoveryMethod) }
            }

            ForgotPasswordStep.SelectRecoveryMethod -> {
                // From recovery method, always go back to user type selection.
                _uiState.update { it.copy(currentStep = ForgotPasswordStep.SelectUserType) }
            }

            ForgotPasswordStep.SelectUserType -> {
                // If we are at the first step, trigger the main navigation back event.
                sendEvent(ForgotPasswordEvent.NavigateBack)
            }
        }
    }

    private fun verifyOtp(otp: String) {
        viewModelScope.launch {

        }
    }


    private fun startResendTimer() {
        resendTimer?.cancel() // Cancel any existing timer
        _uiState.update { it.copy(isResendOtpEnabled = false) }
        val intervalMillis = RESEND_TIME_INTERVAL_SEC * 1000L
        resendTimer = object : CountDownTimer(intervalMillis, 1000) { // 30 seconds timer
            override fun onTick(millisUntilFinished: Long) {
                _uiState.update { it.copy(resendTimerSeconds = (millisUntilFinished / 1000).toInt()) }
            }

            override fun onFinish() {
                _uiState.update { it.copy(isResendOtpEnabled = true, resendTimerSeconds = 0) }
            }
        }.start()
    }

    private fun resendOtp() {
        viewModelScope.launch {
            startResendTimer()
        }
    }

    override fun onCleared() {
        super.onCleared()
        resendTimer?.cancel() // Important: Clean up the timer to prevent leaks
    }

    @AssistedFactory
    interface Factory :
        AssistedViewModelFactory<LoginNavigationGraph.ForgotPassword, ForgotPasswordViewModel> {
        override fun create(param: LoginNavigationGraph.ForgotPassword): ForgotPasswordViewModel
    }
}

@Immutable
data class ForgotPasswordUiState(
    @param:StringRes val headlineTitle: Int = R.string.feature_login_forgot_password_title,
    @param:StringRes val description: Int = R.string.feature_login_forgot_password_description,
    val descriptionArgs: List<Any> = emptyList(), // <-- Add this field
    val currentStep: ForgotPasswordStep = ForgotPasswordStep.SelectUserType,
    val isLoading: Boolean = false,
    val selectedUserType: UserType = UserType.PARENT,
    val errorMessage: SnackbarMessage? = null,
    val availableWards: List<Ward> = emptyList(),
    val selectedWard: Ward? = null,
    val selectedRecoveryMethod: RecoveryMethod = RecoveryMethod.MOBILE,
    val recoveryInput: String = "", // Holds the text for mobile/email
    val recoveryInputError: Boolean = false,
    val otpCode: String = "",
    val isOtpError: Boolean = false,
    val resendTimerSeconds: Int = RESEND_TIME_INTERVAL_SEC,
    val isResendOtpEnabled: Boolean = false,
)

// Represents each screen/step in the flow
enum class ForgotPasswordStep(val stepName: Int = 0, val progress: String? = null) {
    SelectUserType(R.string.feature_login_select_user_type, "1/3"),
    SelectRecoveryMethod(R.string.feature_login_recovery_method, "3/3"),
    SelectWard(R.string.feature_login_select_ward, "2/3"),
    OtpVerification(),
    EmailSentConfirmation();
}

// User actions from the UI
sealed interface ForgotPasswordIntent {
    data object SelectNextStep : ForgotPasswordIntent
    data object GoToPreviousStep : ForgotPasswordIntent
    data class SelectUserType(val type: UserType) : ForgotPasswordIntent
    data class SelectWard(val ward: Ward) : ForgotPasswordIntent
    data object NavigateBack : ForgotPasswordIntent
    data class SelectRecoveryMethod(val method: RecoveryMethod) : ForgotPasswordIntent
    data class OnRecoveryInputChanged(val input: String) : ForgotPasswordIntent
    data class OnOtpChanged(val otp: String) : ForgotPasswordIntent
    data object OnOtpSubmitted : ForgotPasswordIntent
    data object ResendOtpClicked : ForgotPasswordIntent
    data object OpenEmailAppClicked : ForgotPasswordIntent
}

sealed interface ForgotPasswordEvent {
    data object NavigateBack : ForgotPasswordEvent
    data object OpenEmailApp : ForgotPasswordEvent
    data class ShowMessage(val message: SnackbarMessage) : ForgotPasswordEvent
}

// Assuming a UserType enum


enum class RecoveryMethod(val key: String) {
    MOBILE("mob"),
    EMAIL("email")
}
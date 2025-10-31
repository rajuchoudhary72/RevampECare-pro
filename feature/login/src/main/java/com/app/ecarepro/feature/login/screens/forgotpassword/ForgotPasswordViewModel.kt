package com.app.ecarepro.feature.login.screens.forgotpassword

import android.os.CountDownTimer
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.repository.UserRepository
import com.app.ecarepro.core.ui.viewmodel.AssistedViewModelFactory
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.feature.login.R
import com.app.ecarepro.feature.login.component.previewWards
import com.app.ecarepro.feature.login.navigation.LoginNavigationGraph
import com.app.ecarepro.feature.login.screens.forgotpassword.ForgotPasswordViewModel.Companion.RESEND_TIME_INTERVAL_SEC
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    }

    private fun goToPreviousStep() {

    }

    private fun verifyOtp(otp: String) {
        viewModelScope.launch {
            // TODO: Implement the actual OTP verification logic with your repository
            // _uiState.update { it.copy(isLoading = true) }
            // val result = repository.verifyOtp(otp)
            // if (result.isSuccess) {
            //     // Navigate to the final "Reset Password" screen
            //     _uiState.update { it.copy(isLoading = false, currentStep = ForgotPasswordStep.ResetPassword) }
            // } else {
            //     _uiState.update { it.copy(isLoading = false, isOtpError = true) }
            // }
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
            // TODO: Implement your API call logic to resend the OTP here
            // val result = repository.sendOtp(uiState.value.recoveryInput)
            // if (result.isSuccess) { ... }

            // After successfully triggering the resend, restart the timer
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
    val availableWards: List<Ward> = previewWards,
    val selectedWard: Ward? = null,
    val selectedRecoveryMethod: RecoveryMethod? = RecoveryMethod.MOBILE,
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
    // data class ShowMessage(val message: SnackbarMessage) : ForgotPasswordEvent
}

// Assuming a UserType enum
enum class UserType(@param:StringRes val value: Int) {
    PARENT(R.string.feature_login_parent),
    STUDENT(R.string.feature_login_student),
    STAFF(R.string.feature_login_staff)
}

enum class RecoveryMethod(val key: String) {
    MOBILE("mob"),
    EMAIL("email")
}

data class Ward(
    val id: Int,
    val name: String,
    val photoUrl: String?,
    val isSelected: Boolean,
)
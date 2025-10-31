package com.app.ecarepro.feature.login.screens.forgotpassword

import RecoveryMethodSelector
import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.designsystem.core.component.Button
import com.app.ecarepro.designsystem.core.component.CodeInput
import com.app.ecarepro.designsystem.core.component.EcareProOutlineButton
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.login.R
import com.app.ecarepro.feature.login.component.ForgotPasswordHeader
import com.app.ecarepro.feature.login.component.UserTypeSelector
import com.app.ecarepro.feature.login.component.WardList
import com.app.ecarepro.feature.login.component.previewWards
import com.app.ecarepro.feature.login.screens.forgotpassword.ForgotPasswordIntent.OnRecoveryInputChanged
import com.app.ecarepro.feature.login.screens.forgotpassword.ForgotPasswordIntent.SelectNextStep
import com.app.ecarepro.feature.login.screens.forgotpassword.ForgotPasswordIntent.SelectRecoveryMethod
import com.app.ecarepro.feature.login.screens.forgotpassword.ForgotPasswordIntent.SelectUserType
import com.app.ecarepro.feature.login.screens.forgotpassword.ForgotPasswordIntent.SelectWard

@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel = hiltViewModel(),
    navigateToBack: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                ForgotPasswordEvent.NavigateBack -> navigateToBack()
                ForgotPasswordEvent.OpenEmailApp -> {
                    val intent = Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_APP_EMAIL)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    val chooser = Intent.createChooser(intent, "Open Email App")
                    context.startActivity(chooser)
                }
            }
        }
    }

    ForgotPasswordContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent
    )
}

@Composable
private fun ForgotPasswordContent(
    uiState: ForgotPasswordUiState,
    handleIntent: (ForgotPasswordIntent) -> Unit = {},
) {
    EcareProScaffold(
        containerColor = White,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            ForgotPasswordHeader(
                title = stringResource(uiState.headlineTitle),
                descriptionText = stringResource(
                    uiState.description,
                    *uiState.descriptionArgs.toTypedArray()
                ),
                onClose = { handleIntent(ForgotPasswordIntent.NavigateBack) }
            )


            if (uiState.currentStep.stepName != 0) {
                Spacer(modifier = Modifier.height(48.dp))
                Stepper(
                    title = stringResource(uiState.currentStep.stepName),
                    stepText = uiState.currentStep.progress.orEmpty(),
                )
                Spacer(modifier = Modifier.height(12.dp))
            }



            AnimatedContent(
                targetState = uiState.currentStep,
                transitionSpec = {
                    if (targetState.ordinal > initialState.ordinal) {
                        // Forward animation: Enter from right, exit to left
                        slideInHorizontally { width -> width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> -width } + fadeOut()
                    } else {
                        // Backward animation: Enter from left, exit to right
                        slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> width } + fadeOut()
                    }
                }
            ) { step ->
                when (step) {
                    ForgotPasswordStep.SelectUserType -> {
                        Step1_UserTypeSelection(
                            selectedUserType = uiState.selectedUserType,
                            onUserTypeSelected = {
                                handleIntent.invoke(
                                    SelectUserType(
                                        it
                                    )
                                )
                            },
                            onNextClicked = {
                                handleIntent.invoke(SelectNextStep)
                            }
                        )
                    }

                    ForgotPasswordStep.SelectWard -> {
                        Step2_SelectWard(
                            wards = uiState.availableWards,
                            selectedWard = uiState.selectedWard,
                            onWardSelected = {
                                handleIntent.invoke(SelectWard(it))
                            },
                            onContinueClicked = {
                                handleIntent.invoke(SelectNextStep)
                            },
                            onBackClicked = {
                                handleIntent.invoke(ForgotPasswordIntent.GoToPreviousStep)
                            }
                        )
                    }

                    ForgotPasswordStep.SelectRecoveryMethod -> {
                        Step3_SelectRecoveryMethod(
                            inputValue = uiState.recoveryInput,
                            onInputValueChange = {
                                handleIntent(OnRecoveryInputChanged(it))
                            },
                            selectedMethod = uiState.selectedRecoveryMethod,
                            onMethodSelected = {
                                handleIntent(SelectRecoveryMethod(it))
                            },
                            isError = uiState.recoveryInputError,
                            onContinueClicked = {
                                handleIntent.invoke(SelectNextStep)
                            },
                            onBackClicked = {
                                handleIntent.invoke(ForgotPasswordIntent.GoToPreviousStep)
                            }
                        )
                    }

                    ForgotPasswordStep.OtpVerification -> {
                        Step4_OtpVerification(
                            otpCode = uiState.otpCode,
                            isError = uiState.isOtpError,
                            onOtpEntered = { otp ->
                                handleIntent(ForgotPasswordIntent.OnOtpChanged(otp))
                            },
                            onBackClicked = {
                                handleIntent(ForgotPasswordIntent.GoToPreviousStep)
                            },
                            onVerifyOtpClicked = {
                                handleIntent(ForgotPasswordIntent.OnOtpSubmitted)
                            },
                            timerSeconds = uiState.resendTimerSeconds,
                            isResendEnabled = uiState.isResendOtpEnabled,
                            onResendClicked = {
                                handleIntent(ForgotPasswordIntent.ResendOtpClicked)
                            }
                        )
                    }

                    ForgotPasswordStep.EmailSentConfirmation -> {
                        Step5_EmailSentConfirmation(
                            onOpenEmailClicked = {
                                handleIntent(ForgotPasswordIntent.OpenEmailAppClicked)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Stepper(
    modifier: Modifier = Modifier,
    title: String,
    stepText: String,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 16.sp),
            color = MaterialTheme.appColors.textPrimary,
            textAlign = TextAlign.Start,
        )
        Text(
            text = stepText,
            style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 16.sp),
            color = MaterialTheme.appColors.textPrimary,
            textAlign = TextAlign.Start,
        )
    }
}

@Composable
private fun Step1_UserTypeSelection(
    selectedUserType: UserType?,
    onUserTypeSelected: (UserType) -> Unit = {},
    onNextClicked: () -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            UserTypeSelector(
                options = UserType.entries.toList(),
                selectedOption = selectedUserType,
                onOptionSelected = onUserTypeSelected,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Button(
            title = stringResource(R.string.feature_login_select_your_ward),
            onClick = onNextClicked,
            enabled = selectedUserType != null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        )
    }
}

@Composable
private fun Step2_SelectWard(
    wards: List<Ward>,
    onWardSelected: (Ward) -> Unit,
    onBackClicked: () -> Unit,
    onContinueClicked: () -> Unit,
    selectedWard: Ward?,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        WardList(
            wards = wards,
            selectedWard = selectedWard,
            onWardSelected = onWardSelected,
            modifier = Modifier.weight(1f)
        )


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            EcareProOutlineButton(
                modifier = Modifier.weight(.6f),
                title = stringResource(R.string.feature_login_back),
                onClick = onBackClicked,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.feature_login_continue),
                onClick = onContinueClicked,
                enabled = wards.any { it.isSelected },
            )
        }


    }
}

@Composable
private fun Step3_SelectRecoveryMethod(
    inputValue: String,
    onInputValueChange: (String) -> Unit,
    selectedMethod: RecoveryMethod?,
    onMethodSelected: (RecoveryMethod) -> Unit,
    isError: Boolean,
    onBackClicked: () -> Unit,
    onContinueClicked: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        RecoveryMethodSelector(
            modifier = Modifier.fillMaxWidth(),
            selectedMethod = selectedMethod,
            onMethodSelected = onMethodSelected,
            inputValue = inputValue,
            onInputValueChange = onInputValueChange,
            isError = isError,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            EcareProOutlineButton(
                modifier = Modifier.weight(.6f),
                title = stringResource(R.string.feature_login_back),
                onClick = onBackClicked,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.feature_login_continue),
                onClick = onContinueClicked,
                enabled = selectedMethod != null,
            )
        }
    }
}

@Composable
private fun Step4_OtpVerification(
    otpCode: String,
    isError: Boolean,
    onOtpEntered: (String) -> Unit,
    onBackClicked: () -> Unit,
    onVerifyOtpClicked: () -> Unit,
    timerSeconds: Int,
    isResendEnabled: Boolean,
    onResendClicked: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            CodeInput(
                modifier = Modifier.padding(top = 16.dp),
                itemHeight = 48.dp,
                itemWidth = 84.dp,
                code = otpCode,
                isError = isError,
                onOtpEntered = onOtpEntered,
                otpLength = ForgotPasswordViewModel.EXPECTED_OTP_LENGTH,
                backgroundColor = MaterialTheme.appColors.background,
                filledStrokeColor = MaterialTheme.appColors.primary
            )
            Spacer(modifier = Modifier.height(4.dp))

            ResendOtpSection(
                timerSeconds = timerSeconds,
                isResendEnabled = isResendEnabled,
                onResendClicked = onResendClicked
            )
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            EcareProOutlineButton(
                modifier = Modifier.weight(.6f),
                title = stringResource(R.string.feature_login_back),
                onClick = onBackClicked,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.feature_login_verify_otp),
                onClick = onVerifyOtpClicked,
            )
        }
    }
}

@Composable
private fun ResendOtpSection(
    timerSeconds: Int,
    isResendEnabled: Boolean,
    onResendClicked: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (isResendEnabled) {
            Text(
                text = stringResource(R.string.feature_login_didn_t_receive_a_code),
                style = MaterialTheme.appTypography.interRegular12px,
                color = MaterialTheme.appColors.textSecondary
            )
        } else
            Text(
                text = stringResource(
                    R.string.feature_login_please_wait_seconds_to_resend,
                    timerSeconds
                ),
                style = MaterialTheme.appTypography.interRegular12px,
                color = MaterialTheme.appColors.textSecondary
            )

        Text(
            modifier = Modifier
                .padding(8.dp)
                .clickable(
                    onClick = onResendClicked,
                    enabled = isResendEnabled
                ),
            text = stringResource(R.string.feature_login_resend_otp),
            style = MaterialTheme.appTypography.interRegular12px.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.appColors.textSecondary
        )
    }
}

@Composable
private fun Step5_EmailSentConfirmation(
    onOpenEmailClicked: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 32.dp), // To push the button to the bottom
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // This Box will keep the icon and text centered in the available space
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_email_sent),
                        contentDescription = "Email Sent",
                        modifier = Modifier.size(120.dp)
                    )
                }
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.feature_login_open_your_email),
                onClick = onOpenEmailClicked
            )
        }
    }
}

// ... (imports)
// Add these new preview composables at the end of your file
@Preview(showBackground = true, name = "Step 1: Select User Type")
@Composable
private fun ForgotPasswordContentStep1Preview() {
    EcareProTheme {
        ForgotPasswordContent(
            uiState = ForgotPasswordUiState(
                currentStep = ForgotPasswordStep.SelectUserType
            )
        )
    }
}

@Preview(showBackground = true, name = "Step 2: Select Recovery Method")
@Composable
private fun ForgotPasswordContentStep3Preview() {
    EcareProTheme {
        ForgotPasswordContent(
            uiState = ForgotPasswordUiState(
                currentStep = ForgotPasswordStep.SelectRecoveryMethod,
                selectedUserType = UserType.PARENT,
                selectedRecoveryMethod = RecoveryMethod.MOBILE,
                recoveryInput = "9876543210"
            )
        )
    }
}


@Preview(showBackground = true, name = "Step 3: Select Ward")
@Composable
private fun ForgotPasswordContentStep2Preview() {
    EcareProTheme {
        ForgotPasswordContent(
            uiState = ForgotPasswordUiState(
                currentStep = ForgotPasswordStep.SelectWard,
                selectedUserType = UserType.PARENT,
                availableWards = previewWards, // Using the preview data
                selectedWard = previewWards.first()
            )
        )
    }
}

@Preview(showBackground = true, name = "Step 4: OTP Verification")
@Composable
private fun ForgotPasswordContentStep4Preview() {
    EcareProTheme {
        ForgotPasswordContent(
            uiState = ForgotPasswordUiState(
                currentStep = ForgotPasswordStep.OtpVerification,
                headlineTitle = R.string.feature_login_we_just_sent_an_sms,
                description = R.string.feature_login_forgot_password_otp_description,
                descriptionArgs = listOf("+919876543210"),
                otpCode = "123"
            )
        )
    }
}

@Preview(showBackground = true, name = "Step 5: Email Sent")
@Composable
private fun ForgotPasswordContentStep5Preview() {
    EcareProTheme {
        ForgotPasswordContent(
            uiState = ForgotPasswordUiState(
                currentStep = ForgotPasswordStep.EmailSentConfirmation,
                headlineTitle = R.string.feature_login_sent_you_an_email,
                description = R.string.feature_login_forgot_password_email_description,
                descriptionArgs = listOf("test@example.com")
            )
        )
    }
}
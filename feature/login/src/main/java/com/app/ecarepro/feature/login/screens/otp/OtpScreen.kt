package com.app.ecarepro.feature.login.screens.otp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.User
import com.app.ecarepro.designsystem.core.component.Button
import com.app.ecarepro.designsystem.core.component.CodeInput
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.login.R
import com.app.ecarepro.feature.login.component.Footer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpScreen(
    viewModel: OtpViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
    onOtpVerificationComplete: (User) -> Unit,
) {

    val uiState: OtpUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }
    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                OtpEvent.NavigateBack -> navigateToBack()
                is OtpEvent.ShowMessage -> {
                    snackbarMessage = event.snackbarMessage
                    snackbarHostState.showSnackbar(event.snackbarMessage.text)
                }

                is OtpEvent.OnOtpVerificationComplete -> onOtpVerificationComplete(event.user)
            }
        }
    }


    OtpScreenContent(
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage == null },
        uiState = uiState,
        handleIntent = viewModel::handleIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OtpScreenContent(
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    snackbarMessage: SnackbarMessage?,
    uiState: OtpUiState,
    onSnackbarDismissed: () -> Unit = {},
    handleIntent: (OtpIntent) -> Unit,
) {
    EcareProScaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .shadow(
                        elevation = 2.dp
                    ),
                title = {
                    Text(
                        text = "OTP Verification",
                        style = MaterialTheme.appTypography.interMedium16px,
                        color = MaterialTheme.appColors.textPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { handleIntent(OtpIntent.OnBackClicked) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        isLoading = uiState.isLoading,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = onSnackbarDismissed,
        containerColor = White,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = stringResource(R.string.feature_login_we_just_sent_an_sms),
                style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 16.sp),
                color = MaterialTheme.appColors.textPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                modifier = Modifier.padding(horizontal = 12.dp),
                text = uiState.message,
                style = MaterialTheme.appTypography.interRegular14px,
                color = MaterialTheme.appColors.textSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            CodeInput(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                otpLength = 4,
                code = uiState.otp,
                isError = uiState.hasError,
                onOtpEntered = { otp -> handleIntent(OtpIntent.OnOtpChanged(otp)) },
                filledStrokeColor = MaterialTheme.appColors.primary,
                textStyle = MaterialTheme.appTypography.interSemiBold14px.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                textColor = MaterialTheme.appColors.primary,
                backgroundColor = MaterialTheme.appColors.background
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                title = stringResource(R.string.feature_login_verify_otp),
                onClick = {
                    handleIntent(OtpIntent.OnVerifyClicked)
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (uiState.isResendEnabled) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.feature_login_didn_t_receive_a_code),
                        style = MaterialTheme.appTypography.interRegular12px,
                        color = MaterialTheme.appColors.textSecondary
                    )

                    Text(
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable(
                                role = Role.Button,
                                onClick = {
                                    handleIntent(OtpIntent.OnResendClicked)
                                }
                            ),
                        text = "Resend",
                        style = MaterialTheme.appTypography.interSemiBold14px.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.appColors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (uiState.isResendEnabled.not()) {
                Text(
                    text = stringResource(
                        R.string.feature_login_please_wait_seconds_to_resend_otp,
                        uiState.resendCountdown
                    ),
                    style = MaterialTheme.appTypography.interRegular12px,
                    color = MaterialTheme.appColors.textSecondary
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Footer(
                color = MaterialTheme.appColors.textSecondary
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
fun OtpScreenPreview() {
    EcareProTheme {
        OtpScreenContent(
            uiState = OtpUiState(message = "We've sent an OTP to your mobile number/Email ID. Please check your SMS or inbox/spam folder and enter the code to proceed. The OTP is valid for 15 minutes. You have 2 attempt(s) left."),
            handleIntent = {},
            snackbarHostState = SnackbarHostState(),
            snackbarMessage = null,
            onSnackbarDismissed = {}
        )
    }
}

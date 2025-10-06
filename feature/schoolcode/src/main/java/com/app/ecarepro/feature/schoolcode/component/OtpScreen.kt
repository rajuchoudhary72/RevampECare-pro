package com.app.ecarepro.feature.schoolcode.component


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import kotlinx.coroutines.launch

@Composable
fun OtpScreen(
    modifier: Modifier = Modifier,
    expectedOtp: String = "123456"
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var otp by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    var submittedOtp by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { androidx.compose.material3.SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Enter the OTP",
                style = TextStyle(fontSize = MaterialTheme.typography.headlineMedium.fontSize, fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(20.dp))

            OtpTextField(
                otpLength = 6,
                initialOtp = "12345",
                boxSize = 56.dp,
                boxSpacing = 10.dp,
                cornerRadius = 8.dp,
                borderWidth = 1.dp,
                borderColor = Color.LightGray,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                errorBorderColor = MaterialTheme.colorScheme.error,
                textStyle = TextStyle(fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface),
                maskInput = false,
                isError = error,
                keyboardType = KeyboardType.Number,
                disableCopyPaste = true,
                autoFocusFirst = true,
                onOtpChange = { newOtp ->
                    otp = newOtp
                    // clear error when user types
                    if (error) error = false
                },
                onOtpComplete = { fullOtp ->
                    submittedOtp = fullOtp
                    if (fullOtp == expectedOtp) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("OTP verified!")
                        }
                    } else {
                        error = true
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Incorrect OTP")
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                // manual submit - just demonstration
                if (otp.length < 6) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Please enter full OTP")
                    }
                } else {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Submitting: $otp")
                    }
                }
            }) {
                Text("Submit")
            }
        }
    }
}

@Preview
@Composable
fun OtpScreenPreview() {
    EcareProTheme {
        OtpScreen()
    }
}


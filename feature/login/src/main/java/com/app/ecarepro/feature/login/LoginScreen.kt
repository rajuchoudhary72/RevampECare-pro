package com.app.ecarepro.feature.login


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.designsystem.core.component.EcareProBackground
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.feature.login.component.FooterSection
import com.app.ecarepro.feature.login.component.HeaderSection
import com.app.ecarepro.feature.login.component.LoginForm

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    schoolCode: String,
    backToSchoolCode: () -> Unit = {},
    navigateToForgotPassword: () -> Unit = {},
    navigateToHelp: () -> Unit = {},
    navigateToMain: () -> Unit = {},
) {

    val uiState: LoginUiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.setSchoolCode(schoolCode)
        viewModel.screenEvent.collect { event ->
            when (event) {
                LoginEvent.NavigateBackToSchoolCode -> backToSchoolCode()
                LoginEvent.NavigateToForgotPasswordScreen -> navigateToForgotPassword()
                LoginEvent.NavigateToHelpScreen -> navigateToHelp()
                LoginEvent.NavigateToMainScreen -> navigateToMain()
            }
        }

    }

    EcareProBackground(
        overlayColor = MaterialTheme.appColors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(
                modifier = Modifier
                    .height(32.dp)
            )
            HeaderSection(
                schoolDetails = uiState.schoolDetails,
            )
            Spacer(
                modifier = Modifier
                    .height(10.dp)
            )
            LoginForm(
                username = uiState.username,
                password = uiState.password,
                onUsernameChanged = { userName ->
                    viewModel.handleIntent(
                        LoginIntent.OnUsernameChanged(
                            userName
                        )
                    )
                },
                onPasswordChanged = { password ->
                    viewModel.handleIntent(
                        LoginIntent.OnPasswordChanged(
                            password
                        )
                    )
                },
                onLoginClicked = { viewModel.handleIntent(LoginIntent.OnLoginClicked) },
                onForgotPasswordClicked = { viewModel.handleIntent(LoginIntent.OnForgotPasswordClicked) },
            )
            FooterSection(
                onChangeSchoolClicked = {
                    viewModel.handleIntent(LoginIntent.OnChangeSchoolClicked)
                },
                onHelpClicked = {
                    viewModel.handleIntent(LoginIntent.OnHelpClicked)
                })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    EcareProTheme {
        LoginScreen(schoolCode = "DEMOIN")
    }
}
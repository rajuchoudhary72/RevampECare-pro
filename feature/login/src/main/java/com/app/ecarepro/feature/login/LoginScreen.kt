package com.app.ecarepro.feature.login


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.User
import com.app.ecarepro.designsystem.core.component.EcareProBackground
import com.app.ecarepro.designsystem.core.component.EcareProSnackbar
import com.app.ecarepro.designsystem.core.component.Loader
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.feature.login.component.FooterSection
import com.app.ecarepro.feature.login.component.HeaderSection
import com.app.ecarepro.feature.login.component.LoginForm

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    backToSchoolCode: () -> Unit = {},
    navigateToForgotPassword: () -> Unit = {},
    navigateToHelp: () -> Unit = {},
    selectHomeScreenType: (User) -> Unit = {},
    ) {

    val uiState: LoginUiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                LoginEvent.NavigateBackToSchoolCode -> backToSchoolCode()
                LoginEvent.NavigateToForgotPasswordScreen -> navigateToForgotPassword()
                LoginEvent.NavigateToHelpScreen -> navigateToHelp()
                is LoginEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(event.message.text)
                }

                is LoginEvent.NavigateToMainScreen -> selectHomeScreenType(event.user)
            }
        }

    }



    LoginScreenContent(
        snackbarHostState = snackbarHostState,
        uiState = uiState,
        handleIntent = { intent -> viewModel.handleIntent(intent)}
    )


}

@Composable
private fun LoginScreenContent(
    snackbarHostState: SnackbarHostState,
    uiState: LoginUiState,
    handleIntent:(LoginIntent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        EcareProBackground(
            overlayColor = MaterialTheme.appColors.background
        ) {

            Scaffold(
                containerColor = Color.Transparent,
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState) {
                        uiState.errorMessage?.let {
                            EcareProSnackbar(it)
                        }
                    }
                }
            ) { paddingValues ->

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
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
                        isError = uiState.errorMessage != null,
                        onUsernameChanged = { userName ->
                            handleIntent(
                                LoginIntent.OnUsernameChanged(
                                    userName
                                )
                            )
                        },
                        onPasswordChanged = { password ->
                            handleIntent(
                                LoginIntent.OnPasswordChanged(
                                    password
                                )
                            )
                        },
                        onLoginClicked = { handleIntent(LoginIntent.OnLoginClicked) },
                        onForgotPasswordClicked = { handleIntent(LoginIntent.OnForgotPasswordClicked) },
                    )
                    FooterSection(
                        onChangeSchoolClicked = {
                            handleIntent(LoginIntent.OnChangeSchoolClicked)
                        },
                        onHelpClicked = {
                            handleIntent(LoginIntent.OnHelpClicked)
                        })
                }

            }


        }

        if (uiState.isLoading) {
            Loader()
        }

    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    EcareProTheme {
        LoginScreenContent(
            snackbarHostState = remember { SnackbarHostState() },
            uiState = LoginUiState(errorMessage = SnackbarMessage("Error message")),
            handleIntent = {}
        )
    }
}
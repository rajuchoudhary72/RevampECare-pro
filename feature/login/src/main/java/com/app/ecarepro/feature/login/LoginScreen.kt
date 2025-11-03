package com.app.ecarepro.feature.login


import EcareProAlertDialog
import OnLifecycleEvent
import android.Manifest
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.User
import com.app.ecarepro.core.location.LocationUtils
import com.app.ecarepro.designsystem.core.component.EcareProBackground
import com.app.ecarepro.designsystem.core.component.EcareProSnackbar
import com.app.ecarepro.designsystem.core.component.Loader
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.PermissionRequester
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
    navigateToBack: () -> Unit = {},
    navigateToForgotPassword: () -> Unit = {},
    navigateToHelp: () -> Unit = {},
    selectHomeScreenType: (User) -> Unit = {},
    ) {

    val uiState: LoginUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current


    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessageType = MessageType.SUCCESS

    OnLifecycleEvent(
        events = arrayOf(Lifecycle.Event.ON_RESUME),
        onEvent = { _, _ ->
            viewModel.handleIntent(LoginIntent.OnResumed)
        }
    )
    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                LoginEvent.NavigateBackToSchoolCode -> backToSchoolCode()
                LoginEvent.NavigateToForgotPasswordScreen -> navigateToForgotPassword()
                LoginEvent.NavigateToHelpScreen -> navigateToHelp()
                LoginEvent.NavigateToBack -> navigateToBack()
                is LoginEvent.ShowMessage -> {
                    snackbarMessageType = event.message.type
                    snackbarHostState.showSnackbar(event.message.text)
                }

                is LoginEvent.NavigateToMainScreen -> selectHomeScreenType(event.user)
                LoginEvent.TurnOnGps -> LocationUtils.openGpsSettings(context)
                LoginEvent.OpenAppSettings -> LocationUtils.openAppSettings(context)
            }
        }

    }



    LoginScreenContent(
        snackbarHostState = snackbarHostState,
        snackbarMessageType = snackbarMessageType,
        uiState = uiState,
        handleIntent = viewModel::handleIntent
    )
    if (uiState.isRequestingPermissions) {
        locationPermissionRequester(
            handleIntent = viewModel::handleIntent
        )
    }

    if (uiState.isRequestingGpsEnabled) {
        GpsEnableRequester(
            handleIntent = viewModel::handleIntent
        )
    }

}

@Composable
private fun LoginScreenContent(
    snackbarHostState: SnackbarHostState,
    snackbarMessageType: MessageType = MessageType.SUCCESS,
    uiState: LoginUiState,
    handleIntent: (LoginIntent) -> Unit,
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
                        snackbarHostState.currentSnackbarData?.visuals?.message?.let { text ->
                            EcareProSnackbar(SnackbarMessage(text, snackbarMessageType))
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

@Composable
private fun GpsEnableRequester(
    handleIntent: (LoginIntent) -> Unit,
) {
    EcareProAlertDialog(
        title = stringResource(R.string.feature_login_enable_gps),
        description = stringResource(R.string.feature_login_this_app_requires_location_to_be_enabled_to_function_please_enable_it_in_settings),
        isDismissable = false,
        positiveButtonText = stringResource(R.string.feature_login_go_to_settings),
        positiveButtonOnClick = {
            handleIntent(LoginIntent.OnOpenGpsSettings)
        },
        negativeButtonText = stringResource(R.string.feature_login_exit),
        negativeButtonOnClick = {
            handleIntent(LoginIntent.OnNavigateToBack)
        }
    )
}

@Composable
private fun locationPermissionRequester(
    handleIntent: (LoginIntent) -> Unit,
) {
    PermissionRequester(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ),
        rationale = { requestPermissions ->
            EcareProAlertDialog(
                title = stringResource(R.string.feature_login_location_permission_required),
                description = stringResource(R.string.feature_login_this_app_requires_location_access_for_security_and_to_function_correctly_please_grant_the_permission_to_continue),
                isDismissable = false,
                positiveButtonText = stringResource(R.string.feature_login_grant_permission),
                positiveButtonOnClick = { requestPermissions() },
            )
        },
        onPermissionsDenied = {
            EcareProAlertDialog(
                title = stringResource(R.string.feature_login_permission_denied),
                description = stringResource(R.string.feature_login_location_permission_is_required_to_use_this_app_please_enable_it_in_the_app_settings_or_exit_the_app),
                isDismissable = false,
                positiveButtonText = stringResource(R.string.feature_login_go_to_settings),
                positiveButtonOnClick = {
                    handleIntent(LoginIntent.OnOpenAppSettings)
                },
            )
        },
        onPermissionsGranted = {
            handleIntent(LoginIntent.OnLocationPermissionGranted)
        }
    )
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
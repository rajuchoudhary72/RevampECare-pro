package com.app.ecarepro.feature.setting.change_password

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.setting.R
import com.app.ecarepro.feature.setting.settings_main.component.RulesSection

@Composable
fun ChangePasswordScreen(
    navigateBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ChangePasswordViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage: SnackbarMessage? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is ChangePasswordEvent.ShowMessage -> {
                    snackbarMessage = event.message
                    snackbarHostState.showSnackbar(event.message.text)
                }
                is ChangePasswordEvent.RedirectToLogin -> onLogout()
            }
        }
    }

    ChangePasswordContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null },
        navigateBack = navigateBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChangePasswordContent(
    uiState: ChangePasswordUiState,
    handleIntent: (ChangePasswordIntent) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    snackbarMessage: SnackbarMessage? = null,
    onSnackbarDismissed: () -> Unit = {},
    navigateBack: () -> Unit = {},
) {
    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = stringResource(R.string.settings_change_password),
                onNavigationClicked = navigateBack,
            )
        },
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = onSnackbarDismissed,
        containerColor = Color.White,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PasswordTextField(
                label = stringResource(R.string.settings_current_password),
                value = uiState.currentPassword,
                onValueChange = { handleIntent(ChangePasswordIntent.OnCurrentPasswordChanged(it)) },
                isPasswordVisible = uiState.showCurrentPassword,
                onToggleVisibility = { handleIntent(ChangePasswordIntent.ToggleCurrentPasswordVisibility) },
                errorMessage = uiState.currentPasswordError,
                modifier = Modifier.fillMaxWidth(),
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PasswordTextField(
                    label = stringResource(R.string.settings_new_password),
                    value = uiState.newPassword,
                    onValueChange = { handleIntent(ChangePasswordIntent.OnNewPasswordChanged(it)) },
                    isPasswordVisible = uiState.showNewPassword,
                    onToggleVisibility = { handleIntent(ChangePasswordIntent.ToggleNewPasswordVisibility) },
                    errorMessage = uiState.newPasswordError,
                    modifier = Modifier.weight(1f),
                )
                PasswordTextField(
                    label = stringResource(R.string.settings_confirm_password),
                    value = uiState.confirmPassword,
                    onValueChange = { handleIntent(ChangePasswordIntent.OnConfirmPasswordChanged(it)) },
                    isPasswordVisible = uiState.showConfirmPassword,
                    onToggleVisibility = { handleIntent(ChangePasswordIntent.ToggleConfirmPasswordVisibility) },
                    errorMessage = uiState.confirmPasswordError,
                    modifier = Modifier.weight(1f),
                )
            }

            Button(
                onClick = { handleIntent(ChangePasswordIntent.OnSubmit) },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.appColors.primary),
            ) {
                Text(
                    text = stringResource(R.string.settings_confirm_update),
                    style = MaterialTheme.appTypography.interSemiBold14px,
                    color = Color.White,
                )
            }

            RulesSection(
                rules = listOf(
                    stringResource(R.string.settings_password_rule_1),
                    stringResource(R.string.settings_password_rule_2),
                    stringResource(R.string.settings_password_rule_3),
                )
            )
        }
    }
}

@Composable
private fun PasswordTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onToggleVisibility: () -> Unit,
    errorMessage: String?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.appTypography.interRegular12px,
            color = MaterialTheme.appColors.textSecondary,
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(label, style = MaterialTheme.appTypography.interRegular14px) },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = MaterialTheme.appColors.textSecondary,
                    )
                }
            },
            isError = errorMessage != null,
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            textStyle = MaterialTheme.appTypography.interRegular14px,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.appColors.textSecondary.copy(alpha = 0.3f),
                errorBorderColor = MaterialTheme.appColors.error,
            ),
        )
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.appTypography.interRegular12px,
                color = MaterialTheme.appColors.error,
            )
        }
    }
}

@Preview(showBackground = true, name = "Change Password")
@Composable
private fun PreviewChangePassword() {
    EcareProTheme {
        ChangePasswordContent(
            uiState = ChangePasswordUiState(),
            handleIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "Change Password - With Errors")
@Composable
private fun PreviewChangePasswordErrors() {
    EcareProTheme {
        ChangePasswordContent(
            uiState = ChangePasswordUiState(showFieldErrors = true),
            handleIntent = {},
        )
    }
}

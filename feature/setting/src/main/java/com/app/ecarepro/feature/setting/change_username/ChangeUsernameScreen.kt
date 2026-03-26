package com.app.ecarepro.feature.setting.change_username

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.designsystem.core.component.EcareProInputField
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.setting.R
import com.app.ecarepro.feature.setting.settings_main.component.RulesSection

@Composable
fun ChangeUsernameScreen(
    navigateBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ChangeUsernameViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage: SnackbarMessage? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is ChangeUsernameEvent.ShowMessage -> {
                    snackbarMessage = event.message
                    snackbarHostState.showSnackbar(event.message.text)
                }
                is ChangeUsernameEvent.RedirectToLogin -> onLogout()
            }
        }
    }

    ChangeUsernameContent(
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
private fun ChangeUsernameContent(
    uiState: ChangeUsernameUiState,
    handleIntent: (ChangeUsernameIntent) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    snackbarMessage: SnackbarMessage? = null,
    onSnackbarDismissed: () -> Unit = {},
    navigateBack: () -> Unit = {},
) {
    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = stringResource(R.string.settings_change_username),
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
            EcareProInputField(
                label = stringResource(R.string.settings_existing_username),
                value = uiState.existingUsername,
                placeholder = "",
                onValueChange = {},
            )

            EcareProInputField(
                label = stringResource(R.string.settings_new_username),
                value = uiState.newUsername,
                placeholder = stringResource(R.string.settings_new_username_placeholder),
                onValueChange = { handleIntent(ChangeUsernameIntent.OnNewUsernameChanged(it)) },
            )

            if (uiState.newUsernameError != null) {
                Text(
                    text = uiState.newUsernameError!!,
                    style = MaterialTheme.appTypography.interRegular12px,
                    color = MaterialTheme.appColors.error,
                )
            }

            Button(
                onClick = { handleIntent(ChangeUsernameIntent.OnSubmit) },
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
                    stringResource(R.string.settings_username_rule_1),
                    stringResource(R.string.settings_username_rule_2),
                    stringResource(R.string.settings_username_rule_3),
                    stringResource(R.string.settings_username_rule_4),
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewChangeUsername() {
    EcareProTheme {
        ChangeUsernameContent(
            uiState = ChangeUsernameUiState(existingUsername = "ravigupta9363"),
            handleIntent = {},
        )
    }
}

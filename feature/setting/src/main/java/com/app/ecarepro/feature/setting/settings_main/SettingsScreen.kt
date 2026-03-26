package com.app.ecarepro.feature.setting.settings_main

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.setting.R
import com.app.ecarepro.feature.setting.settings_main.component.SettingsItemCard

@Composable
fun SettingsScreen(
    navigateBack: () -> Unit,
    navigateTo: (NavKey) -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage: SnackbarMessage? by remember { mutableStateOf(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is SettingsEvent.NavigateTo -> navigateTo(event.destination)
                is SettingsEvent.OpenPlayStore -> {
                    val uri = Uri.parse("market://details?id=${context.packageName}")
                    runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, uri)) }
                }
                is SettingsEvent.ShowMessage -> {
                    snackbarMessage = event.message
                    snackbarHostState.showSnackbar(event.message.text)
                }
            }
        }
    }

    SettingsContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null },
        handleIntent = viewModel::handleIntent,
        navigateBack = navigateBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsContent(
    uiState: SettingsUiState,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    snackbarMessage: SnackbarMessage? = null,
    onSnackbarDismissed: () -> Unit = {},
    handleIntent: (SettingsIntent) -> Unit,
    navigateBack: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        EcareProScaffold(
            topBar = {
                EcareProTopAppBar(
                    title = stringResource(R.string.settings_title),
                    onNavigationClicked = navigateBack,
                )
            },
            snackbarHostState = snackbarHostState,
            snackbarMessage = snackbarMessage,
            onSnackbarDismissed = onSnackbarDismissed,
            containerColor = MaterialTheme.appColors.background,
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SettingsItemType.entries.forEach { item ->
                    SettingsItemCard(
                        itemType = item,
                        onClick = { handleIntent(SettingsIntent.OnItemClicked(item)) },
                    )
                    if (item == SettingsItemType.SYNC_DATA && uiState.lastSyncDate != null) {
                        Text(
                            text = stringResource(R.string.settings_last_sync, uiState.lastSyncDate),
                            style = MaterialTheme.appTypography.interRegular12px,
                            color = MaterialTheme.appColors.textSecondary,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }

        if (uiState.isSyncing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = MaterialTheme.appColors.primary)
            }
        }
    }
}

@Preview(showBackground = true, name = "Settings - Loaded")
@Composable
private fun PreviewSettingsLoaded() {
    EcareProTheme {
        SettingsContent(
            uiState = SettingsUiState(lastSyncDate = "09 March 2026 at 7:08 PM"),
            handleIntent = {},
            navigateBack = {},
        )
    }
}

@Preview(showBackground = true, name = "Settings - Syncing")
@Composable
private fun PreviewSettingsSyncing() {
    EcareProTheme {
        SettingsContent(
            uiState = SettingsUiState(isSyncing = true),
            handleIntent = {},
            navigateBack = {},
        )
    }
}

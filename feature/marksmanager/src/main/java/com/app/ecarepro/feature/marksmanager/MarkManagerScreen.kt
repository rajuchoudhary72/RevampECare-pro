package com.app.ecarepro.feature.marksmanager

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.EcareProWebView
import com.app.ecarepro.designsystem.core.component.rememberEcareProWebViewState
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.feature.marksmanager.navigation.MarkManagerType

@Composable
fun MarkManagerScreen(
    viewModel: MarkManagerViewModel,
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                MarkManagerEvent.NavigateBack -> onBackClick()
            }
        }
    }

    MarkManagerContent(
        type = viewModel.navKey.type,
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MarkManagerContent(
    type: MarkManagerType,
    uiState: UiState<String>,
    handleIntent: (MarkManagerIntent) -> Unit,
) {
    val webViewState = rememberEcareProWebViewState()

    val title = when (type) {
        MarkManagerType.MARKS_ENTRY -> "Mark Manager"
        MarkManagerType.SCHOOL_WEBSITE -> "School Website"
    }
    val loadingMessage = when (type) {
        MarkManagerType.MARKS_ENTRY -> "Generating access token\u2026"
        MarkManagerType.SCHOOL_WEBSITE -> "Loading\u2026"
    }
    val errorTitle = when (type) {
        MarkManagerType.MARKS_ENTRY -> "Unable to open Mark Manager"
        MarkManagerType.SCHOOL_WEBSITE -> "Unable to open School Website"
    }

    // Intercept system back-press: let the WebView consume it first (page navigation),
    // then fall through to the screen back action.
    BackHandler {
        if (!webViewState.navigateBack()) {
            handleIntent(MarkManagerIntent.OnBackClicked)
        }
    }

    EcareProScaffold(
        containerColor = White,
        topBar = {
            EcareProTopAppBar(
                modifier = Modifier.shadow(elevation = 1.dp),
                title = title,
                onNavigationClicked = { handleIntent(MarkManagerIntent.OnBackClicked) },
            )
        },
    ) { paddingValues ->
        when (uiState) {
            is UiState.Loading -> LoadingView(
                message = loadingMessage,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            )

            is UiState.Success -> EcareProWebView(
                url = uiState.data,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                state = webViewState,
                enableDownload = true,
                enableFileUpload = type == MarkManagerType.MARKS_ENTRY,
            )

            is UiState.Error -> ErrorView(
                title = errorTitle,
                message = uiState.message,
                onRetry = { handleIntent(MarkManagerIntent.OnRetry) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            )
        }
    }
}

// ─── Loading ──────────────────────────────────────────────────────────────────

@Composable
private fun LoadingView(
    message: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// ─── Error ────────────────────────────────────────────────────────────────────

@Composable
private fun ErrorView(
    title: String,
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(20.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

// ─── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "Loading state – Marks Entry")
@Composable
private fun PreviewLoading() {
    EcareProTheme {
        MarkManagerContent(
            type = MarkManagerType.MARKS_ENTRY,
            uiState = UiState.Loading,
            handleIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "Error state – Marks Entry")
@Composable
private fun PreviewError() {
    EcareProTheme {
        MarkManagerContent(
            type = MarkManagerType.MARKS_ENTRY,
            uiState = UiState.Error("Marks entry is not configured for your school."),
            handleIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "Loading state – School Website")
@Composable
private fun PreviewWebsiteLoading() {
    EcareProTheme {
        MarkManagerContent(
            type = MarkManagerType.SCHOOL_WEBSITE,
            uiState = UiState.Loading,
            handleIntent = {},
        )
    }
}

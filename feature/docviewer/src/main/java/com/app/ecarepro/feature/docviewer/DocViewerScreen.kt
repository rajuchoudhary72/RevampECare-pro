package com.app.ecarepro.feature.docviewer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.DocType
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.feature.docviewer.components.DOCViewer

@Composable
fun DocViewerScreen(
    viewModel: DocViewerViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                DocViewerEvent.NavigateBack -> onBackClick()
            }
        }
    }
    DocViewerScreenContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocViewerScreenContent(
    uiState: DocViewerUiState,
    handleIntent: (DocViewerIntent) -> Unit,
) {
    EcareProScaffold(
        isLoading = uiState.isLoading,
        containerColor = White,
        topBar = {
            EcareProTopAppBar(
                modifier = Modifier.shadow(elevation = 1.dp),
                title = uiState.title,
                onNavigationClicked = { handleIntent(DocViewerIntent.OnBackClicked) },
                actions = {
                    IconButton(onClick = { handleIntent(DocViewerIntent.OnShareClicked) }) {
                        Icon(
                            painter = painterResource(R.drawable.icon_share),
                            contentDescription = "Share"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.docType.isImage()) {
            EcareProAsyncImage(
                imageUrl = uiState.docUrl,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentScale = ContentScale.Fit
            )
        } else {
            DOCViewer(
                modifier = Modifier.padding(paddingValues),
                url = uiState.docUrl,
                docType = uiState.docType
            )

        }
    }
}


@Preview
@Composable
private fun DocViewerScreenContentPreview() {
    EcareProTheme {
        DocViewerScreenContent(
            uiState = DocViewerUiState(
                title = "Syllabus",
                docUrl = "https://www.google.com",
                docType = DocType.DOC
            ),
            handleIntent = {

            }
        )

    }
}


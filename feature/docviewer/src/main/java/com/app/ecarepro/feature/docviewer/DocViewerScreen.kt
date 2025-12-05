package com.app.ecarepro.feature.docviewer

import android.content.Intent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                DocViewerEvent.NavigateBack -> onBackClick()
                is DocViewerEvent.ShareDocument -> {
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "${event.title}\n\n${event.url}")
                        putExtra(Intent.EXTRA_SUBJECT, event.title)
                        type = "text/plain"
                    }
                    context.startActivity(
                        Intent.createChooser(shareIntent, "Share ${event.title}")
                    )
                }
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
            ZoomableImage(
                imageUrl = uiState.docUrl,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
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

@Composable
fun ZoomableImage(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 5f)

        val maxX = (scale - 1) * 1000f
        val maxY = (scale - 1) * 1000f

        offset = Offset(
            x = (offset.x + offsetChange.x).coerceIn(-maxX, maxX),
            y = (offset.y + offsetChange.y).coerceIn(-maxY, maxY)
        )
    }

    EcareProAsyncImage(
        imageUrl = imageUrl,
        modifier = modifier
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offset.x,
                translationY = offset.y
            )
            .transformable(state = state)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        if (scale > 1f) {
                            scale = 1f
                            offset = Offset.Zero
                        } else {
                            scale = 2.5f
                        }
                    }
                )
            },
        contentScale = ContentScale.Fit
    )
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


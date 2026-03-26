package com.app.ecarepro.feature.announcement.circular

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.Circular
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
import com.app.ecarepro.designsystem.core.component.ECAttachment
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.feature.announcement.common.NoticeDetailContent

@Composable
fun CircularDetailScreen(
    viewModel: CircularDetailViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToAttachment: (List<ECAttachment>) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is CircularDetailEvent.NavigateBack -> navigateBack()
                is CircularDetailEvent.ViewAttachment -> navigateToAttachment(event.attachments)
                is CircularDetailEvent.ShowMessage -> {
                    snackbarMessage = event.snackbarMessage
                    snackbarHostState.showSnackbar(event.snackbarMessage.text)
                }
            }
        }
    }

    CircularDetailContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CircularDetailContent(
    uiState: UiState<CircularDetailUiState>,
    handleIntent: (CircularDetailIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit,
) {
    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = "Circular details",
                onNavigationClicked = { handleIntent(CircularDetailIntent.OnBackClicked) }
            )
        },
        containerColor = White,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = onSnackbarDismissed,
    ) { paddingValues ->
        UiStateHandler(
            modifier = Modifier.padding(paddingValues),
            state = uiState
        ) { data ->
            NoticeDetailContent(
                heading = data.circular.title,
                detail = data.circular.message,
                noticeDate = data.circular.cirDate,
                updatedOn = data.circular.updatedOn,
                hasAttachment = data.circular.hasAttachment,
                filePath = data.circular.filePath,
                fileSize = data.circular.fileSize,
                onViewAttachment = { handleIntent(CircularDetailIntent.OnViewAttachment) },
                onDownloadAttachment = { handleIntent(CircularDetailIntent.OnDownloadAttachment) },
                modifier = Modifier.padding(paddingValues),
            )
        }
    }
}

@Preview(showBackground = true, name = "Circular Detail - With Attachment")
@Composable
private fun CircularDetailWithAttachmentPreview() {
    EcareProTheme {
        CircularDetailContent(
            uiState = UiState.Success(
                CircularDetailUiState(
                    circular = Circular(
                        cirID = 332,
                        title = "Testing new circular",
                        message = "<p>HOLIDAY - School will remain closed on Monday.</p>",
                        cirDate = "10-Feb-2026",
                        updatedOn = "10-Feb-2026 17:23 PM",
                        isNew = false,
                        isRead = true,
                        hasAttachment = true,
                        filePath = "https://example.com/circular.jpg",
                        fileSize = "268.37 KB",
                        mustRead = false,
                        id = "9ue6TTKZNYEOWjmngRdrNw==",
                        postedByName = "Mr. Mohit Singh Pawar",
                        postedByPhoto = null,
                        postedByRole = "Manager",
                    )
                )
            ),
            handleIntent = {},
            snackbarHostState = remember { SnackbarHostState() },
            snackbarMessage = null,
            onSnackbarDismissed = {},
        )
    }
}

@Preview(showBackground = true, name = "Circular Detail - No Attachment")
@Composable
private fun CircularDetailNoAttachmentPreview() {
    EcareProTheme {
        CircularDetailContent(
            uiState = UiState.Success(
                CircularDetailUiState(
                    circular = Circular(
                        cirID = 331,
                        title = "Testing circular",
                        message = "<p>Saturday will be a holiday for the technical support team.</p>",
                        cirDate = "02-Feb-2026",
                        updatedOn = "10-Feb-2026 16:55 PM",
                        isNew = false,
                        isRead = true,
                        hasAttachment = false,
                        filePath = null,
                        fileSize = null,
                        mustRead = true,
                        id = "E/4zSTNBe0TYLLL3jML/Xw==",
                        postedByName = null,
                        postedByPhoto = null,
                        postedByRole = null,
                    )
                )
            ),
            handleIntent = {},
            snackbarHostState = remember { SnackbarHostState() },
            snackbarMessage = null,
            onSnackbarDismissed = {},
        )
    }
}

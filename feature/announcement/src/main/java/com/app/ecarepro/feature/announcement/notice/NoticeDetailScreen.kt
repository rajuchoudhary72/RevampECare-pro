package com.app.ecarepro.feature.announcement.notice

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
import com.app.ecarepro.core.domain.model.Notice
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
fun NoticeDetailScreen(
    viewModel: NoticeDetailViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToAttachment: (List<ECAttachment>) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is NoticeDetailEvent.NavigateBack -> navigateBack()
                is NoticeDetailEvent.ViewAttachment -> navigateToAttachment(event.attachments)
                is NoticeDetailEvent.ShowMessage -> {
                    snackbarMessage = event.snackbarMessage
                    snackbarHostState.showSnackbar(event.snackbarMessage.text)
                }
            }
        }
    }

    NoticeDetailScreenContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoticeDetailScreenContent(
    uiState: UiState<NoticeDetailUiState>,
    handleIntent: (NoticeDetailIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit,
) {
    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = "Notice details",
                onNavigationClicked = { handleIntent(NoticeDetailIntent.OnBackClicked) }
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
                heading = data.notice.heading,
                detail = data.notice.detail,
                noticeDate = data.notice.noticeDate,
                updatedOn = data.notice.updatedOn,
                hasAttachment = data.notice.hasAttachment,
                filePath = data.notice.filePath,
                fileSize = data.notice.fileSize,
                onViewAttachment = { handleIntent(NoticeDetailIntent.OnViewAttachment) },
                onDownloadAttachment = { handleIntent(NoticeDetailIntent.OnDownloadAttachment) },
                modifier = Modifier.padding(paddingValues),
            )
        }
    }
}

@Preview(showBackground = true, name = "Notice Detail - With Attachment")
@Composable
private fun NoticeDetailWithAttachmentPreview() {
    EcareProTheme {
        NoticeDetailScreenContent(
            uiState = UiState.Success(
                NoticeDetailUiState(
                    notice = Notice(
                        id = "1",
                        ntID = 67,
                        heading = "Mandatory Uniform Guidelines Update",
                        detail = "<p>Students must follow the dress code, wear required attire, and look professional during school hours.</p>",
                        noticeDate = "08 Aug",
                        updatedOn = "2025-08-08T14:34:00",
                        isNew = false,
                        isRead = true,
                        hasAttachment = true,
                        filePath = "https://example.com/notice.pdf",
                        fileSize = "348.75 KB",
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

@Preview(showBackground = true, name = "Notice Detail - No Attachment")
@Composable
private fun NoticeDetailNoAttachmentPreview() {
    EcareProTheme {
        NoticeDetailScreenContent(
            uiState = UiState.Success(
                NoticeDetailUiState(
                    notice = Notice(
                        id = "2",
                        ntID = 101,
                        heading = "Parent-Teacher Meeting Schedule",
                        detail = "<p>The meeting is scheduled for this Friday from 10 AM to 1 PM in the school hall.</p>",
                        noticeDate = "21-Jan-2025",
                        updatedOn = "2025-01-21T14:34:00",
                        isNew = false,
                        isRead = true,
                        hasAttachment = false,
                        filePath = null,
                        fileSize = null,
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

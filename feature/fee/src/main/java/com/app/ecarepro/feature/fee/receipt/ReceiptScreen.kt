package com.app.ecarepro.feature.fee.receipt

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.designsystem.core.component.EcareProEmptyState
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.fee.receipt.component.ReceiptCard
import kotlinx.coroutines.launch

@Composable
fun ReceiptScreen(
    navigateBack: () -> Unit,
    viewModel: ReceiptViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is ReceiptEvent.NavigateBack -> navigateBack()
                is ReceiptEvent.OpenPdfViewer -> {
                    snackbarMessage = SnackbarMessage("Receipt ready: ${event.filePath}", com.app.ecarepro.designsystem.core.component.MessageType.INFO)
                    snackbarHostState.showSnackbar(event.filePath)
                }
                is ReceiptEvent.ShowMessage -> {
                    snackbarMessage = event.message
                    snackbarHostState.showSnackbar(event.message.text)
                }
            }
        }
    }

    Content(
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
private fun Content(
    uiState: ReceiptUiState,
    snackbarHostState: SnackbarHostState,
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit,
    handleIntent: (ReceiptIntent) -> Unit,
    navigateBack: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                modifier = Modifier.shadow(elevation = 1.dp),
                title = "Fee receipt",
                onNavigationClicked = { handleIntent(ReceiptIntent.OnBackClicked) },
            )
        },
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = onSnackbarDismissed,
        containerColor = Color.White,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.appColors.primary,
                    )
                }
                uiState.isError -> {
                    EcareProEmptyState(message = "Failed to load receipts")
                }
                uiState.receipts.isEmpty() -> {
                    EcareProEmptyState(message = "No receipts found")
                }
                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(uiState.receipts, key = { it.recId }) { card ->
                                ReceiptCard(
                                    card = card,
                                    sessionId = uiState.selectedSession?.yrid ?: 0,
                                    isExpanded = uiState.expandedReceiptId == card.recId,
                                    onToggle = { handleIntent(ReceiptIntent.ToggleReceipt(card.recId)) },
                                    onView = {
                                        handleIntent(
                                            ReceiptIntent.ViewReceipt(
                                                recId = card.recId,
                                                sessionId = uiState.selectedSession?.yrid ?: 0,
                                                stId = card.stId,
                                            )
                                        )
                                    },
                                    onDownload = {
                                        handleIntent(
                                            ReceiptIntent.DownloadReceipt(
                                                recId = card.recId,
                                                sessionId = uiState.selectedSession?.yrid ?: 0,
                                                stId = card.stId,
                                                receiptNumber = card.receiptNumber,
                                            )
                                        )
                                    },
                                )
                            }
                        }

                        // Session selector at bottom
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { handleIntent(ReceiptIntent.ToggleSessionPicker) }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = uiState.selectedSession?.yearname ?: "Select session",
                                style = MaterialTheme.appTypography.interSemiBold14px,
                                color = MaterialTheme.appColors.textPrimary,
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Select session",
                                tint = MaterialTheme.appColors.textSecondary,
                            )
                        }
                    }
                }
            }
        }
    }

    if (uiState.showSessionPicker) {
        ModalBottomSheet(
            onDismissRequest = { handleIntent(ReceiptIntent.DismissSessionPicker) },
            sheetState = sheetState,
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Select Session",
                    style = MaterialTheme.appTypography.interSemiBold16px,
                    color = MaterialTheme.appColors.textPrimary,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
                uiState.sessions.forEach { session ->
                    Text(
                        text = session.yearname,
                        style = if (session.yrid == uiState.selectedSession?.yrid)
                            MaterialTheme.appTypography.interSemiBold14px
                        else MaterialTheme.appTypography.interRegular14px,
                        color = if (session.yrid == uiState.selectedSession?.yrid)
                            MaterialTheme.appColors.primary
                        else MaterialTheme.appColors.textPrimary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                coroutineScope.launch {
                                    sheetState.hide()
                                    handleIntent(ReceiptIntent.SelectSession(session.yrid))
                                }
                            }
                            .padding(vertical = 12.dp),
                    )
                }
            }
        }
    }
}

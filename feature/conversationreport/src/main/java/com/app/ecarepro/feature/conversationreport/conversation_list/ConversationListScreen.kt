package com.app.ecarepro.feature.conversationreport.conversation_list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.designsystem.core.component.Button
import com.app.ecarepro.designsystem.core.component.EcareProDateRangeField
import com.app.ecarepro.designsystem.core.component.EcareProEmptyState
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.TextButton
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.conversationreport.R
import com.app.ecarepro.feature.conversationreport.conversation_list.component.ConversationCard
import com.app.ecarepro.feature.conversationreport.conversation_list.component.ConversationFilterSheet
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ConversationListScreen(
    navigateBack: () -> Unit,
    navigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ConversationListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<com.app.ecarepro.designsystem.core.component.SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is ConversationListEvent.NavigateBack -> navigateBack()
                is ConversationListEvent.NavigateToDetail -> navigateToDetail(event.msgId)
                is ConversationListEvent.ShowMessage -> {
                    snackbarMessage = event.message
                    snackbarHostState.showSnackbar(event.message.text)
                }
            }
        }
    }

    ConversationListContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConversationListContent(
    uiState: ConversationListUiState,
    handleIntent: (ConversationListIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    snackbarMessage: com.app.ecarepro.designsystem.core.component.SnackbarMessage?,
    onSnackbarDismissed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val displayFmt = remember { SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH) }
    val apiFmt = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }

    var startDateDisplay by rememberSaveable { mutableStateOf(displayFmt.format(uiState.startDate)) }
    var endDateDisplay by rememberSaveable { mutableStateOf(displayFmt.format(uiState.endDate)) }
    var startDateMillis by rememberSaveable { mutableLongStateOf(uiState.startDate.time) }

    EcareProScaffold(
        modifier = modifier,
        topBar = {
            EcareProTopAppBar(
                title = stringResource(R.string.conversation_report_title),
                onNavigationClicked = { handleIntent(ConversationListIntent.OnBackClicked) },
                actions = {
                    IconButton(onClick = { handleIntent(ConversationListIntent.OnRefreshClicked) }) {
                        Icon(
                            painter = painterResource(com.app.ecarepro.core.designsystem.R.drawable.ic_hori_menu),
                            contentDescription = null,
                            tint = MaterialTheme.appColors.textPrimary,
                        )
                    }
                    IconButton(onClick = { handleIntent(ConversationListIntent.ShowFilterSheet) }) {
                        Icon(
                            painter = painterResource(com.app.ecarepro.core.designsystem.R.drawable.ic_hori_menu),
                            contentDescription = null,
                            tint = MaterialTheme.appColors.textPrimary,
                        )
                    }
                },
            )
        },
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = onSnackbarDismissed,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            EcareProDateRangeField(
                startDateDisplay = startDateDisplay,
                endDateDisplay = endDateDisplay,
                startDateLabel = stringResource(R.string.conversation_start_date),
                endDateLabel = stringResource(R.string.conversation_end_date),
                startDateMillis = startDateMillis,
                minStartDateMillis = Long.MIN_VALUE,
                onStartDateSelected = { apiDate, displayDate, millis ->
                    startDateDisplay = displayDate
                    startDateMillis = millis
                    val end = uiState.endDate
                    handleIntent(
                        ConversationListIntent.OnDateRangeSelected(
                            startDate = apiFmt.parse(apiDate) ?: uiState.startDate,
                            endDate = end,
                        )
                    )
                },
                onEndDateSelected = { apiDate, displayDate, _ ->
                    endDateDisplay = displayDate
                    handleIntent(
                        ConversationListIntent.OnDateRangeSelected(
                            startDate = uiState.startDate,
                            endDate = apiFmt.parse(apiDate) ?: uiState.endDate,
                        )
                    )
                },
                apiDateFormat = "yyyy-MM-dd",
                displayDateFormat = "MMMM dd, yyyy",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            )

            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.appColors.primary)
                    }
                }

                uiState.error != null -> {
                    EcareProEmptyState(message = uiState.error)
                }

                uiState.conversations.isEmpty() -> {
                    EcareProEmptyState(message = stringResource(R.string.conversation_no_results))
                }

                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(uiState.conversations, key = { it.id }) { card ->
                            ConversationCard(
                                card = card,
                                canDelete = uiState.canDelete,
                                onTap = { handleIntent(ConversationListIntent.OnConversationTapped(card.id)) },
                                onDelete = { handleIntent(ConversationListIntent.OnDeleteClicked(card)) },
                            )
                            HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)
                        }
                    }
                }
            }
        }
    }

    if (uiState.showFilterSheet) {
        ConversationFilterSheet(
            currentFilter = uiState.filterState,
            onApply = { handleIntent(ConversationListIntent.OnFilterApplied(it)) },
            onClearAll = { handleIntent(ConversationListIntent.OnFilterCleared) },
            onDismiss = { handleIntent(ConversationListIntent.DismissFilterSheet) },
        )
    }

    if (uiState.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { handleIntent(ConversationListIntent.OnDeleteDismissed) },
            title = {
                Text(
                    text = stringResource(R.string.conversation_delete_title),
                    style = MaterialTheme.appTypography.interSemiBold16px,
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.conversation_delete_message),
                    style = MaterialTheme.appTypography.interRegular14px,
                )
            },
            confirmButton = {
                Button(
                    title = stringResource(R.string.conversation_delete_confirm),
                    onClick = { handleIntent(ConversationListIntent.OnDeleteConfirmed) },
                    backgroundColor = Color(0xFFF44336),
                )
            },
            dismissButton = {
                TextButton(
                    title = stringResource(android.R.string.cancel),
                    onClick = { handleIntent(ConversationListIntent.OnDeleteDismissed) },
                    titleColor = MaterialTheme.appColors.textSecondary,
                )
            },
        )
    }
}

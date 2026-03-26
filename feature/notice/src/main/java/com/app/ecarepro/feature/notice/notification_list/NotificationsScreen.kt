package com.app.ecarepro.feature.notice.notification_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.designsystem.core.component.EcareProEmptyState
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.feature.notice.notification_list.component.NotificationCard

@Composable
fun NotificationsScreen(
    navigateBack: () -> Unit,
    navigateTo: (NavKey) -> Unit,
    viewModel: NotificationsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is NotificationsEvent.NavigateBack -> navigateBack()
                is NotificationsEvent.NavigateToDestination -> {
                    // Deep-link routing via chMenuID — handled by the host
                    // navigateTo called with resolved NavKey if available
                }
            }
        }
    }

    Content(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    uiState: NotificationsUiState,
    handleIntent: (NotificationsIntent) -> Unit,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible to totalItems
        }.collect { (lastVisible, total) ->
            if (total > 0 && lastVisible >= total - 1) {
                handleIntent(NotificationsIntent.LoadMore)
            }
        }
    }

    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = "Notifications",
                onNavigationClicked = { handleIntent(NotificationsIntent.OnBackClicked) },
            )
        },
        containerColor = Color(0xFFF5F5F5),
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = MaterialTheme.appColors.primary)
                }
            }

            uiState.error != null -> {
                EcareProEmptyState(
                    message = uiState.error,
                    modifier = Modifier.padding(paddingValues),
                )
            }

            uiState.notifications.isEmpty() -> {
                EcareProEmptyState(
                    message = "No Notifications",
                    modifier = Modifier.padding(paddingValues),
                )
            }

            else -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(16.dp),
                ) {
                    itemsIndexed(
                        items = uiState.notifications,
                        key = { _, item -> item.id },
                    ) { _, notification ->
                        NotificationCard(
                            presentation = notification,
                            onView = { handleIntent(NotificationsIntent.OnNotificationTapped(notification)) },
                        )
                    }

                    if (uiState.isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.appColors.primary,
                                    modifier = Modifier.padding(8.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationsScreenPreview() {
    EcareProTheme {
        Content(
            uiState = NotificationsUiState(
                isLoading = false,
                notifications = listOf(
                    NotificationCardPresentation(
                        id = "1",
                        title = "Staff leave request",
                        body = "A new leave request has been submitted for 1 day on 05 Mar 2026 by Anand Dwivedi",
                        sentOn = "24 March '26",
                        iconUrl = null,
                        isRead = false,
                        moduleID = 2,
                        chMenuID = 6,
                        refID = null,
                    ),
                    NotificationCardPresentation(
                        id = "2",
                        title = "Survey",
                        body = "You have a new survey",
                        sentOn = "24 March '26",
                        iconUrl = null,
                        isRead = true,
                        moduleID = 8,
                        chMenuID = 8,
                        refID = null,
                    ),
                ),
            ),
            handleIntent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationsEmptyPreview() {
    EcareProTheme {
        Content(
            uiState = NotificationsUiState(isLoading = false, notifications = emptyList()),
            handleIntent = {},
        )
    }
}

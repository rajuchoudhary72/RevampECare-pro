package com.app.ecarepro.feature.message.screens

import com.app.ecarepro.designsystem.core.component.EcareConfirmationBottomSheet
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
import com.app.ecarepro.core.ui.component.EcareProPullToRefresh
import com.app.ecarepro.core.ui.component.LazyListLoadMoreHandler
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.message.common.MessageAvatar
import com.app.ecarepro.feature.message.common.MessageCard
import com.app.ecarepro.feature.message.common.MessageEmptyState
import com.app.ecarepro.feature.message.navigation.MessageNavigationGraph

// ============== ENTRY POINT ==============

@Composable
internal fun SentScreen(
    viewModel: SentViewModel = hiltViewModel(),
    onChatClick: (MessageNavigationGraph.ChatDetail) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SentScreenContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        onChatClick = onChatClick,
    )
}

// ============== CONTENT ==============

@Composable
private fun SentScreenContent(
    uiState: UiState<SentUiState>,
    handleIntent: (SentIntent) -> Unit,
    onChatClick: (MessageNavigationGraph.ChatDetail) -> Unit = {},
) {
    UiStateHandler(
        state = uiState,
        onRetry = { handleIntent(SentIntent.OnRetry) },
    ) { data ->
        // Delete confirmation bottom sheet
        if (data.deleteMessageId != null) {
            EcareConfirmationBottomSheet(
                title = "Delete Message",
                description = "Are you sure you want to delete this message?",
                buttonText = "Delete",
                buttonColor = MaterialTheme.appColors.error,
                onDismiss = { handleIntent(SentIntent.OnDeleteDismiss) },
                onDeleteClick = { handleIntent(SentIntent.OnDeleteConfirm) },
            )
        }

        EcareProPullToRefresh(
            isRefreshing = data.isRefreshing,
            onRefresh = { handleIntent(SentIntent.OnRefresh) },
            modifier = Modifier.fillMaxSize(),
        ) {
            if (data.messages.isEmpty()) {
                MessageEmptyState(modifier = Modifier.fillMaxSize())
            } else {
                val listState = rememberLazyListState()

                LazyListLoadMoreHandler(
                    listState = listState,
                    enabled = data.showLoadMoreView,
                    onLoadMore = { handleIntent(SentIntent.OnLoadMore) },
                )

                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(data.messages, key = { it.id }) { message ->
                        SentMessageListItem(
                            message = message,
                            onDeleteClick = { handleIntent(SentIntent.OnDeleteClick(message.id)) },
                            onChatClick = {
                                val firstRecipient = message.recipients.firstOrNull()
                                onChatClick(
                                    MessageNavigationGraph.ChatDetail(
                                        messageId = message.id,
                                        title = message.title,
                                        participantName = firstRecipient?.name ?: "",
                                        participantRole = firstRecipient?.designation ?: "",
                                        participantPhotoUrl = firstRecipient?.photo ?: "",
                                    )
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}

// ============== SENT LIST ITEM ==============

@Composable
private fun SentMessageListItem(
    message: SentMessageItemData,
    onDeleteClick: () -> Unit,
    onChatClick: () -> Unit = {},
) {
    val firstRecipient = message.recipients.firstOrNull()
    val extraCount = message.recipients.size - 1

    MessageCard(onClick = onChatClick) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            // Row 1: Title + optional delete + Timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = message.title,
                    style = MaterialTheme.appTypography.interSemiBold14px,
                    color = MaterialTheme.appColors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(8.dp))
                if (message.canDelete) {
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(28.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete message",
                            tint = MaterialTheme.appColors.error,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = message.timestamp,
                    style = MaterialTheme.appTypography.interRegular12px,
                    color = MaterialTheme.appColors.textSecondary,
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Row 2: Preview text
            Text(
                text = message.preview,
                style = MaterialTheme.appTypography.interRegular13px,
                color = MaterialTheme.appColors.textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            if (firstRecipient != null) {
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.8.dp)
                        .background(Color(0xFFEEEEEE)),
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Row 3: Recipient info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    MessageAvatar(
                        name = firstRecipient.name,
                        photoUrl = firstRecipient.photo,
                        size = 38.dp,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = firstRecipient.name,
                            style = MaterialTheme.appTypography.interSemiBold14px,
                            color = MaterialTheme.appColors.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        firstRecipient.designation?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.appTypography.interRegular12px,
                                color = MaterialTheme.appColors.textSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                    if (extraCount > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(Color(0xFFF0F0F0), CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "+$extraCount",
                                style = MaterialTheme.appTypography.interMedium12px.copy(
                                    fontSize = 10.sp,
                                ),
                                color = MaterialTheme.appColors.textSecondary,
                            )
                        }
                    }
                }
            }
        }
    }
}

// ============== PREVIEW ==============

@Preview(showBackground = true)
@Composable
private fun SentScreenPreview() {
    EcareProTheme {
        SentScreenContent(
            uiState = UiState.Success(
                SentUiState(
                    messages = listOf(
                        SentMessageItemData(
                            id = "s1",
                            title = "Upcoming Unit Test Schedule",
                            preview = "Hey Aarav, are you free? Please review the brief first.",
                            timestamp = "1:15 PM",
                            canDelete = true,
                            recipients = listOf(
                                RecipientData("Mr. Mohit Kumar", "Principal, Modern School", ""),
                                RecipientData("Ms. Priya Sharma", "Head of Mathematics", ""),
                            ),
                        ),
                        SentMessageItemData(
                            id = "s2",
                            title = "Math Final Exam",
                            preview = "Don't forget to check the syllabus updates!",
                            timestamp = "Yesterday",
                            canDelete = false,
                            recipients = listOf(
                                RecipientData("Ms. Priya Sharma", "Head of Mathematics", ""),
                            ),
                        ),
                    ),
                )
            ),
            handleIntent = {},
        )
    }
}

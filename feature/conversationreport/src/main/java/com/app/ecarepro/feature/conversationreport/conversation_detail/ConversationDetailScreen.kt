package com.app.ecarepro.feature.conversationreport.conversation_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.component.EcareProEmptyState
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.conversationreport.R
import com.app.ecarepro.feature.conversationreport.conversation_detail.component.RecipientListSheet

@Composable
fun ConversationDetailScreen(
    viewModel: ConversationDetailViewModel,
    navigateBack: () -> Unit,
    navigateToAttachment: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is ConversationDetailEvent.NavigateBack -> navigateBack()
                is ConversationDetailEvent.OpenAttachment -> navigateToAttachment(event.url)
            }
        }
    }

    ConversationDetailContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        modifier = modifier,
    )
}

@Composable
private fun ConversationDetailContent(
    uiState: ConversationDetailUiState,
    handleIntent: (ConversationDetailIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.appColors.background),
    ) {
        // Custom sender header (no TopAppBar per spec)
        SenderHeader(
            name = uiState.senderName,
            designation = uiState.senderDesignation,
            photo = uiState.senderPhoto,
            onClose = { handleIntent(ConversationDetailIntent.OnBackClicked) },
        )
        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)

        // Subject + view recipients
        SubjectHeader(
            subject = uiState.subject,
            onViewRecipients = { handleIntent(ConversationDetailIntent.ShowRecipientSheet) },
        )
        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)

        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.appColors.primary)
                }
            }

            uiState.error != null -> {
                EcareProEmptyState(message = uiState.error)
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp),
                ) {
                    items(uiState.messages, key = { it.id }) { message ->
                        MessageCell(
                            message = message,
                            onAttachmentTapped = { handleIntent(ConversationDetailIntent.OnAttachmentClicked(it)) },
                        )
                        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)
                    }
                }
            }
        }
    }

    if (uiState.showRecipientSheet) {
        RecipientListSheet(
            recipients = uiState.recipients,
            onDismiss = { handleIntent(ConversationDetailIntent.DismissRecipientSheet) },
        )
    }
}

@Composable
private fun SenderHeader(
    name: String,
    designation: String,
    photo: String?,
    onClose: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        EcareProAsyncImage(
            imageUrl = photo,
            contentDescription = name,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.appTypography.interSemiBold14px,
                color = MaterialTheme.appColors.textPrimary,
            )
            Text(
                text = designation,
                style = MaterialTheme.appTypography.interRegular13px,
                color = MaterialTheme.appColors.textSecondary,
            )
        }
        IconButton(onClick = onClose) {
            Icon(
                painter = painterResource(com.app.ecarepro.core.designsystem.R.drawable.icon_close),
                contentDescription = null,
                tint = MaterialTheme.appColors.textPrimary,
            )
        }
    }
}

@Composable
private fun SubjectHeader(
    subject: String,
    onViewRecipients: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.conversation_subject),
                style = MaterialTheme.appTypography.interRegular12px,
                color = MaterialTheme.appColors.textSecondary,
            )
            Text(
                text = subject,
                style = MaterialTheme.appTypography.interSemiBold14px,
                color = MaterialTheme.appColors.textPrimary,
            )
        }
        Text(
            text = stringResource(R.string.conversation_view_recipients),
            style = MaterialTheme.appTypography.interMedium14px.copy(textDecoration = TextDecoration.Underline),
            color = MaterialTheme.appColors.primary,
            modifier = Modifier.clickable { onViewRecipients() },
        )
    }
}

@Composable
private fun MessageCell(
    message: MessagePresentation,
    onAttachmentTapped: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            EcareProAsyncImage(
                imageUrl = message.senderPhoto,
                contentDescription = message.senderName,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = message.senderName,
                    style = MaterialTheme.appTypography.interSemiBold14px,
                    color = MaterialTheme.appColors.textPrimary,
                )
                Text(
                    text = message.sentDate,
                    style = MaterialTheme.appTypography.interRegular12px,
                    color = MaterialTheme.appColors.textSecondary,
                )
            }
        }

        if (message.body.isNotBlank()) {
            Text(
                text = message.body,
                style = MaterialTheme.appTypography.interRegular14px,
                color = MaterialTheme.appColors.textPrimary,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (message.attachments.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(message.attachments) { url ->
                    val isDocument = url.endsWith(".pdf", true) ||
                        url.endsWith(".doc", true) ||
                        url.endsWith(".docx", true) ||
                        url.endsWith(".xlsx", true)

                    if (isDocument) {
                        Row(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.appColors.primary.copy(alpha = 0.1f),
                                    RoundedCornerShape(8.dp),
                                )
                                .clickable { onAttachmentTapped(url) }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                painter = painterResource(com.app.ecarepro.core.designsystem.R.drawable.icon_doc),
                                contentDescription = null,
                                tint = MaterialTheme.appColors.primary,
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = url.substringAfterLast('/'),
                                style = MaterialTheme.appTypography.interRegular12px,
                                color = MaterialTheme.appColors.primary,
                            )
                        }
                    } else {
                        EcareProAsyncImage(
                            imageUrl = url,
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onAttachmentTapped(url) },
                        )
                    }
                }
            }
        }
    }
}

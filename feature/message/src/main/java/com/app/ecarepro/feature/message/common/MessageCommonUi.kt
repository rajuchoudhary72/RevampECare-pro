package com.app.ecarepro.feature.message.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.message.AttachmentType
import com.app.ecarepro.feature.message.MessageItemData

// ============== COMMON CARD CONTAINER ==============

@Composable
internal fun MessageCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
        colors = CardDefaults.cardColors(containerColor = White),
    ) {
        content()
    }
}

// ============== AVATAR ==============

@Composable
internal fun MessageAvatar(
    name: String,
    photoUrl: String,
    size: Dp = 44.dp,
) {
    if (photoUrl.isNotEmpty()) {
        EcareProAsyncImage(
            imageUrl = photoUrl,
            contentDescription = name,
            modifier = Modifier
                .size(size)
                .clip(CircleShape),
        )
    } else {
        Box(
            modifier = Modifier
                .size(size)
                .background(MaterialTheme.appColors.border.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = name.take(1).uppercase(),
                style = MaterialTheme.appTypography.interSemiBold14px.copy(
                    fontSize = (size.value * 0.36f).sp,
                ),
                color = MaterialTheme.appColors.textPrimary,
            )
        }
    }
}

// ============== EMPTY STATE ==============

@Composable
internal fun MessageEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.Inbox,
            contentDescription = null,
            tint = MaterialTheme.appColors.border,
            modifier = Modifier.size(64.dp),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "No messages yet",
            style = MaterialTheme.appTypography.interSemiBold14px,
            color = MaterialTheme.appColors.textSecondary,
        )
    }
}

// ============== ATTACHMENT ICON ==============

internal fun AttachmentType.toIcon(): ImageVector = when (this) {
    AttachmentType.IMAGE -> Icons.Outlined.Image
    AttachmentType.AUDIO -> Icons.Outlined.MusicNote
    AttachmentType.PDF -> Icons.Outlined.PictureAsPdf
    AttachmentType.CONTACT -> Icons.Outlined.AccountCircle
}

// ============== INBOX MESSAGE CARD (reused by Inbox + Timeline) ==============

internal val UnreadBadgeColor = Color(0xFFE53935)

@Composable
internal fun InboxMessageListItem(
    message: MessageItemData,
    onClick: () -> Unit = {},
) {
    val isUnread = message.unreadCount > 0

    MessageCard(onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            MessageAvatar(name = message.senderName, photoUrl = message.senderPhoto)

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Sender name + timestamp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = message.senderName,
                        style = MaterialTheme.appTypography.interRegular13px,
                        color = MaterialTheme.appColors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = message.timestamp,
                        style = MaterialTheme.appTypography.interRegular12px,
                        color = MaterialTheme.appColors.textSecondary,
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Bold title + unread badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                    if (isUnread) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .background(UnreadBadgeColor, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = message.unreadCount.toString(),
                                style = MaterialTheme.appTypography.interMedium12px.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                ),
                                color = White,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Preview text + attachment icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Text(
                        text = message.preview,
                        style = MaterialTheme.appTypography.interRegular13px,
                        color = MaterialTheme.appColors.textSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    message.attachmentType?.let { type ->
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = type.toIcon(),
                            contentDescription = type.name,
                            tint = MaterialTheme.appColors.textSecondary,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
        }
    }
}

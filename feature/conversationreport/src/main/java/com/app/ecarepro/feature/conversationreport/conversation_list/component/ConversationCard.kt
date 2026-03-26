package com.app.ecarepro.feature.conversationreport.conversation_list.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.conversationreport.R
import com.app.ecarepro.feature.conversationreport.conversation_list.ConversationCardPresentation

@Composable
fun ConversationCard(
    card: ConversationCardPresentation,
    canDelete: Boolean,
    onTap: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onTap() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        // Subject row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = card.subject,
                style = MaterialTheme.appTypography.interSemiBold14px,
                color = MaterialTheme.appColors.textPrimary,
                modifier = Modifier.weight(1f),
            )
            if (canDelete) {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(24.dp),
                ) {
                    Icon(
                        painter = painterResource(com.app.ecarepro.core.designsystem.R.drawable.ic_hori_menu),
                        contentDescription = null,
                        tint = MaterialTheme.appColors.textSecondary,
                    )
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(R.string.conversation_delete_title),
                                    style = MaterialTheme.appTypography.interRegular14px,
                                    color = Color(0xFFF44336),
                                )
                            },
                            onClick = {
                                showMenu = false
                                onDelete()
                            },
                        )
                    }
                }
            }
        }

        // Message preview
        Text(
            text = card.messagePreview,
            style = MaterialTheme.appTypography.interRegular12px,
            color = MaterialTheme.appColors.textPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )

        // Sent to
        Text(
            text = "${stringResource(R.string.conversation_sent_to)} ${card.sentToName}",
            style = MaterialTheme.appTypography.interRegular12px,
            color = MaterialTheme.appColors.textSecondary,
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Sender row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            EcareProAsyncImage(
                imageUrl = card.senderPhoto,
                contentDescription = card.senderName,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.conversation_sent_by),
                    style = MaterialTheme.appTypography.interRegular12px,
                    color = MaterialTheme.appColors.textSecondary,
                )
                Text(
                    text = "${card.senderName}, ${card.senderDesignation}",
                    style = MaterialTheme.appTypography.interMedium14px,
                    color = MaterialTheme.appColors.textPrimary,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = stringResource(R.string.conversation_sent_on),
                    style = MaterialTheme.appTypography.interRegular12px,
                    color = MaterialTheme.appColors.textSecondary,
                )
                Text(
                    text = card.sentDate,
                    style = MaterialTheme.appTypography.interMedium12px,
                    color = MaterialTheme.appColors.textPrimary,
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Read badge
        val hasReads = card.readCount > 0
        val badgeBg = if (hasReads) MaterialTheme.appColors.primary.copy(alpha = 0.1f) else Color(0xFFF0F0F0)
        val badgeText = if (hasReads) MaterialTheme.appColors.primary else MaterialTheme.appColors.textSecondary
        val label = "${stringResource(R.string.conversation_read_by)} ${card.readCount} (${card.readPercentage}%) ${stringResource(R.string.conversation_recipients)}"

        Text(
            text = label,
            style = MaterialTheme.appTypography.interMedium12px,
            color = badgeText,
            modifier = Modifier
                .fillMaxWidth()
                .background(badgeBg, RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
        )
    }
}

package com.app.ecarepro.feature.conversationreport.conversation_detail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.conversationreport.R
import com.app.ecarepro.feature.conversationreport.conversation_detail.RecipientPresentation
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipientListSheet(
    recipients: List<RecipientPresentation>,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.conversation_recipient_count, recipients.size),
                    style = MaterialTheme.appTypography.interSemiBold16px,
                    color = MaterialTheme.appColors.textPrimary,
                )
                IconButton(onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                }) {
                    Icon(
                        painter = painterResource(com.app.ecarepro.core.designsystem.R.drawable.icon_close),
                        contentDescription = null,
                        tint = MaterialTheme.appColors.textPrimary,
                    )
                }
            }
            HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(recipients, key = { it.id }) { recipient ->
                    RecipientRow(recipient = recipient)
                    HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)
                }
            }
        }
    }
}

@Composable
private fun RecipientRow(recipient: RecipientPresentation) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        EcareProAsyncImage(
            imageUrl = recipient.photo,
            contentDescription = recipient.name,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = recipient.name,
                style = MaterialTheme.appTypography.interSemiBold14px,
                color = MaterialTheme.appColors.textPrimary,
            )
            if (recipient.subtitle.isNotBlank()) {
                Text(
                    text = recipient.subtitle,
                    style = MaterialTheme.appTypography.interRegular13px,
                    color = MaterialTheme.appColors.textSecondary,
                )
            }
            Text(
                text = "${stringResource(R.string.conversation_status)} ${recipient.readStatus}",
                style = MaterialTheme.appTypography.interMedium12px,
                color = if (recipient.isRead) MaterialTheme.appColors.primary else Color(0xFFF44336),
            )
        }
    }
}

package com.app.ecarepro.feature.notice.notification_list.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.notice.notification_list.NotificationCardPresentation

@Composable
fun NotificationCard(
    presentation: NotificationCardPresentation,
    onView: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onView,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = presentation.title,
                        style = MaterialTheme.appTypography.interSemiBold14px,
                        color = MaterialTheme.appColors.textPrimary,
                    )
                    Text(
                        text = presentation.body,
                        style = MaterialTheme.appTypography.interRegular13px,
                        color = MaterialTheme.appColors.textSecondary,
                    )
                }

                Box(modifier = Modifier.size(48.dp)) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF0F0F0)),
                        contentAlignment = Alignment.Center,
                    ) {
                        EcareProAsyncImage(
                            imageUrl = presentation.iconUrl,
                            contentDescription = presentation.title,
                            modifier = Modifier.size(28.dp),
                        )
                    }
                    if (!presentation.isRead) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2196F3))
                                .align(Alignment.TopEnd),
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "On: ",
                    style = MaterialTheme.appTypography.interRegular12px,
                    color = MaterialTheme.appColors.textSecondary,
                )
                Text(
                    text = presentation.sentOn,
                    style = MaterialTheme.appTypography.interMedium12px,
                    color = MaterialTheme.appColors.textPrimary,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "View",
                    style = MaterialTheme.appTypography.interMedium12px,
                    color = MaterialTheme.appColors.primary,
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.appColors.primary,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
private fun NotificationCardUnreadPreview() {
    EcareProTheme {
        NotificationCard(
            presentation = NotificationCardPresentation(
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
            onView = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
private fun NotificationCardReadPreview() {
    EcareProTheme {
        NotificationCard(
            presentation = NotificationCardPresentation(
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
            onView = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

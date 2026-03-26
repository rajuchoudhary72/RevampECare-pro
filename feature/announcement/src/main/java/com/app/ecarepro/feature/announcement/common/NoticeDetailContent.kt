package com.app.ecarepro.feature.announcement.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

@Composable
fun NoticeDetailContent(
    heading: String,
    detail: String?,
    noticeDate: String?,
    updatedOn: String?,
    hasAttachment: Boolean,
    filePath: String?,
    fileSize: String?,
    onViewAttachment: () -> Unit,
    onDownloadAttachment: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = heading,
            style = MaterialTheme.appTypography.interSemiBold14px,
            color = MaterialTheme.appColors.textPrimary
        )

        if (!detail.isNullOrBlank()) {
            val plainText = detail.replace(Regex("<[^>]*>"), "").trim()
            Text(
                text = plainText,
                style = MaterialTheme.appTypography.interRegular14px,
                color = MaterialTheme.appColors.textSecondary
            )
        }

        HorizontalDivider(thickness = 0.5.dp)

        DetailInfoRow(
            icon = Icons.Default.CalendarToday,
            label = "Added",
            value = noticeDate ?: "-"
        )

        HorizontalDivider(thickness = 0.5.dp)

        DetailInfoRow(
            icon = Icons.Outlined.Schedule,
            label = "Updated on",
            value = updatedOn?.take(16) ?: "-"
        )

        if (hasAttachment) {
            HorizontalDivider(thickness = 0.5.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.AttachFile,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.appColors.textSecondary
                )
                Text(
                    text = "Attachment",
                    style = MaterialTheme.appTypography.interRegular14px,
                    color = MaterialTheme.appColors.textSecondary,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )
                TextButton(onClick = onViewAttachment) {
                    Icon(
                        imageVector = Icons.Default.RemoveRedEye,
                        contentDescription = "View",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.appColors.primary
                    )
                    Text(
                        text = "View",
                        style = MaterialTheme.appTypography.interRegular14px,
                        color = MaterialTheme.appColors.primary,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                Text(
                    text = "|",
                    style = MaterialTheme.appTypography.interRegular14px,
                    color = MaterialTheme.appColors.textSecondary
                )
                TextButton(onClick = onDownloadAttachment) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Download",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.appColors.primary
                    )
                    Text(
                        text = "Download",
                        style = MaterialTheme.appTypography.interRegular14px,
                        color = MaterialTheme.appColors.primary,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            // Attachment preview box
            if (!filePath.isNullOrBlank()) {
                val isPdf = filePath.endsWith(".pdf", ignoreCase = true)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = if (isPdf) Color(0xFFFCE4E4) else MaterialTheme.appColors.primary.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isPdf) "PDF" else "File",
                        style = MaterialTheme.appTypography.interSemiBold14px,
                        color = if (isPdf) Color(0xFFE53935) else MaterialTheme.appColors.primary
                    )
                    if (!fileSize.isNullOrBlank()) {
                        Text(
                            text = fileSize,
                            style = MaterialTheme.appTypography.interRegular12px,
                            color = MaterialTheme.appColors.textSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.appColors.textSecondary
        )
        Text(
            text = label,
            style = MaterialTheme.appTypography.interRegular14px,
            color = MaterialTheme.appColors.textSecondary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.appTypography.interRegular14px,
            color = MaterialTheme.appColors.textPrimary
        )
    }
}

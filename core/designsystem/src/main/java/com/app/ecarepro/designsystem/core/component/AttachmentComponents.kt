package com.app.ecarepro.designsystem.core.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

/**
 * Main screen for displaying a list of downloadable file attachments
 *
 * @param attachments List of attachments to display
 * @param onBackPressed Callback when back button is pressed
 * @param onAttachmentClick Callback when attachment icon is clicked
 * @param onDownloadClick Callback when download icon is clicked with attachment details
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadFilesView(
    attachments: List<ECAttachment>,
    onBackPressed: () -> Unit,
    onAttachmentClick: (ECAttachment) -> Unit = {},
    onDownloadClick: (ECAttachment) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Header
        EcareProTopAppBar(
            title = "Attachments",
            onNavigationClicked = onBackPressed
        )

        // Attachment List
        if (attachments.isEmpty()) {
            EmptyAttachmentsView()
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(
                    items = attachments,
                    key = { it.id }
                ) { attachment ->
                    AttachmentRowView(
                        attachment = attachment,
                        onAttachmentClick = { onAttachmentClick(attachment) },
                        onDownloadClick = { onDownloadClick(attachment) }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(top = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.appColors.divider
                    )
                }
            }
        }
    }
}

/**
 * Individual attachment row item
 *
 * @param attachment The attachment to display
 * @param onAttachmentClick Callback when attachment icon is clicked
 * @param onDownloadClick Callback when download icon is clicked
 */
@Composable
fun AttachmentRowView(
    attachment: ECAttachment,
    onAttachmentClick: () -> Unit = {},
    onDownloadClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // File Icon
        AttachmentFileIcon()

        // Attachment Name
        Text(
            text = attachment.name,
            style = MaterialTheme.appTypography.interMedium16px,
            color = MaterialTheme.appColors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        // Attachment Icon
        Icon(
            imageVector = Icons.Default.AttachFile,
            contentDescription = "View attachment",
            modifier = Modifier
                .size(20.dp)
                .clickable { onAttachmentClick() },
            tint = MaterialTheme.appColors.primary
        )

        // Download Icon
        Icon(
            imageVector = Icons.Default.Download,
            contentDescription = "Download attachment",
            modifier = Modifier
                .size(20.dp)
                .clickable { onDownloadClick() },
            tint = MaterialTheme.appColors.primary
        )
    }
}

/**
 * File icon with colored background
 */
@Composable
private fun AttachmentFileIcon(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .background(
                color = MaterialTheme.appColors.primary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Description,
            contentDescription = "File icon",
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.appColors.primary
        )
    }
}

/**
 * Empty state when no attachments are available
 */
@Composable
private fun EmptyAttachmentsView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.appColors.textSecondary.copy(alpha = 0.5f)
            )
            Text(
                text = "No attachments available",
                style = MaterialTheme.appTypography.interMedium16px,
                color = MaterialTheme.appColors.textSecondary
            )
        }
    }
}

// Preview
@Preview(showBackground = true)
@Composable
private fun AttachmentRowViewPreview() {
    EcareProTheme {
        AttachmentRowView(
            attachment = ECAttachment(
                name = "Assignment_1.pdf",
                url = "https://example.com/file.pdf"
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DownloadFilesViewPreview() {
    EcareProTheme {
        DownloadFilesView(
            attachments = listOf(
                ECAttachment(
                    name = "Assignment_1.pdf",
                    url = "https://example.com/file1.pdf"
                ),
                ECAttachment(
                    name = "Syllabus_2024.pdf",
                    url = "https://example.com/file2.pdf"
                ),
                ECAttachment(
                    name = "Notes_Chapter_5_Very_Long_Filename_That_Should_Be_Truncated.pdf",
                    url = "https://example.com/file3.pdf"
                ),
                ECAttachment(
                    name = "Reference_Material.pdf",
                    url = "https://example.com/file4.pdf"
                )
            ),
            onBackPressed = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyAttachmentsViewPreview() {
    EcareProTheme {
        DownloadFilesView(
            attachments = emptyList(),
            onBackPressed = {}
        )
    }
}

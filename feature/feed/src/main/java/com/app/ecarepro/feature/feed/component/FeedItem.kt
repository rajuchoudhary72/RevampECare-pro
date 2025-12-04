package com.app.ecarepro.feature.feed.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.app.ecarepro.core.domain.model.Attachment
import com.app.ecarepro.core.domain.model.FeedType
import com.app.ecarepro.core.domain.model.FeedUpdate
import com.app.ecarepro.core.domain.model.FileType
import com.app.ecarepro.core.domain.model.GalleryUpdate
import com.app.ecarepro.core.domain.model.toAttachments
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors

@Composable
fun FeedItem(
    feedUpdate: FeedUpdate,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.appColors.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with module type badge and date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FeedTypeBadge(feedType = feedUpdate.feedType)

                Text(
                    text = feedUpdate.updatedOn,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.appColors.textSecondary,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Caption/Title
            Text(
                text = feedUpdate.caption,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.appColors.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Message detail if available
            feedUpdate.msgDTL?.let { msgDetail ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = msgDetail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.appColors.textSecondary,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Attachments section - merge galleryUpdate with regular attachments
            val allAttachments = buildList {
                addAll(feedUpdate.attachments)
                feedUpdate.galleryUpdate?.let { galleryUpdate ->
                    addAll(galleryUpdate.toAttachments())
                }
            }

            if (allAttachments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                val imageAttachments = allAttachments.filter { it.fileType == FileType.IMAGE }
                val documentAttachments = allAttachments.filter { it.fileType != FileType.IMAGE }

                // Display images
                if (imageAttachments.isNotEmpty()) {
                    ImageGallery(images = imageAttachments)

                    if (documentAttachments.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                // Display document attachments
                if (documentAttachments.isNotEmpty()) {
                    DocumentAttachments(documents = documentAttachments)
                }
            }
        }
    }
}

@Composable
private fun FeedTypeBadge(
    feedType: FeedType,
    modifier: Modifier = Modifier
) {
    val backgroundColor = try {
        Color(feedType.colorHex.toColorInt())
    } catch (e: Exception) {
        MaterialTheme.appColors.primary
    }

    Box(
        modifier = modifier
            .background(
                color = backgroundColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = feedType.displayName,
            style = MaterialTheme.typography.labelMedium,
            color = backgroundColor,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun ImageGallery(
    images: List<Attachment>,
    modifier: Modifier = Modifier
) {
    when {
        images.size == 1 -> {
            // Single image - display full width
            EcareProAsyncImage(
                imageUrl = images[0].fileUrl,
                contentDescription = "Feed image",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
        else -> {
            // Multiple images - scrollable horizontal list
            LazyRow(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(images) { image ->
                    EcareProAsyncImage(
                        imageUrl = image.fileUrl,
                        contentDescription = "Feed image",
                        modifier = Modifier
                            .width(200.dp)
                            .aspectRatio(4f / 3f)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@Composable
private fun DocumentAttachments(
    documents: List<Attachment>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        documents.forEach { document ->
            DocumentItem(
                attachment = document
            )
        }
    }
}

@Composable
private fun DocumentItem(
    attachment: Attachment,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.appColors.background,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = attachment.fileType.getIcon(),
            contentDescription = null,
            tint = attachment.fileType.getColor(),
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = attachment.fileName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.appColors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = attachment.fileType.name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.appColors.textSecondary,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun FileType.getIcon(): ImageVector {
    return when (this) {
        FileType.IMAGE -> Icons.Default.Image
        FileType.PDF -> Icons.Default.PictureAsPdf
        FileType.DOC, FileType.DOCX -> Icons.Default.Description
        FileType.XLS, FileType.XLSX -> Icons.Default.Description
        FileType.PPT, FileType.PPTX -> Icons.Default.Description
        FileType.TXT -> Icons.Default.Description
        FileType.UNKNOWN -> Icons.Default.AttachFile
    }
}

@Composable
private fun FileType.getColor(): Color {
    return when (this) {
        FileType.PDF -> Color(0xFFD32F2F) // Red
        FileType.DOC, FileType.DOCX -> Color(0xFF1976D2) // Blue
        FileType.XLS, FileType.XLSX -> Color(0xFF388E3C) // Green
        FileType.PPT, FileType.PPTX -> Color(0xFFD84315) // Orange
        else -> MaterialTheme.appColors.textSecondary
    }
}

// ============================================
// Preview Section
// ============================================

@Preview(showBackground = true, name = "FeedItem - No Attachments")
@Composable
private fun FeedItemNoAttachmentsPreview() {
    EcareProTheme {
        FeedItem(
            feedUpdate = FeedUpdate(
                menuID = 7,
                chMenuID = 10,
                sbChMenuID = 0,
                module = "Circular",
                id = "circular_1",
                caption = "Holiday Homework Reminder",
                hasAttachment = false,
                updatedOn = "06 Nov",
                msgDTL = "This is a reminder to complete your holiday homework before the school reopens.",
                galleryUpdate = null,
                webLink = "/Portal/Circular?ID=def456",
                feedType = FeedType.CIRCULAR,
                attachments = emptyList()
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "FeedItem - Single Image")
@Composable
private fun FeedItemSingleImagePreview() {
    EcareProTheme {
        FeedItem(
            feedUpdate = FeedUpdate(
                menuID = 7,
                chMenuID = 10,
                sbChMenuID = 0,
                module = "Circular",
                id = "circular_2",
                caption = "Annual Sports Day Announcement",
                hasAttachment = true,
                updatedOn = "07 Nov",
                msgDTL = "We are excited to announce the Annual Sports Day. All students are requested to participate.",
                galleryUpdate = null,
                webLink = "/Portal/Circular?ID=abc123",
                feedType = FeedType.CIRCULAR,
                attachments = listOf(
                    Attachment(
                        fileName = "sports_day.jpg",
                        fileUrl = "https://picsum.photos/800/600",
                        fileType = FileType.IMAGE
                    )
                )
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "FeedItem - Multiple Images (Regular)")
@Composable
private fun FeedItemMultipleImagesPreview() {
    EcareProTheme {
        FeedItem(
            feedUpdate = FeedUpdate(
                menuID = 34,
                chMenuID = 48,
                sbChMenuID = 0,
                module = "Photo",
                id = "photo_1",
                caption = "Science Exhibition 2024",
                hasAttachment = false,
                updatedOn = "13 Oct",
                msgDTL = "Highlights from our annual science exhibition showcasing amazing projects by students.",
                galleryUpdate = null,
                webLink = "/Portal/PhotoAlbums",
                feedType = FeedType.PHOTO,
                attachments = listOf(
                    Attachment(
                        fileName = "photo1.jpg",
                        fileUrl = "https://picsum.photos/800/500",
                        fileType = FileType.IMAGE
                    ),
                    Attachment(
                        fileName = "photo2.jpg",
                        fileUrl = "https://picsum.photos/800/501",
                        fileType = FileType.IMAGE
                    ),
                    Attachment(
                        fileName = "photo3.jpg",
                        fileUrl = "https://picsum.photos/800/502",
                        fileType = FileType.IMAGE
                    ),
                    Attachment(
                        fileName = "photo4.jpg",
                        fileUrl = "https://picsum.photos/800/503",
                        fileType = FileType.IMAGE
                    )
                )
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "FeedItem - Gallery Update (API Format)")
@Composable
private fun FeedItemGalleryUpdatePreview() {
    EcareProTheme {
        FeedItem(
            feedUpdate = FeedUpdate(
                menuID = 34,
                chMenuID = 48,
                sbChMenuID = 0,
                module = "Photo",
                id = "gzklUmg3mma+0XPviSQW0w==",
                caption = "Yoga Days",
                hasAttachment = false,
                updatedOn = "19 Nov",
                msgDTL = null,
                galleryUpdate = GalleryUpdate(
                    sMdlID = 1,
                    subModule = null,
                    total = 3,
                    fileURL = "https://picsum.photos/800/",
                    fileNames = listOf("500", "501", "502")
                ),
                webLink = "/Portal/PhotoAlbums",
                feedType = FeedType.PHOTO,
                attachments = emptyList()
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "FeedItem - Document Attachments")
@Composable
private fun FeedItemDocumentAttachmentsPreview() {
    EcareProTheme {
        FeedItem(
            feedUpdate = FeedUpdate(
                menuID = 7,
                chMenuID = 11,
                sbChMenuID = 0,
                module = "Notice",
                id = "notice_1",
                caption = "Parent-Teacher Meeting Schedule",
                hasAttachment = true,
                updatedOn = "16 Oct",
                msgDTL = "Please find attached the schedule for upcoming parent-teacher meetings.",
                galleryUpdate = null,
                webLink = "/Portal/Notice?ID=xyz789",
                feedType = FeedType.NOTICE,
                attachments = listOf(
                    Attachment(
                        fileName = "PTM_Schedule.pdf",
                        fileUrl = "https://example.com/ptm.pdf",
                        fileType = FileType.PDF
                    ),
                    Attachment(
                        fileName = "Guidelines.docx",
                        fileUrl = "https://example.com/guidelines.docx",
                        fileType = FileType.DOCX
                    ),
                    Attachment(
                        fileName = "Timetable.xlsx",
                        fileUrl = "https://example.com/timetable.xlsx",
                        fileType = FileType.XLSX
                    )
                )
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "FeedItem - Mixed Attachments")
@Composable
private fun FeedItemMixedAttachmentsPreview() {
    EcareProTheme {
        FeedItem(
            feedUpdate = FeedUpdate(
                menuID = 7,
                chMenuID = 10,
                sbChMenuID = 0,
                module = "Circular",
                id = "circular_3",
                caption = "Staff Meeting on Upcoming Exams Schedule",
                hasAttachment = true,
                updatedOn = "12 Nov",
                msgDTL = "Important meeting regarding the upcoming examination schedule. Please review all attached materials.",
                galleryUpdate = null,
                webLink = "/Portal/Circular?ID=ib9WicQcCDNjho6kNt6LdA==",
                feedType = FeedType.CIRCULAR,
                attachments = listOf(
                    Attachment(
                        fileName = "meeting_venue.jpg",
                        fileUrl = "https://picsum.photos/800/600",
                        fileType = FileType.IMAGE
                    ),
                    Attachment(
                        fileName = "agenda.jpg",
                        fileUrl = "https://picsum.photos/800/601",
                        fileType = FileType.IMAGE
                    ),
                    Attachment(
                        fileName = "Meeting_Agenda.pdf",
                        fileUrl = "https://example.com/agenda.pdf",
                        fileType = FileType.PDF
                    ),
                    Attachment(
                        fileName = "Schedule.xlsx",
                        fileUrl = "https://example.com/schedule.xlsx",
                        fileType = FileType.XLSX
                    )
                )
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "FeedItem - Notice with Long Text")
@Composable
private fun FeedItemLongTextPreview() {
    EcareProTheme {
        FeedItem(
            feedUpdate = FeedUpdate(
                menuID = 7,
                chMenuID = 11,
                sbChMenuID = 0,
                module = "Notice",
                id = "notice_2",
                caption = "Upcoming Examination Schedule and Preparation Guidelines for All Students",
                hasAttachment = false,
                updatedOn = "12 Nov",
                msgDTL = "This is a comprehensive notice regarding the upcoming examination schedule. All students are requested to prepare accordingly and follow the guidelines mentioned in the circular. Please contact the administration for any clarifications.",
                galleryUpdate = null,
                webLink = "/Portal/Notice?ID=zo5uj7DiY0sOEjA1djdU+w==",
                feedType = FeedType.NOTICE,
                attachments = emptyList()
            ),
            onClick = {}
        )
    }
}

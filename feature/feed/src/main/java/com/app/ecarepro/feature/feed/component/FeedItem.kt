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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
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
    onPinClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val accentColor = try {
        Color(feedUpdate.accentColor.toColorInt())
    } catch (e: Exception) {
        Color(0xFF4CAF50)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.appColors.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawRect(
                        color = accentColor,
                        topLeft = androidx.compose.ui.geometry.Offset(size.width - 4.dp.toPx(), 0f),
                        size = Size(4.dp.toPx(), size.height),
                    )
                }
                .padding(start = 12.dp, top = 12.dp, end = 16.dp, bottom = 12.dp),
        ) {
                // Header: avatar + name/date + pin icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                ) {
                    FeedAvatar(
                        name = feedUpdate.userName,
                        imageUrl = feedUpdate.userImageUrl,
                        accentColor = feedUpdate.accentColor,
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = feedUpdate.userName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.appColors.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = feedUpdate.updatedOn,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.appColors.textSecondary,
                            fontSize = 12.sp,
                        )
                    }

                    IconButton(
                        onClick = onPinClick,
                        modifier = Modifier.size(32.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PushPin,
                            contentDescription = "Pin",
                            tint = MaterialTheme.appColors.textSecondary,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Caption / title
                Text(
                    text = feedUpdate.caption,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.appColors.textPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                // Description if available
                feedUpdate.msgDTL?.let { msg ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = msg,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.appColors.textSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                // Attachments
                val allAttachments = buildList {
                    addAll(feedUpdate.attachments)
                    feedUpdate.galleryUpdate?.let { addAll(it.toAttachments()) }
                }

                if (allAttachments.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    val images = allAttachments.filter { it.fileType == FileType.IMAGE }
                    val docs = allAttachments.filter { it.fileType != FileType.IMAGE }

                    if (images.isNotEmpty()) {
                        ImageGallery(images = images)
                        if (docs.isNotEmpty()) Spacer(modifier = Modifier.height(8.dp))
                    }
                    if (docs.isNotEmpty()) {
                        DocumentAttachments(documents = docs)
                    }
                }
        }
    }
}

@Composable
private fun FeedAvatar(
    name: String,
    imageUrl: String?,
    accentColor: String,
    modifier: Modifier = Modifier,
) {
    val color = try {
        Color(accentColor.toColorInt())
    } catch (e: Exception) {
        Color(0xFF4CAF50)
    }

    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center,
    ) {
        if (imageUrl != null) {
            EcareProAsyncImage(
                imageUrl = imageUrl,
                contentDescription = name,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
        } else {
            Text(
                text = name.firstOrNull()?.uppercaseChar()?.toString() ?: "S",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color,
            )
        }
    }
}

@Composable
private fun ImageGallery(
    images: List<Attachment>,
    modifier: Modifier = Modifier,
) {
    when {
        images.size == 1 -> {
            EcareProAsyncImage(
                imageUrl = images[0].fileUrl,
                contentDescription = "Feed image",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
            )
        }
        else -> {
            LazyRow(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(images) { image ->
                    EcareProAsyncImage(
                        imageUrl = image.fileUrl,
                        contentDescription = "Feed image",
                        modifier = Modifier
                            .width(200.dp)
                            .aspectRatio(4f / 3f)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
        }
    }
}

@Composable
private fun DocumentAttachments(
    documents: List<Attachment>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        documents.forEach { document ->
            DocumentItem(attachment = document)
        }
    }
}

@Composable
private fun DocumentItem(
    attachment: Attachment,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.appColors.background,
                shape = RoundedCornerShape(8.dp),
            )
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = attachment.fileType.getIcon(),
            contentDescription = null,
            tint = attachment.fileType.getColor(),
            modifier = Modifier.size(28.dp),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = attachment.fileName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.appColors.textPrimary,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = attachment.fileType.name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.appColors.textSecondary,
                fontSize = 11.sp,
            )
        }
    }
}

private fun FileType.getIcon(): ImageVector = when (this) {
    FileType.IMAGE -> Icons.Default.Image
    FileType.PDF -> Icons.Default.PictureAsPdf
    FileType.DOC, FileType.DOCX -> Icons.Default.Description
    FileType.XLS, FileType.XLSX -> Icons.Default.Description
    FileType.PPT, FileType.PPTX -> Icons.Default.Description
    FileType.TXT -> Icons.Default.Description
    FileType.UNKNOWN -> Icons.Default.AttachFile
}

private fun FileType.getColor(): Color = when (this) {
    FileType.PDF -> Color(0xFFD32F2F)
    FileType.DOC, FileType.DOCX -> Color(0xFF1976D2)
    FileType.XLS, FileType.XLSX -> Color(0xFF388E3C)
    FileType.PPT, FileType.PPTX -> Color(0xFFD84315)
    else -> Color(0xFF9E9E9E)
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
                menuID = 55,
                module = "Circular",
                id = "circular_1",
                caption = "Holiday Homework Reminder",
                hasAttachment = false,
                updatedOn = "06 Nov",
                msgDTL = "This is a reminder to complete your holiday homework before the school reopens.",
                galleryUpdate = null,
                webLink = "/Portal/Circular?ID=def456",
                feedType = FeedType.CIRCULAR,
                attachments = emptyList(),
                userName = "School",
                accentColor = "#4CAF50",
            ),
            onClick = {},
        )
    }
}

@Preview(showBackground = true, name = "FeedItem - Notice with Attachment")
@Composable
private fun FeedItemNoticePreview() {
    EcareProTheme {
        FeedItem(
            feedUpdate = FeedUpdate(
                menuID = 52,
                module = "Notice",
                id = "notice_1",
                caption = "Parent-Teacher Meeting Schedule",
                hasAttachment = true,
                updatedOn = "10 Mar",
                msgDTL = "Please find the attached schedule for upcoming parent-teacher meetings.",
                galleryUpdate = null,
                webLink = "/Portal/Notice?ID=xyz789",
                feedType = FeedType.SCHOOL_NOTICE,
                attachments = listOf(
                    Attachment(
                        fileName = "PTM_Schedule.pdf",
                        fileUrl = "https://example.com/ptm.pdf",
                        fileType = FileType.PDF,
                    )
                ),
                userName = "School",
                accentColor = "#FFC107",
            ),
            onClick = {},
        )
    }
}

@Preview(showBackground = true, name = "FeedItem - Gallery")
@Composable
private fun FeedItemGalleryPreview() {
    EcareProTheme {
        FeedItem(
            feedUpdate = FeedUpdate(
                menuID = 71,
                module = "Photo",
                id = "photo_1",
                caption = "Yoga Day Celebration",
                hasAttachment = false,
                updatedOn = "19 Nov",
                msgDTL = null,
                galleryUpdate = GalleryUpdate(
                    sMdlID = 1,
                    subModule = null,
                    total = 3,
                    fileURL = "https://picsum.photos/800/",
                    fileNames = listOf("500", "501", "502"),
                ),
                webLink = "/Portal/PhotoAlbums",
                feedType = FeedType.PHOTO,
                attachments = emptyList(),
                userName = "Gallery",
                accentColor = "#00BCD4",
            ),
            onClick = {},
        )
    }
}

package com.app.ecarepro.designsystem.core.component

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.core.designsystem.R
import com.app.ecarepro.core.domain.model.DocType
import com.app.ecarepro.designsystem.core.modifier.drawBehindBorder
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import java.io.File


@Composable
fun EcareProFileAttachment(
    selectedFile: List<SelectedFileDetails>,
    onClickPickFile: () -> Unit,
    onClickPickMoreFile: () -> Unit = {},
    onClickDeleteFile: (SelectedFileDetails) -> Unit,
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Add file",
                style = MaterialTheme.appTypography.interRegular12px,
                color = MaterialTheme.appColors.textSecondary
            )

            if (selectedFile.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onClickPickFile() }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.icon_paperclip), // Replace with your attachment icon
                        contentDescription = null,
                        tint = MaterialTheme.appColors.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Add more files",
                        style = MaterialTheme.appTypography.interRegular14px,
                        color = MaterialTheme.appColors.primary,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
        }

        if (selectedFile.isEmpty()) {
            AttachmentSelectBox(onClickPickFile)
        } else {
            Attachments(selectedFile, onClickDeleteFile)
        }

    }

}

@Composable
private fun Attachments(
    selectedFile: List<SelectedFileDetails>,
    onClickDeleteFile: (SelectedFileDetails) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Chunk the list into groups of 2 for the grid effect
            selectedFile.chunked(2).forEach { rowFiles ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowFiles.forEach { file ->
                        Box(modifier = Modifier.weight(1f)) {
                            FileItem(fileDetails = file, onDelete = onClickDeleteFile)
                        }
                    }
                    // If row has only 1 item, add an empty spacer to keep the grid alignment
                    if (rowFiles.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

    }
}

@Composable
private fun AttachmentSelectBox(onClickPickFile: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(MaterialTheme.appColors.background, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClickPickFile() }
            .drawBehindBorder(
                strokeWidth = 1.dp,
                color = MaterialTheme.appColors.border,
                cornerRadius = 8.dp,
                dashLength = 8.dp,
                gapLength = 8.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            EcareProLottieAnimation(
                modifier = Modifier
                    .height(49.dp)
                    .width(56.dp),
                lottieRawId = R.raw.upload_to_cloud
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.core_designsystem_click_here_to_add_file),
                style = MaterialTheme.appTypography.nunitoBold12px.copy(fontSize = 16.sp),
                color = MaterialTheme.appColors.textSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.core_designsystem_max_10_mb_files_are_allowed),
                style = MaterialTheme.appTypography.nunitoMedium12px,
                color = MaterialTheme.appColors.textSecondary
            )
        }
    }
}

@Composable
private fun FileItem(
    fileDetails: SelectedFileDetails,
    onDelete: (SelectedFileDetails) -> Unit,
) {
    val docType = DocType.fromFile(fileDetails.file)
    val isImage = fileDetails.type == SelectedFileType.IMAGE || docType?.isImage() == true

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (isImage) MaterialTheme.appColors.surface else getDocBackgroundColor(docType)
                )
        ) {
            if (isImage) {
                EcareProAsyncImage(
                    imageUrl = fileDetails.file.path,
                    contentDescription = fileDetails.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Document Icon Center
                Icon(
                    painter = painterResource(getDocIconRes(docType)),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(68.dp)
                )
            }

            // Delete Button (Top Right)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.appColors.textPrimary.copy(alpha = 0.8f)) // Semi-transparent black
                    .clickable { onDelete(fileDetails) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove",
                    tint = White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Filename
        Text(
            text = fileDetails.name,
            style = MaterialTheme.appTypography.interRegular12px,
            color = MaterialTheme.appColors.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun getDocBackgroundColor(docType: DocType?): Color {
    // Colors based on your design image roughly
    return when (docType) {
        DocType.DOC -> Color(0xFFE3F2FD) // Light Blue
        DocType.PDF -> Color(0xFFFFEBEE) // Light Red
        DocType.SPREADSHEET -> Color(0xFFE8F5E9) // Light Green
        else -> Color(0xFFF5F5F5) // Gray
    }
}

@Composable
private fun getDocIconRes(docType: DocType?): Int {
    // You need to replace these with your actual drawable resources
    return when (docType) {
        DocType.DOC -> R.drawable.icon_doc // Replace with your doc icon
        DocType.PDF -> R.drawable.icon_pdf // Replace with your pdf icon
        DocType.SPREADSHEET -> R.drawable.icon_xls // Replace with your xls icon
        else -> R.drawable.icon_pdf // Fallback
    }
}

@Preview(showBackground = true)
@Composable
private fun EcareProFileAttachmentPreview() {
    EcareProTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Scenario 1: Default / Empty State
            Text("State: Empty", style = MaterialTheme.typography.labelLarge)
            EcareProFileAttachment(
                onClickPickFile = {},
                selectedFile = emptyList(),
                onClickDeleteFile = {}
            )

            // Scenario 2: Image Selected
            Text("State: Image Selected", style = MaterialTheme.typography.labelLarge)
            EcareProFileAttachment(
                onClickPickFile = {},
                selectedFile = listOf(
                    SelectedFileDetails(
                        uri = Uri.parse("https://dummyimage.com/600x400/000/fff.png"),
                        file = File("https://dummyimage.com/600x400/000/ffffff.png"),
                        name = "image.jpg",
                        size = 123456,
                        formattedSize = "123.45 MB",
                        mimeType = "image/jpeg",
                        type = SelectedFileType.IMAGE
                    ),
                    SelectedFileDetails(
                        uri = Uri.parse("https://dummyimage.com/600x400/000/fff.pdf"),
                        file = File("https://dummyimage.com/600x400/000/fff.pdf"),
                        name = "image.jpg",
                        size = 123456,
                        formattedSize = "123.45 MB",
                        mimeType = "image/jpeg",
                        type = SelectedFileType.DOCUMENT
                    ),
                    SelectedFileDetails(
                        uri = Uri.parse("https://dummyimage.com/600x400/000/fff.doc"),
                        file = File("https://dummyimage.com/600x400/000/fff.doc"),
                        name = "image.jpg",
                        size = 123456,
                        formattedSize = "123.45 MB",
                        mimeType = "image/jpeg",
                        type = SelectedFileType.DOCUMENT
                    ),
                    SelectedFileDetails(
                        uri = Uri.parse("https://dummyimage.com/600x400/000/fff.xls"),
                        file = File("https://dummyimage.com/600x400/000/fff.xls"),
                        name = "image.jpg",
                        size = 123456,
                        formattedSize = "123.45 MB",
                        mimeType = "image/jpeg",
                        type = SelectedFileType.DOCUMENT
                    ),
                ),
                onClickDeleteFile = {}
            )

        }
    }
}

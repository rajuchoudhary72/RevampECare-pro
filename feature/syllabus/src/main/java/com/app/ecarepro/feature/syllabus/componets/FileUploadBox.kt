package com.app.ecarepro.feature.syllabus.componets

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.component.EcareProLottieAnimation
import com.app.ecarepro.designsystem.core.component.SelectedFileDetails
import com.app.ecarepro.designsystem.core.component.SelectedFileType
import com.app.ecarepro.designsystem.core.modifier.drawBehindBorder
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.syllabus.R

@Composable
fun FileUploadBox(
    selectedFile: SelectedFileDetails?,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(MaterialTheme.appColors.background, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .drawBehindBorder(
                strokeWidth = 1.dp,
                color = MaterialTheme.appColors.border,
                cornerRadius = 8.dp,
                dashLength = 8.dp,
                gapLength = 8.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        if (selectedFile != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                if (selectedFile.type == SelectedFileType.DOCUMENT) {
                    Image(
                        modifier = Modifier.size(60.dp),
                        painter = painterResource(com.app.ecarepro.core.designsystem.R.drawable.icon_document),
                        contentScale = ContentScale.Fit,
                        contentDescription = null
                    )
                } else {
                    EcareProAsyncImage(
                        modifier = Modifier.size(60.dp),
                        imageUrl = selectedFile.file.toString(),
                        placeholder = painterResource(com.app.ecarepro.core.designsystem.R.drawable.icon_gallery),
                        error = painterResource(com.app.ecarepro.core.designsystem.R.drawable.icon_gallery),
                        contentScale = ContentScale.Fit
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = selectedFile.name,
                    style = MaterialTheme.appTypography.interRegular14px
                )
            }

        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                EcareProLottieAnimation(
                    modifier = Modifier
                        .height(49.dp)
                        .width(56.dp),
                    lottieRawId = R.raw.upload_to_cloud
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.feature_syllabus_click_here_to_add_file),
                    style = MaterialTheme.appTypography.nunitoBold12px.copy(fontSize = 16.sp),
                    color = MaterialTheme.appColors.textSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.feature_syllabus_max_10_mb_files_are_allowed),
                    style = MaterialTheme.appTypography.nunitoMedium12px,
                    color = MaterialTheme.appColors.textSecondary
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun FileUploadBoxPreview() {
    EcareProTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Scenario 1: Default / Empty State
            Text("State: Empty", style = MaterialTheme.typography.labelLarge)
            FileUploadBox(
                onClick = {},
                selectedFile = null,
            )

            // Scenario 2: Image Selected
            Text("State: Image Selected", style = MaterialTheme.typography.labelLarge)
            FileUploadBox(
                onClick = {},
                selectedFile = SelectedFileDetails(
                    uri = Uri.parse("https://dummyimage.com/600x400/000/fff"),
                    file = java.io.File("https://dummyimage.com/600x400/000/fff"),
                    name = "image.jpg",
                    size = 123456,
                    formattedSize = "123.45 MB",
                    mimeType = "image/jpeg",
                    type = SelectedFileType.IMAGE
                )
            )

            // Scenario 3: Document Selected
            Text("State: Document Selected", style = MaterialTheme.typography.labelLarge)
            FileUploadBox(
                onClick = {},
                selectedFile = SelectedFileDetails(
                    uri = Uri.parse("https://dummyimage.com/600x400/000/fff"),
                    file = java.io.File("https://dummyimage.com/600x400/000/fff"),
                    name = "image.doc",
                    size = 123456,
                    formattedSize = "123.45 MB",
                    mimeType = "image/jpeg",
                    type = SelectedFileType.DOCUMENT
                )
            )
        }
    }
}

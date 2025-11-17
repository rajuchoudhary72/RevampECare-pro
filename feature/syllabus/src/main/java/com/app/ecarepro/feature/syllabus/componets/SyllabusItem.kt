package com.app.ecarepro.feature.syllabus.componets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.designsystem.core.component.TextButton
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.syllabus.R
import com.app.ecarepro.feature.syllabus.Syllabus

@Composable
fun SyllabusItem(
    syllabus: Syllabus,
    onViewClick: () -> Unit,
    onMenuClick: () -> Unit,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = syllabus.title,
                    style = MaterialTheme.appTypography.interSemiBold14px,

                    )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = syllabus.className,
                        style = MaterialTheme.appTypography.interMedium16px.copy(fontSize = 12.sp),
                        color = MaterialTheme.appColors.primary
                    )

                    Indicator()
                    Text(
                        text = syllabus.subject,
                        style = MaterialTheme.appTypography.interRegular12px,
                        color = MaterialTheme.appColors.textPrimary
                    )
                    Indicator()
                    Text(
                        text = syllabus.date,
                        style = MaterialTheme.appTypography.interRegular12px,
                        color = MaterialTheme.appColors.textSecondary
                    )

                }

            }

            IconButton(onClick = onMenuClick) {
                Icon(
                    painterResource(R.drawable.ic_hori_menu),
                    contentDescription = "More options"
                )
            }
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                modifier = Modifier
                    .height(24.dp)
                    .padding(horizontal = 2.dp),
                title = "View",
                onClick = onViewClick,
                leadingIcon = R.drawable.ic_eye,
                contentPadding = PaddingValues(),
                titleColor = MaterialTheme.appColors.textSecondary
            )

            VerticalDivider(
                modifier = Modifier
                    .height(16.dp)
                    .padding(horizontal = 20.dp),
            )

            TextButton(
                modifier = Modifier
                    .height(24.dp)
                    .padding(horizontal = 2.dp),
                title = "Download",
                onClick = onDownloadClick,
                leadingIcon = R.drawable.ic_download,
                titleColor = MaterialTheme.appColors.textSecondary,
                contentPadding = PaddingValues(),
            )

        }
    }
}

@Composable
private fun Indicator() {
    Spacer(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .size(4.dp)
            .background(
                MaterialTheme.appColors.border,
                CircleShape
            )
    )
}

@Preview(showBackground = true)
@Composable
private fun SyllabusItemPreview() {
    EcareProTheme {
        SyllabusItem(
            syllabus = Syllabus(1, "English syllabus", "9th class", "English", "08 Aug 2025"),
            onViewClick = {},
            onMenuClick = {},
            onDownloadClick = {}
        )
    }
}
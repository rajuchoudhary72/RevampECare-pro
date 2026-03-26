package com.app.ecarepro.feature.report.birthday_report.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.report.birthday_report.BirthdayCardPresentation

@Composable
fun BirthdayCard(
    presentation: BirthdayCardPresentation,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        EcareProAsyncImage(
            imageUrl = presentation.photoUrl ?: "",
            contentDescription = presentation.name,
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(8.dp)),
        )

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = presentation.name,
                style = MaterialTheme.appTypography.interSemiBold14px,
                color = MaterialTheme.appColors.textPrimary,
            )
            if (!presentation.phoneNo.isNullOrBlank()) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Phone no:",
                        style = MaterialTheme.appTypography.interRegular14px,
                        color = MaterialTheme.appColors.textSecondary,
                    )
                    Text(
                        text = presentation.phoneNo,
                        style = MaterialTheme.appTypography.interRegular14px,
                        color = MaterialTheme.appColors.textSecondary,
                    )
                }
            }
            if (presentation.subtitle.isNotEmpty()) {
                Text(
                    text = presentation.subtitle,
                    style = MaterialTheme.appTypography.interRegular14px,
                    color = MaterialTheme.appColors.textSecondary,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Birthday on:",
                    style = MaterialTheme.appTypography.interRegular14px,
                    color = MaterialTheme.appColors.textSecondary,
                )
                Text(
                    text = presentation.birthdayOn,
                    style = MaterialTheme.appTypography.interMedium14px,
                    color = MaterialTheme.appColors.primary,
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Birthday Card - Student with phone")
@Composable
private fun PreviewBirthdayCardStudent() {
    EcareProTheme {
        BirthdayCard(
            presentation = BirthdayCardPresentation(
                id = 1,
                name = "Aaditi Rawat, LKG A",
                phoneNo = "9487145788",
                subtitle = "",
                birthdayOn = "18th March",
                photoUrl = null,
            ),
        )
    }
}

@Preview(showBackground = true, name = "Birthday Card - Staff")
@Composable
private fun PreviewBirthdayCardStaff() {
    EcareProTheme {
        BirthdayCard(
            presentation = BirthdayCardPresentation(
                id = 2,
                name = "PIYUSH CHILWAL, TEACHER1",
                phoneNo = "9876543210",
                subtitle = "Father/Spouse: LAL SINGH CHILWAL",
                birthdayOn = "1 Mar",
                photoUrl = null,
            ),
        )
    }
}

@Preview(showBackground = true, name = "Birthday Card - Parent")
@Composable
private fun PreviewBirthdayCardParent() {
    EcareProTheme {
        BirthdayCard(
            presentation = BirthdayCardPresentation(
                id = 3,
                name = "Sunita Devi",
                phoneNo = null,
                subtitle = "F/O: Rajesh Kumar, 10th-Section-A",
                birthdayOn = "15 Mar",
                photoUrl = null,
            ),
        )
    }
}

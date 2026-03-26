package com.app.ecarepro.feature.report.report.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
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
import com.app.ecarepro.core.domain.model.smsreport.SMSReportItem
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

@Composable
fun SMSReportCard(
    item: SMSReportItem,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            // Left column: photo + status badge
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                EcareProAsyncImage(
                    imageUrl = item.receiverPhotoUrl ?: "",
                    contentDescription = item.receiverName,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(8.dp)),
                )
                Spacer(modifier = Modifier.height(6.dp))
                val statusColor = if (item.isSent) Color(0xFF4CAF50) else Color(0xFFF44336)
                // New Box container for light background
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(statusColor.copy(alpha = 0.12f)) // Light background
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = item.status,
                        style = MaterialTheme.appTypography.interMedium12px,
                        color = statusColor,
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Right column: all text content
            Column(modifier = Modifier.weight(1f)) {
                // Name + designation
                Row {
                    Text(
                        text = item.receiverName,
                        style = MaterialTheme.appTypography.interSemiBold14px,
                        color = MaterialTheme.appColors.textPrimary,
                    )
                    if (item.receiverDesignation.isNotBlank()) {
                        Text(
                            text = ", ${item.receiverDesignation}",
                            style = MaterialTheme.appTypography.interRegular14px,
                            color = MaterialTheme.appColors.textSecondary,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Message text
                Text(
                    text = item.messageText,
                    style = MaterialTheme.appTypography.interRegular14px,
                    color = MaterialTheme.appColors.textSecondary,
                )

                Spacer(modifier = Modifier.height(6.dp))

                // SMS type
                Row {
                    Text(
                        text = "Type: ",
                        style = MaterialTheme.appTypography.interSemiBold14px,
                        color = MaterialTheme.appColors.textPrimary,
                    )
                    Text(
                        text = item.smsTypeName,
                        style = MaterialTheme.appTypography.interRegular14px,
                        color = MaterialTheme.appColors.textPrimary,
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Sender row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Sender photo
                    if (item.senderPhotoUrl != null) {
                        EcareProAsyncImage(
                            imageUrl = item.senderPhotoUrl,
                            contentDescription = item.senderName,
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape),
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0E0E0)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.appColors.textSecondary,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                    // Sent by
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Sent by:",
                            style = MaterialTheme.appTypography.interRegular12px,
                            color = MaterialTheme.appColors.textSecondary,
                        )
                        Text(
                            text = item.senderName,
                            style = MaterialTheme.appTypography.interMedium14px,
                            color = MaterialTheme.appColors.textPrimary,
                        )
                    }

                    // Sent on
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Sent on:",
                            style = MaterialTheme.appTypography.interRegular12px,
                            color = MaterialTheme.appColors.textSecondary,
                        )
                        Text(
                            text = item.sentOnDate,
                            style = MaterialTheme.appTypography.interMedium14px,
                            color = MaterialTheme.appColors.textPrimary,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "SMS Report Card - Sent")
@Composable
private fun PreviewSMSReportCardSent() {
    EcareProTheme {
        SMSReportCard(
            item = SMSReportItem(
                receiverName = "Mrs. Aastha Saini",
                receiverDesignation = "Officer",
                receiverPhotoUrl = null,
                messageText = "Dear staff, Your login details for e-care pro is as follows:\nSchool code: DEMOIN",
                smsTypeName = "Credentials SMS",
                status = "Sent",
                isSent = true,
                senderName = "e-care",
                senderPhotoUrl = null,
                sentOnDate = "12 March 2026",
            ),
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, name = "SMS Report Card - Failed")
@Composable
private fun PreviewSMSReportCardFailed() {
    EcareProTheme {
        SMSReportCard(
            item = SMSReportItem(
                receiverName = "Mr. Rahul Sharma",
                receiverDesignation = "Teacher",
                receiverPhotoUrl = null,
                messageText = "Your attendance has been marked absent today.",
                smsTypeName = "Absentee SMS",
                status = "Failed",
                isSent = false,
                senderName = "e-care",
                senderPhotoUrl = null,
                sentOnDate = "11 March 2026",
            ),
            modifier = Modifier.padding(16.dp),
        )
    }
}

package com.app.ecarepro.feature.leave.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.app.ecarepro.core.domain.model.LeaveApplication
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors

@Composable
fun LeaveCard(
    leave: LeaveApplication,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onApproveClick: (() -> Unit)? = null,
    onRejectClick: (() -> Unit)? = null,
    onForwardClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    showActionButtons: Boolean = false
) {
    Box (modifier = Modifier
        .fillMaxWidth()
        .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 0.dp)

    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // Header Section with Profile
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Profile Image
                AsyncImage(
                    model = leave.applicantPhoto.ifEmpty { "default_profile" },
                    contentDescription = "Profile photo",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Name and Details
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Name
                    Text(
                        text = leave.applicantName.ifEmpty { leave.studentName },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1A1A1A)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Applied Date
                    Text(
                        text = "Applied: ${leave.submittedOn}",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Leave Type
                    Text(
                        text = "Leave type: ${leave.leaveType}",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Date Range Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // From Date
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Blue dot
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFF4A90E2), shape = CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "From",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 12.sp,
                            color = Color(0xFF666666)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = leave.fromDate,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color(0xFF1A1A1A)
                    )
                }

                // Dashed Line and Duration
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    // Dashed line
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(1.dp)
                    ) {
                        drawLine(
                            color = Color(0xFFCCCCCC),
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f),
                            strokeWidth = 2f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Duration
                    Text(
                        text = "${leave.durationStr} days",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                }

                // To Date
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Green dot
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFF4CAF50), shape = CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "To",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 12.sp,
                            color = Color(0xFF666666)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = leave.tillDate,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color(0xFF1A1A1A)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Reason Section
            if (leave.reason.isNotEmpty()) {
                Text(
                    text = leave.reason,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    color = Color(0xFF333333),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Attachment Icon
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = "Attachment",
                        tint = Color(0xFF666666),
                        modifier = Modifier.size(20.dp)
                    )
                    if (leave.attachment.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "1",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 12.sp,
                            color = Color(0xFF666666)
                        )
                    }
                }

                if (showActionButtons) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Reject Button
                        ActionButton(
                            text = "Reject",
                            backgroundColor = Color(0xFFFFEBEE),
                            textColor = Color(0xFFE53935),
                            icon = Icons.Default.Close,
                            onClick = { onRejectClick?.invoke() }
                        )

                        // Approve Button
                        ActionButton(
                            text = "Approve",
                            backgroundColor = Color(0xFFE8F5E9),
                            textColor = Color(0xFF43A047),
                            icon = Icons.Default.Done,
                            onClick = { onApproveClick?.invoke() }
                        )

                        // Forward Button
                        ActionButton(
                            text = "Forward",
                            backgroundColor = Color(0xFFFFF3E0),
                            textColor = Color(0xFFFF9800),
                            icon = Icons.Default.Share,
                            onClick = { onForwardClick?.invoke() }
                        )


                    }
                } else {
                    // Status Badge for non-action view
                    StatusBadge(status = leave.status)
                }
            }

            // Rejection Reason (if rejected)
            if (leave.status.equals(
                    "Rejected",
                    ignoreCase = true
                ) && leave.rejectionReason.isNotEmpty()
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Rejection Reason: ${leave.rejectionReason}",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 12.sp,
                    color = Color(0xFFE53935),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                )
            }

            // Approved By Section (if approved)
            if (leave.status.equals(
                    "Approved",
                    ignoreCase = true
                ) && leave.teacherName.isNotEmpty()
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFF5F5F5),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = leave.photo.ifEmpty { "default_profile" },
                        contentDescription = "Approved by photo",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Approved by:",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = Color(0xFF666666)
                        )
                        Text(
                            text = leave.teacherName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = Color(0xFF1A1A1A)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider()
        }
    }
    }


@Composable
private fun ActionButton(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = textColor
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        ),
        modifier = Modifier.height(36.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun StatusBadge(status: String) {
    val (backgroundColor, textColor) = when (status.lowercase()) {
        "pending" -> Color(0xFFE3F2FD) to Color(0xFF1976D2)
        "approved" -> Color(0xFFE8F5E9) to Color(0xFF43A047)
        "rejected" -> Color(0xFFFFEBEE) to Color(0xFFE53935)
        "cancelled" -> Color(0xFFF5F5F5) to Color(0xFF757575)
        else -> Color(0xFFE3F2FD) to Color(0xFF1976D2)
    }

    Box(
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = status,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = textColor,
            fontSize = 11.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LeaveCardWithActionsPreview() {
    EcareProTheme {
        Column(modifier = Modifier.background(Color(0xFFF5F5F5))) {
            LeaveCard(
                leave = LeaveApplication(
                    lvID = 1,
                    fromDate = "18 Oct 2025",
                    tillDate = "22 Oct 2025",
                    duration = 4.0,
                    durationStr = "4",
                    reason = "I will be travelling with my family to attend a wedding ceremony out of town, and hence will not be able to attend school during these days.",
                    leaveType = "Casual leave",
                    leaveAbbr = "CL",
                    status = "Pending",
                    submittedOn = "08 Aug, 2:34 PM",
                    actionOn = "",
                    rejectionReason = "",
                    attachment = "attachment.pdf",
                    applicantName = "Aastha Saini",
                    applicantPhoto = "",
                    studentName = "",
                    studentPhoto = "",
                    studentClass = "",
                    designation = "Director",
                    teacherID = 0,
                    teacherName = "",
                    sid = 0,
                    photo = "",
                    halfdayDTL = null,
                    forwardedBy = 0,
                    forwardedByName = null,
                    cancelby = null,
                    cancelledOn = null,
                    showCancelButton = false,
                    attPer = ""
                ),
                onClick = {},
                onDeleteClick = {},
                onApproveClick = {},
                onRejectClick = {},
                onForwardClick = {},
                showActionButtons = true
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LeaveCardApprovedPreview() {
    EcareProTheme {
        Column(modifier = Modifier.background(Color(0xFFF5F5F5))) {
            LeaveCard(
                leave = LeaveApplication(
                    lvID = 1,
                    fromDate = "18 Oct 2025",
                    tillDate = "22 Oct 2025",
                    duration = 4.0,
                    durationStr = "4",
                    reason = "Family function to attend.",
                    leaveType = "Casual leave",
                    leaveAbbr = "CL",
                    status = "Approved",
                    submittedOn = "08 Aug, 2:34 PM",
                    actionOn = "09 Aug, 2024",
                    rejectionReason = "",
                    attachment = "",
                    applicantName = "Aastha Saini",
                    applicantPhoto = "",
                    studentName = "",
                    studentPhoto = "",
                    studentClass = "",
                    designation = "",
                    teacherID = 123,
                    teacherName = "Principal Name",
                    sid = 0,
                    photo = "",
                    halfdayDTL = null,
                    forwardedBy = 0,
                    forwardedByName = null,
                    cancelby = null,
                    cancelledOn = null,
                    showCancelButton = false,
                    attPer = ""
                ),
                onClick = {},
                onDeleteClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LeaveCardRejectedPreview() {
    EcareProTheme {
        Column(modifier = Modifier.background(Color(0xFFF5F5F5))) {
            LeaveCard(
                leave = LeaveApplication(
                    lvID = 1,
                    fromDate = "18 Oct 2025",
                    tillDate = "22 Oct 2025",
                    duration = 4.0,
                    durationStr = "4",
                    reason = "Personal work",
                    leaveType = "Casual leave",
                    leaveAbbr = "CL",
                    status = "Rejected",
                    submittedOn = "08 Aug, 2:34 PM",
                    actionOn = "",
                    rejectionReason = "Insufficient leave balance available",
                    attachment = "",
                    applicantName = "Aastha Saini",
                    applicantPhoto = "",
                    studentName = "",
                    studentPhoto = "",
                    studentClass = "",
                    designation = "",
                    teacherID = 0,
                    teacherName = "",
                    sid = 0,
                    photo = "",
                    halfdayDTL = null,
                    forwardedBy = 0,
                    forwardedByName = null,
                    cancelby = null,
                    cancelledOn = null,
                    showCancelButton = false,
                    attPer = ""
                ),
                onClick = {},
                onDeleteClick = {}
            )
        }
    }
}

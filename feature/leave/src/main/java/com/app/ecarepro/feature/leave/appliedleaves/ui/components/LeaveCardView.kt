package com.app.ecarepro.feature.leave.appliedleaves.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.leave.appliedleaves.data.AppliedLeavesScreenType
import com.app.ecarepro.feature.leave.appliedleaves.data.LeaveCardPresentation

@Composable
fun LeaveCardView(
    leave: LeaveCardPresentation,
    onSelect: (() -> Unit)? = null,
    onApprove: (() -> Unit)? = null,
    onReject: (() -> Unit)? = null,
    onForward: (() -> Unit)? = null,
    onViewAttachment: (() -> Unit)? = null,
    onMenuTap: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 16.dp)
    ) {
        // Header
        LeaveCardHeader(
            leave = leave,
            onSelect = onSelect,
            onMenuTap = onMenuTap
        )

        // Timeline
        LeaveTimeline(
            fromDate = leave.fromDate,
            toDate = leave.toDate,
            duration = leave.durationText,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        )

        // Reason
        Text(
            text = leave.reason,
            style = MaterialTheme.appTypography.interRegular12px,
            color = if (leave.reason == "No reason provided")
                MaterialTheme.appColors.textSecondary
            else MaterialTheme.appColors.textPrimary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        // Action Buttons or Status Badge
        if (leave.showActionButtons) {
            LeaveActionButtons(
                hasAttachment = leave.hasAttachment,
                attachmentCount = leave.attachmentCount,
                showForward = leave.showForwardButton,
                onApprove = onApprove,
                onReject = onReject,
                onForward = onForward,
                onViewAttachment = onViewAttachment
            )
        } else if (leave.showStatusBadge) {
            StatusBadge(
                text = leave.statusBadgeText,
                backgroundColor = leave.status.backgroundColor,
                textColor = leave.status.color
            )
        }
    }
}

@Composable
private fun LeaveCardHeader(
    leave: LeaveCardPresentation,
    onSelect: (() -> Unit)?,
    onMenuTap: (() -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Profile Photo
        AsyncImage(
            model = leave.photoURL,
            contentDescription = "Profile photo",
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.appColors.divider),
            contentScale = ContentScale.Crop
        )

        // Header Info
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row {
                Text(
                    text = leave.displayName,
                    style = MaterialTheme.appTypography.interSemiBold14px,
                    color = MaterialTheme.appColors.textPrimary
                )
                if (leave.displaySubtitle.isNotEmpty()) {
                    Text(
                        text = ", ${leave.displaySubtitle}",
                        style = MaterialTheme.appTypography.interSemiBold14px,
                        color = MaterialTheme.appColors.textPrimary
                    )
                }
            }

            Text(
                text = leave.appliedDate,
                style = MaterialTheme.appTypography.interRegular12px,
                color = MaterialTheme.appColors.textSecondary
            )

            if (leave.showAttendance && leave.attendanceText != null) {
                Text(
                    text = leave.attendanceText,
                    style = MaterialTheme.appTypography.interRegular12px,
                    color = MaterialTheme.appColors.textSecondary
                )
            }

            if (leave.showLeaveType && leave.leaveType != null) {
                Text(
                    text = "Leave type: ${leave.leaveType}",
                    style = MaterialTheme.appTypography.interRegular12px,
                    color = MaterialTheme.appColors.textSecondary
                )
            }

            if (leave.showApplicantName && leave.applicantName != null) {
                Text(
                    text = leave.applicantName,
                    style = MaterialTheme.appTypography.interRegular12px,
                    color = MaterialTheme.appColors.textSecondary
                )
            }
        }

        // Checkbox or Menu
        if (leave.screenType == AppliedLeavesScreenType.SELF_LEAVES) {
            if (onMenuTap != null) {
                IconButton(onClick = onMenuTap) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menu",
                        tint = MaterialTheme.appColors.textSecondary
                    )
                }
            }
        } else if (leave.showCheckbox && onSelect != null) {
            IconButton(onClick = onSelect) {
                Icon(
                    imageVector = if (leave.isSelected)
                        Icons.Default.CheckBox
                    else Icons.Default.CheckBoxOutlineBlank,
                    contentDescription = "Select",
                    tint = if (leave.isSelected)
                        MaterialTheme.appColors.primary
                    else MaterialTheme.appColors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun LeaveActionButtons(
    hasAttachment: Boolean,
    attachmentCount: Int,
    showForward: Boolean,
    onApprove: (() -> Unit)?,
    onReject: (() -> Unit)?,
    onForward: (() -> Unit)?,
    onViewAttachment: (() -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Attachment indicator
        if (hasAttachment && onViewAttachment != null) {
            Surface(
                color = MaterialTheme.appColors.textSecondary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(6.dp),
                onClick = onViewAttachment
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = "Attachment",
                        tint = MaterialTheme.appColors.textSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    if (attachmentCount > 0) {
                        Text(
                            text = "$attachmentCount",
                            style = MaterialTheme.appTypography.interRegular12px,
                            color = MaterialTheme.appColors.textSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Reject button
        if (onReject != null) {
            ActionButton(
                text = "Reject",
                icon = Icons.Default.Close,
                backgroundColor = Color(0xFFF44336).copy(alpha = 0.1f),
                contentColor = Color(0xFFF44336),
                onClick = onReject
            )
        }

        // Approve button
        if (onApprove != null) {
            ActionButton(
                text = "Approve",
                icon = Icons.Default.Check,
                backgroundColor = Color(0xFF4CAF50).copy(alpha = 0.1f),
                contentColor = Color(0xFF4CAF50),
                onClick = onApprove
            )
        }

        // Forward button (staff only)
        if (showForward && onForward != null) {
            ActionButton(
                text = "Forward",
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                backgroundColor = Color(0xFFFF9800).copy(alpha = 0.1f),
                contentColor = Color(0xFFFF9800),
                onClick = onForward
            )
        }
    }
}

@Composable
private fun ActionButton(
    text: String,
    icon: ImageVector,
    backgroundColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(6.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.appTypography.interMedium16px
        )
    }
}

@Composable
private fun StatusBadge(
    text: String,
    backgroundColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.appTypography.interMedium16px.copy(fontWeight = FontWeight.Medium),
            color = textColor
        )
    }
}

package com.app.ecarepro.feature.taskmanger

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

// ============== COLOR DEFINITIONS ==============

// Colors for section headers (Overdue, Today, Upcoming, Closed)
object StatusColors {
    val OverdueRed = Color(0xFFF44336)
    val TodayGreen = Color(0xFF4CAF50)
    val UpcomingBlue = Color(0xFF2196F3)
    val ClosedGray = Color(0xFF9E9E9E)
}

// Colors for task priority levels
object PriorityColors {
    val NoPriority = Color(0xFF9FA297)
    val Low = Color(0xFF1893D9)
    val Medium = Color(0xFFFFA800)
    val High = Color(0xFFCD251F)
}

// Colors for task status values
object TaskStatusColors {
    val Open = Color(0xFF9E9E9E)
    val InProgress = Color(0xFF2196F3)
    val Hold = Color(0xFFFFA800)
    val Closed = Color(0xFF4CAF50)
}

// ============== COLOR HELPER FUNCTIONS ==============

// Returns color for a given priority
fun getPriorityColor(priority: TaskPriority): Color {
    return when (priority) {
        TaskPriority.NO_PRIORITY -> PriorityColors.NoPriority
        TaskPriority.LOW -> PriorityColors.Low
        TaskPriority.MEDIUM -> PriorityColors.Medium
        TaskPriority.HIGH -> PriorityColors.High
    }
}

// Returns color for a given task status
fun getStatusColor(status: TaskStatus): Color {
    return when (status) {
        TaskStatus.OPEN -> TaskStatusColors.Open
        TaskStatus.IN_PROGRESS -> TaskStatusColors.InProgress
        TaskStatus.HOLD -> TaskStatusColors.Hold
        TaskStatus.CLOSED -> TaskStatusColors.Closed
    }
}

// ============== SHARED COMPOSABLES ==============

// Circular avatar that shows user photo or first letter of name
@Composable
fun UserAvatar(
    name: String,
    photoUrl: String,
    size: Int,
    showBorder: Boolean = false,
) {
    if (photoUrl.isNotEmpty()) {
        EcareProAsyncImage(
            imageUrl = photoUrl,
            contentDescription = name,
            modifier = Modifier
                .size(size.dp)
                .clip(CircleShape)
                .then(
                    if (showBorder) Modifier.border(2.dp, White, CircleShape)
                    else Modifier
                )
        )
    } else {
        Box(
            modifier = Modifier
                .size(size.dp)
                .background(MaterialTheme.appColors.border, CircleShape)
                .then(
                    if (showBorder) Modifier.border(2.dp, White, CircleShape)
                    else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.take(1).uppercase(),
                style = MaterialTheme.appTypography.interSemiBold14px.copy(
                    fontSize = (size * 0.4).sp
                ),
                color = MaterialTheme.appColors.textPrimary
            )
        }
    }
}

// Chip showing priority with colored dot and label
@Composable
fun PriorityChip(
    priority: TaskPriority,
    onClick: (() -> Unit)? = null,
) {
    val color = getPriorityColor(priority)

    Row(
        modifier = Modifier
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = priority.displayName,
            style = MaterialTheme.appTypography.interMedium12px,
            color = color
        )
    }
}

// Small circular flag icon showing priority color
@Composable
fun PriorityFlagIcon(
    priority: TaskPriority,
    onClick: () -> Unit = {},
) {
    val color = getPriorityColor(priority)

    Box(
        modifier = Modifier
            .size(22.dp)
            .clickable(onClick = onClick)
            .background(color.copy(alpha = 0.1f), CircleShape)
            .border(1.dp, color, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Flag,
            contentDescription = priority.displayName,
            modifier = Modifier.size(10.dp),
            tint = color
        )
    }
}

// Overlapping row of assignee avatars with "+N" badge
@Composable
fun AssigneeAvatars(
    assignees: List<TaskAssigneePresentation>,
    onClick: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy((-8).dp),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        assignees.take(2).forEach { assignee ->
            UserAvatar(
                name = assignee.name,
                photoUrl = assignee.photo,
                size = 24,
                showBorder = true,
            )
        }

        if (assignees.size > 2) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        MaterialTheme.appColors.background,
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+${assignees.size - 2}",
                    style = MaterialTheme.appTypography.interSemiBold14px.copy(
                        fontSize = 10.sp
                    ),
                    color = MaterialTheme.appColors.textPrimary
                )
            }
        }
    }
}

// Full-width colored banner showing task status
@Composable
fun StatusBanner(status: TaskStatus, onClick: () -> Unit) {
    val color = getStatusColor(status)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = status.displayName.uppercase(),
            style = MaterialTheme.appTypography.interSemiBold14px,
            color = White
        )
    }
}

// Radio-button style circle showing task status
@Composable
fun StatusIndicator(status: TaskStatus, onClick: () -> Unit) {
    val color = getStatusColor(status)

    Box(
        modifier = Modifier
            .size(20.dp)
            .clickable(onClick = onClick)
            .border(width = 2.dp, color = color, shape = CircleShape)
            .padding(3.dp)
    ) {
        if (status == TaskStatus.CLOSED || status == TaskStatus.IN_PROGRESS) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color, CircleShape)
            )
        }
    }
}

package com.app.ecarepro.feature.transport_att.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.core.domain.model.transport.TransportConstants
import com.app.ecarepro.core.domain.model.transport.TransportStudent
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

@Composable
fun StudentAttendanceItem(
    student: TransportStudent,
    tripType: Int,
    freezDrop: Boolean,
    freezPickup: Boolean,
    onPresentClick: () -> Unit,
    onAbsentClick: () -> Unit,
    onLeaveClick: () -> Unit = {},
    onDropClick: () -> Unit = {},
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Student photo
            EcareProAsyncImage(
                imageUrl = student.photo,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentDescription = student.stName,
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Student info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${student.stName}, ${student.className}",
                    style = MaterialTheme.appTypography.interSemiBold14px,
                )
                Text(
                    text = "Stop: ${student.stop}",
                    style = MaterialTheme.appTypography.interRegular14px.copy(fontSize = 12.sp),
                    color = MaterialTheme.appColors.textSecondary,
                )
                Text(
                    text = "Admission no: ${student.admissionNo}",
                    style = MaterialTheme.appTypography.interRegular14px.copy(fontSize = 12.sp),
                    color = MaterialTheme.appColors.textSecondary,
                )
            }

            // Attendance buttons
            when (tripType) {
                TransportConstants.UP_TRIP -> {
                    AttendanceButtons(
                        currentStatus = student.pickupStatus,
                        showOP = false,
                        enabled = !freezPickup,
                        onPresentClick = onPresentClick,
                        onAbsentClick = onAbsentClick,
                    )
                }

                TransportConstants.DOWN_TRIP -> {
                    AttendanceButtons(
                        currentStatus = student.dropStatus,
                        showOP = true,
                        enabled = !freezDrop,
                        onPresentClick = onPresentClick,
                        onAbsentClick = onAbsentClick,
                        onOPClick = onLeaveClick,
                    )
                }

                TransportConstants.DROP_STUDENT_TRIP -> {
                    if (student.isDropped) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                        ) {
                            Text(
                                text = "Dropped",
                                style = MaterialTheme.appTypography.interRegular14px,
                                color = MaterialTheme.appColors.textSecondary,
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFE3F2FD), RoundedCornerShape(8.dp))
                                .clickable { onDropClick() }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                        ) {
                            Text(
                                text = "Mark as Drop",
                                style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 12.sp),
                                color = Color(0xFF1976D2),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AttendanceButtons(
    currentStatus: Int,
    showOP: Boolean,
    enabled: Boolean,
    onPresentClick: () -> Unit = {},
    onAbsentClick: () -> Unit = {},
    onOPClick: () -> Unit = {},
) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        if (showOP) {
            AttendanceChip(
                text = "OP",
                isSelected = currentStatus == TransportConstants.OP,
                selectedColor = Color(0xFFFFA726),
                enabled = enabled,
                onClick = onOPClick,
            )
        }

        AttendanceChip(
            text = "A",
            isSelected = currentStatus == TransportConstants.ABSENT,
            selectedColor = Color(0xFFEF5350),
            enabled = enabled,
            onClick = onAbsentClick,
        )

        AttendanceChip(
            text = "P",
            isSelected = currentStatus == TransportConstants.PRESENT,
            selectedColor = Color(0xFF4CAF50),
            enabled = enabled,
            onClick = onPresentClick,
        )
    }
}

@Composable
private fun AttendanceChip(
    text: String,
    isSelected: Boolean,
    selectedColor: Color,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val bgColor = if (isSelected) selectedColor else Color(0xFFF5F5F5)
    val textColor = if (isSelected) Color.White else Color(0xFF9E9E9E)

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 13.sp),
            color = textColor,
        )
    }
}

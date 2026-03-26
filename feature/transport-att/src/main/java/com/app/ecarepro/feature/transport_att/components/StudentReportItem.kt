package com.app.ecarepro.feature.transport_att.components

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.core.domain.model.transport.TransportStudent
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun StudentReportItem(
    student: TransportStudent,
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
            EcareProAsyncImage(
                imageUrl = student.photo,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentDescription = student.stName,
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = student.stName,
                    style = MaterialTheme.appTypography.interSemiBold14px,
                )
                Text(
                    text = "Class: ${student.className}",
                    style = MaterialTheme.appTypography.interRegular14px.copy(fontSize = 12.sp),
                    color = MaterialTheme.appColors.textSecondary,
                )
                Text(
                    text = "Admission no: ${student.admissionNo}",
                    style = MaterialTheme.appTypography.interRegular14px.copy(fontSize = 12.sp),
                    color = MaterialTheme.appColors.textSecondary,
                )
            }

            Row (horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                StatusChip(
                    text = student.pickupTime.ifEmpty { "--" },
                    color = statusColor(student.pickupAtt),
                )
                StatusChip(
                    text = student.dropTime.ifEmpty { "--" },
                    color = statusColor(student.dropAtt),
                )
            }
        }
    }
}

@Composable
fun OutPassStudentItem(
    student: TransportStudent,
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
            EcareProAsyncImage(
                imageUrl = student.photo,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentDescription = student.stName,
            )

            Spacer(modifier = Modifier.width(12.dp))

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
                    text = "${student.route}",
                    style = MaterialTheme.appTypography.interRegular14px.copy(fontSize = 12.sp),
                    color = MaterialTheme.appColors.textSecondary,
                )
            }
        }
    }
}

@Composable
private fun StatusChip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color, RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 11.sp),
        )
    }
}

private fun statusColor(status: String): Color {
    return when {
        status.contains("Present", ignoreCase = true) -> Color(0xFF4CAF50)
        status.contains("Absent", ignoreCase = true) -> Color(0xFFEF5350)
        else -> Color(0xFFFFA726)
    }
}

private val previewStudent = TransportStudent(
    admissionNo = "6736",
    className = "4-C",
    dropAtt = "Absent",
    dropStatus = 0,
    dropTime = "",
    isConstant = false,
    isDropped = false,
    photo = "",
    pickupAtt = "Present",
    pickupStatus = 1,
    pickupTime = "02:13 PM",
    rollNo = "20",
    route = "Route 1 - Kashipur",
    stID = 2805,
    stName = "VIDAAD KHAN",
    stop = "Kashipur",
    stopID = 484,
)

@Preview(showBackground = true)
@Composable
private fun StudentReportItemPreview() {
    EcareProTheme {
        StudentReportItem(student = previewStudent)
    }
}

@Preview(showBackground = true)
@Composable
private fun StudentReportItemAbsentPreview() {
    EcareProTheme {
        StudentReportItem(
            student = previewStudent.copy(
                pickupAtt = "Absent",
                pickupTime = "",
                stName = "DAKSHITH SINGH SAINI",
                className = "6-D",
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OutPassStudentItemPreview() {
    EcareProTheme {
        OutPassStudentItem(student = previewStudent)
    }
}

@Preview(showBackground = true)
@Composable
private fun StatusChipPresentPreview() {
    EcareProTheme {
        StatusChip(text = "02:13 PM", color = Color(0xFF4CAF50))
    }
}

@Preview(showBackground = true)
@Composable
private fun StatusChipAbsentPreview() {
    EcareProTheme {
        StatusChip(text = "--", color = Color(0xFFEF5350))
    }
}

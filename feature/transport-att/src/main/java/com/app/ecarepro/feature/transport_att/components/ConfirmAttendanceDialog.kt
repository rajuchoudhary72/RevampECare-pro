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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.app.ecarepro.core.domain.model.transport.TransportConstants
import com.app.ecarepro.designsystem.core.component.Button
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

@Composable
fun ConfirmAttendanceDialog(
    tripType: Int,
    presentCount: Int,
    absentCount: Int,
    leaveCount: Int,
    date: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Confirm attendance",
                        style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 18.sp),
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "Marked on $date",
                    style = MaterialTheme.appTypography.interRegular14px,
                    color = MaterialTheme.appColors.textSecondary,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    // Present card
                    StatBox(
                        modifier = Modifier.weight(1f),
                        count = presentCount,
                        label = "Present Students",
                        backgroundColor = Color(0xFFE8F5E9),
                        iconColor = Color(0xFF4CAF50),
                        icon = Icons.Default.CheckCircle,
                    )

                    // Absent card
                    StatBox(
                        modifier = Modifier.weight(1f),
                        count = absentCount,
                        label = "Absent Students",
                        backgroundColor = Color(0xFFFFEBEE),
                        iconColor = Color(0xFFEF5350),
                        icon = Icons.Default.Close,
                    )
                }

                if (tripType == TransportConstants.DOWN_TRIP && leaveCount > 0) {
                    Spacer(modifier = Modifier.height(12.dp))
                    StatBox(
                        modifier = Modifier.fillMaxWidth(0.48f),
                        count = leaveCount,
                        label = "Outpass",
                        backgroundColor = Color(0xFFFFF8E1),
                        iconColor = Color(0xFFFFA726),
                        icon = Icons.Default.Lock,
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    androidx.compose.material3.OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        title = "Confirm",
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun StatBox(
    modifier: Modifier = Modifier,
    count: Int,
    label: String,
    backgroundColor: Color,
    iconColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
) {
    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .padding(12.dp),
    ) {
        Column {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = String.format("%02d", count),
                style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 24.sp),
                color = iconColor,
            )
            Text(
                text = label,
                style = MaterialTheme.appTypography.interRegular14px.copy(fontSize = 12.sp),
                color = MaterialTheme.appColors.textSecondary,
            )
        }
    }
}

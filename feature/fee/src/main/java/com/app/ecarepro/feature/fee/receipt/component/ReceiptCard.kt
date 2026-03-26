package com.app.ecarepro.feature.fee.receipt.component

import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.fee.receipt.ReceiptCardUi

@Composable
fun ReceiptCard(
    card: ReceiptCardUi,
    sessionId: Int,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onView: () -> Unit,
    onDownload: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column {
            // Header row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF2196F3).copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(20.dp),
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = card.recDate,
                        style = MaterialTheme.appTypography.interSemiBold14px,
                        color = MaterialTheme.appColors.textPrimary,
                    )
                    Text(
                        text = "Paid amount: ${card.paidAmount}",
                        style = MaterialTheme.appTypography.interRegular12px,
                        color = MaterialTheme.appColors.textSecondary,
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = MaterialTheme.appColors.textSecondary,
                )
            }

            if (isExpanded) {
                HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    ReceiptDetailRow(label = "Receipt number", value = card.receiptNumber)
                    Spacer(modifier = Modifier.height(6.dp))
                    ReceiptDetailRow(label = "Paid amount", value = card.paidAmount)
                    Spacer(modifier = Modifier.height(6.dp))
                    ReceiptDetailRow(label = "Payment mode", value = card.paymentMode)
                    Spacer(modifier = Modifier.height(6.dp))
                    ReceiptDetailRow(label = "Instalment", value = card.installment)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        OutlinedButton(
                            onClick = onView,
                            modifier = Modifier.weight(1f),
                            enabled = !card.isDownloading,
                        ) {
                            Text(
                                text = "View",
                                style = MaterialTheme.appTypography.interSemiBold14px,
                            )
                        }
                        Button(
                            onClick = onDownload,
                            modifier = Modifier.weight(1f),
                            enabled = !card.isDownloading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.appColors.primary,
                            ),
                        ) {
                            Text(
                                text = if (card.isDownloading) "..." else "Download",
                                style = MaterialTheme.appTypography.interSemiBold14px,
                                color = Color.White,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiptDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.appTypography.interRegular14px,
            color = MaterialTheme.appColors.textSecondary,
        )
        Text(
            text = value,
            style = MaterialTheme.appTypography.interSemiBold14px,
            color = MaterialTheme.appColors.textPrimary,
        )
    }
}

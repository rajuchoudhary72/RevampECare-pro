package com.app.ecarepro.feature.leave.leave_report.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors

@Composable
fun LeaveReportBottomBar(
    selectedCount: Int,
    onApproveAllClicked: () -> Unit,
    onRejectAllClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = MaterialTheme.appColors.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.appColors.surface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Selected count
            Text(
                text = "$selectedCount Selected",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.appColors.textPrimary
            )

            // Action buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Reject All Button
                OutlinedButton(
                    onClick = onRejectAllClicked,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.appColors.error
                    )
                ) {
                    Text("Reject All")
                }

                // Approve All Button
                Button(
                    onClick = onApproveAllClicked,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.appColors.primary
                    )
                ) {
                    Text("Approve All")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LeaveReportBottomBarPreview() {
    EcareProTheme {
        LeaveReportBottomBar(
            selectedCount = 5,
            onApproveAllClicked = {},
            onRejectAllClicked = {}
        )
    }
}

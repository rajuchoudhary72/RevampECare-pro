package com.app.ecarepro.feature.discipline.components

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.ecarepro.core.domain.model.discipline.InfractionRecord
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

@Composable
fun InfractionItemCard(
    record: InfractionRecord,
    onDeleteClick: () -> Unit,
    onComplianceClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Header: Category + menu
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = record.infraction.orEmpty(),
                style = MaterialTheme.appTypography.interSemiBold14px,
                color = Color(0xFFE53935),
                modifier = Modifier.weight(1f),
            )

            if (record.canDelete == true) {
                Box {
                    IconButton(onClick = { showMenu = true }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu", modifier = Modifier.size(18.dp))
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Delete", color = Color(0xFFE53935)) },
                            onClick = {
                                showMenu = false
                                onDeleteClick()
                            },
                        )
                    }
                }
            }
        }

        // Description (subcategory + corrective action)
        val description = buildString {
            record.subInfraction?.let { append(it) }
            record.correctiveAction?.let {
                if (isNotEmpty()) append(", ")
                append(it)
            }
        }
        if (description.isNotEmpty()) {
            Text(
                text = "$description.",
                style = MaterialTheme.appTypography.interRegular12px,
                color = MaterialTheme.appColors.textSecondary,
            )
        }

        // Consequence + Instance
        if (record.consequences != null || record.instance != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                record.consequences?.let {
                    Text(text = "Consequence: ", style = MaterialTheme.appTypography.interRegular12px, color = MaterialTheme.appColors.textSecondary)
                    Text(text = it, style = MaterialTheme.appTypography.interRegular12px, color = MaterialTheme.appColors.textPrimary)
                }
                if (record.consequences != null && record.instance != null) {
                    Text(text = " • ", style = MaterialTheme.appTypography.interRegular12px, color = MaterialTheme.appColors.textSecondary)
                }
                record.instance?.let {
                    Text(text = "Instance: ", style = MaterialTheme.appTypography.interRegular12px, color = MaterialTheme.appColors.textSecondary)
                    Text(text = it, style = MaterialTheme.appTypography.interRegular12px, color = MaterialTheme.appColors.textPrimary)
                }
            }
        }

        // Date + Staff info
        val staffInfo = record.issueBy ?: record.staffName
        if (record.infractionOn != null || staffInfo != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0xFFF2F2F7), shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
            ) {
                val dateStaff = buildString {
                    record.infractionOn?.let { append("Infraction on $it") }
                    staffInfo?.let {
                        if (isNotEmpty()) append(" • ")
                        append(it)
                    }
                }
                Text(
                    text = dateStaff,
                    style = MaterialTheme.appTypography.interRegular12px,
                    color = MaterialTheme.appColors.textSecondary,
                )
            }
        }

        // Status tags
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            record.isComplianceActive?.let { isActive ->
                Row(
                    modifier = Modifier.clickable { onComplianceClick() },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (isActive) Color(0xFF2196F3) else Color(0xFFFFA500),
                    )
                    Text(
                        text = if (isActive) "Compliance Active" else "Compliance Inactive",
                        style = MaterialTheme.appTypography.interRegular12px,
                        color = if (isActive) Color(0xFF2196F3) else Color(0xFFFFA500),
                    )
                }

                if (isActive) {
                    record.isResolved?.let { isResolved ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (isResolved) Color(0xFF4CAF50) else MaterialTheme.appColors.textSecondary,
                            )
                            Text(
                                text = if (isResolved) "Resolved" else "Not Resolved",
                                style = MaterialTheme.appTypography.interRegular12px,
                                color = if (isResolved) Color(0xFF4CAF50) else MaterialTheme.appColors.textSecondary,
                            )
                        }
                    }
                }
            }
        }
    }
}

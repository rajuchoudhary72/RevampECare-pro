package com.app.ecarepro.feature.discipline.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
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
import com.app.ecarepro.core.domain.model.discipline.AppreciationRecord
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

@Composable
fun AppreciationItemCard(
    record: AppreciationRecord,
    onDeleteClick: () -> Unit,
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
                text = record.appreciation.orEmpty(),
                style = MaterialTheme.appTypography.interSemiBold14px,
                color = Color(0xFF4CAF50),
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

        // Subcategory
        record.subAppreciation?.let {
            Text(
                text = it,
                style = MaterialTheme.appTypography.interRegular12px,
                color = MaterialTheme.appColors.textSecondary,
            )
        }

        // Reward + Instance
        if (record.reward != null || record.instance != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                record.reward?.let {
                    Text(text = "Reward: ", style = MaterialTheme.appTypography.interRegular12px, color = MaterialTheme.appColors.textSecondary)
                    Text(text = it, style = MaterialTheme.appTypography.interRegular12px, color = MaterialTheme.appColors.textPrimary)
                }
                if (record.reward != null && record.instance != null) {
                    Text(text = " • ", style = MaterialTheme.appTypography.interRegular12px, color = MaterialTheme.appColors.textSecondary)
                }
                record.instance?.let {
                    Text(text = "Instance: ", style = MaterialTheme.appTypography.interRegular12px, color = MaterialTheme.appColors.textSecondary)
                    Text(text = it.toString(), style = MaterialTheme.appTypography.interRegular12px, color = MaterialTheme.appColors.textPrimary)
                }
            }
        }

        // Remark
        record.remark?.let {
            if (it.isNotBlank()) {
                Text(
                    text = it,
                    style = MaterialTheme.appTypography.interRegular12px,
                    color = MaterialTheme.appColors.textSecondary,
                )
            }
        }

        // Date + Staff info
        val staffInfo = record.staffName
        if (record.appreciationOn != null || staffInfo != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0xFFF2F2F7), shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
            ) {
                val dateStaff = buildString {
                    record.appreciationOn?.let { append("Appreciation on $it") }
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

        // Points
        record.point?.let { points ->
            if (points > 0) {
                Text(
                    text = "Points: $points",
                    style = MaterialTheme.appTypography.interRegular12px,
                    color = Color(0xFF4CAF50),
                )
            }
        }
    }
}

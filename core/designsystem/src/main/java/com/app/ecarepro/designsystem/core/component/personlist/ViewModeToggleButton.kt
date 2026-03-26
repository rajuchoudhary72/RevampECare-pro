package com.app.ecarepro.designsystem.core.component.personlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.theme.appColors

@Composable
fun ViewModeToggleButton(
    viewMode: ListViewMode,
    onToggle: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(color = Color(0xFFF2F2F7), shape = RoundedCornerShape(8.dp))
            .clickable(onClick = onToggle),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (viewMode == ListViewMode.GRID) Icons.Default.ViewList else Icons.Default.GridView,
            contentDescription = "Toggle view mode",
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.appColors.textPrimary
        )
    }
}

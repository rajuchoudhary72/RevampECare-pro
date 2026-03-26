package com.app.ecarepro.feature.report.birthday_report.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.report.birthday_report.FilterMode

@Composable
fun BirthdayFilterBar(
    filterMode: FilterMode,
    filterValueLabel: String,
    onFilterModeClick: () -> Unit,
    onFilterValueClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp)
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FilterDropdown(
            label = filterMode.label,
            onClick = onFilterModeClick,
            modifier = Modifier.weight(1f),
        )
        FilterDropdown(
            label = filterValueLabel,
            onClick = onFilterValueClick,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun FilterDropdown(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
            .background(MaterialTheme.appColors.background)
            .clickable { onClick() }
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.appTypography.interMedium14px,
            color = MaterialTheme.appColors.textPrimary,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = MaterialTheme.appColors.textSecondary,
        )
    }
}

@Preview(showBackground = true, name = "Filter Bar - Month mode")
@Composable
private fun PreviewFilterBarMonth() {
    EcareProTheme {
        BirthdayFilterBar(
            filterMode = FilterMode.MONTH,
            filterValueLabel = "March",
            onFilterModeClick = {},
            onFilterValueClick = {},
        )
    }
}

@Preview(showBackground = true, name = "Filter Bar - Date mode")
@Composable
private fun PreviewFilterBarDate() {
    EcareProTheme {
        BirthdayFilterBar(
            filterMode = FilterMode.DATE,
            filterValueLabel = "22 March 2026",
            onFilterModeClick = {},
            onFilterValueClick = {},
        )
    }
}

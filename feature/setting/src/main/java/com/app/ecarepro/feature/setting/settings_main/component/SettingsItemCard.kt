package com.app.ecarepro.feature.setting.settings_main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.setting.R
import com.app.ecarepro.feature.setting.settings_main.SettingsItemType

@Composable
fun SettingsItemCard(
    itemType: SettingsItemType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val icon: ImageVector = when (itemType) {
        SettingsItemType.RATE_APP -> Icons.Default.Star
        SettingsItemType.CHANGE_USERNAME -> Icons.Default.Person
        SettingsItemType.CHANGE_PASSWORD -> Icons.Default.Lock
        SettingsItemType.CHANGE_LANGUAGE -> Icons.Default.Language
        SettingsItemType.SYNC_DATA -> Icons.Default.Refresh
    }
    val iconColor: Color = when (itemType) {
        SettingsItemType.RATE_APP -> Color(0xFF4285F4)
        SettingsItemType.CHANGE_USERNAME -> Color(0xFF2196F3)
        SettingsItemType.CHANGE_PASSWORD -> Color(0xFFE53935)
        SettingsItemType.CHANGE_LANGUAGE -> Color(0xFFF5A623)
        SettingsItemType.SYNC_DATA -> Color(0xFF4CAF50)
    }
    val titleRes: Int = when (itemType) {
        SettingsItemType.RATE_APP -> R.string.settings_rate_us
        SettingsItemType.CHANGE_USERNAME -> R.string.settings_change_username
        SettingsItemType.CHANGE_PASSWORD -> R.string.settings_change_password
        SettingsItemType.CHANGE_LANGUAGE -> R.string.settings_change_language
        SettingsItemType.SYNC_DATA -> R.string.settings_sync_data
    }
    val hasChevron = itemType != SettingsItemType.SYNC_DATA

    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp))
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = iconColor,
            )
        }

        Text(
            text = stringResource(titleRes),
            style = MaterialTheme.appTypography.interMedium16px,
            color = MaterialTheme.appColors.textPrimary,
            modifier = Modifier.weight(1f),
        )

        if (hasChevron) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.appColors.textSecondary,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSettingsItemCard() {
    EcareProTheme {
        SettingsItemCard(itemType = SettingsItemType.CHANGE_USERNAME, onClick = {})
    }
}

package com.app.ecarepro.feature.setting.settings_main.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

@Composable
fun RulesSection(rules: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "RULES:",
            style = MaterialTheme.appTypography.interSemiBold14px,
            color = MaterialTheme.appColors.textPrimary,
        )
        rules.forEachIndexed { index, rule ->
            Text(
                text = "${index + 1}. $rule",
                style = MaterialTheme.appTypography.interRegular13px,
                color = MaterialTheme.appColors.textSecondary,
            )
        }
    }
}

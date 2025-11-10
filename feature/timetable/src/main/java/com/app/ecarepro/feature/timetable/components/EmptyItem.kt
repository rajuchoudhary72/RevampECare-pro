package com.app.ecarepro.feature.timetable.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography


@Composable
fun EmptyItem() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            "No classes scheduled for this day.",
            color = MaterialTheme.appColors.textSecondary,
            style = MaterialTheme.appTypography.interMedium16px
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyItemPreview() {
    EcareProTheme {
        EmptyItem()
    }
}
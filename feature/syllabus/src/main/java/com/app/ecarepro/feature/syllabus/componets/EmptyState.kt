package com.app.ecarepro.feature.syllabus.componets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.syllabus.R

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.icon_book),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No results. \n" +
                    "Maybe try a broader search?",
            style = MaterialTheme.appTypography.interRegular12px,
            textAlign = TextAlign.Center,
            color = MaterialTheme.appColors.textSecondary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyStatePreview() {
    EcareProTheme() {
        EmptyState()
    }
}
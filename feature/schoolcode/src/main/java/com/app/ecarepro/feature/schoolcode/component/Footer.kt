package com.app.ecarepro.feature.schoolcode.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.theme.EcareProTheme

@Composable
fun Footer() {
    Text(
        text = "Powered by FRANCISCAN", // stringResource(R.string.powered_by)
        style = MaterialTheme.typography.bodySmall,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(vertical = 16.dp)
    )
}
@Preview
@Composable
fun FooterScreenPreview() {
    EcareProTheme {
        Footer()
    }
}

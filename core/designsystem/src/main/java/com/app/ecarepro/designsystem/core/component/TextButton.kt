package com.app.ecarepro.designsystem.core.component

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

@Composable
fun TextButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    TextButton (
        modifier = modifier,
        onClick = onClick,
        shape = MaterialTheme.shapes.small.copy(CornerSize(12.dp)),
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors().copy(contentColor = MaterialTheme.appColors.textSecondary)
    ) {
        Text(
            title,
            style = MaterialTheme.appTypography.interRegular14px
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ButtonPreview() {
    EcareProTheme {
        TextButton(
            title = "Preview Button",
            onClick = {}
        )
    }
}
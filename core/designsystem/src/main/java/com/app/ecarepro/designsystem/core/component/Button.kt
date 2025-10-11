package com.app.ecarepro.designsystem.core.component

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

@Composable
fun Button(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = MaterialTheme.appColors.primary,
) {
    androidx.compose.material3.Button(
        modifier = modifier.height(48.dp),
        onClick = onClick,
        shape = MaterialTheme.shapes.small.copy(CornerSize(12.dp)),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors().copy(contentColor = White, containerColor = backgroundColor),
        elevation = ButtonDefaults.buttonElevation(0.5.dp)
    ) {
        Text(
            title,
            style = MaterialTheme.appTypography.interMedium16px
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ButtonPreview() {
    EcareProTheme {
        Button(
            title = "Preview Button",
            onClick = {}
        )
    }
}
package com.app.ecarepro.designsystem.core.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
fun EcareProOutlineButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    borderColor: Color = MaterialTheme.appColors.textSecondary,
) {
    OutlinedButton(
        modifier = modifier.height(48.dp),
        onClick = onClick,
        shape = MaterialTheme.shapes.small.copy(CornerSize(12.dp)),
        enabled = enabled,
        border = BorderStroke(1.dp, borderColor),
        colors = ButtonDefaults.outlinedButtonColors().copy(containerColor = White)
    ) {
        Text(
            title,
            style = MaterialTheme.appTypography.interMedium16px.copy(color = MaterialTheme.appColors.textSecondary)
        )
    }
}

@Preview(showBackground = false)
@Composable
private fun EcareProOutlineButtonPreview() {
    EcareProTheme {
        EcareProOutlineButton(
            title = "Preview Button",
            onClick = {}
        )
    }
}
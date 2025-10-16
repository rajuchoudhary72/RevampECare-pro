package com.app.ecarepro.designsystem.core.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
    enabled: Boolean = true,
    @DrawableRes leadingIcon: Int = 0,
    titleColor: Color = MaterialTheme.appColors.textSecondary,
    contentPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
) {
    TextButton(
        modifier = modifier,
        onClick = onClick,
        shape = MaterialTheme.shapes.small.copy(CornerSize(12.dp)),
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors()
            .copy(contentColor = MaterialTheme.appColors.textSecondary),
        contentPadding = contentPadding
    ) {
        if (leadingIcon != 0) {
            Icon(
                painter = painterResource(id = leadingIcon),
                contentDescription = null,
                tint = MaterialTheme.appColors.textSecondary
            )
        }
        Text(
            title,
            style = MaterialTheme.appTypography.interRegular14px.copy(color = titleColor)
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
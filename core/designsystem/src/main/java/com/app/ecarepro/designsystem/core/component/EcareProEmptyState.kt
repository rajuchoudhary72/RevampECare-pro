package com.app.ecarepro.designsystem.core.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

@Composable
fun EcareProEmptyState(
    modifier: Modifier = Modifier,
    message: String,
    icon: ImageVector? = Icons.Default.Inbox,
    @DrawableRes drawableRes: Int? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            drawableRes != null -> {
                Image(
                    painter = painterResource(id = drawableRes),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.appColors.border.copy(alpha = 0.6f))
                )
            }
            icon != null -> {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.appColors.border
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            style = MaterialTheme.appTypography.interRegular14px,
            textAlign = TextAlign.Center,
            color = MaterialTheme.appColors.textSecondary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EcareProEmptyStatePreview() {
    EcareProTheme {
        EcareProEmptyState(
            message = "No items found.\nMaybe try a different search?"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EcareProEmptyStateCustomIconPreview() {
    EcareProTheme {
        EcareProEmptyState(
            message = "No student profiles found.\nMaybe try a different search?",
            icon = Icons.Default.Inbox
        )
    }
}

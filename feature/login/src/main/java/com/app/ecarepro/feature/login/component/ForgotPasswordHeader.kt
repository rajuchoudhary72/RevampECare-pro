package com.app.ecarepro.feature.login.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appTypography

@Composable
fun ForgotPasswordHeader(
    modifier: Modifier = Modifier,
    title: String,
    descriptionText: String,
    onClose: () -> Unit,
) {
    Column {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 20.sp),
                modifier = Modifier.weight(1f),
            )

            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close"
                )
            }
        }
        Text(
            text = descriptionText,
            style = MaterialTheme.appTypography.interRegular14px,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun ForgotPasswordHeaderPreview() {
    EcareProTheme {
        ForgotPasswordHeader(
            title = "Forget password",
            descriptionText = "Don’t worry — we’ll help you reset it in just a few steps.",
            onClose = {}
        )
    }
}
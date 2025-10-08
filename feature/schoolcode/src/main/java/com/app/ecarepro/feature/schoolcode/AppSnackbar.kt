package com.app.ecarepro.feature.schoolcode

// In a new file, e.g., core/designsystem/src/main/java/com/app/ecarepro/designsystem/core/component/snackbar/AppSnackbar.kt


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.type
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.color
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography


enum class MessageType {
    INFO,
    SUCCESS,
    WARNING,
    ERROR;
}

data class SnackbarMessage(
    val text: String,
    val type: MessageType = MessageType.ERROR,
)

@Composable
fun AppSnackbar(
    snackbarMessage: SnackbarMessage,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = when (snackbarMessage.type) {
        MessageType.INFO -> MaterialTheme.appColors.info
        MessageType.SUCCESS -> MaterialTheme.appColors.success
        MessageType.WARNING -> MaterialTheme.appColors.warning
        MessageType.ERROR -> MaterialTheme.appColors.error
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = snackbarMessage.text,
            color = Color.White,
            style = MaterialTheme.appTypography.interMedium16px.copy(fontSize = 14.sp),
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
private fun AppSnackbarWarningPreview() {
    EcareProTheme {
        AppSnackbar(
            snackbarMessage = SnackbarMessage(
                text = "We couldn't match that code. Enter a different one.",
                type = MessageType.WARNING
            )
        )
    }
}

@Preview
@Composable
private fun AppSnackbarSuccessPreview() {
    EcareProTheme {
        AppSnackbar(
            snackbarMessage = SnackbarMessage(
                text = "Success! You will be redirected.",
                type = MessageType.SUCCESS
            )
        )
    }
}
@Preview
@Composable
private fun AppSnackbarInfoPreview() {
    EcareProTheme {
        AppSnackbar(
            snackbarMessage = SnackbarMessage(
                text = "Success! You will be redirected.",
                type = MessageType.INFO
            )
        )
    }
}
@Preview
@Composable
private fun AppSnackbarErrorPreview() {
    EcareProTheme {
        AppSnackbar(
            snackbarMessage = SnackbarMessage(
                text = "Success! You will be redirected.",
                type = MessageType.ERROR
            )
        )
    }
}
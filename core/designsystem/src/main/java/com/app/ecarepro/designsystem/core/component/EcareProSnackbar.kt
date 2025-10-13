package com.app.ecarepro.designsystem.core.component

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val text: String = "Unknown error occurred",
    val type: MessageType = MessageType.ERROR,
)

@Composable
fun EcareProSnackbar(
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
private fun EcareProSnackbarWarningPreview() {
    EcareProTheme {
        EcareProSnackbar(
            snackbarMessage = SnackbarMessage(
                text = "We couldn't match that code. Enter a different one.",
                type = MessageType.WARNING
            )
        )
    }
}

@Preview
@Composable
private fun EcareProSnackbarSuccessPreview() {
    EcareProTheme {
        EcareProSnackbar(
            snackbarMessage = SnackbarMessage(
                text = "Success! You will be redirected.",
                type = MessageType.SUCCESS
            )
        )
    }
}
@Preview
@Composable
private fun EcareProSnackbarInfoPreview() {
    EcareProTheme {
        EcareProSnackbar(
            snackbarMessage = SnackbarMessage(
                text = "Success! You will be redirected.",
                type = MessageType.INFO
            )
        )
    }
}
@Preview
@Composable
private fun EcareProSnackbarErrorPreview() {
    EcareProTheme {
        EcareProSnackbar(
            snackbarMessage = SnackbarMessage(
                text = "Success! You will be redirected.",
                type = MessageType.ERROR
            )
        )
    }
}
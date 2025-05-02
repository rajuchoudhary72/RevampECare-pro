package com.app.ecarepro.compose.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.app.ecarepro.compose.theme.ECareProTheme

@Composable
fun LoadingDialog() {
    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false, // Crucial for removing default dialog padding
        ),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = Color.Black.copy(alpha = 0.2f)
        ) {


            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LoadingDialogPreview() {
    ECareProTheme {
        LoadingDialog()
    }
}
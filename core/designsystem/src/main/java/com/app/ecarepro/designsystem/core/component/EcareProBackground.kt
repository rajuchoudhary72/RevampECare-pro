package com.app.ecarepro.designsystem.core.component


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.app.ecarepro.core.designsystem.R
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors

@Composable
fun EcareProBackground(
    modifier: Modifier = Modifier,
    imagePainter: Painter = painterResource(id = R.drawable.background_transparent_image),
    overlayColor: Color = Color(0xFF66BB6A),
    content: @Composable () -> Unit,
) {


    Surface(
        color = overlayColor,
        modifier = modifier.fillMaxSize(),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = imagePainter,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.None
            )
            content()
        }
    }
}

@Preview
@Composable
fun BackgroundPreview() {
    EcareProTheme {
        EcareProBackground {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                Text("Preview Content")
            }
        }
    }
}
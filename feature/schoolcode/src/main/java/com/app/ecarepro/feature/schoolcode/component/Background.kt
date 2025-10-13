package com.app.ecarepro.feature.schoolcode.component


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
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.feature.schoolcode.R

@Composable
fun Background(
    modifier: Modifier = Modifier,
    imagePainter: Painter = painterResource(id = R.drawable.background_image_2),
    overlayColor: Color = MaterialTheme.appColors.primary,
    overlayAlpha: Float = 0.9f,
    content: @Composable () -> Unit,
) {


    Surface(
        color = Color.Transparent,
        modifier = modifier.fillMaxSize(),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = imagePainter,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.None
            )


            if (overlayColor != Color.Unspecified) {
                Surface(
                    color = overlayColor.copy(alpha = overlayAlpha),
                    modifier = Modifier.fillMaxSize()
                ) {}
            }

            content()
        }
    }
}

@Preview
@Composable
fun BackgroundPreview() {
    EcareProTheme {
        Background {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                Text("Preview Content")
            }
        }
    }
}
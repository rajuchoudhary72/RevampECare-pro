package com.app.ecarepro.designsystem.core.component

import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.app.ecarepro.core.designsystem.R
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

@Composable
fun Loader(
    modifier: Modifier = Modifier,
    loaderGifRes:Int = R.drawable.pencil_loader,
    showText: Boolean = true,
    loadingText: String = "Loading..."
) {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                loaderGifRes,
                imageLoader = imageLoader
            ),
            contentDescription = "Loading animation",
            modifier = Modifier.Companion.size(200.dp)
        )
        if (showText) {
            Spacer(modifier = Modifier.Companion.height(16.dp))
            Text(
                text = loadingText,
                style = MaterialTheme.appTypography.interRegular14px,
                color = MaterialTheme.appColors.textSecondary
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoaderPreview() {
    EcareProTheme {
        Surface {
            Loader()
        }
    }
}
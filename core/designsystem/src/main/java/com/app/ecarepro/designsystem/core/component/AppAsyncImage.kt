package com.app.ecarepro.designsystem.core.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.app.ecarepro.core.designsystem.R

/**
 * A reusable composable for loading images from a URL with Coil.
 * It provides default placeholders and error images for a consistent look.
 *
 * @param imageUrl The URL of the image to load.
 * @param contentDescription The description for accessibility.
 * @param modifier Modifier for this composable.
 * @param placeholder The painter to display while the image is loading.
 * @param error The painter to display if the image fails to load.
 * @param contentScale The scaling to apply to the image.
 */
@Composable
fun AppAsyncImage(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    contentDescription: String? = null,
    placeholder: Painter = painterResource(id = R.drawable.img_placeholder),
    error: Painter = painterResource(id = R.drawable.img_placeholder),
    contentScale: ContentScale = ContentScale.Crop,
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        modifier = modifier,
        placeholder = placeholder,
        error = error,
        contentScale = contentScale
    )
}
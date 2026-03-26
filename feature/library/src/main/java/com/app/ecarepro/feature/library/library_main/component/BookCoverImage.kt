package com.app.ecarepro.feature.library.library_main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage

@Composable
fun BookCoverImage(
    url: String?,
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier,
) {
    if (!url.isNullOrEmpty()) {
        EcareProAsyncImage(
            imageUrl = url,
            contentDescription = "Book cover",
            modifier = modifier
                .size(width, height)
                .clip(RoundedCornerShape(6.dp)),
        )
    } else {
        Box(
            modifier = modifier
                .size(width, height)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.MenuBook,
                contentDescription = "No cover",
                tint = Color(0xFFBDBDBD),
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

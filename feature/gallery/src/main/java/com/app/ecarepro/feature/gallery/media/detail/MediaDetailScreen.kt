package com.app.ecarepro.feature.gallery.media.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import androidx.compose.ui.tooling.preview.Preview
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaDetailScreen(
    viewModel: MediaDetailViewModel,
    navigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is MediaDetailEvent.NavigateBack -> navigateBack()
            }
        }
    }

    val data = (uiState as? UiState.Success)?.data ?: return

    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = "Media details",
                onNavigationClicked = { viewModel.handleIntent(MediaDetailIntent.OnBackClicked) },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            // Full-size image
            if (data.fullSizeUrl.isNotBlank() && isImageUrl(data.fullSizeUrl)) {
                EcareProAsyncImage(
                    imageUrl = data.fullSizeUrl,
                    contentDescription = data.headline,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f),
                )
            } else if (data.thumbnailUrl.isNotBlank() && isImageUrl(data.thumbnailUrl)) {
                EcareProAsyncImage(
                    imageUrl = data.thumbnailUrl,
                    contentDescription = data.headline,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f),
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // Headline
                Text(
                    text = data.headline,
                    style = MaterialTheme.appTypography.interSemiBold16px,
                    color = MaterialTheme.appColors.textPrimary,
                )

                // Description (strip HTML tags)
                if (data.description.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stripHtmlTags(data.description),
                        style = MaterialTheme.appTypography.interRegular14px,
                        color = MaterialTheme.appColors.textSecondary,
                    )
                }

                Spacer(Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(Modifier.height(12.dp))

                // Metadata rows
                if (data.publishedOn.isNotBlank()) {
                    MetaRow(
                        icon = Icons.Outlined.CalendarToday,
                        label = "Added",
                        value = data.publishedOn,
                    )
                    Spacer(Modifier.height(12.dp))
                }
                if (data.updatedOn.isNotBlank()) {
                    MetaRow(
                        icon = Icons.Outlined.Update,
                        label = "Updated on",
                        value = data.updatedOn,
                    )
                    Spacer(Modifier.height(12.dp))
                }
                if (data.newsName.isNotBlank()) {
                    MetaRow(
                        icon = Icons.Outlined.Newspaper,
                        label = "Newspaper",
                        value = data.newsName,
                    )
                }
            }
        }
    }
}

@Composable
private fun MetaRow(
    icon: ImageVector,
    label: String,
    value: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.appColors.textSecondary,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = label,
            style = MaterialTheme.appTypography.interRegular14px,
            color = MaterialTheme.appColors.textSecondary,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = MaterialTheme.appTypography.interRegular14px,
            color = MaterialTheme.appColors.textPrimary,
        )
    }
}

private fun isImageUrl(url: String): Boolean {
    val lower = url.lowercase()
    return lower.endsWith(".jpg") || lower.endsWith(".jpeg") ||
        lower.endsWith(".png") || lower.endsWith(".webp") ||
        lower.endsWith(".gif")
}

private fun stripHtmlTags(html: String): String {
    return html.replace(Regex("<[^>]*>"), "").trim()
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Light")
@Composable
private fun MediaDetailScreenPreviewLight() {
    val data = MediaDetailUiState(
        id = 1,
        newsName = "Hindustan Times",
        headline = "Annual Day Celebration 2025",
        publishedOn = "10-Jan-2025",
        updatedOn = "12-Jan-2025",
        thumbnailUrl = "https://picsum.photos/600/338",
        fullSizeUrl = "",
        description = "<p>A grand celebration was held at the school auditorium.</p>",
    )
    EcareProTheme {
        EcareProScaffold(
            topBar = {
                EcareProTopAppBar(
                    title = "Media details",
                    onNavigationClicked = {},
                )
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                if (data.thumbnailUrl.isNotBlank() && isImageUrl(data.thumbnailUrl)) {
                    EcareProAsyncImage(
                        imageUrl = data.thumbnailUrl,
                        contentDescription = data.headline,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f),
                    )
                }
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = data.headline,
                        style = MaterialTheme.appTypography.interSemiBold16px,
                        color = MaterialTheme.appColors.textPrimary,
                    )
                    if (data.description.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = stripHtmlTags(data.description),
                            style = MaterialTheme.appTypography.interRegular14px,
                            color = MaterialTheme.appColors.textSecondary,
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(12.dp))
                    if (data.publishedOn.isNotBlank()) {
                        MetaRow(icon = Icons.Outlined.CalendarToday, label = "Added", value = data.publishedOn)
                        Spacer(Modifier.height(12.dp))
                    }
                    if (data.updatedOn.isNotBlank()) {
                        MetaRow(icon = Icons.Outlined.Update, label = "Updated on", value = data.updatedOn)
                        Spacer(Modifier.height(12.dp))
                    }
                    if (data.newsName.isNotBlank()) {
                        MetaRow(icon = Icons.Outlined.Newspaper, label = "Newspaper", value = data.newsName)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, name = "Dark")
@Composable
private fun MediaDetailScreenPreviewDark() {
    val data = MediaDetailUiState(
        id = 1,
        newsName = "Hindustan Times",
        headline = "Annual Day Celebration 2025",
        publishedOn = "10-Jan-2025",
        updatedOn = "12-Jan-2025",
        thumbnailUrl = "https://picsum.photos/600/338",
        fullSizeUrl = "",
        description = "<p>A grand celebration was held at the school auditorium.</p>",
    )
    EcareProTheme {
        EcareProScaffold(
            topBar = {
                EcareProTopAppBar(
                    title = "Media details",
                    onNavigationClicked = {},
                )
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                if (data.thumbnailUrl.isNotBlank() && isImageUrl(data.thumbnailUrl)) {
                    EcareProAsyncImage(
                        imageUrl = data.thumbnailUrl,
                        contentDescription = data.headline,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f),
                    )
                }
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = data.headline,
                        style = MaterialTheme.appTypography.interSemiBold16px,
                        color = MaterialTheme.appColors.textPrimary,
                    )
                    if (data.description.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = stripHtmlTags(data.description),
                            style = MaterialTheme.appTypography.interRegular14px,
                            color = MaterialTheme.appColors.textSecondary,
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(12.dp))
                    if (data.publishedOn.isNotBlank()) {
                        MetaRow(icon = Icons.Outlined.CalendarToday, label = "Added", value = data.publishedOn)
                        Spacer(Modifier.height(12.dp))
                    }
                    if (data.updatedOn.isNotBlank()) {
                        MetaRow(icon = Icons.Outlined.Update, label = "Updated on", value = data.updatedOn)
                        Spacer(Modifier.height(12.dp))
                    }
                    if (data.newsName.isNotBlank()) {
                        MetaRow(icon = Icons.Outlined.Newspaper, label = "Newspaper", value = data.newsName)
                    }
                }
            }
        }
    }
}

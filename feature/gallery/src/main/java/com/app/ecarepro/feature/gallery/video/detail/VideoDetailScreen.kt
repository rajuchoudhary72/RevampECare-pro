package com.app.ecarepro.feature.gallery.video.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
import com.app.ecarepro.core.domain.model.Video
import com.app.ecarepro.core.domain.model.PhotoSetting
import com.app.ecarepro.core.domain.model.VideoAlbumDetail
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import androidx.compose.ui.tooling.preview.Preview
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

@Composable
fun VideoDetailScreen(
    viewModel: VideoDetailViewModel,
    navigateBack: () -> Unit,
    navigateToPlayer: (Int) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is VideoDetailEvent.NavigateBack -> navigateBack()
                is VideoDetailEvent.NavigateToPlayer -> navigateToPlayer(event.initialVideoIndex)
                is VideoDetailEvent.ShowMessage -> {
                    snackbarMessage = event.snackbarMessage
                    snackbarHostState.showSnackbar(event.snackbarMessage.text)
                }
            }
        }
    }

    VideoDetailContent(
        albumTitle = viewModel.navKey.albumTitle,
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VideoDetailContent(
    albumTitle: String,
    uiState: UiState<VideoDetailUiState>,
    handleIntent: (VideoDetailIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit,
) {
    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = albumTitle,
                onNavigationClicked = { handleIntent(VideoDetailIntent.OnBackClicked) }
            )
        },
        containerColor = White,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = onSnackbarDismissed,
    ) { paddingValues ->
        UiStateHandler(
            modifier = Modifier.padding(paddingValues),
            state = uiState,
        ) { data ->
            PullToRefreshBox(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                isRefreshing = data.isRefreshing,
                onRefresh = { handleIntent(VideoDetailIntent.OnRefresh) }
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    if (data.albumDetail.description.isNotBlank()) {
                        item(span = { GridItemSpan(3) }) {
                            AlbumDescription(
                                description = data.albumDetail.description,
                                isExpanded = data.isDescriptionExpanded,
                                onToggle = { handleIntent(VideoDetailIntent.OnToggleDescription) }
                            )
                        }
                    }

                    itemsIndexed(data.albumDetail.videos) { index, video ->
                        VideoThumbnailCell(
                            videoUrl = video.url,
                            title = video.title,
                            onClick = { handleIntent(VideoDetailIntent.OnVideoClicked(index)) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VideoThumbnailCell(
    videoUrl: String,
    title: String,
    onClick: () -> Unit,
) {
    val thumbnailUrl = extractYouTubeThumbnail(videoUrl)
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.Black)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (thumbnailUrl != null) {
            EcareProAsyncImage(
                imageUrl = thumbnailUrl,
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
            )
        }
        Icon(
            imageVector = Icons.Default.PlayCircle,
            contentDescription = "Play",
            tint = Color.White.copy(alpha = 0.85f),
            modifier = Modifier.size(36.dp),
        )
    }
}

@Composable
private fun AlbumDescription(
    description: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
) {
    val plainText = description
        .replace(Regex("<[^>]*>"), "")
        .replace("&nbsp;", " ")
        .trim()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Text(
            text = plainText,
            style = MaterialTheme.appTypography.interRegular14px,
            color = MaterialTheme.appColors.textPrimary,
            maxLines = if (isExpanded) Int.MAX_VALUE else 3,
            overflow = TextOverflow.Ellipsis,
        )
        if (plainText.length > 150) {
            Text(
                text = if (isExpanded) "view less" else "view more...",
                style = MaterialTheme.appTypography.interSemiBold14px,
                color = MaterialTheme.appColors.primary,
                modifier = Modifier.clickable(onClick = onToggle),
            )
        }
    }
}

private fun extractYouTubeThumbnail(url: String): String? {
    val videoId = Regex("/embed/([\\w-]+)").find(url)?.groupValues?.getOrNull(1) ?: return null
    return "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
}

@Preview(showBackground = true, name = "Light")
@Composable
private fun VideoDetailScreenPreviewLight() {
    EcareProTheme {
        VideoDetailContent(
            albumTitle = "Nature Videos",
            uiState = UiState.Success(
                VideoDetailUiState(
                    albumDetail = VideoAlbumDetail(
                        title = "Nature Videos",
                        description = "",
                        eventDate = "",
                        totalVideos = 1,
                        setting = PhotoSetting(isLikeEnabled = true, isShareEnabled = true, isAddFavouriteEnabled = true),
                        videos = listOf(Video("1", "Nature Clip", "https://www.youtube.com/embed/abc123", 0, false, false)),
                    )
                )
            ),
            handleIntent = {},
            snackbarHostState = SnackbarHostState(),
            snackbarMessage = null,
            onSnackbarDismissed = {},
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, name = "Dark")
@Composable
private fun VideoDetailScreenPreviewDark() {
    EcareProTheme {
        VideoDetailContent(
            albumTitle = "Nature Videos",
            uiState = UiState.Success(
                VideoDetailUiState(
                    albumDetail = VideoAlbumDetail(
                        title = "Nature Videos",
                        description = "",
                        eventDate = "",
                        totalVideos = 1,
                        setting = PhotoSetting(isLikeEnabled = true, isShareEnabled = true, isAddFavouriteEnabled = true),
                        videos = listOf(Video("1", "Nature Clip", "https://www.youtube.com/embed/abc123", 0, false, false)),
                    )
                )
            ),
            handleIntent = {},
            snackbarHostState = SnackbarHostState(),
            snackbarMessage = null,
            onSnackbarDismissed = {},
        )
    }
}

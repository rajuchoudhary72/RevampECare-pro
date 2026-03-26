package com.app.ecarepro.feature.gallery.video.player

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.Photo
import com.app.ecarepro.core.domain.model.PhotoSetting
import com.app.ecarepro.core.domain.model.Video
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import androidx.compose.ui.tooling.preview.Preview
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun VideoPlayerScreen(
    viewModel: VideoPlayerViewModel,
    navigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is VideoPlayerEvent.NavigateBack -> navigateBack()
                is VideoPlayerEvent.ShareVideo -> shareVideo(context, event.videoUrl)
                is VideoPlayerEvent.ShowMessage -> {
                    snackbarMessage = event.snackbarMessage
                    snackbarHostState.showSnackbar(event.snackbarMessage.text)
                }
            }
        }
    }

    VideoPlayerContent(
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
private fun VideoPlayerContent(
    albumTitle: String,
    uiState: UiState<VideoPlayerUiState>,
    handleIntent: (VideoPlayerIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit,
) {
    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = albumTitle,
                onNavigationClicked = { handleIntent(VideoPlayerIntent.OnBackClicked) },
            )
        },
        containerColor = Color.Black,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = onSnackbarDismissed,
    ) { paddingValues ->
        UiStateHandler(
            modifier = Modifier.padding(paddingValues),
            state = uiState,
        ) { data ->
            val currentVideo = data.videos.getOrNull(data.currentIndex)
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(Color.Black),
            ) {
                item {
                    if (currentVideo != null) {
                        val videoId = extractYouTubeVideoId(currentVideo.url)
                        if (videoId != null) {
                            key(videoId) {
                                YoutubePlayerView(
                                    videoId = videoId,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(16f / 9f),
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16f / 9f)
                                    .background(Color.DarkGray),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "Unable to load video",
                                    style = MaterialTheme.appTypography.interRegular14px,
                                    color = Color.White,
                                )
                            }
                        }

                        if (currentVideo.title.isNotBlank()) {
                            Text(
                                text = currentVideo.title,
                                style = MaterialTheme.appTypography.interSemiBold14px,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            )
                        }

                        VideoActionBar(
                            video = currentVideo,
                            setting = data.setting,
                            handleIntent = handleIntent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.8f))
                                .padding(vertical = 8.dp),
                        )

                        HorizontalDivider(color = Color.DarkGray, thickness = 0.5.dp)
                    }
                }

                if (data.videos.size > 1) {
                    item {
                        Text(
                            text = "All Videos",
                            style = MaterialTheme.appTypography.interSemiBold14px,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        )
                    }

                    itemsIndexed(data.videos) { index, video ->
                        VideoPlaylistItem(
                            video = video,
                            isSelected = index == data.currentIndex,
                            onClick = { handleIntent(VideoPlayerIntent.OnVideoSelected(index)) },
                        )
                        HorizontalDivider(color = Color.DarkGray.copy(alpha = 0.5f), thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}

@Composable
private fun YoutubePlayerView(
    videoId: String,
    modifier: Modifier = Modifier,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        factory = { context ->
            YouTubePlayerView(context).apply {
                lifecycleOwner.lifecycle.addObserver(this)
                addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.loadVideo(videoId, 0f)
                    }
                })
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun VideoPlaylistItem(
    video: Video,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val thumbnailUrl = extractYouTubeThumbnail(video.url)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isSelected) Color.DarkGray.copy(alpha = 0.4f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(width = 100.dp, height = 64.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.DarkGray)
                .then(
                    if (isSelected) Modifier.border(2.dp, MaterialTheme.appColors.primary, RoundedCornerShape(6.dp))
                    else Modifier
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (thumbnailUrl != null) {
                EcareProAsyncImage(
                    imageUrl = thumbnailUrl,
                    contentDescription = video.title,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            Icon(
                imageVector = Icons.Default.PlayCircle,
                contentDescription = "Play",
                tint = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.size(28.dp),
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = video.title.ifBlank { "Video" },
            style = MaterialTheme.appTypography.interRegular14px,
            color = if (isSelected) MaterialTheme.appColors.primary else Color.White,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun VideoActionBar(
    video: Video,
    setting: PhotoSetting,
    handleIntent: (VideoPlayerIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (setting.isAddFavouriteEnabled) {
            VideoActionButton(
                icon = if (video.isFavourite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                label = "Bookmark",
                tint = if (video.isFavourite) MaterialTheme.appColors.primary else Color.White,
                onClick = { handleIntent(VideoPlayerIntent.OnBookmarkClicked(video.id, video.isFavourite)) },
            )
        }
        if (setting.isLikeEnabled) {
            VideoActionButton(
                icon = if (video.isLike) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                label = if (video.likes > 0) "Like (${video.likes})" else "Like",
                tint = if (video.isLike) Color.Red else Color.White,
                onClick = { handleIntent(VideoPlayerIntent.OnLikeClicked(video.id, video.isLike)) },
            )
        }
        if (setting.isShareEnabled) {
            VideoActionButton(
                icon = Icons.Default.Share,
                label = "Share",
                tint = Color.White,
                onClick = { handleIntent(VideoPlayerIntent.OnShareClicked(video.url)) },
            )
        }
    }
}

@Composable
private fun VideoActionButton(
    icon: ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = onClick, modifier = Modifier.size(40.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(24.dp),
            )
        }
        Text(
            text = label,
            style = MaterialTheme.appTypography.interRegular12px,
            color = Color.White,
            textAlign = TextAlign.Center,
        )
    }
}

private fun extractYouTubeVideoId(url: String): String? {
    return Regex("/embed/([\\w-]+)").find(url)?.groupValues?.getOrNull(1)
}

private fun extractYouTubeThumbnail(url: String): String? {
    val videoId = extractYouTubeVideoId(url) ?: return null
    return "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
}

private fun shareVideo(context: Context, videoUrl: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, videoUrl)
    }
    context.startActivity(Intent.createChooser(intent, "Share Video"))
}

@Preview(showBackground = true, name = "Light")
@Composable
private fun VideoPlayerScreenPreviewLight() {
    EcareProTheme {
        VideoPlayerContent(
            albumTitle = "Video Album",
            uiState = UiState.Success(
                VideoPlayerUiState(
                    videos = listOf(Video("1", "Video One", "https://www.youtube.com/embed/dQw4w9WgXcQ", 10, true, false)),
                    setting = PhotoSetting(isLikeEnabled = true, isShareEnabled = true, isAddFavouriteEnabled = true),
                    currentIndex = 0,
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
private fun VideoPlayerScreenPreviewDark() {
    EcareProTheme {
        VideoPlayerContent(
            albumTitle = "Video Album",
            uiState = UiState.Success(
                VideoPlayerUiState(
                    videos = listOf(Video("1", "Video One", "https://www.youtube.com/embed/dQw4w9WgXcQ", 10, true, false)),
                    setting = PhotoSetting(isLikeEnabled = true, isShareEnabled = true, isAddFavouriteEnabled = true),
                    currentIndex = 0,
                )
            ),
            handleIntent = {},
            snackbarHostState = SnackbarHostState(),
            snackbarMessage = null,
            onSnackbarDismissed = {},
        )
    }
}

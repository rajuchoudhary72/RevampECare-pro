package com.app.ecarepro.feature.gallery.favorites.viewer

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.core.domain.model.PhotoSetting
import androidx.compose.ui.tooling.preview.Preview
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun FavoriteViewerScreen(
    viewModel: FavoriteViewerViewModel,
    navigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is FavoriteViewerEvent.NavigateBack -> navigateBack()
                is FavoriteViewerEvent.ShareContent -> shareContent(context, event.url)
                is FavoriteViewerEvent.ShowMessage -> {
                    snackbarMessage = event.snackbarMessage
                    snackbarHostState.showSnackbar(event.snackbarMessage.text)
                }
            }
        }
    }

    FavoriteViewerContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteViewerContent(
    uiState: UiState<FavoriteViewerUiState>,
    handleIntent: (FavoriteViewerIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit,
) {
    val data = (uiState as? UiState.Success)?.data ?: return

    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = "",
                onNavigationClicked = { handleIntent(FavoriteViewerIntent.OnBackClicked) },
            )
        },
        containerColor = Color.Black,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = onSnackbarDismissed,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color.Black),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                if (data.galleryType == 2) {
                    val videoId = extractYouTubeVideoId(data.fileName)
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
                        Text(
                            text = "Unable to load video",
                            style = MaterialTheme.appTypography.interRegular14px,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                        )
                    }
                } else {
                    EcareProAsyncImage(
                        imageUrl = data.fileName,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }

            FavoriteViewerActionBar(
                data = data,
                handleIntent = handleIntent,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.85f))
                    .padding(vertical = 12.dp),
            )
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
private fun FavoriteViewerActionBar(
    data: FavoriteViewerUiState,
    handleIntent: (FavoriteViewerIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (data.setting.isAddFavouriteEnabled) {
            ViewerActionButton(
                icon = Icons.Default.Bookmark,
                label = "Bookmarked",
                tint = MaterialTheme.appColors.primary,
                onClick = { handleIntent(FavoriteViewerIntent.OnUnbookmarkClicked) },
            )
        }
        if (data.setting.isLikeEnabled) {
            ViewerActionButton(
                icon = if (data.isLike) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                label = if (data.totalLike > 0) "Like (${data.totalLike})" else "Like",
                tint = if (data.isLike) Color.Red else Color.White,
                onClick = { handleIntent(FavoriteViewerIntent.OnLikeClicked) },
            )
        }
        if (data.setting.isShareEnabled) {
            ViewerActionButton(
                icon = Icons.Default.Share,
                label = "Share",
                tint = Color.White,
                onClick = { handleIntent(FavoriteViewerIntent.OnShareClicked) },
            )
        }
    }
}

@Composable
private fun ViewerActionButton(
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

private fun shareContent(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, url)
    }
    context.startActivity(Intent.createChooser(intent, "Share"))
}

@Preview(showBackground = true, name = "Light")
@Composable
private fun FavoriteViewerScreenPreviewLight() {
    EcareProTheme {
        FavoriteViewerContent(
            uiState = UiState.Success(
                FavoriteViewerUiState(
                    id = "1",
                    galleryType = 1,
                    fileName = "https://picsum.photos/600/400",
                    totalLike = 5,
                    isLike = true,
                    isFavourite = true,
                    setting = PhotoSetting(isLikeEnabled = true, isShareEnabled = true, isAddFavouriteEnabled = true),
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
private fun FavoriteViewerScreenPreviewDark() {
    EcareProTheme {
        FavoriteViewerContent(
            uiState = UiState.Success(
                FavoriteViewerUiState(
                    id = "1",
                    galleryType = 1,
                    fileName = "https://picsum.photos/600/400",
                    totalLike = 5,
                    isLike = true,
                    isFavourite = true,
                    setting = PhotoSetting(isLikeEnabled = true, isShareEnabled = true, isAddFavouriteEnabled = true),
                )
            ),
            handleIntent = {},
            snackbarHostState = SnackbarHostState(),
            snackbarMessage = null,
            onSnackbarDismissed = {},
        )
    }
}

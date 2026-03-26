package com.app.ecarepro.feature.gallery.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.FavoriteItem
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.core.domain.model.PhotoSetting
import com.app.ecarepro.feature.gallery.navigation.GalleryNavGraph
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun FavoritesScreen(
    navigateBack: () -> Unit,
    navigateToViewer: (GalleryNavGraph.FavoriteViewer) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is FavoritesEvent.NavigateBack -> navigateBack()
                is FavoritesEvent.NavigateToViewer -> navigateToViewer(event.navKey)
                is FavoritesEvent.ShowMessage -> {
                    snackbarMessage = event.snackbarMessage
                    snackbarHostState.showSnackbar(event.snackbarMessage.text)
                }
            }
        }
    }

    FavoritesContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoritesContent(
    uiState: UiState<FavoritesUiState>,
    handleIntent: (FavoritesIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit,
) {
    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = "Your bookmarks",
                onNavigationClicked = { handleIntent(FavoritesIntent.OnBackClicked) },
            )
        },
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = onSnackbarDismissed,
    ) { paddingValues ->
        UiStateHandler(
            modifier = Modifier.padding(paddingValues),
            state = uiState,
        ) { data ->
            val isRefreshing = uiState is UiState.Loading

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { handleIntent(FavoritesIntent.OnRefresh) },
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
            ) {
                if (data.items.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "No bookmarks yet",
                            style = MaterialTheme.appTypography.interRegular14px,
                            color = MaterialTheme.appColors.textSecondary,
                            textAlign = TextAlign.Center,
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        items(data.items, key = { "${it.galleryType}_${it.id}" }) { item ->
                            FavoriteGridItem(
                                item = item,
                                onClick = { handleIntent(FavoritesIntent.OnItemClicked(item)) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteGridItem(
    item: FavoriteItem,
    onClick: () -> Unit,
) {
    val thumbnailUrl = if (item.galleryType == 2) {
        extractYouTubeThumbnail(item.fileName)
    } else {
        item.fileName
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(0.dp))
            .background(MaterialTheme.appColors.surface)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (thumbnailUrl != null) {
            EcareProAsyncImage(
                imageUrl = thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }

        if (item.galleryType == 2) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f)),
            )
            Icon(
                imageVector = Icons.Default.PlayCircle,
                contentDescription = "Video",
                tint = Color.White.copy(alpha = 0.9f),
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.Center),
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
private fun FavoritesScreenPreviewLight() {
    EcareProTheme {
        FavoritesContent(
            uiState = UiState.Success(
                FavoritesUiState(
                    items = listOf(
                        FavoriteItem("1", 1, "https://picsum.photos/300", 5, true, true),
                        FavoriteItem("2", 2, "https://www.youtube.com/embed/abc", 0, false, true),
                        FavoriteItem("3", 1, "https://picsum.photos/301", 2, false, true),
                    ),
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
private fun FavoritesScreenPreviewDark() {
    EcareProTheme {
        FavoritesContent(
            uiState = UiState.Success(
                FavoritesUiState(
                    items = listOf(
                        FavoriteItem("1", 1, "https://picsum.photos/300", 5, true, true),
                        FavoriteItem("2", 2, "https://www.youtube.com/embed/abc", 0, false, true),
                        FavoriteItem("3", 1, "https://picsum.photos/301", 2, false, true),
                    ),
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

package com.app.ecarepro.feature.gallery.photo.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
import com.app.ecarepro.core.domain.model.Photo
import com.app.ecarepro.core.domain.model.PhotoSetting
import com.app.ecarepro.core.domain.model.AlbumDetail
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
fun PhotoDetailScreen(
    viewModel: PhotoDetailViewModel,
    navigateBack: () -> Unit,
    navigateToSlider: (Int) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is PhotoDetailEvent.NavigateBack -> navigateBack()
                is PhotoDetailEvent.NavigateToSlider -> navigateToSlider(event.initialPhotoIndex)
                is PhotoDetailEvent.ShowMessage -> {
                    snackbarMessage = event.snackbarMessage
                    snackbarHostState.showSnackbar(event.snackbarMessage.text)
                }
            }
        }
    }

    PhotoDetailContent(
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
private fun PhotoDetailContent(
    albumTitle: String,
    uiState: UiState<PhotoDetailUiState>,
    handleIntent: (PhotoDetailIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit,
) {
    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = albumTitle,
                onNavigationClicked = { handleIntent(PhotoDetailIntent.OnBackClicked) }
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
                onRefresh = { handleIntent(PhotoDetailIntent.OnRefresh) }
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
                                onToggle = { handleIntent(PhotoDetailIntent.OnToggleDescription) }
                            )
                        }
                    }
                    itemsIndexed(data.albumDetail.photos) { index, photo ->
                        EcareProAsyncImage(
                            imageUrl = photo.photoPath,
                            contentDescription = photo.title,
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(4.dp))
                                .clickable { handleIntent(PhotoDetailIntent.OnPhotoClicked(index)) },
                        )
                    }
                }
            }
        }
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

@Preview(showBackground = true, name = "Light")
@Composable
private fun PhotoDetailScreenPreviewLight() {
    EcareProTheme {
        PhotoDetailContent(
            albumTitle = "Album One",
            uiState = UiState.Success(
                PhotoDetailUiState(
                    albumDetail = AlbumDetail(
                        title = "Album One",
                        description = "",
                        eventDate = "",
                        totalPhotos = 1,
                        setting = PhotoSetting(isLikeEnabled = true, isShareEnabled = true, isAddFavouriteEnabled = true),
                        photos = listOf(Photo("1", "", null, "", 0, false, false)),
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
private fun PhotoDetailScreenPreviewDark() {
    EcareProTheme {
        PhotoDetailContent(
            albumTitle = "Album One",
            uiState = UiState.Success(
                PhotoDetailUiState(
                    albumDetail = AlbumDetail(
                        title = "Album One",
                        description = "",
                        eventDate = "",
                        totalPhotos = 1,
                        setting = PhotoSetting(isLikeEnabled = true, isShareEnabled = true, isAddFavouriteEnabled = true),
                        photos = listOf(Photo("1", "", null, "", 0, false, false)),
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

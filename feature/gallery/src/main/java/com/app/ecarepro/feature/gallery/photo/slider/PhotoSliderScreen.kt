package com.app.ecarepro.feature.gallery.photo.slider

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.Photo
import com.app.ecarepro.core.domain.model.PhotoSetting
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun PhotoSliderScreen(
    viewModel: PhotoSliderViewModel,
    navigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is PhotoSliderEvent.NavigateBack -> navigateBack()
                is PhotoSliderEvent.ShareImage -> shareImage(context, event.photoUrl)
                is PhotoSliderEvent.ShowMessage -> {
                    snackbarMessage = event.snackbarMessage
                    snackbarHostState.showSnackbar(event.snackbarMessage.text)
                }
            }
        }
    }

    PhotoSliderContent(
        albumTitle = viewModel.navKey.albumTitle,
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null },
    )
}
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

@Composable
private fun PhotoSliderContent(
    albumTitle: String,
    uiState: UiState<PhotoSliderUiState>,
    handleIntent: (PhotoSliderIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit,
) {
    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = albumTitle,
                onNavigationClicked = { handleIntent(PhotoSliderIntent.OnBackClicked) },
                actions = {
                    if (uiState is UiState.Success) {
                        IconButton(onClick = { handleIntent(PhotoSliderIntent.OnToggleLayout) }) {
                            Icon(
                                imageVector = if (uiState.data.isVerticalLayout)
                                    Icons.Default.GridView
                                else
                                    Icons.AutoMirrored.Filled.List,
                                contentDescription = "Toggle layout",
                                tint = MaterialTheme.appColors.textPrimary,
                            )
                        }
                    }
                }
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
            if (data.isVerticalLayout) {
                VerticalPhotoList(
                    photos = data.photos,
                    setting = data.setting,
                    paddingValues = paddingValues,
                    handleIntent = handleIntent,
                )
            } else {
                HorizontalPhotoSlider(
                    photos = data.photos,
                    setting = data.setting,
                    initialIndex = data.currentIndex,
                    paddingValues = paddingValues,
                    handleIntent = handleIntent,
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HorizontalPhotoSlider(
    photos: List<Photo>,
    setting: PhotoSetting,
    initialIndex: Int,
    paddingValues: PaddingValues,
    handleIntent: (PhotoSliderIntent) -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = initialIndex) { photos.size }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            handleIntent(PhotoSliderIntent.OnPageChanged(page))
        }
    }

    val currentPhoto = photos.getOrNull(pagerState.currentPage)

    Box(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .background(Color.Black)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            val photo = photos[page]
            EcareProAsyncImage(
                imageUrl = photo.photoPath,
                contentDescription = photo.title,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize(),
            )
        }

        currentPhoto?.let { photo ->
            PhotoActionBar(
                photo = photo,
                setting = setting,
                handleIntent = handleIntent,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .navigationBarsPadding()
                    .padding(vertical = 8.dp),
            )
        }
    }
}

@Composable
private fun VerticalPhotoList(
    photos: List<Photo>,
    setting: PhotoSetting,
    paddingValues: PaddingValues,
    handleIntent: (PhotoSliderIntent) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .background(Color.Black),
    ) {
        itemsIndexed(photos) { _, photo ->
            Column(modifier = Modifier.fillMaxWidth()) {
                EcareProAsyncImage(
                    imageUrl = photo.photoPath,
                    contentDescription = photo.title,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                )
                PhotoActionBar(
                    photo = photo,
                    setting = setting,
                    handleIntent = handleIntent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.8f))
                        .padding(vertical = 4.dp),
                )
                HorizontalDivider(color = Color.DarkGray, thickness = 0.5.dp)
            }
        }
    }
}

@Composable
private fun PhotoActionBar(
    photo: Photo,
    setting: PhotoSetting,
    handleIntent: (PhotoSliderIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (setting.isAddFavouriteEnabled) {
            ActionButton(
                icon = if (photo.isFavourite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                label = "Bookmark",
                tint = if (photo.isFavourite) MaterialTheme.appColors.primary else Color.White,
                onClick = { handleIntent(PhotoSliderIntent.OnBookmarkClicked(photo.id, photo.isFavourite)) },
            )
        }
        if (setting.isLikeEnabled) {
            ActionButton(
                icon = if (photo.isLike) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                label = if (photo.likes > 0) "Like (${photo.likes})" else "Like",
                tint = if (photo.isLike) Color.Red else Color.White,
                onClick = { handleIntent(PhotoSliderIntent.OnLikeClicked(photo.id, photo.isLike)) },
            )
        }
        if (setting.isShareEnabled) {
            ActionButton(
                icon = Icons.Default.Share,
                label = "Share",
                tint = Color.White,
                onClick = { handleIntent(PhotoSliderIntent.OnShareClicked(photo.photoPath)) },
            )
        }
    }
}

@Composable
private fun ActionButton(
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

private fun shareImage(context: Context, photoUrl: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, photoUrl)
    }
    context.startActivity(Intent.createChooser(intent, "Share Image"))
}

@Preview(showBackground = true, name = "Light")
@Composable
private fun PhotoSliderScreenPreviewLight() {
    EcareProTheme {
        PhotoSliderContent(
            albumTitle = "Photo Album",
            uiState = UiState.Success(
                PhotoSliderUiState(
                    photos = listOf(Photo("1", "Photo One", null, "https://picsum.photos/400", 5, true, false)),
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
private fun PhotoSliderScreenPreviewDark() {
    EcareProTheme {
        PhotoSliderContent(
            albumTitle = "Photo Album",
            uiState = UiState.Success(
                PhotoSliderUiState(
                    photos = listOf(Photo("1", "Photo One", null, "https://picsum.photos/400", 5, true, false)),
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

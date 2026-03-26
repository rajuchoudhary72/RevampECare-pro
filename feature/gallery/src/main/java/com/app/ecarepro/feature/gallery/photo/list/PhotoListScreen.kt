package com.app.ecarepro.feature.gallery.photo.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.Album
import com.app.ecarepro.core.domain.model.AlbumType
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
import com.app.ecarepro.designsystem.core.component.BottomSearchBarView
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.component.EcareProEmptyState
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PhotoListScreen(
    viewModel: PhotoListViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToDetail: (String, String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is PhotoListEvent.NavigateBack -> navigateBack()
                is PhotoListEvent.NavigateToDetail -> navigateToDetail(event.albumId, event.albumTitle)
                is PhotoListEvent.ShowMessage -> {
                    snackbarMessage = event.snackbarMessage
                    snackbarHostState.showSnackbar(event.snackbarMessage.text)
                }
            }
        }
    }

    PhotoListContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhotoListContent(
    uiState: UiState<PhotoListUiState>,
    handleIntent: (PhotoListIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit,
) {
    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = "Photo Album",
                onNavigationClicked = { handleIntent(PhotoListIntent.OnBackClicked) }
            )
        },
        containerColor = White,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = onSnackbarDismissed,
        bottomBar = {
            BottomSearchBarView(
                searchText = if (uiState is UiState.Success) uiState.data.searchQuery else "",
                onSearchTextChange = { handleIntent(PhotoListIntent.OnSearchQueryChanged(it)) },
                placeholder = "Search by album name",
            )
        }
    ) { paddingValues ->
        UiStateHandler(
            modifier = Modifier.padding(paddingValues),
            state = uiState
        ) { data ->
            PullToRefreshBox(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                isRefreshing = data.isRefreshing,
                onRefresh = { handleIntent(PhotoListIntent.OnRefresh) }
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    if (data.albumTypes.isNotEmpty()) {
                        AlbumTypeChips(
                            albumTypes = data.albumTypes,
                            selectedType = data.selectedAlbumType,
                            onTypeSelected = { handleIntent(PhotoListIntent.OnAlbumTypeSelected(it)) }
                        )
                    }
                    if (data.filteredAlbums.isEmpty()) {
                        EcareProEmptyState(message = "No albums found")
                    } else {
                        AlbumGrid(
                            albums = data.filteredAlbums,
                            onAlbumClicked = { album ->
                                handleIntent(PhotoListIntent.OnAlbumClicked(album.id, album.title))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AlbumTypeChips(
    albumTypes: List<AlbumType>,
    selectedType: AlbumType,
    onTypeSelected: (AlbumType) -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(albumTypes) { type ->
            FilterChip(
                selected = type.typeID == selectedType.typeID,
                onClick = { onTypeSelected(type) },
                label = {
                    Text(
                        text = type.typeName,
                        style = MaterialTheme.appTypography.interRegular12px,
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.appColors.primary,
                    selectedLabelColor = White,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = type.typeID == selectedType.typeID,
                    selectedBorderColor = MaterialTheme.appColors.primary,
                ),
            )
        }
    }
}

@Composable
private fun AlbumGrid(
    albums: List<Album>,
    onAlbumClicked: (Album) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(albums) { album ->
            AlbumCard(album = album, onClick = { onAlbumClicked(album) })
        }
    }
}

@Composable
private fun AlbumCard(
    album: Album,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        EcareProAsyncImage(
            imageUrl = album.fileName,
            contentDescription = album.title,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.appColors.divider),
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = album.title,
            style = MaterialTheme.appTypography.interSemiBold14px,
            color = MaterialTheme.appColors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 2.dp),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = album.eventDate,
                style = MaterialTheme.appTypography.interRegular12px,
                color = MaterialTheme.appColors.textSecondary,
            )
            Text(
                text = "${album.totalPhotos} items",
                style = MaterialTheme.appTypography.interRegular12px,
                color = MaterialTheme.appColors.textSecondary,
            )
        }
        Spacer(Modifier.height(4.dp))
    }
}

@Preview(showBackground = true, name = "Light")
@Composable
private fun PhotoListScreenPreviewLight() {
    EcareProTheme {
        PhotoListContent(
            uiState = UiState.Success(
                PhotoListUiState(
                    filteredAlbums = listOf(Album("1", "Album One", "", 12, "", "")),
                    albumTypes = listOf(AlbumType(0, "All"), AlbumType(1, "Sports")),
                    selectedAlbumType = AlbumType(0, "All"),
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
private fun PhotoListScreenPreviewDark() {
    EcareProTheme {
        PhotoListContent(
            uiState = UiState.Success(
                PhotoListUiState(
                    filteredAlbums = listOf(Album("1", "Album One", "", 12, "", "")),
                    albumTypes = listOf(AlbumType(0, "All"), AlbumType(1, "Sports")),
                    selectedAlbumType = AlbumType(0, "All"),
                )
            ),
            handleIntent = {},
            snackbarHostState = SnackbarHostState(),
            snackbarMessage = null,
            onSnackbarDismissed = {},
        )
    }
}

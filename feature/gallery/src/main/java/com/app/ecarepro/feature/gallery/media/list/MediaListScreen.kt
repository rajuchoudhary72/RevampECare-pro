package com.app.ecarepro.feature.gallery.media.list

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.MediaItem
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch

private val queryTypeLabels = listOf("All search", "Newspaper", "Headline", "Publish date")

@Composable
fun MediaListScreen(
    navigateBack: () -> Unit,
    navigateToDetail: (MediaItem) -> Unit,
    viewModel: MediaListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is MediaListEvent.NavigateBack -> navigateBack()
                is MediaListEvent.NavigateToDetail -> navigateToDetail(event.item)
            }
        }
    }

    MediaListContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MediaListContent(
    uiState: UiState<MediaListUiState>,
    handleIntent: (MediaListIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = "Media gallery",
                onNavigationClicked = { handleIntent(MediaListIntent.OnBackClicked) },
            )
        },
        snackbarHostState = snackbarHostState,
        bottomBar = {
            val data = (uiState as? UiState.Success)?.data
            MediaSearchBar(
                searchText = data?.searchText.orEmpty(),
                queryTypeLabel = queryTypeLabels.getOrElse(data?.selectedQueryType ?: 0) { "All search" },
                onSearchTextChanged = { handleIntent(MediaListIntent.OnSearchTextChanged(it)) },
                onFilterClicked = { showFilterSheet = true },
            )
        },
    ) { paddingValues ->
        UiStateHandler(
            modifier = Modifier.padding(paddingValues),
            state = uiState,
        ) { data ->
            val isRefreshing = uiState is UiState.Loading
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { handleIntent(MediaListIntent.OnRefresh) },
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
                ) {
                    items(data.filteredItems, key = { it.id }) { item ->
                        MediaGridItem(
                            item = item,
                            onClick = { handleIntent(MediaListIntent.OnItemClicked(item)) },
                        )
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        val data = (uiState as? UiState.Success)?.data
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = sheetState,
        ) {
            FilterBottomSheet(
                years = data?.years ?: emptyList(),
                selectedQueryType = data?.selectedQueryType ?: 0,
                selectedYear = data?.selectedYear ?: 0,
                onQueryTypeSelected = { queryType ->
                    handleIntent(MediaListIntent.OnQueryTypeSelected(queryType))
                    scope.launch {
                        sheetState.hide()
                        showFilterSheet = false
                    }
                },
                onYearSelected = { year ->
                    handleIntent(MediaListIntent.OnYearSelected(year))
                    scope.launch {
                        sheetState.hide()
                        showFilterSheet = false
                    }
                },
                onDismiss = {
                    scope.launch {
                        sheetState.hide()
                        showFilterSheet = false
                    }
                },
            )
        }
    }
}

@Composable
private fun MediaGridItem(
    item: MediaItem,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.appColors.surface),
        ) {
            if (item.thumbnailUrl.isNotBlank() && isImageUrl(item.thumbnailUrl)) {
                EcareProAsyncImage(
                    imageUrl = item.thumbnailUrl,
                    contentDescription = item.headline,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            if (item.newsName.isNotBlank()) {
                Text(
                    text = item.newsName,
                    style = MaterialTheme.appTypography.interRegular10px,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                        .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = item.headline,
            style = MaterialTheme.appTypography.interMedium12px,
            color = MaterialTheme.appColors.textPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        if (item.publishedOn.isNotBlank()) {
            Text(
                text = item.publishedOn,
                style = MaterialTheme.appTypography.interRegular12px,
                color = MaterialTheme.appColors.textSecondary,
            )
        }
        Spacer(Modifier.height(4.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MediaSearchBar(
    searchText: String,
    queryTypeLabel: String,
    onSearchTextChanged: (String) -> Unit,
    onFilterClicked: () -> Unit,
) {
    Surface(
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextField(
                value = searchText,
                onValueChange = onSearchTextChanged,
                placeholder = {
                    Text(
                        text = "Search by title",
                        style = MaterialTheme.appTypography.interRegular14px,
                        color = MaterialTheme.appColors.textSecondary,
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.appColors.textSecondary,
                        modifier = Modifier.size(20.dp),
                    )
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.appColors.surface,
                    unfocusedContainerColor = MaterialTheme.appColors.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
            )
            Spacer(Modifier.width(8.dp))
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.appColors.surface)
                    .clickable(onClick = onFilterClicked)
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = queryTypeLabel,
                    style = MaterialTheme.appTypography.interRegular12px,
                    color = MaterialTheme.appColors.textPrimary,
                )
                Spacer(Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.appColors.textPrimary,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
private fun FilterBottomSheet(
    years: List<String>,
    selectedQueryType: Int,
    selectedYear: Int,
    onQueryTypeSelected: (Int) -> Unit,
    onYearSelected: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Select search",
                style = MaterialTheme.appTypography.interSemiBold16px,
                color = MaterialTheme.appColors.textPrimary,
            )
            Text(
                text = "✕",
                style = MaterialTheme.appTypography.interRegular16px,
                color = MaterialTheme.appColors.textSecondary,
                modifier = Modifier.clickable(onClick = onDismiss),
            )
        }
        HorizontalDivider()

        // Query type options
        queryTypeLabels.forEachIndexed { index, label ->
            FilterRadioItem(
                label = label,
                selected = selectedQueryType == index,
                onClick = { onQueryTypeSelected(index) },
            )
        }

        // Year options
        if (years.isNotEmpty()) {
            FilterRadioItem(
                label = "Year",
                selected = selectedYear != 0,
                onClick = { /* expand year sub-list */ },
            )
            if (selectedYear != 0 || years.isNotEmpty()) {
                years.forEach { year ->
                    val yearInt = year.toIntOrNull() ?: 0
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onYearSelected(yearInt) }
                            .padding(start = 48.dp, end = 20.dp, top = 4.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = selectedYear == yearInt,
                            onClick = { onYearSelected(yearInt) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.appColors.primary,
                            ),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = year,
                            style = MaterialTheme.appTypography.interRegular14px,
                            color = MaterialTheme.appColors.textPrimary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterRadioItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.appColors.primary,
            ),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.appTypography.interRegular14px,
            color = if (selected) MaterialTheme.appColors.primary else MaterialTheme.appColors.textPrimary,
        )
    }
}

private fun isImageUrl(url: String): Boolean {
    val lower = url.lowercase()
    return lower.endsWith(".jpg") || lower.endsWith(".jpeg") ||
        lower.endsWith(".png") || lower.endsWith(".webp") ||
        lower.endsWith(".gif")
}

@Preview(showBackground = true, name = "Light")
@Composable
private fun MediaListScreenPreviewLight() {
    EcareProTheme {
        MediaListContent(
            uiState = UiState.Success(
                MediaListUiState(
                    filteredItems = listOf(
                        MediaItem(1, "Hindustan Times", "Media Headline A", "10-Jan-2025", "12-Jan-2025", "https://picsum.photos/300", "", "<p>Description</p>"),
                        MediaItem(2, "Times of India", "Annual Day", "08-Mar-2025", "09-Mar-2025", "https://picsum.photos/301", "", "<p>Desc</p>"),
                    ),
                    years = listOf("2025", "2024", "2023"),
                )
            ),
            handleIntent = {},
            snackbarHostState = SnackbarHostState(),
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, name = "Dark")
@Composable
private fun MediaListScreenPreviewDark() {
    EcareProTheme {
        MediaListContent(
            uiState = UiState.Success(
                MediaListUiState(
                    filteredItems = listOf(
                        MediaItem(1, "Hindustan Times", "Media Headline A", "10-Jan-2025", "12-Jan-2025", "https://picsum.photos/300", "", "<p>Description</p>"),
                        MediaItem(2, "Times of India", "Annual Day", "08-Mar-2025", "09-Mar-2025", "https://picsum.photos/301", "", "<p>Desc</p>"),
                    ),
                    years = listOf("2025", "2024", "2023"),
                )
            ),
            handleIntent = {},
            snackbarHostState = SnackbarHostState(),
        )
    }
}

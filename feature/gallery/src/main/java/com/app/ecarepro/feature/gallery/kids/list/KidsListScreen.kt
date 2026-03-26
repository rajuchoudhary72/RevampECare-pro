package com.app.ecarepro.feature.gallery.kids.list

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
import com.app.ecarepro.core.domain.model.KidsAcademicYear
import com.app.ecarepro.core.domain.model.KidsAlbum
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

@Composable
fun KidsListScreen(
    navigateBack: () -> Unit,
    navigateToDetail: (String, String) -> Unit,
    viewModel: KidsListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is KidsListEvent.NavigateBack -> navigateBack()
                is KidsListEvent.NavigateToDetail -> navigateToDetail(event.kid, event.title)
            }
        }
    }

    KidsListContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KidsListContent(
    uiState: UiState<KidsListUiState>,
    handleIntent: (KidsListIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    var showYearSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = "Kids corner",
                onNavigationClicked = { handleIntent(KidsListIntent.OnBackClicked) },
            )
        },
        snackbarHostState = snackbarHostState,
        bottomBar = {
            val data = (uiState as? UiState.Success)?.data
            KidsSearchBar(
                searchQuery = data?.searchQuery.orEmpty(),
                selectedYearLabel = data?.selectedYear?.session ?: "All years",
                onSearchQueryChanged = { handleIntent(KidsListIntent.OnSearchQueryChanged(it)) },
                onYearClicked = { showYearSheet = true },
            )
        },
    ) { paddingValues ->
        UiStateHandler(
            modifier = Modifier.padding(paddingValues),
            state = uiState,
        ) { data ->
            PullToRefreshBox(
                isRefreshing = data.isRefreshing,
                onRefresh = { handleIntent(KidsListIntent.OnRefresh) },
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
            ) {
                if (data.albums.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "No albums found",
                            style = MaterialTheme.appTypography.interRegular14px,
                            color = MaterialTheme.appColors.textSecondary,
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
                    ) {
                        items(data.albums, key = { it.kid }) { album ->
                            KidsAlbumCard(
                                album = album,
                                onClick = { handleIntent(KidsListIntent.OnAlbumClicked(album.kid, album.title)) },
                            )
                        }
                    }
                }
            }
        }
    }

    if (showYearSheet) {
        val data = (uiState as? UiState.Success)?.data
        ModalBottomSheet(
            onDismissRequest = { showYearSheet = false },
            sheetState = sheetState,
        ) {
            YearPickerSheet(
                years = data?.academicYears ?: emptyList(),
                selectedYear = data?.selectedYear,
                onYearSelected = { year ->
                    handleIntent(KidsListIntent.OnYearSelected(year))
                    scope.launch {
                        sheetState.hide()
                        showYearSheet = false
                    }
                },
                onDismiss = {
                    scope.launch {
                        sheetState.hide()
                        showYearSheet = false
                    }
                },
            )
        }
    }
}

@Composable
private fun KidsAlbumCard(
    album: KidsAlbum,
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
            contentAlignment = Alignment.Center,
        ) {
            if (album.albumIcon.isNotBlank()) {
                EcareProAsyncImage(
                    imageUrl = album.albumIcon,
                    contentDescription = album.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = album.title,
            style = MaterialTheme.appTypography.interMedium14px,
            color = MaterialTheme.appColors.textPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        if (album.createdOn.isNotBlank() || album.totalPhoto > 0) {
            Spacer(Modifier.height(2.dp))
            val meta = buildString {
                if (album.createdOn.isNotBlank()) append(album.createdOn)
                if (album.totalPhoto > 0) {
                    if (isNotEmpty()) append(" · ")
                    append("${album.totalPhoto} memories")
                }
            }
            Text(
                text = meta,
                style = MaterialTheme.appTypography.interRegular12px,
                color = MaterialTheme.appColors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.height(4.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KidsSearchBar(
    searchQuery: String,
    selectedYearLabel: String,
    onSearchQueryChanged: (String) -> Unit,
    onYearClicked: () -> Unit,
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
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
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
                    .clickable(onClick = onYearClicked)
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = selectedYearLabel,
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
private fun YearPickerSheet(
    years: List<KidsAcademicYear>,
    selectedYear: KidsAcademicYear?,
    onYearSelected: (KidsAcademicYear) -> Unit,
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
                text = "Select year",
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
        years.forEach { year ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onYearSelected(year) }
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = selectedYear?.yrID == year.yrID,
                    onClick = { onYearSelected(year) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.appColors.primary,
                    ),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = year.session,
                    style = MaterialTheme.appTypography.interRegular14px,
                    color = if (selectedYear?.yrID == year.yrID) MaterialTheme.appColors.primary
                    else MaterialTheme.appColors.textPrimary,
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Light")
@Composable
private fun KidsListScreenPreviewLight() {
    EcareProTheme {
        KidsListContent(
            uiState = UiState.Success(
                KidsListUiState(
                    albums = listOf(
                        KidsAlbum("kid1", "Kids Corner Media Headlines", "", "https://picsum.photos/300", "2025-2026", 17, "08 Aug", "08 Aug"),
                        KidsAlbum("kid2", "Annual Kids Day", "", "https://picsum.photos/301", "2025-2026", 17, "08 Aug", "08 Aug"),
                    ),
                    academicYears = listOf(KidsAcademicYear(9, "2025-2026", false), KidsAcademicYear(8, "2024-2025", false)),
                    selectedYear = KidsAcademicYear(9, "2025-2026", false),
                )
            ),
            handleIntent = {},
            snackbarHostState = SnackbarHostState(),
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, name = "Dark")
@Composable
private fun KidsListScreenPreviewDark() {
    EcareProTheme {
        KidsListContent(
            uiState = UiState.Success(
                KidsListUiState(
                    albums = listOf(
                        KidsAlbum("kid1", "Kids Corner Media Headlines", "", "https://picsum.photos/300", "2025-2026", 17, "08 Aug", "08 Aug"),
                        KidsAlbum("kid2", "Annual Kids Day", "", "https://picsum.photos/301", "2025-2026", 17, "08 Aug", "08 Aug"),
                    ),
                    academicYears = listOf(KidsAcademicYear(9, "2025-2026", false), KidsAcademicYear(8, "2024-2025", false)),
                    selectedYear = KidsAcademicYear(9, "2025-2026", false),
                )
            ),
            handleIntent = {},
            snackbarHostState = SnackbarHostState(),
        )
    }
}

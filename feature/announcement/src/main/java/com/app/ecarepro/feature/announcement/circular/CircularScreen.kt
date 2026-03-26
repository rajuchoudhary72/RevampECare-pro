package com.app.ecarepro.feature.announcement.circular

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.AcademicYear
import com.app.ecarepro.core.domain.model.Circular
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
import com.app.ecarepro.designsystem.core.component.BottomSearchBarView
import com.app.ecarepro.designsystem.core.component.EcareProEmptyState
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProSelectionBottomSheet
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.announcement.common.CircularItem
import com.app.ecarepro.feature.announcement.common.DropdownSelectorField
import com.app.ecarepro.feature.announcement.common.NoticeFilterBottomSheet

@Composable
fun CircularScreen(
    viewModel: CircularViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToDetail: (String) -> Unit,
    navigateToCreate: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is CircularEvent.NavigateBack -> navigateBack()
                is CircularEvent.NavigateToDetail -> navigateToDetail(event.circularId)
                is CircularEvent.ShowMessage -> {
                    snackbarMessage = event.snackbarMessage
                    snackbarHostState.showSnackbar(event.snackbarMessage.text)
                }
            }
        }
    }

    CircularScreenContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null },
        navigateToCreate = navigateToCreate,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun CircularScreenContent(
    uiState: UiState<CircularUiState>,
    handleIntent: (CircularIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit,
    navigateToCreate: () -> Unit = {},
) {
    EcareProScaffold(
        topBar = {
            Column {
                EcareProTopAppBar(
                    title = "All Circular",
                    onNavigationClicked = { handleIntent(CircularIntent.OnBackClicked) },
                    actions = {
                        IconButton(onClick = navigateToCreate) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Create Circular"
                            )
                        }
                    }
                )
                if (uiState is UiState.Success) {
                    DropdownSelectorField(
                        selectedLabel = uiState.data.selectedYear?.session ?: "Select Year",
                        onClick = { handleIntent(CircularIntent.OnShowYearPicker) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        },
        containerColor = White,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = onSnackbarDismissed,
        bottomBar = {
            BottomSearchBarView(
                searchText = if (uiState is UiState.Success) uiState.data.searchQuery else "",
                onSearchTextChange = { handleIntent(CircularIntent.OnSearchQueryChanged(it)) },
                placeholder = "Search by title",
                rightIconVector = Icons.Default.FilterList,
                onRightIconClick = { handleIntent(CircularIntent.OnShowFilter) }
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
                onRefresh = { handleIntent(CircularIntent.OnRefresh) }
            ) {
                if (data.filteredCirculars.isEmpty()) {
                    EcareProEmptyState(message = "No circulars found")
                } else {
                    CircularGroupedList(
                        circulars = data.filteredCirculars,
                        onCircularClicked = { id -> handleIntent(CircularIntent.OnCircularClicked(id)) }
                    )
                }
            }

            if (data.isFilterVisible) {
                NoticeFilterBottomSheet(
                    selectedFilter = data.selectedFilter,
                    onFilterSelected = { handleIntent(CircularIntent.OnFilterSelected(it)) },
                    onDismiss = { handleIntent(CircularIntent.OnDismissFilter) }
                )
            }

            if (data.isYearPickerVisible) {
                EcareProSelectionBottomSheet(
                    title = "Select academic year",
                    isVisible = true,
                    options = data.academicYears.map { it.session },
                    selectedOptions = listOf(data.selectedYear?.session ?: ""),
                    isMultiSelection = false,
                    onDismiss = { handleIntent(CircularIntent.OnDismissYearPicker) },
                    onOptionsSelected = { selected ->
                        val year = data.academicYears.find { it.session == selected.firstOrNull() }
                        year?.let { handleIntent(CircularIntent.OnYearSelected(it)) }
                    }
                )
            }
        }
    }
}

private val previewAcademicYears = listOf(
    AcademicYear(9, "2025-2026", isCur = true, startDate = "01-Apr-2025", endDate = "31-Mar-2026"),
    AcademicYear(8, "2024-2025", isCur = false, startDate = "01-Apr-2024", endDate = "31-Mar-2025"),
    AcademicYear(7, "2023-2024", isCur = false, startDate = "01-Apr-2023", endDate = "31-Mar-2024"),
)

private val previewCirculars = listOf(
    Circular(332, "Testing new circular", "<p>HOLIDAY</p>", "10-Feb-2026", "10-Feb-2026 17:23 PM", isNew = true, isRead = false, hasAttachment = true, filePath = null, fileSize = null, mustRead = true, id = "9ue6TTKZNYEOWjmngRdrNw==", postedByName = null, postedByPhoto = null, postedByRole = null),
    Circular(331, "Testing circular", "<p>Saturday will be holiday.</p>", "02-Feb-2026", "10-Feb-2026 16:55 PM", isNew = true, isRead = false, hasAttachment = false, filePath = null, fileSize = null, mustRead = true, id = "E/4zSTNBe0TYLLL3jML/Xw==", postedByName = null, postedByPhoto = null, postedByRole = null),
    Circular(330, "WINTER HOLIDAY NOTICE 2025", "<p>WINTER HOLIDAY NOTICE 2025</p>", "22-Dec-2025", "19-Jan-2026 12:31 PM", isNew = false, isRead = true, hasAttachment = true, filePath = null, fileSize = null, mustRead = true, id = "so7GpND5qOhNs0SIZPonGQ==", postedByName = null, postedByPhoto = null, postedByRole = null),
    Circular(329, "Transport Route Timing Changes", "<p>test</p>", "31-Dec-2025", "31-Dec-2025 10:18 AM", isNew = false, isRead = true, hasAttachment = false, filePath = null, fileSize = null, mustRead = false, id = "1R17NAGlliYBQrmMJNujjg==", postedByName = null, postedByPhoto = null, postedByRole = null),
)

@Preview(showBackground = true, name = "Circular List")
@Composable
private fun CircularListPreview() {
    EcareProTheme {
        CircularScreenContent(
            uiState = UiState.Success(
                CircularUiState(
                    allCirculars = previewCirculars,
                    filteredCirculars = previewCirculars,
                    academicYears = previewAcademicYears,
                    selectedYear = previewAcademicYears.first(),
                )
            ),
            handleIntent = {},
            snackbarHostState = remember { SnackbarHostState() },
            snackbarMessage = null,
            onSnackbarDismissed = {},
            navigateToCreate = {},
        )
    }
}

@Preview(showBackground = true, name = "Circular List Empty")
@Composable
private fun CircularListEmptyPreview() {
    EcareProTheme {
        CircularScreenContent(
            uiState = UiState.Success(CircularUiState(selectedYear = previewAcademicYears.first())),
            handleIntent = {},
            snackbarHostState = remember { SnackbarHostState() },
            snackbarMessage = null,
            onSnackbarDismissed = {},
            navigateToCreate = {},
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CircularGroupedList(
    circulars: List<com.app.ecarepro.core.domain.model.Circular>,
    onCircularClicked: (String) -> Unit,
) {
    val grouped = circulars.groupBy { it.cirDate ?: "" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        grouped.forEach { (date, circularsForDate) ->
            stickyHeader {
                Text(
                    text = date.uppercase(),
                    style = MaterialTheme.appTypography.interRegular12px,
                    color = MaterialTheme.appColors.textSecondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
            items(circularsForDate) { circular ->
                CircularItem(
                    circular = circular,
                    onClick = { onCircularClicked(circular.id) }
                )
                HorizontalDivider(thickness = 0.5.dp)
            }
        }
    }
}

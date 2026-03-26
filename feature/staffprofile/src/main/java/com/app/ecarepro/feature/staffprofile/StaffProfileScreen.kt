package com.app.ecarepro.feature.staffprofile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.StaffProfile
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
import com.app.ecarepro.designsystem.core.component.BottomSearchBarView
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.PersonFilterSheet
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.component.SortBottomSheet
import com.app.ecarepro.designsystem.core.component.SortOption
import com.app.ecarepro.designsystem.core.component.personlist.ListViewMode
import com.app.ecarepro.designsystem.core.component.personlist.PersonCardView
import com.app.ecarepro.designsystem.core.component.personlist.PersonRowView
import com.app.ecarepro.designsystem.core.component.personlist.StatsHeader
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.component.EcareProEmptyState
import androidx.compose.material.icons.filled.Person
import kotlinx.coroutines.launch

@Composable
fun StaffProfileScreen(
    viewModel: StaffProfileViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
    navigateToProfileDetail: (StaffProfile) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                StaffProfileEvent.NavigateBack -> navigateToBack()
                is StaffProfileEvent.NavigateToProfileDetail -> navigateToProfileDetail(event.profile)
                is StaffProfileEvent.ShowMessage -> {
                    snackbarMessage = event.snackbarMessage
                    snackbarHostState.showSnackbar(event.snackbarMessage.text)
                }
            }
        }
    }

    StaffProfileScreenContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage == null },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StaffProfileScreenContent(
    uiState: UiState<StaffProfileUiState>,
    handleIntent: (StaffProfileIntent) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit = {},
) {
    // Sort bottom sheet state
    var showSortBottomSheet by remember { mutableStateOf(false) }
    val sortSheetState = rememberModalBottomSheetState()

    // Filter bottom sheet state
    var showFilterSheet by remember { mutableStateOf(false) }
    val filterSheetState = rememberModalBottomSheetState()

    val scope = rememberCoroutineScope()

    EcareProScaffold(
        topBar = {
            Column {
                EcareProTopAppBar(
                    title = "Staff List",
                    onNavigationClicked = { handleIntent(StaffProfileIntent.OnBackClicked) },
                    actions = {
                        // View mode toggle button
                        if (uiState is UiState.Success) {
                            ViewModeToggleButton(
                                viewMode = uiState.data.viewMode,
                                onToggle = { handleIntent(StaffProfileIntent.OnViewModeToggle) }
                            )
                        }
                    }
                )

                // Stats Header
                if (uiState is UiState.Success) {
                    StatsHeader(stats = uiState.data.stats)
                }
            }
        },
        containerColor = White,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = onSnackbarDismissed,
        isLoading = if (uiState is UiState.Success) {
            uiState.data.isLoading
        } else {
            false
        },
        bottomBar = {
            // Bottom Search Bar with local search, sort, and filter buttons
            BottomSearchBarView(
                searchText = if (uiState is UiState.Success) uiState.data.searchQuery else "",
                onSearchTextChange = { handleIntent(StaffProfileIntent.OnSearchQueryChanged(it)) },
                placeholder = "Search by name, designation...",
                showSortButton = true,
                onSortClick = { showSortBottomSheet = true },
                showFilterButton = true,
                onFilterClick = { showFilterSheet = true },
                filterBadgeCount = if (uiState is UiState.Success) uiState.data.activeFilterCount else 0
            )
        }
    ) { paddingValues ->
        UiStateHandler(
            state = uiState
        ) { data ->
            PullToRefreshBox(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                isRefreshing = data.isRefreshing,
                onRefresh = { handleIntent(StaffProfileIntent.OnRefresh) }
            ) {
                if (data.filteredProfiles.isEmpty()) {
                    EcareProEmptyState(
                        message = "No staff profiles found.\nMaybe try a different search?",
                        icon = Icons.Default.Person
                    )
                } else {
                    // Grid or List view based on viewMode
                    when (data.viewMode) {
                        ListViewMode.GRID -> {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(data.filteredProfiles) { person ->
                                    PersonCardView(
                                        person = person,
                                        onClick = {
                                            handleIntent(
                                                StaffProfileIntent.OnProfileClicked(
                                                    person.id
                                                )
                                            )
                                        }
                                    )
                                }
                            }
                        }

                        ListViewMode.LIST -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(data.filteredProfiles) { person ->
                                    PersonRowView(
                                        person = person,
                                        onClick = {
                                            handleIntent(
                                                StaffProfileIntent.OnProfileClicked(
                                                    person.id
                                                )
                                            )
                                        }
                                    )
                                    HorizontalDivider(
                                        modifier = Modifier.padding(start = 76.dp),
                                        thickness = 0.5.dp,
                                        color = MaterialTheme.appColors.border
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Sort Bottom Sheet
    if (showSortBottomSheet && uiState is UiState.Success) {
        SortBottomSheet(
            currentSortConfig = uiState.data.sortConfig,
            onSortSelected = { sortConfig ->
                handleIntent(StaffProfileIntent.OnSortSelected(sortConfig))
                scope.launch {
                    sortSheetState.hide()
                    showSortBottomSheet = false
                }
            },
            onDismiss = {
                scope.launch {
                    sortSheetState.hide()
                    showSortBottomSheet = false
                }
            },
            sheetState = sortSheetState,
            availableOptions = listOf(SortOption.DATE_OF_JOINING, SortOption.NAME)
        )
    }

    // Filter Bottom Sheet
    if (showFilterSheet && uiState is UiState.Success) {
        PersonFilterSheet(
            filterSections = uiState.data.filterSections,
            currentFilters = uiState.data.selectedFilters,
            onApplyFilters = { filters ->
                handleIntent(StaffProfileIntent.OnFiltersApplied(filters))
            },
            onDismiss = {
                scope.launch {
                    filterSheetState.hide()
                    showFilterSheet = false
                }
            },
            sheetState = filterSheetState
        )
    }
}

/**
 * View Mode Toggle Button
 * Shows grid icon in list mode, shows list icon in grid mode
 */
@Composable
private fun ViewModeToggleButton(
    viewMode: ListViewMode,
    onToggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(
                color = Color(0xFFF2F2F7),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onToggle),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (viewMode == ListViewMode.GRID) {
                Icons.Default.ViewList
            } else {
                Icons.Default.GridView
            },
            contentDescription = "Toggle view mode",
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.appColors.textPrimary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StaffProfileScreenPreview() {
    EcareProTheme {
        StaffProfileScreenContent(
            uiState = UiState.Success(
                StaffProfileUiState()
            ),
            handleIntent = {},
            snackbarHostState = SnackbarHostState(),
            snackbarMessage = null,
            onSnackbarDismissed = {}
        )
    }
}

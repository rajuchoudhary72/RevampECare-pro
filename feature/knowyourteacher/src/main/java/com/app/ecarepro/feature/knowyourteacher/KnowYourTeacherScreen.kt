package com.app.ecarepro.feature.knowyourteacher

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
import com.app.ecarepro.designsystem.core.component.BottomSearchBarView
import com.app.ecarepro.designsystem.core.component.EcareProEmptyState
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
import com.app.ecarepro.designsystem.core.component.personlist.ViewModeToggleButton
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import kotlinx.coroutines.launch

@Composable
fun KnowYourTeacherScreen(
    viewModel: KnowYourTeacherViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                KnowYourTeacherEvent.NavigateBack -> navigateToBack()
                is KnowYourTeacherEvent.ShowMessage -> {
                    snackbarMessage = event.snackbarMessage
                    snackbarHostState.showSnackbar(event.snackbarMessage.text)
                }
            }
        }
    }

    KnowYourTeacherContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KnowYourTeacherContent(
    uiState: UiState<KnowYourTeacherUiState>,
    handleIntent: (KnowYourTeacherIntent) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit = {},
) {
    var showSortBottomSheet by remember { mutableStateOf(false) }
    val sortSheetState = rememberModalBottomSheetState()

    var showFilterSheet by remember { mutableStateOf(false) }
    val filterSheetState = rememberModalBottomSheetState()

    val scope = rememberCoroutineScope()

    EcareProScaffold(
        topBar = {
            Column {
                EcareProTopAppBar(
                    title = "Know Your Teacher",
                    onNavigationClicked = { handleIntent(KnowYourTeacherIntent.OnBackClicked) },
                    actions = {
                        if (uiState is UiState.Success) {
                            ViewModeToggleButton(
                                viewMode = uiState.data.viewMode,
                                onToggle = { handleIntent(KnowYourTeacherIntent.OnViewModeToggle) }
                            )
                        }
                    }
                )
                if (uiState is UiState.Success) {
                    StatsHeader(stats = uiState.data.stats)
                }
            }
        },
        containerColor = White,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = onSnackbarDismissed,
        bottomBar = {
            BottomSearchBarView(
                searchText = (uiState as? UiState.Success)?.data?.searchQuery ?: "",
                onSearchTextChange = { handleIntent(KnowYourTeacherIntent.OnSearchQueryChanged(it)) },
                placeholder = "Search by name, designation...",
                showSortButton = true,
                onSortClick = { showSortBottomSheet = true },
                showFilterButton = true,
                onFilterClick = { showFilterSheet = true },
                filterBadgeCount = (uiState as? UiState.Success)?.data?.activeFilterCount ?: 0
            )
        }
    ) { paddingValues ->
        UiStateHandler(state = uiState) { data ->
            PullToRefreshBox(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                isRefreshing = data.isRefreshing,
                onRefresh = { handleIntent(KnowYourTeacherIntent.OnRefresh) }
            ) {
                if (data.filteredTeachers.isEmpty()) {
                    EcareProEmptyState(
                        message = "No teachers found.\nMaybe try a different search?",
                        icon = Icons.Default.Person
                    )
                } else {
                    when (data.viewMode) {
                        ListViewMode.GRID -> {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(data.filteredTeachers) { person ->
                                    PersonCardView(person = person, onClick = {})
                                }
                            }
                        }
                        ListViewMode.LIST -> {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(data.filteredTeachers) { person ->
                                    PersonRowView(person = person, onClick = {})
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

    if (showSortBottomSheet && uiState is UiState.Success) {
        SortBottomSheet(
            currentSortConfig = uiState.data.sortConfig,
            onSortSelected = { sortConfig ->
                handleIntent(KnowYourTeacherIntent.OnSortSelected(sortConfig))
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
            availableOptions = listOf(SortOption.NAME)
        )
    }

    if (showFilterSheet && uiState is UiState.Success) {
        PersonFilterSheet(
            filterSections = uiState.data.filterSections,
            currentFilters = uiState.data.selectedFilters,
            onApplyFilters = { filters -> handleIntent(KnowYourTeacherIntent.OnFiltersApplied(filters)) },
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

@Preview(showBackground = true)
@Composable
private fun KnowYourTeacherPreview() {
    EcareProTheme {
        KnowYourTeacherContent(
            uiState = UiState.Success(KnowYourTeacherUiState()),
            handleIntent = {},
            snackbarHostState = SnackbarHostState(),
            snackbarMessage = null,
            onSnackbarDismissed = {}
        )
    }
}

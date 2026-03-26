package com.app.ecarepro.feature.studentprofile

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.feature.studentprofile.R
import com.app.ecarepro.core.domain.model.StudentProfile
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
import com.app.ecarepro.designsystem.core.component.BottomSearchBarView
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.HorizontalTabBar
import com.app.ecarepro.designsystem.core.component.HorizontalTabBarConfiguration
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.component.SortOption
import com.app.ecarepro.designsystem.core.component.personlist.ListViewMode
import com.app.ecarepro.designsystem.core.component.personlist.PersonCardView
import com.app.ecarepro.designsystem.core.component.personlist.PersonRowView
import com.app.ecarepro.designsystem.core.component.personlist.ScholarTypeDropdown
import com.app.ecarepro.designsystem.core.component.personlist.StatsHeader
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.component.EcareProEmptyState
import com.app.ecarepro.designsystem.core.component.SortBottomSheet
import androidx.compose.material.icons.filled.Person
import kotlinx.coroutines.launch

@Composable
fun StudentProfileScreen(
    viewModel: StudentProfileViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
    navigateToProfileDetail: (StudentProfile) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                StudentProfileEvent.NavigateBack -> navigateToBack()
                is StudentProfileEvent.NavigateToProfileDetail -> navigateToProfileDetail(event.profile)
                is StudentProfileEvent.ShowMessage -> {
                    snackbarMessage = event.snackbarMessage
                    snackbarHostState.showSnackbar(event.snackbarMessage.text)
                }
            }
        }
    }

    StudentProfileScreenContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage == null },
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun StudentProfileScreenContent(
    uiState: UiState<StudentProfileUiState>,
    handleIntent: (StudentProfileIntent) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit = {},
) {
    // Sort bottom sheet state
    var showSortBottomSheet by remember { mutableStateOf(false) }
    val sortSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val pagerState = rememberPagerState(
        initialPage = if (uiState is UiState.Success) uiState.data.selectedClassIndex else 0,
        pageCount = { if (uiState is UiState.Success) uiState.data.classTabs.size else 0 }
    )

    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            handleIntent(StudentProfileIntent.OnClassSelected(pagerState.currentPage))
        }
    }

    EcareProScaffold(
        topBar = {
            Column {
                EcareProTopAppBar(
                    title = stringResource(R.string.feature_studentprofile_title),
                    onNavigationClicked = { handleIntent(StudentProfileIntent.OnBackClicked) },
                    actions = {
                        // Scholar Type Dropdown
                        if (uiState is UiState.Success) {
                            ScholarTypeDropdown(
                                selectedScholarType = uiState.data.selectedScholarType,
                                count = uiState.data.stats.total,
                                onScholarTypeSelected = { scholarType ->
                                    handleIntent(StudentProfileIntent.OnScholarTypeSelected(scholarType))
                                }
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                        // View mode toggle button
                        if (uiState is UiState.Success) {
                            ViewModeToggleButton(
                                viewMode = uiState.data.viewMode,
                                onToggle = { handleIntent(StudentProfileIntent.OnViewModeToggle) }
                            )
                        }
                    }
                )

                // Class Tabs
                if (uiState is UiState.Success && uiState.data.classTabs.isNotEmpty()) {
                    HorizontalTabBar(
                        tabs = uiState.data.classTabs,
                        selectedTab = uiState.data.classTabs.getOrNull(uiState.data.selectedClassIndex)
                            ?: "All",
                        onTabSelected = { tab ->
                            val index = uiState.data.classTabs.indexOf(tab)
                            if (index != -1) {
                                scope.launch {
                                    handleIntent(StudentProfileIntent.OnClassSelected(index))
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                        },
                        configuration = HorizontalTabBarConfiguration(
                            tabSpacing = 24.dp
                        )
                    )
                }

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
            // Bottom Search Bar with local search and sort button
            BottomSearchBarView(
                searchText = if (uiState is UiState.Success) uiState.data.searchQuery else "",
                onSearchTextChange = { handleIntent(StudentProfileIntent.OnSearchQueryChanged(it)) },
                placeholder = stringResource(R.string.feature_studentprofile_search_placeholder),
                showSortButton = true,
                onSortClick = { showSortBottomSheet = true }
            )
        }
    ) { paddingValues ->
        UiStateHandler(
            state = uiState
        ) { data ->
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                PullToRefreshBox(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    isRefreshing = data.isRefreshing,
                    onRefresh = { handleIntent(StudentProfileIntent.OnRefresh) }
                ) {
                    if (data.filteredProfiles.isEmpty()) {
                        EcareProEmptyState(
                            message = stringResource(R.string.feature_studentprofile_empty_state),
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
                                                    StudentProfileIntent.OnProfileClicked(
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
                                                    StudentProfileIntent.OnProfileClicked(
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
    }

    // Sort Bottom Sheet
    if (showSortBottomSheet && uiState is UiState.Success) {
        SortBottomSheet(
            currentSortConfig = uiState.data.sortConfig,
            onSortSelected = { sortConfig ->
                handleIntent(StudentProfileIntent.OnSortSelected(sortConfig))
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
            availableOptions = listOf(SortOption.ROLL_NUMBER, SortOption.ADMISSION_NUMBER, SortOption.NAME)
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
private fun StudentProfileScreenPreview() {
    EcareProTheme {
        StudentProfileScreenContent(
            uiState = UiState.Success(
                StudentProfileUiState(
                    classTabs = listOf("All", "UKG", "LKG")
                )
            ),
            handleIntent = {},
            snackbarHostState = SnackbarHostState(),
            snackbarMessage = null,
            onSnackbarDismissed = {}
        )
    }
}

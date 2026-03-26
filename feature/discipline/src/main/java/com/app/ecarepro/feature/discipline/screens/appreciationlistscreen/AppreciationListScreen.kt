package com.app.ecarepro.feature.discipline.screens.appreciationlistscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
import androidx.compose.ui.tooling.preview.Preview
import com.app.ecarepro.designsystem.core.component.BottomSearchBarView
import com.app.ecarepro.designsystem.core.component.EcareProClassTabs
import com.app.ecarepro.designsystem.core.component.EcareProEmptyState
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.component.personlist.Gender
import com.app.ecarepro.designsystem.core.component.personlist.ListStatsPresentation
import com.app.ecarepro.designsystem.core.component.personlist.PersonPresentation
import com.app.ecarepro.designsystem.core.component.personlist.PersonType
import com.app.ecarepro.designsystem.core.component.personlist.StatsHeader
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.feature.discipline.components.DisciplinePersonItem
import kotlinx.coroutines.launch

@Composable
fun AppreciationListScreen(
    viewModel: AppreciationListViewModel,
    navigateToBack: () -> Unit,
    navigateToAddAppreciation: (Int) -> Unit,
    navigateToViewAllAppreciations: (Int) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                AppreciationListEvent.NavigateBack -> navigateToBack()
                is AppreciationListEvent.NavigateToAddAppreciation -> navigateToAddAppreciation(event.studentId)
                is AppreciationListEvent.NavigateToViewAll -> navigateToViewAllAppreciations(event.studentId)
                is AppreciationListEvent.ShowMessage -> {
                    snackbarMessage = event.snackbarMessage
                    snackbarHostState.showSnackbar(event.snackbarMessage.text)
                }
            }
        }
    }

    AppreciationListContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AppreciationListContent(
    uiState: UiState<AppreciationListUiState>,
    handleIntent: (AppreciationListIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    EcareProScaffold(
        topBar = {
            Column {
                EcareProTopAppBar(
                    title = "Appreciations",
                    onNavigationClicked = { handleIntent(AppreciationListIntent.OnBackClicked) },
                )

                if (uiState is UiState.Success && uiState.data.classTabs.isNotEmpty()) {
                    EcareProClassTabs(
                        selectedTabIndex = uiState.data.selectedClassIndex,
                        tabs = uiState.data.classTabs,
                        onTabClick = { index ->
                            coroutineScope.launch {
                                handleIntent(AppreciationListIntent.OnClassSelected(index))
                            }
                        }
                    )
                }

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
                searchText = if (uiState is UiState.Success) uiState.data.searchQuery else "",
                onSearchTextChange = { handleIntent(AppreciationListIntent.OnSearchQueryChanged(it)) },
                placeholder = "Search by name, admission number...",
                rightIconVector = Icons.Default.FilterList,
                onRightIconClick = {},
            )
        }
    ) { paddingValues ->
        UiStateHandler(
            modifier = Modifier.padding(paddingValues),
            state = uiState,
        ) { data ->
            PullToRefreshBox(
                modifier = Modifier.fillMaxSize(),
                isRefreshing = data.isRefreshing,
                onRefresh = { handleIntent(AppreciationListIntent.OnRefresh) },
            ) {
                if (data.filteredPersons.isEmpty()) {
                    EcareProEmptyState(message = "No students found")
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        items(data.filteredPersons, key = { it.id }) { person ->
                            DisciplinePersonItem(
                                person = person,
                                addLabel = "Add appreciation",
                                viewLabel = "View all",
                                onAddClick = { handleIntent(AppreciationListIntent.OnAddClicked(person)) },
                                onViewClick = { handleIntent(AppreciationListIntent.OnViewAllClicked(person)) },
                            )
                            HorizontalDivider(thickness = 0.5.dp)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Appreciation List Screen")
@Composable
private fun AppreciationListScreenPreview() {
    EcareProTheme {
        AppreciationListContent(
            uiState = UiState.Success(
                AppreciationListUiState(
                    classTabs = listOf("All", "10", "11", "12"),
                    selectedClassIndex = 0,
                    filteredPersons = listOf(
                        PersonPresentation(
                            id = "1", name = "John Doe",
                            subtitle = "Class: 10-A | Roll: 01",
                            detail = "Adm No: ADM001",
                            profileImageURL = null, gender = Gender.MALE,
                            personType = PersonType.STUDENT,
                        ),
                        PersonPresentation(
                            id = "2", name = "Jane Smith",
                            subtitle = "Class: 10-A | Roll: 02",
                            detail = "Adm No: ADM002",
                            profileImageURL = null, gender = Gender.FEMALE,
                            personType = PersonType.STUDENT,
                        ),
                    ),
                    stats = ListStatsPresentation(
                        total = 2, maleCount = 1, femaleCount = 1,
                        maleLabel = "Boys", femaleLabel = "Girls",
                    ),
                ),
            ),
            handleIntent = {},
            snackbarHostState = SnackbarHostState(),
            snackbarMessage = null,
            onSnackbarDismissed = {},
        )
    }
}

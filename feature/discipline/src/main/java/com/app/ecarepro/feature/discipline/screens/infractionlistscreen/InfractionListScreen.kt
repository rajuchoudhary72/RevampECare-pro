package com.app.ecarepro.feature.discipline.screens.infractionlistscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import com.app.ecarepro.feature.discipline.DisciplineUserType
import com.app.ecarepro.feature.discipline.components.DisciplinePersonItem
import kotlinx.coroutines.launch

@Composable
fun InfractionListScreen(
    viewModel: InfractionListViewModel,
    navigateToBack: () -> Unit,
    navigateToAddInfraction: (DisciplineUserType, Int) -> Unit,
    navigateToViewAllInfractions: (DisciplineUserType, Int) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                InfractionListEvent.NavigateBack -> navigateToBack()
                is InfractionListEvent.NavigateToAddInfraction -> navigateToAddInfraction(event.userType, event.userId)
                is InfractionListEvent.NavigateToViewAll -> navigateToViewAllInfractions(event.userType, event.userId)
                is InfractionListEvent.ShowMessage -> {
                    snackbarMessage = event.snackbarMessage
                    snackbarHostState.showSnackbar(event.snackbarMessage.text)
                }
            }
        }
    }

    InfractionListContent(
        uiState = uiState,
        userType = viewModel.userType,
        handleIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun InfractionListContent(
    uiState: UiState<InfractionListUiState>,
    userType: DisciplineUserType,
    handleIntent: (InfractionListIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    EcareProScaffold(
        topBar = {
            Column {
                EcareProTopAppBar(
                    title = "Infractions",
                    onNavigationClicked = { handleIntent(InfractionListIntent.OnBackClicked) },
                )

                if (uiState is UiState.Success && userType == DisciplineUserType.STUDENT && uiState.data.classTabs.isNotEmpty()) {
                    EcareProClassTabs(
                        selectedTabIndex = uiState.data.selectedClassIndex,
                        tabs = uiState.data.classTabs,
                        onTabClick = { index ->
                            coroutineScope.launch {
                                handleIntent(InfractionListIntent.OnClassSelected(index))
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
                onSearchTextChange = { handleIntent(InfractionListIntent.OnSearchQueryChanged(it)) },
                placeholder = if (userType == DisciplineUserType.STUDENT)
                    "Search by name, admission number..." else "Search by name, designation...",
                rightIconVector = Icons.Default.FilterList,
                onRightIconClick = {},
            )
        }
    ) { paddingValues ->
        UiStateHandler(
            modifier = Modifier.padding(paddingValues),
            state = uiState
        ) { data ->
            PullToRefreshBox(
                modifier = Modifier.fillMaxSize(),
                isRefreshing = data.isRefreshing,
                onRefresh = { handleIntent(InfractionListIntent.OnRefresh) }
            ) {
                if (data.filteredPersons.isEmpty()) {
                    EcareProEmptyState(
                        message = if (userType == DisciplineUserType.STUDENT)
                            "No students found" else "No staff found"
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        items(data.filteredPersons, key = { it.id }) { person ->
                            DisciplinePersonItem(
                                person = person,
                                addLabel = "Add infraction",
                                viewLabel = "View all",
                                onAddClick = { handleIntent(InfractionListIntent.OnAddClicked(person)) },
                                onViewClick = { handleIntent(InfractionListIntent.OnViewAllClicked(person)) },
                            )
                            HorizontalDivider(thickness = 0.5.dp)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Infraction List - Student")
@Composable
private fun InfractionListScreenPreview() {
    EcareProTheme {
        InfractionListContent(
            uiState = UiState.Success(
                InfractionListUiState(
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
            userType = DisciplineUserType.STUDENT,
            handleIntent = {},
            snackbarHostState = SnackbarHostState(),
            snackbarMessage = null,
            onSnackbarDismissed = {},
        )
    }
}

package com.app.ecarepro.feature.discipline.screens.viewallappreciationscreen

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
import androidx.compose.ui.tooling.preview.Preview
import com.app.ecarepro.core.domain.model.discipline.AppreciationRecord
import com.app.ecarepro.core.domain.model.discipline.DisciplineUserInfo
import com.app.ecarepro.designsystem.core.component.BottomSearchBarView
import com.app.ecarepro.designsystem.core.component.EcareProEmptyState
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.feature.discipline.components.AppreciationItemCard
import com.app.ecarepro.feature.discipline.components.DeleteConfirmationSheet
import com.app.ecarepro.feature.discipline.components.UserHeaderCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewAllAppreciationsScreen(
    viewModel: ViewAllAppreciationsViewModel,
    navigateToBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                ViewAllAppreciationsEvent.NavigateBack -> navigateToBack()
                is ViewAllAppreciationsEvent.ShowMessage -> {
                    snackbarMessage = event.snackbarMessage
                    snackbarHostState.showSnackbar(event.snackbarMessage.text)
                }
            }
        }
    }

    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = "All Appreciations",
                onNavigationClicked = { viewModel.handleIntent(ViewAllAppreciationsIntent.OnBackClicked) },
            )
        },
        containerColor = White,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null },
        isLoading = (uiState as? UiState.Success)?.data?.isLoading == true,
        bottomBar = {
            BottomSearchBarView(
                searchText = (uiState as? UiState.Success)?.data?.searchQuery.orEmpty(),
                onSearchTextChange = { viewModel.handleIntent(ViewAllAppreciationsIntent.OnSearchQueryChanged(it)) },
                placeholder = "Search appreciations...",
                rightIconVector = Icons.Default.FilterList,
                onRightIconClick = {},
            )
        }
    ) { paddingValues ->
        UiStateHandler(
            modifier = Modifier.padding(paddingValues),
            state = uiState,
        ) { data ->
            Column(modifier = Modifier.fillMaxSize()) {
                data.userInfo?.let { UserHeaderCard(userInfo = it) }

                if (data.filteredRecords.isEmpty()) {
                    EcareProEmptyState(message = "No appreciations found")
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        items(data.filteredRecords, key = { it.id.orEmpty() }) { record ->
                            AppreciationItemCard(
                                record = record,
                                onDeleteClick = {
                                    record.id?.let {
                                        viewModel.handleIntent(ViewAllAppreciationsIntent.OnShowDeleteSheet(it))
                                    }
                                },
                            )
                            HorizontalDivider(thickness = 0.5.dp)
                        }
                    }
                }
            }

            if (data.isDeleteSheetVisible) {
                DeleteConfirmationSheet(
                    title = "Delete Appreciation",
                    message = "Are you sure you want to delete this appreciation?",
                    onDismiss = { viewModel.handleIntent(ViewAllAppreciationsIntent.OnDismissDeleteSheet) },
                    onConfirm = { viewModel.handleIntent(ViewAllAppreciationsIntent.OnConfirmDelete) },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ViewAllAppreciationsContent(
    data: ViewAllAppreciationsUiState,
    onIntent: (ViewAllAppreciationsIntent) -> Unit = {},
) {
    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = "All Appreciations",
                onNavigationClicked = { onIntent(ViewAllAppreciationsIntent.OnBackClicked) },
            )
        },
        containerColor = White,
        snackbarHostState = remember { SnackbarHostState() },
        isLoading = data.isLoading,
        bottomBar = {
            BottomSearchBarView(
                searchText = data.searchQuery,
                onSearchTextChange = { onIntent(ViewAllAppreciationsIntent.OnSearchQueryChanged(it)) },
                placeholder = "Search appreciations...",
                rightIconVector = Icons.Default.FilterList,
                onRightIconClick = {},
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            data.userInfo?.let { UserHeaderCard(userInfo = it) }

            if (data.filteredRecords.isEmpty()) {
                EcareProEmptyState(message = "No appreciations found")
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    items(data.filteredRecords, key = { it.id.orEmpty() }) { record ->
                        AppreciationItemCard(
                            record = record,
                            onDeleteClick = {
                                record.id?.let {
                                    onIntent(ViewAllAppreciationsIntent.OnShowDeleteSheet(it))
                                }
                            },
                        )
                        HorizontalDivider(thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "View All Appreciations Screen")
@Composable
private fun ViewAllAppreciationsScreenPreview() {
    EcareProTheme {
        ViewAllAppreciationsContent(
            data = ViewAllAppreciationsUiState(
                userInfo = DisciplineUserInfo(
                    id = 1, name = "John Doe", className = "10-A",
                    admissionNo = "ADM001", photo = null, contactPerson = null,
                    contactMob = null, designation = null, mobile = null, qualification = null,
                ),
                filteredRecords = listOf(
                    AppreciationRecord(
                        id = "1", appreciation = "Good Behavior",
                        subAppreciation = "Helping others", appreciationOn = "15-Jan-2026",
                        admissionNo = "ADM001", stID = 1, studentName = "John Doe",
                        reward = "Star Badge", remark = "Very helpful", point = 10,
                        instance = 1, staffName = "Mr. Smith", stffPhoto = null,
                        recordClass = "10-A", photo = null, designation = null,
                        canDelete = true,
                    ),
                    AppreciationRecord(
                        id = "2", appreciation = "Academic",
                        subAppreciation = "Excellence in Math", appreciationOn = "10-Jan-2026",
                        admissionNo = "ADM001", stID = 1, studentName = "John Doe",
                        reward = "Certificate", remark = null, point = 15,
                        instance = 2, staffName = "Mrs. Johnson", stffPhoto = null,
                        recordClass = "10-A", photo = null, designation = null,
                        canDelete = true,
                    ),
                ),
            ),
        )
    }
}

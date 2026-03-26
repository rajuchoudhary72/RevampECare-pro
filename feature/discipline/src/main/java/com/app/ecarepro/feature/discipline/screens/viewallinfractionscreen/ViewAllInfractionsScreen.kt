package com.app.ecarepro.feature.discipline.screens.viewallinfractionscreen

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
import com.app.ecarepro.core.domain.model.discipline.DisciplineUserInfo
import com.app.ecarepro.core.domain.model.discipline.InfractionRecord
import com.app.ecarepro.designsystem.core.component.BottomSearchBarView
import com.app.ecarepro.designsystem.core.component.EcareProEmptyState
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.feature.discipline.DisciplineUserType
import com.app.ecarepro.feature.discipline.components.InfractionItemCard
import com.app.ecarepro.feature.discipline.components.UserHeaderCard
import com.app.ecarepro.feature.discipline.components.DeleteConfirmationSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewAllInfractionsScreen(
    viewModel: ViewAllInfractionsViewModel,
    navigateToBack: () -> Unit,
    navigateToAddCompliance: (String, DisciplineUserType, Int) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                ViewAllInfractionsEvent.NavigateBack -> navigateToBack()
                is ViewAllInfractionsEvent.NavigateToCompliance -> navigateToAddCompliance(event.infractionID, event.userType, event.userId)
                is ViewAllInfractionsEvent.ShowMessage -> {
                    snackbarMessage = event.snackbarMessage
                    snackbarHostState.showSnackbar(event.snackbarMessage.text)
                }
            }
        }
    }

    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = "All Infractions",
                onNavigationClicked = { viewModel.handleIntent(ViewAllInfractionsIntent.OnBackClicked) },
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
                onSearchTextChange = { viewModel.handleIntent(ViewAllInfractionsIntent.OnSearchQueryChanged(it)) },
                placeholder = "Search infractions...",
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
                    EcareProEmptyState(message = "No infractions found")
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        items(data.filteredRecords, key = { it.infractionID }) { record ->
                            InfractionItemCard(
                                record = record,
                                onDeleteClick = {
                                    viewModel.handleIntent(ViewAllInfractionsIntent.OnShowDeleteSheet(record.infractionID))
                                },
                                onComplianceClick = {
                                    viewModel.handleIntent(ViewAllInfractionsIntent.OnComplianceClicked(record.infractionID))
                                },
                            )
                            HorizontalDivider(thickness = 0.5.dp)
                        }
                    }
                }
            }

            if (data.isDeleteSheetVisible) {
                DeleteConfirmationSheet(
                    title = "Delete Infraction",
                    message = "Are you sure you want to delete this infraction?",
                    onDismiss = { viewModel.handleIntent(ViewAllInfractionsIntent.OnDismissDeleteSheet) },
                    onConfirm = { viewModel.handleIntent(ViewAllInfractionsIntent.OnConfirmDelete) },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ViewAllInfractionsContent(
    data: ViewAllInfractionsUiState,
    onIntent: (ViewAllInfractionsIntent) -> Unit = {},
) {
    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = "All Infractions",
                onNavigationClicked = { onIntent(ViewAllInfractionsIntent.OnBackClicked) },
            )
        },
        containerColor = White,
        snackbarHostState = remember { SnackbarHostState() },
        isLoading = data.isLoading,
        bottomBar = {
            BottomSearchBarView(
                searchText = data.searchQuery,
                onSearchTextChange = { onIntent(ViewAllInfractionsIntent.OnSearchQueryChanged(it)) },
                placeholder = "Search infractions...",
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
                EcareProEmptyState(message = "No infractions found")
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    items(data.filteredRecords, key = { it.infractionID }) { record ->
                        InfractionItemCard(
                            record = record,
                            onDeleteClick = {
                                onIntent(ViewAllInfractionsIntent.OnShowDeleteSheet(record.infractionID))
                            },
                            onComplianceClick = {
                                onIntent(ViewAllInfractionsIntent.OnComplianceClicked(record.infractionID))
                            },
                        )
                        HorizontalDivider(thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "View All Infractions Screen")
@Composable
private fun ViewAllInfractionsScreenPreview() {
    EcareProTheme {
        ViewAllInfractionsContent(
            data = ViewAllInfractionsUiState(
                userInfo = DisciplineUserInfo(
                    id = 1, name = "John Doe", className = "10-A",
                    admissionNo = "ADM001", photo = null, contactPerson = null,
                    contactMob = null, designation = null, mobile = null, qualification = null,
                ),
                filteredRecords = listOf(
                    InfractionRecord(
                        infractionID = "1", infraction = "Bullying",
                        subInfraction = "Verbal", consequences = "Warning",
                        designation = null, stffPhoto = null, admissionNo = "ADM001",
                        correctiveAction = "Counseling", stID = 1,
                        studentName = "John Doe", point = 5, instance = "2",
                        infractionOn = "15-Jan-2026", staffName = "Mr. Smith",
                        issueBy = "Admin", recordClass = "10-A", photo = null,
                        isResolved = false, canDelete = true, showResolvedButton = true,
                        isComplianceActive = true, complianceAttachment = null,
                        contactMob = null, remarks = null,
                    ),
                    InfractionRecord(
                        infractionID = "2", infraction = "Late Coming",
                        subInfraction = "Habitual", consequences = "Detention",
                        designation = null, stffPhoto = null, admissionNo = "ADM001",
                        correctiveAction = null, stID = 1,
                        studentName = "John Doe", point = 3, instance = "1",
                        infractionOn = "10-Jan-2026", staffName = "Mrs. Johnson",
                        issueBy = "Admin", recordClass = "10-A", photo = null,
                        isResolved = true, canDelete = true, showResolvedButton = false,
                        isComplianceActive = false, complianceAttachment = null,
                        contactMob = null, remarks = null,
                    ),
                ),
            ),
        )
    }
}

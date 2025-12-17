package com.app.ecarepro.feature.syllabus

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.Syllabus
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
import com.app.ecarepro.designsystem.core.component.BottomSearchBarView
import com.app.ecarepro.designsystem.core.component.ECAttachment
import com.app.ecarepro.designsystem.core.component.EcareProClassTabs
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.syllabus.componets.DeleteBottomSheet
import com.app.ecarepro.feature.syllabus.componets.EmptyState
import com.app.ecarepro.feature.syllabus.componets.MenuBottomSheet
import com.app.ecarepro.feature.syllabus.componets.SyllabusItem

@Composable
fun SyllabusScreen(
    viewModel: SyllabusViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
    navigateToAddSyllabus: (Syllabus?) -> Unit,
    navigateToAttachmentList: (List<ECAttachment>) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                SyllabusEvent.NavigateBack -> navigateToBack()
                is SyllabusEvent.NavigateToAttachmentList -> navigateToAttachmentList(event.attachments)
                is SyllabusEvent.ShowMessage -> {
                    snackbarMessage = event.snackbarMessage
                    snackbarHostState.showSnackbar(event.snackbarMessage.text)
                }

                SyllabusEvent.NavigateToAddSyllabus -> navigateToAddSyllabus(null)
                is SyllabusEvent.EditSyllabus -> navigateToAddSyllabus(event.syllabus)
            }
        }
    }

    SyllabusScreenContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage == null },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SyllabusScreenContent(
    uiState: UiState<SyllabusUiState>,
    handleIntent: (SyllabusIntent) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit = {},
) {

    EcareProScaffold(
        topBar = {
            Column {
                EcareProTopAppBar(
                    title = "Syllabus",
                    onNavigationClicked = { handleIntent(SyllabusIntent.OnBackClicked) },
                    actions = {
                        TextButton(onClick = { handleIntent(SyllabusIntent.OnAddNewClicked) }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_add),
                                contentDescription = "Add new",
                                tint = MaterialTheme.appColors.primary
                            )
                            Text(
                                text = "Add new",
                                style = MaterialTheme.appTypography.interSemiBold14px,
                                color = MaterialTheme.appColors.primary
                            )
                        }
                    }
                )

                if (uiState is UiState.Success) {
                    EcareProClassTabs(
                        selectedTabIndex = uiState.data.selectedClassIndex,
                        tabs = uiState.data.classTabs,
                        onTabClick = { handleIntent(SyllabusIntent.OnClassSelected(it)) }
                    )
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

    /*    bottomBar = {
            // Bottom Search Bar with filter icon
            BottomSearchBar(
                searchQuery = if (uiState is UiState.Success) uiState.data.searchQuery else "",
                onSearchQueryChanged = { handleIntent(SyllabusIntent.OnSearchQueryChanged(it)) },
                rightIcon = {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filter/Sort",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.appColors.textPrimary
                    )
                },
                onRightIconClick = {
                    // TODO: Implement filter/sort functionality
                    // You can add a new intent for this
                }
            )
        }*/
        bottomBar = {
            // Bottom Search Bar with filter icon using common component
            BottomSearchBarView(
                searchText = if (uiState is UiState.Success) uiState.data.searchQuery else "",
                onSearchTextChange = { handleIntent(SyllabusIntent.OnSearchQueryChanged(it)) },
                placeholder = "Search by title or subject",
                rightIconVector = Icons.Default.FilterList,
                onRightIconClick = {
                    // TODO: Implement filter/sort functionality
                    // You can add a new intent for this
                }
            )
        }
    ) { paddingValues ->
        UiStateHandler(
            modifier = Modifier.padding(paddingValues),
            state = uiState
        ) { data ->
            Box {
                if (data.filteredSyllabuses.isEmpty()) {
                    EmptyState()
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        items(data.filteredSyllabuses) { syllabus ->
                            SyllabusItem(
                                syllabus = syllabus,
                                onViewClick = { handleIntent(SyllabusIntent.OnViewClicked(syllabus.id)) },
                                onDownloadClick = {
                                    handleIntent(
                                        SyllabusIntent.OnDownloadClicked(
                                            syllabus.id
                                        )
                                    )
                                },
                                onMenuClick = { handleIntent(SyllabusIntent.OnMenuClicked(syllabus.id)) }
                            )

                            HorizontalDivider(
                                thickness = 0.5.dp
                            )
                        }
                    }
                }

                if (data.isMenuVisible) {
                    MenuBottomSheet(
                        onDismiss = { handleIntent(SyllabusIntent.OnDismissMenu) },
                        onClickEdit = { handleIntent(SyllabusIntent.OnEditClicked(data.selectedSyllabusId)) },
                        onClickDelete = { handleIntent(SyllabusIntent.ShowDeleteBottomSheet) }
                    )
                }

                if (data.isDeleteSheetVisible) {
                    DeleteBottomSheet(
                        onDismiss = { handleIntent(SyllabusIntent.OnDismissDeleteBottomSheet) },
                        onDeleteClick = { handleIntent(SyllabusIntent.OnDeleteClicked(data.selectedSyllabusId)) }
                    )
                }

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun SyllabusScreenPreview() {
    EcareProTheme {
        SyllabusScreenContent(
            uiState = UiState.Success(
                SyllabusUiState(
                    classTabs = listOf("UKG", "LKG")
                )
            ),
            handleIntent = {},
            snackbarHostState = SnackbarHostState(),
            snackbarMessage = null,
            onSnackbarDismissed = {}
        )
    }
}
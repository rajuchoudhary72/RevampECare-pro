package com.app.ecarepro.feature.assignment

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.designsystem.R
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.assignment.components.AssignmentItem
import com.app.ecarepro.feature.assignment.components.AssignmentList
import com.app.ecarepro.feature.assignment.components.EmptyState
import com.app.ecarepro.feature.assignment.components.SearchAndFilterBottomBar

@Composable
fun AssignmentScreen(
    viewModel: AssignmentViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
    navigateToAddAssignment: () -> Unit,
    navigateToDetails: () -> Unit,
    openDocViewer: (title: String, url: String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                AssignmentEvent.NavigateBack -> navigateToBack()
                AssignmentEvent.NavigateToAddAssignment -> navigateToAddAssignment()
                is AssignmentEvent.ViewAssignment -> openDocViewer(event.title, event.url)
                is AssignmentEvent.ShowMessage -> {
                    snackbarMessage = event.snackbarMessage
                    snackbarHostState.showSnackbar(event.snackbarMessage.text)
                }

                AssignmentEvent.ViewReport -> navigateToDetails()
            }
        }
    }

    AssignmentScreenContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage == null },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AssignmentScreenContent(
    uiState: UiState<AssignmentUiState>,
    handleIntent: (AssignmentIntent) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit = {},
) {
    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                modifier = Modifier.shadow(elevation = 1.dp),
                title = "Assignments",
                onNavigationClicked = { handleIntent(AssignmentIntent.OnBackClicked) },
                actions = {
                    TextButton(onClick = { handleIntent(AssignmentIntent.OnAddNewClicked) }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_add),
                            contentDescription = "Add new",
                            tint = MaterialTheme.appColors.primary
                        )
                        Text(
                            text = "Add New",
                            style = MaterialTheme.appTypography.interSemiBold14px,
                            color = MaterialTheme.appColors.primary
                        )
                    }
                }
            )
        },
        containerColor = White,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = onSnackbarDismissed,
        isLoading = if (uiState is UiState.Success) uiState.data.isLoading else false,
        bottomBar = {
            if (uiState is UiState.Success) {
                SearchAndFilterBottomBar(
                    searchQuery = uiState.data.searchQuery,
                    onSearchQueryChanged = { handleIntent(AssignmentIntent.OnSearchQueryChanged(it)) },
                    onClickFilter = {}
                )
            }
        }
    ) { paddingValues ->
        UiStateHandler(
            modifier = Modifier.padding(paddingValues),
            state = uiState
        ) { data ->
            if (data.filteredAssignments.isEmpty()) {
                EmptyState(
                    modifier = Modifier.padding(paddingValues)
                )
            } else {
                AssignmentList(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    assignments = data.filteredAssignments,
                    onViewClick = { handleIntent(AssignmentIntent.OnViewClicked(it)) },
                    onDownloadClick = { handleIntent(AssignmentIntent.OnDownloadClicked(it)) },
                    onViewReportClick = { handleIntent(AssignmentIntent.OnViewReportClicked(it)) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AssignmentScreenPreview() {
    val assignments = listOf(
        Assignment(
            id = "1",
            title = "Physics assignment",
            className = "9th class",
            subject = "English",
            createdDate = "08 Aug 2025",
            dueDate = "22 Oct",
            submittedCount = 24,
            totalCount = 30,
            isOverdue = false,
            filePath = "http://sample.pdf"
        ),
        Assignment(
            id = "2",
            title = "Physics assignment",
            className = "9th class",
            subject = "English",
            createdDate = "08 Aug 2025",
            dueDate = "22 Oct",
            submittedCount = 24,
            totalCount = 30,
            isOverdue = false,
            filePath = "http://sample.pdf"
        ),
        Assignment(
            id = "3",
            title = "Physics assignment",
            className = "9th class",
            subject = "English",
            createdDate = "08 Aug 2025",
            dueDate = "22 Oct",
            submittedCount = 24,
            totalCount = 30,
            isOverdue = true,
            filePath = "http://sample.pdf"
        ),
        Assignment(
            id = "4",
            title = "Physics assignment",
            className = "9th class",
            subject = "English",
            createdDate = "08 Aug 2025",
            dueDate = "22 Oct",
            submittedCount = 24,
            totalCount = 30,
            isOverdue = true,
            filePath = "http://sample.pdf"
        )
    )
    EcareProTheme {
        AssignmentScreenContent(
            uiState = UiState.Success(
                AssignmentUiState(
                    filteredAssignments = assignments
                )
            ),
            handleIntent = {},
            snackbarHostState = SnackbarHostState(),
            snackbarMessage = null
        )
    }
}
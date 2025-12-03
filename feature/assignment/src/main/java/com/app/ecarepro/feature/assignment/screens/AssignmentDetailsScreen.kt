package com.app.ecarepro.feature.assignment.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.assignment.components.AssignmentDetailsTopBar
import com.app.ecarepro.feature.assignment.components.AssignmentTabs
import com.app.ecarepro.feature.assignment.components.StudentRowItem

@Composable
fun AssignmentDetailsScreen(
    viewModel: AssignmentDetailsViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                AssignmentDetailsEvent.NavigateBack -> navigateToBack()
                is AssignmentDetailsEvent.ShowMessage -> {
                    snackbarMessage = event.snackbarMessage
                    snackbarHostState.showSnackbar(event.snackbarMessage.text)
                }
            }
        }
    }

    AssignmentDetailsContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState
    )
}

@Composable
private fun AssignmentDetailsContent(
    uiState: UiState<AssignmentDetailsUiState>,
    handleIntent: (AssignmentDetailsIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    EcareProScaffold(
        containerColor = White,
        topBar = {
            if (uiState is UiState.Success) {
                Column {
                    AssignmentDetailsTopBar(
                        details = uiState.data.headerDetails,
                        onCloseClick = { handleIntent(AssignmentDetailsIntent.OnBackClicked) }
                    )
                    AssignmentTabs(
                        selectedTab = uiState.data.selectedTab,
                        onTabSelected = { tab ->
                            handleIntent(
                                AssignmentDetailsIntent.OnTabSelected(tab)
                            )
                            }
                    )
                }

            }
        }
    ) { paddingValues ->
        UiStateHandler(
            modifier = Modifier.padding(paddingValues),
            state = uiState
        ) { data ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(top = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val style = MaterialTheme.appTypography.interMedium16px.copy(
                        fontSize = 12.sp,
                        color = MaterialTheme.appColors.textPrimary
                    )
                    Text(
                        text = "Roll.no",
                        style = style,
                        modifier = Modifier.width(50.dp)
                    )
                    Text(
                        text = "Student Name",
                        style = style,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "Mode of Submission",
                        style = style,
                    )
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp)
                ) {
                    itemsIndexed(data.filteredStudents) { index, student ->
                        StudentRowItem(student = student, index = index)
                    }
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
private fun AssignmentDetailsScreenPreview() {
    val mockHeader = AssignmentDetailsHeader(
        title = "Physics Assignment",
        className = "9th class",
        subject = "English",
        date = "08 Aug 2025"
    )

    val mockStudents = List(10) { index ->
        StudentSubmissionItem(
            id = index.toString(),
            rollNo = index + 1,
            name = if (index % 2 == 0) "Aditya Chauhan" else "Absam Khan",
            submissionMode = "Offline",
            isSubmitted = index < 5
        )
    }

    val uiState = UiState.Success(
        AssignmentDetailsUiState(
            headerDetails = mockHeader,
            allStudents = mockStudents,
            filteredStudents = mockStudents.take(5),
            selectedTab = SubmissionTab.SUBMITTED
        )
    )

    EcareProTheme {
        AssignmentDetailsContent(
            uiState = uiState,
            handleIntent = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

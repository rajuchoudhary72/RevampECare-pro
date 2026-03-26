package com.app.ecarepro.feature.questionpaper

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.QuestionPaperItem
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.designsystem.core.component.EcareProClassTabs
import com.app.ecarepro.designsystem.core.component.EcareProDropdownField
import com.app.ecarepro.designsystem.core.component.EcareProEmptyState
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProSelectionBottomSheet
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionPaperScreen(
    navigateBack: () -> Unit,
    navigateToViewer: (String, String) -> Unit = { _, _ -> },
    viewModel: QuestionPaperViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is QuestionPaperEvent.NavigateBack -> navigateBack()
                is QuestionPaperEvent.NavigateToViewer -> navigateToViewer(event.title, event.url)
                is QuestionPaperEvent.ShowMessage -> {
                    snackbarMessage = event.snackbarMessage
                    snackbarHostState.showSnackbar(event.snackbarMessage.text)
                }
            }
        }
    }

    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = "Question Paper",
                onNavigationClicked = { viewModel.handleIntent(QuestionPaperIntent.OnBackClicked) },
            )
        },
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null },
        containerColor = MaterialTheme.appColors.background,
    ) { paddingValues ->
        when (val state = uiState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = MaterialTheme.appColors.primary)
                }
            }
            is UiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = state.message,
                        style = MaterialTheme.appTypography.interRegular14px,
                        color = MaterialTheme.appColors.error,
                    )
                }
            }
            is UiState.Success -> {
                QuestionPaperContent(
                    uiState = state.data,
                    handleIntent = viewModel::handleIntent,
                    modifier = Modifier.padding(paddingValues),
                )
            }
        }
    }
}

@Composable
private fun QuestionPaperContent(
    uiState: QuestionPaperUiState,
    handleIntent: (QuestionPaperIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentPapers = uiState.subjectPapers.getOrNull(uiState.selectedSubjectIndex)?.questionPapers ?: emptyList()

    Column(modifier = modifier.fillMaxSize()) {
        if (uiState.subjectTabs.isNotEmpty()) {
            EcareProClassTabs(
                selectedTabIndex = uiState.selectedSubjectIndex,
                tabs = uiState.subjectTabs,
                onTabClick = { handleIntent(QuestionPaperIntent.OnSubjectTabSelected(it)) },
                applyOrdinalTransform = false,
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            if (uiState.isPapersLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.appColors.primary)
                }
            } else if (currentPapers.isEmpty()) {
                EcareProEmptyState(message = "No question papers available")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                ) {
                    items(currentPapers) { paper ->
                        QuestionPaperCard(
                            paper = paper,
                            onViewClick = { handleIntent(QuestionPaperIntent.OnViewClicked(paper)) },
                            onDownloadClick = { handleIntent(QuestionPaperIntent.OnDownloadClicked(paper)) },
                        )
                        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)
                    }
                }
            }
        }

        // Bottom dropdowns
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.appColors.background)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (uiState.isStaff) {
                EcareProDropdownField(
                    label = "Class",
                    value = uiState.selectedClass?.className,
                    placeholder = "Select Class",
                    onClick = { handleIntent(QuestionPaperIntent.OnClassSelectClicked) },
                )
            }
            EcareProDropdownField(
                label = "Academic Year",
                value = uiState.selectedYear?.session,
                placeholder = "Select Academic Year",
                onClick = { handleIntent(QuestionPaperIntent.OnYearSelectClicked) },
            )
        }
    }

    // Class selection bottom sheet (staff only)
    if (uiState.isStaff) {
        val classOptions = uiState.classes.map { it.className ?: it.id }
        val selectedClassName = uiState.selectedClass?.className ?: uiState.selectedClass?.id
        EcareProSelectionBottomSheet(
            isVisible = uiState.showClassSheet,
            title = "Select Class",
            options = classOptions,
            selectedOptions = listOfNotNull(selectedClassName),
            onDismiss = { handleIntent(QuestionPaperIntent.OnDismissClassSheet) },
            onOptionsSelected = { selected ->
                val selectedName = selected.firstOrNull() ?: return@EcareProSelectionBottomSheet
                val cls = uiState.classes.firstOrNull { (it.className ?: it.id) == selectedName } ?: return@EcareProSelectionBottomSheet
                handleIntent(QuestionPaperIntent.OnClassSelected(cls.id))
            },
        )
    }

    // Year selection bottom sheet
    val yearOptions = uiState.academicYears.map { it.session }
    EcareProSelectionBottomSheet(
        isVisible = uiState.showYearSheet,
        title = "Select Academic Year",
        options = yearOptions,
        selectedOptions = listOfNotNull(uiState.selectedYear?.session),
        onDismiss = { handleIntent(QuestionPaperIntent.OnDismissYearSheet) },
        onOptionsSelected = { selected ->
            val selectedSession = selected.firstOrNull() ?: return@EcareProSelectionBottomSheet
            val year = uiState.academicYears.firstOrNull { it.session == selectedSession } ?: return@EcareProSelectionBottomSheet
            handleIntent(QuestionPaperIntent.OnYearSelected(year.yrID))
        },
    )
}

@Composable
private fun QuestionPaperCard(
    paper: QuestionPaperItem,
    onViewClick: () -> Unit,
    onDownloadClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.appColors.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(id = com.app.ecarepro.core.designsystem.R.drawable.icon_document),
                contentDescription = null,
                tint = MaterialTheme.appColors.primary,
                modifier = Modifier.size(20.dp),
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = paper.examName ?: "Question Paper",
                style = MaterialTheme.appTypography.interSemiBold14px,
                color = MaterialTheme.appColors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!paper.fileSize.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = paper.fileSize!!,
                    style = MaterialTheme.appTypography.interRegular12px,
                    color = MaterialTheme.appColors.textSecondary,
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Card(
                onClick = onViewClick,
                shape = RoundedCornerShape(6.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.appColors.primary),
            ) {
                Text(
                    text = "View",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.appTypography.interSemiBold14px,
                    color = Color.White,
                )
            }
            Card(
                onClick = onDownloadClick,
                shape = RoundedCornerShape(6.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                border = BorderStroke(1.dp, MaterialTheme.appColors.primary),
            ) {
                Text(
                    text = "Download",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.appTypography.interSemiBold14px,
                    color = MaterialTheme.appColors.primary,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QuestionPaperCardPreview() {
    EcareProTheme {
        QuestionPaperCard(
            paper = QuestionPaperItem(
                examName = "Mid Term Exam 2024",
                file = "https://example.com/paper.pdf",
                fileSize = "1.2 MB",
                updatedOn = "2024-01-15",
            ),
            onViewClick = {},
            onDownloadClick = {},
        )
    }
}

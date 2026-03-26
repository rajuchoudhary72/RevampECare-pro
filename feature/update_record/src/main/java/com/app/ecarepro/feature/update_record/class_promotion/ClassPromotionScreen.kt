package com.app.ecarepro.feature.update_record.class_promotion

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.Class
import com.app.ecarepro.core.domain.model.NextSessionClass
import com.app.ecarepro.core.domain.model.PromotionSection
import com.app.ecarepro.core.domain.model.PromotionStudent
import com.app.ecarepro.designsystem.core.component.BottomSearchBarView
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.component.EcareProClassTabs
import com.app.ecarepro.designsystem.core.component.EcareProEmptyState
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProSelectionBottomSheet
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.component.SortBottomSheet
import com.app.ecarepro.designsystem.core.component.SortOption
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.update_record.class_promotion.component.PromoteClassBottomSheet

@Composable
fun ClassPromotionScreen(
    viewModel: ClassPromotionViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is ClassPromotionEvent.NavigateBack -> navigateBack()
                is ClassPromotionEvent.ShowMessage -> {
                    snackbarMessage = event.message
                    snackbarHostState.showSnackbar(event.message.text)
                }
            }
        }
    }

    ClassPromotionContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null },
        handleIntent = viewModel::handleIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClassPromotionContent(
    uiState: ClassPromotionUiState,
    snackbarHostState: SnackbarHostState,
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit,
    handleIntent: (ClassPromotionIntent) -> Unit,
) {
    val promotingStudent = uiState.promotingStudentStID?.let { stID ->
        uiState.allStudents.find { it.student.stID == stID }?.student
    }

    if (uiState.showSectionSheet && uiState.selectedNextClass != null) {
        val sections = uiState.selectedNextClass.sections
        EcareProSelectionBottomSheet(
            title = "Select section",
            isVisible = true,
            options = sections.map { it.secName },
            selectedOptions = emptyList(),
            isMultiSelection = false,
            onDismiss = { handleIntent(ClassPromotionIntent.DismissSectionSheet) },
            onOptionsSelected = { selected ->
                val pickedName = selected.firstOrNull() ?: return@EcareProSelectionBottomSheet
                val sec = sections.find { it.secName == pickedName } ?: return@EcareProSelectionBottomSheet
                handleIntent(ClassPromotionIntent.OnSectionSelected(sec.secID, sec.secName))
            },
        )
    }

    if (uiState.showClassSheet && promotingStudent != null) {
        PromoteClassBottomSheet(
            nextSessionClasses = promotingStudent.nextSessionClasses,
            selectedClass = uiState.selectedNextClass,
            onClassSelected = { handleIntent(ClassPromotionIntent.OnNextClassSelected(it)) },
            onPromoteClick = { handleIntent(ClassPromotionIntent.OnPromoteClassConfirm) },
            onDismiss = { handleIntent(ClassPromotionIntent.DismissClassSheet) },
        )
    }

    if (uiState.showSortSheet) {
        SortBottomSheet(
            currentSortConfig = uiState.sortConfig,
            onSortSelected = { handleIntent(ClassPromotionIntent.OnSortSelected(it)) },
            onDismiss = { handleIntent(ClassPromotionIntent.DismissSortSheet) },
            availableOptions = listOf(SortOption.ROLL_NUMBER, SortOption.ADMISSION_NUMBER, SortOption.NAME),
        )
    }

    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = "Class promotion",
                onNavigationClicked = { handleIntent(ClassPromotionIntent.OnBackClicked) },
            )
        },
        bottomBar = {
            Column(modifier = Modifier.navigationBarsPadding()) {
                BottomSearchBarView(
                    searchText = uiState.searchQuery,
                    onSearchTextChange = { handleIntent(ClassPromotionIntent.OnSearchQueryChanged(it)) },
                    placeholder = "Search by name, admission number...",
                    showSortButton = true,
                    onSortClick = { handleIntent(ClassPromotionIntent.OnSortClick) },
                )
            }
        },
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = onSnackbarDismissed,
        containerColor = White,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            if (uiState.isLoadingClasses) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.appColors.primary)
                }
                return@EcareProScaffold
            }

            if (uiState.classes.isNotEmpty()) {
                EcareProClassTabs(
                    selectedTabIndex = uiState.selectedClassIndex,
                    tabs = uiState.classes.map { it.className ?: "" },
                    onTabClick = { handleIntent(ClassPromotionIntent.SelectClass(it)) },
                    applyOrdinalTransform = false,
                )
                HorizontalDivider(color = Color(0xFFEEEEEE))
            }

            when {
                uiState.isLoadingStudents -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.appColors.primary)
                    }
                }
                uiState.errorMessage != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = uiState.errorMessage,
                            style = MaterialTheme.appTypography.interRegular14px,
                            color = MaterialTheme.appColors.textSecondary,
                        )
                    }
                }
                uiState.filteredStudents.isEmpty() -> {
                    EcareProEmptyState(message = "No students found")
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(uiState.filteredStudents, key = { it.student.stID }) { studentState ->
                            StudentPromotionItem(
                                studentState = studentState,
                                onPromoteClick = { handleIntent(ClassPromotionIntent.OnPromoteClick(studentState.student.stID)) },
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                thickness = 0.5.dp,
                                color = Color(0xFFEEEEEE),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentPromotionItem(
    studentState: PromotionStudentUiState,
    onPromoteClick: () -> Unit,
) {
    val student = studentState.student
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        EcareProAsyncImage(
            imageUrl = student.photo,
            contentDescription = student.name,
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${student.name}, ${student.studentClass}",
                style = MaterialTheme.appTypography.interSemiBold14px,
                color = MaterialTheme.appColors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Admission No: ${student.admissionNumber}  ·  Roll No: ${student.rollNumber}",
                style = MaterialTheme.appTypography.interRegular14px.copy(fontSize = 12.sp),
                color = MaterialTheme.appColors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "D/O: ${student.fatherName}",
                style = MaterialTheme.appTypography.interRegular14px.copy(fontSize = 12.sp),
                color = MaterialTheme.appColors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        if (studentState.isPromoted) {
            Row(
                modifier = Modifier
                    .background(
                        color = Color(0xFF34C759).copy(alpha = 0.12f),
                        shape = RoundedCornerShape(16.dp),
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(Color(0xFF34C759), CircleShape),
                )
                Text(
                    text = "Promoted",
                    style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 12.sp),
                    color = Color(0xFF34C759),
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.appColors.primary.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(16.dp),
                    )
                    .clickable { onPromoteClick() }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(MaterialTheme.appColors.primary, CircleShape),
                )
                Text(
                    text = "Promote",
                    style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 12.sp),
                    color = MaterialTheme.appColors.primary,
                )
            }
        }
    }
}

// ─── Preview Data ──────────────────────────────────────────────────────────────

private val previewClasses = listOf(
    Class(classID = 0, className = "LKG A", id = "1", isSelect = false),
    Class(classID = 0, className = "LKG B", id = "2", isSelect = false),
    Class(classID = 0, className = "UKG A", id = "3", isSelect = false),
)

private fun makeStudent(id: Int, name: String, father: String, promoted: Boolean = false) =
    PromotionStudentUiState(
        student = PromotionStudent(
            stID = id,
            name = name,
            studentClass = "LKG A",
            rollNumber = id.toString(),
            admissionNumber = "AB${1000 + id}",
            fatherName = father,
            photo = "",
            isSelected = promoted,
            nextSessionClasses = listOf(
                NextSessionClass(classID = 10, className = "LKG B", sections = listOf(PromotionSection(classID = 10, secID = 1, secName = "A", isSelected = true)), isSelected = true),
                NextSessionClass(classID = 11, className = "UKG A", sections = listOf(PromotionSection(classID = 11, secID = 2, secName = "A", isSelected = false)), isSelected = false),
            ),
        ),
        promotedToClassID = if (promoted) 10 else null,
        promotedToSectionID = if (promoted) 1 else null,
        promotedClassName = if (promoted) "LKG B" else null,
        promotedSectionName = if (promoted) "A" else null,
    )

private val previewStudents = listOf(
    makeStudent(1, "AASTHA SAINI", "Mr. Anil Singh Saini", promoted = true),
    makeStudent(2, "ANAND KUMAR", "Mr. Rajesh Kumar"),
    makeStudent(3, "ADITYA YADAV", "Mr. Suresh Yadav"),
)

@Preview(showBackground = true, name = "Class Promotion - List")
@Composable
private fun ClassPromotionContentPreview() {
    EcareProTheme {
        ClassPromotionContent(
            uiState = ClassPromotionUiState(
                classes = previewClasses,
                selectedClassIndex = 0,
                allStudents = previewStudents,
                filteredStudents = previewStudents,
            ),
            snackbarHostState = remember { SnackbarHostState() },
            snackbarMessage = null,
            onSnackbarDismissed = {},
            handleIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "Class Promotion - Loading")
@Composable
private fun ClassPromotionLoadingPreview() {
    EcareProTheme {
        ClassPromotionContent(
            uiState = ClassPromotionUiState(isLoadingClasses = true),
            snackbarHostState = remember { SnackbarHostState() },
            snackbarMessage = null,
            onSnackbarDismissed = {},
            handleIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "Student Promotion Item - Not Promoted")
@Composable
private fun StudentPromotionItemNotPromotedPreview() {
    EcareProTheme {
        StudentPromotionItem(
            studentState = makeStudent(1, "AASTHA SAINI", "Mr. Anil Singh Saini"),
            onPromoteClick = {},
        )
    }
}

@Preview(showBackground = true, name = "Student Promotion Item - Promoted")
@Composable
private fun StudentPromotionItemPromotedPreview() {
    EcareProTheme {
        StudentPromotionItem(
            studentState = makeStudent(1, "AASTHA SAINI", "Mr. Anil Singh Saini", promoted = true),
            onPromoteClick = {},
        )
    }
}

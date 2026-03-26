package com.app.ecarepro.feature.update_record.update_house

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.derivedStateOf
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
import com.app.ecarepro.core.domain.model.HouseItem
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

private val NotAssignedBackground = Color(0xFFFFE5E5)
private val NotAssignedBorder = Color(0xFFFF3B30)
private val NotAssignedText = Color(0xFFFF3B30)
private val AssignedBackground = Color(0xFFF0F8FF)
private val AssignedBorder = Color(0xFFB0D4F1)

@Composable
fun UpdateHouseScreen(
    viewModel: UpdateHouseViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is UpdateHouseEvent.NavigateBack -> navigateBack()
                is UpdateHouseEvent.ShowMessage -> {
                    snackbarMessage = event.message
                    snackbarHostState.showSnackbar(event.message.text)
                }
            }
        }
    }

    UpdateHouseContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null },
        handleIntent = viewModel::handleIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UpdateHouseContent(
    uiState: UpdateHouseUiState,
    snackbarHostState: SnackbarHostState,
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit,
    handleIntent: (UpdateHouseIntent) -> Unit,
) {
    val listState = rememberLazyListState()

    val filteredStudents by remember(uiState.students, uiState.searchQuery) {
        derivedStateOf {
            if (uiState.searchQuery.isBlank()) uiState.students
            else uiState.students.filter { it.name.contains(uiState.searchQuery, ignoreCase = true) }
        }
    }

    val houseSheetStudent = uiState.houseSheetForStID?.let { stID ->
        uiState.students.find { it.stID == stID }
    }
    if (houseSheetStudent != null) {
        EcareProSelectionBottomSheet(
            title = "Select house",
            isVisible = true,
            options = uiState.houses.map { it.houseName },
            selectedOptions = if (houseSheetStudent.currentHouseID != 0) {
                listOf(uiState.houses.find { it.houseID == houseSheetStudent.currentHouseID }?.houseName ?: "")
            } else emptyList(),
            isMultiSelection = false,
            onDismiss = { handleIntent(UpdateHouseIntent.DismissHouseSheet) },
            onOptionsSelected = { selected ->
                val house = uiState.houses.find { it.houseName == selected.firstOrNull() }
                if (house != null) {
                    handleIntent(UpdateHouseIntent.OnHouseSelected(houseSheetStudent.stID, house.houseID))
                }
            },
        )
    }

    if (uiState.showSortSheet) {
        SortBottomSheet(
            currentSortConfig = uiState.sortConfig,
            onSortSelected = { handleIntent(UpdateHouseIntent.OnSortSelected(it)) },
            onDismiss = { handleIntent(UpdateHouseIntent.DismissSortSheet) },
            availableOptions = listOf(SortOption.ROLL_NUMBER, SortOption.ADMISSION_NUMBER, SortOption.NAME),
        )
    }

    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = "Assign house",
                onNavigationClicked = { handleIntent(UpdateHouseIntent.OnBackClicked) },
            )
        },
        bottomBar = {
            Column(modifier = Modifier.navigationBarsPadding()) {
                BottomSearchBarView(
                    searchText = uiState.searchQuery,
                    onSearchTextChange = { handleIntent(UpdateHouseIntent.OnSearchQueryChanged(it)) },
                    placeholder = "Search by student name",
                    showSortButton = true,
                    onSortClick = { handleIntent(UpdateHouseIntent.OnSortClick) },
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
                    onTabClick = { handleIntent(UpdateHouseIntent.SelectClass(it)) },
                    applyOrdinalTransform = false,
                )
                HorizontalDivider(color = Color(0xFFEEEEEE))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "STUDENT INFO",
                    style = MaterialTheme.appTypography.interRegular14px.copy(fontSize = 11.sp),
                    color = MaterialTheme.appColors.textSecondary,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "HOUSE NAME",
                    style = MaterialTheme.appTypography.interRegular14px.copy(fontSize = 11.sp),
                    color = MaterialTheme.appColors.textSecondary,
                )
            }
            HorizontalDivider(color = Color(0xFFEEEEEE))

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
                filteredStudents.isEmpty() -> {
                    EcareProEmptyState(message = "No students found")
                }
                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        itemsIndexed(filteredStudents, key = { _, s -> s.stID }) { _, studentState ->
                            HouseStudentItem(
                                studentState = studentState,
                                onHouseButtonClicked = { handleIntent(UpdateHouseIntent.OnHouseButtonClicked(studentState.stID)) },
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
private fun HouseStudentItem(
    studentState: HouseStudentUiState,
    onHouseButtonClicked: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        EcareProAsyncImage(
            imageUrl = studentState.photo,
            contentDescription = studentState.name,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = studentState.name,
                style = MaterialTheme.appTypography.interSemiBold14px,
                color = MaterialTheme.appColors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Admission No: ${studentState.admissionNumber}",
                style = MaterialTheme.appTypography.interRegular14px.copy(fontSize = 12.sp),
                color = MaterialTheme.appColors.textSecondary,
            )
        }
        Spacer(modifier = Modifier.width(12.dp))

        val isAssigned = studentState.currentHouseID != 0
        Box(
            modifier = Modifier
                .background(
                    color = if (isAssigned) AssignedBackground else NotAssignedBackground,
                    shape = RoundedCornerShape(20.dp),
                )
                .border(
                    width = 1.dp,
                    color = if (isAssigned) AssignedBorder else NotAssignedBorder,
                    shape = RoundedCornerShape(20.dp),
                )
                .clickable { onHouseButtonClicked() }
                .padding(horizontal = 14.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (isAssigned) studentState.currentHouseName else "Not assigned",
                style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 13.sp),
                color = if (isAssigned) MaterialTheme.appColors.textPrimary else NotAssignedText,
                maxLines = 1,
            )
        }
    }
}

// ─── Preview Data ──────────────────────────────────────────────────────────────

private val previewClasses = listOf(
    Class(classID = 0, className = "LKG A", id = "1", isSelect = false),
    Class(classID = 0, className = "LKG B", id = "2", isSelect = false),
)

private val previewHouses = listOf(
    HouseItem(houseID = 1, houseName = "Blue"),
    HouseItem(houseID = 2, houseName = "Yellow"),
    HouseItem(houseID = 3, houseName = "Green"),
    HouseItem(houseID = 4, houseName = "Red"),
)

private val previewStudents = listOf(
    HouseStudentUiState(stID = 1, name = "ANAISHA", photo = "", admissionNumber = "7054", rollNumber = "1", currentHouseID = 1, currentHouseName = "Blue"),
    HouseStudentUiState(stID = 2, name = "ANISHKA RAWAT", photo = "", admissionNumber = "A7063", rollNumber = "2", currentHouseID = 0, currentHouseName = ""),
    HouseStudentUiState(stID = 3, name = "ANSHUMAN NEGI", photo = "", admissionNumber = "6626", rollNumber = "3", currentHouseID = 2, currentHouseName = "Yellow"),
)

@Preview(showBackground = true, name = "Update House - List")
@Composable
private fun UpdateHouseContentPreview() {
    EcareProTheme {
        UpdateHouseContent(
            uiState = UpdateHouseUiState(
                classes = previewClasses,
                students = previewStudents,
                originalHouses = mapOf(1 to 1, 2 to 0, 3 to 2),
                houses = previewHouses,
            ),
            snackbarHostState = remember { SnackbarHostState() },
            snackbarMessage = null,
            onSnackbarDismissed = {},
            handleIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "Update House - Loading")
@Composable
private fun UpdateHouseLoadingPreview() {
    EcareProTheme {
        UpdateHouseContent(
            uiState = UpdateHouseUiState(isLoadingClasses = true),
            snackbarHostState = remember { SnackbarHostState() },
            snackbarMessage = null,
            onSnackbarDismissed = {},
            handleIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "House Student Item - Assigned")
@Composable
private fun HouseStudentItemAssignedPreview() {
    EcareProTheme {
        HouseStudentItem(
            studentState = HouseStudentUiState(stID = 1, name = "ANAISHA", photo = "", admissionNumber = "7054", rollNumber = "1", currentHouseID = 1, currentHouseName = "Blue"),
            onHouseButtonClicked = {},
        )
    }
}

@Preview(showBackground = true, name = "House Student Item - Not Assigned")
@Composable
private fun HouseStudentItemNotAssignedPreview() {
    EcareProTheme {
        HouseStudentItem(
            studentState = HouseStudentUiState(stID = 2, name = "ANISHKA RAWAT", photo = "", admissionNumber = "A7063", rollNumber = "2", currentHouseID = 0, currentHouseName = ""),
            onHouseButtonClicked = {},
        )
    }
}

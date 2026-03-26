package com.app.ecarepro.feature.update_record.manage_roll_number

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.Class
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.component.EcareProClassTabs
import com.app.ecarepro.designsystem.core.component.EcareProEmptyState
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.component.SortBottomSheet
import com.app.ecarepro.designsystem.core.component.SortOption
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

private val DuplicateRed = Color(0xFFFFE5E5)
private val DuplicateBorder = Color(0xFFFF3B30)
private val NormalBorder = Color(0xFFE0E0E0)

@Composable
fun ManageRollNumberScreen(
    viewModel: ManageRollNumberViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is ManageRollNumberEvent.NavigateBack -> navigateBack()
                is ManageRollNumberEvent.ShowMessage -> {
                    snackbarMessage = event.message
                    snackbarHostState.showSnackbar(event.message.text)
                }
            }
        }
    }

    ManageRollNumberContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null },
        handleIntent = viewModel::handleIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ManageRollNumberContent(
    uiState: ManageRollNumberUiState,
    snackbarHostState: SnackbarHostState,
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit,
    handleIntent: (ManageRollNumberIntent) -> Unit,
) {
    val listState = rememberLazyListState()

    if (uiState.showSortSheet) {
        SortBottomSheet(
            currentSortConfig = uiState.sortConfig,
            onSortSelected = { handleIntent(ManageRollNumberIntent.OnSortSelected(it)) },
            onDismiss = { handleIntent(ManageRollNumberIntent.DismissSortSheet) },
            availableOptions = listOf(SortOption.ROLL_NUMBER, SortOption.ADMISSION_NUMBER, SortOption.NAME),
        )
    }

    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = "Manage roll number",
                onNavigationClicked = { handleIntent(ManageRollNumberIntent.OnBackClicked) },
            )
        },
        bottomBar = {
            Column(modifier = Modifier.navigationBarsPadding()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(White)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Button(
                        onClick = { handleIntent(ManageRollNumberIntent.OnAutoAssign) },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(23.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.appColors.primary),
                    ) {
                        Text(
                            text = "Auto Assign",
                            style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 15.sp),
                            color = White,
                        )
                    }
                    TextButton(
                        onClick = { handleIntent(ManageRollNumberIntent.OnSortClick) },
                        modifier = Modifier.height(46.dp),
                    ) {
                        Text(
                            text = "Roll no.",
                            style = MaterialTheme.appTypography.interMedium16px.copy(fontSize = 14.sp),
                            color = MaterialTheme.appColors.textPrimary,
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Sort",
                            tint = MaterialTheme.appColors.textPrimary,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
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
                    onTabClick = { handleIntent(ManageRollNumberIntent.SelectClass(it)) },
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
                    text = "ROLL NUMBER",
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
                uiState.students.isEmpty() -> {
                    EcareProEmptyState(message = "No students found")
                }
                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        itemsIndexed(uiState.students, key = { _, s -> s.stID }) { _, studentState ->
                            RollNumberStudentItem(
                                studentState = studentState,
                                onRollNumberChanged = { handleIntent(ManageRollNumberIntent.OnRollNumberChanged(studentState.stID, it)) },
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
private fun RollNumberStudentItem(
    studentState: RollNumberStudentUiState,
    onRollNumberChanged: (String) -> Unit,
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

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val containerColor = if (studentState.isDuplicate) DuplicateRed else White
            val borderColor = if (studentState.isDuplicate) DuplicateBorder else NormalBorder

            BasicTextField(
                value = studentState.currentRollNumber,
                onValueChange = { new ->
                    if (new.all { it.isDigit() } && new.length <= 4) onRollNumberChanged(new)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                cursorBrush = SolidColor(MaterialTheme.appColors.primary),
                textStyle = MaterialTheme.appTypography.interSemiBold14px.copy(
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    color = if (studentState.isDuplicate) DuplicateBorder else MaterialTheme.appColors.textPrimary,
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .size(width = 56.dp, height = 40.dp)
                            .background(containerColor, RoundedCornerShape(8.dp))
                            .border(1.dp, borderColor, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        innerTextField()
                    }
                },
            )
            if (studentState.isDuplicate) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Duplicate",
                    style = MaterialTheme.appTypography.interRegular14px.copy(fontSize = 10.sp),
                    color = DuplicateBorder,
                )
            }
        }
    }
}

// ─── Preview Data ──────────────────────────────────────────────────────────────

private val previewClasses = listOf(
    Class(classID = 0, className = "LKG A", id = "1", isSelect = false),
    Class(classID = 0, className = "LKG B", id = "2", isSelect = false),
)

private val previewStudents = listOf(
    RollNumberStudentUiState(stID = 1, name = "ANAISHA", photo = "", admissionNumber = "7054", houseID = 0, currentRollNumber = "1"),
    RollNumberStudentUiState(stID = 2, name = "ANISHKA RAWAT", photo = "", admissionNumber = "A7063", houseID = 0, currentRollNumber = "1", isDuplicate = true),
    RollNumberStudentUiState(stID = 3, name = "ANSHUMAN NEGI", photo = "", admissionNumber = "6626", houseID = 0, currentRollNumber = "3"),
    RollNumberStudentUiState(stID = 4, name = "ARNAV RAWAT", photo = "", admissionNumber = "6931", houseID = 0, currentRollNumber = ""),
)

@Preview(showBackground = true, name = "Manage Roll Number - List")
@Composable
private fun ManageRollNumberContentPreview() {
    EcareProTheme {
        ManageRollNumberContent(
            uiState = ManageRollNumberUiState(
                classes = previewClasses,
                students = previewStudents,
                originalRollNumbers = mapOf(1 to "2", 2 to "1", 3 to "3", 4 to ""),
            ),
            snackbarHostState = remember { SnackbarHostState() },
            snackbarMessage = null,
            onSnackbarDismissed = {},
            handleIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "Manage Roll Number - Loading")
@Composable
private fun ManageRollNumberLoadingPreview() {
    EcareProTheme {
        ManageRollNumberContent(
            uiState = ManageRollNumberUiState(isLoadingClasses = true),
            snackbarHostState = remember { SnackbarHostState() },
            snackbarMessage = null,
            onSnackbarDismissed = {},
            handleIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "Roll Number Item - Normal")
@Composable
private fun RollNumberStudentItemNormalPreview() {
    EcareProTheme {
        RollNumberStudentItem(
            studentState = RollNumberStudentUiState(stID = 1, name = "ANAISHA", photo = "", admissionNumber = "7054", houseID = 0, currentRollNumber = "5"),
            onRollNumberChanged = {},
        )
    }
}

@Preview(showBackground = true, name = "Roll Number Item - Duplicate")
@Composable
private fun RollNumberStudentItemDuplicatePreview() {
    EcareProTheme {
        RollNumberStudentItem(
            studentState = RollNumberStudentUiState(stID = 2, name = "ANISHKA RAWAT", photo = "", admissionNumber = "A7063", houseID = 0, currentRollNumber = "5", isDuplicate = true),
            onRollNumberChanged = {},
        )
    }
}

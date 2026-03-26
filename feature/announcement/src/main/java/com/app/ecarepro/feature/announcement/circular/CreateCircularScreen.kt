package com.app.ecarepro.feature.announcement.circular

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
import com.app.ecarepro.designsystem.core.component.Button
import com.app.ecarepro.designsystem.core.component.EcareProDatePicker
import com.app.ecarepro.designsystem.core.component.EcareProDropdownField
import com.app.ecarepro.designsystem.core.component.EcareProFileAttachment
import com.app.ecarepro.designsystem.core.component.EcareProFileUploadBottomSheet
import com.app.ecarepro.designsystem.core.component.EcareProInputField
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProSelectionBottomSheet
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CreateCircularScreen(
    viewModel: CreateCircularViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is CreateCircularEvent.NavigateBack -> navigateBack()
                is CreateCircularEvent.CircularSaved -> navigateBack()
                is CreateCircularEvent.ShowMessage -> {
                    snackbarMessage = event.snackbarMessage
                    snackbarHostState.showSnackbar(event.snackbarMessage.text)
                }
            }
        }
    }

    CreateCircularScreenContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateCircularScreenContent(
    uiState: UiState<CreateCircularUiState>,
    handleIntent: (CreateCircularIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit,
) {
    val isSaving = (uiState as? UiState.Success)?.data?.isSaving ?: false

    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = "Create circular",
                onNavigationClicked = { handleIntent(CreateCircularIntent.OnBackClicked) },
            )
        },
        containerColor = White,
        isLoading = isSaving,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = onSnackbarDismissed,
    ) { paddingValues ->
        UiStateHandler(
            modifier = Modifier.padding(paddingValues),
            state = uiState,
            onRetry = { handleIntent(CreateCircularIntent.OnRetry) },
        ) { data ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Scrollable form content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Title field
                    EcareProInputField(
                        label = "Title",
                        value = data.title,
                        placeholder = "Add circular title",
                        onValueChange = { handleIntent(CreateCircularIntent.OnTitleChanged(it)) },
                    )

                    // Date field
                    EcareProDropdownField(
                        label = "Date",
                        value = data.releadOnDisplay.ifEmpty { null },
                        placeholder = "Select date",
                        onClick = { handleIntent(CreateCircularIntent.OnShowDatePicker) },
                    )

                    // Recipient field
                    EcareProDropdownField(
                        label = "Send circular to",
                        value = data.selectedRecipientType.label,
                        placeholder = "Add one or more assignee",
                        onClick = { handleIntent(CreateCircularIntent.OnShowRecipientPicker) },
                    )

                    // Conditional: Staff type + Select Staff
                    if (data.selectedRecipientType == RecipientType.STAFF_TYPE) {
                        EcareProDropdownField(
                            label = "Staff Type",
                            value = if (data.selectedStaffTypes.isEmpty()) null
                            else data.selectedStaffTypes.joinToString(", ") { it.staffType },
                            placeholder = "Select staff type",
                            onClick = { handleIntent(CreateCircularIntent.OnShowStaffTypePicker) },
                        )

                        if (data.selectedStaffTypes.isNotEmpty()) {
                            EcareProDropdownField(
                                label = "Select Staff",
                                value = if (data.selectedStaff.isEmpty()) null
                                else data.selectedStaff.joinToString(", ") { it.name },
                                placeholder = "Select staff members",
                                onClick = { handleIntent(CreateCircularIntent.OnShowStaffPicker) },
                            )
                        }
                    }

                    // Conditional: Scholar Type + Select Class (for ALL_CLASSES or STUDENTS_PARENTS)
                    if (data.selectedRecipientType == RecipientType.ALL_CLASSES ||
                        data.selectedRecipientType == RecipientType.STUDENTS_PARENTS
                    ) {
                        EcareProDropdownField(
                            label = "Scholar Type",
                            value = data.selectedScholarType.label,
                            placeholder = "Select scholar type",
                            onClick = { handleIntent(CreateCircularIntent.OnShowScholarTypePicker) },
                        )

                        EcareProDropdownField(
                            label = "Select Class",
                            value = if (data.selectedClasses.isEmpty()) null
                            else data.selectedClasses.joinToString(", ") { it.className ?: "" },
                            placeholder = "Select classes",
                            onClick = { handleIntent(CreateCircularIntent.OnShowClassPicker) },
                        )

                        if (data.selectedRecipientType == RecipientType.STUDENTS_PARENTS &&
                            data.selectedClasses.isNotEmpty()
                        ) {
                            EcareProDropdownField(
                                label = "Students/Parents",
                                value = if (data.selectedStudentParents.isEmpty()) null
                                else "${data.selectedStudentParents.size} selected",
                                placeholder = "Select students/parents",
                                onClick = { handleIntent(CreateCircularIntent.OnShowStudentParentPicker) },
                            )
                        }
                    }

                    // Description field
                    EcareProInputField(
                        label = "Add description",
                        value = data.description,
                        placeholder = "Explain what this circular is about",
                        singleLine = false,
                        minLines = 4,
                        onValueChange = { handleIntent(CreateCircularIntent.OnDescriptionChanged(it)) },
                    )

                    // File attachment
                    EcareProFileAttachment(
                        selectedFile = if (data.selectedFile != null) listOf(data.selectedFile) else emptyList(),
                        onClickPickFile = { handleIntent(CreateCircularIntent.OnShowFilePicker) },
                        onClickDeleteFile = { handleIntent(CreateCircularIntent.OnFileRemoved) },
                    )

                    // Make it Active row
                    CheckboxRow(
                        label = "Make it Active",
                        checked = data.isActive,
                        onCheckedChange = { handleIntent(CreateCircularIntent.OnStatusChanged(it)) },
                    )

                    // Must read row
                    CheckboxRow(
                        label = "Must read",
                        checked = data.mustRead,
                        onCheckedChange = { handleIntent(CreateCircularIntent.OnMustReadChanged(it)) },
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Bottom button (outside scroll)
                Button(
                    title = "Add circular",
                    onClick = { handleIntent(CreateCircularIntent.OnSubmit) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    enabled = !isSaving,
                )
            }

            // Date picker
            EcareProDatePicker(
                isVisible = data.isDatePickerVisible,
                onDismiss = { handleIntent(CreateCircularIntent.OnDismissDatePicker) },
                onDateSelected = { apiDate ->
                    val displayDate = try {
                        val parsed = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(apiDate)
                        SimpleDateFormat("dd MMMM yyyy", Locale.US).format(parsed ?: Date())
                    } catch (e: Exception) {
                        apiDate
                    }
                    handleIntent(CreateCircularIntent.OnDateSelected(apiDate, displayDate))
                    handleIntent(CreateCircularIntent.OnDismissDatePicker)
                },
            )

            // Recipient picker bottom sheet
            if (data.isRecipientPickerVisible) {
                RecipientPickerBottomSheet(
                    selectedType = data.selectedRecipientType,
                    onTypeSelected = { handleIntent(CreateCircularIntent.OnRecipientTypeSelected(it)) },
                    onDismiss = { handleIntent(CreateCircularIntent.OnDismissRecipientPicker) },
                )
            }

            // Staff type picker
            EcareProSelectionBottomSheet(
                title = "Staff Type",
                isVisible = data.isStaffTypePickerVisible,
                options = data.staffTypes.map { it.staffType },
                selectedOptions = data.selectedStaffTypes.map { it.staffType },
                isMultiSelection = true,
                onDismiss = { handleIntent(CreateCircularIntent.OnDismissStaffTypePicker) },
                onOptionsSelected = { selected ->
                    val staffTypes = data.staffTypes.filter { it.staffType in selected }
                    handleIntent(CreateCircularIntent.OnStaffTypesSelected(staffTypes))
                },
            )

            // Staff picker
            EcareProSelectionBottomSheet(
                title = "Select Staff",
                isVisible = data.isStaffPickerVisible,
                options = data.staffContacts.map { it.name },
                selectedOptions = data.selectedStaff.map { it.name },
                isMultiSelection = true,
                onDismiss = { handleIntent(CreateCircularIntent.OnDismissStaffPicker) },
                onOptionsSelected = { selected ->
                    val staff = data.staffContacts.filter { it.name in selected }
                    handleIntent(CreateCircularIntent.OnStaffSelected(staff))
                },
            )

            // Scholar type picker
            EcareProSelectionBottomSheet(
                title = "Scholar Type",
                isVisible = data.isScholarTypePickerVisible,
                options = ScholarType.entries.map { it.label },
                selectedOptions = listOf(data.selectedScholarType.label),
                isMultiSelection = false,
                onDismiss = { handleIntent(CreateCircularIntent.OnDismissScholarTypePicker) },
                onOptionsSelected = { selected ->
                    val scholarType = ScholarType.entries.find { it.label == selected.firstOrNull() }
                    scholarType?.let { handleIntent(CreateCircularIntent.OnScholarTypeSelected(it)) }
                },
            )

            // Class picker
            EcareProSelectionBottomSheet(
                title = "Select Class",
                isVisible = data.isClassPickerVisible,
                options = data.classes.mapNotNull { it.className },
                selectedOptions = data.selectedClasses.mapNotNull { it.className },
                isMultiSelection = true,
                onDismiss = { handleIntent(CreateCircularIntent.OnDismissClassPicker) },
                onOptionsSelected = { selected ->
                    val classes = data.classes.filter { it.className in selected }
                    handleIntent(CreateCircularIntent.OnClassesSelected(classes))
                },
            )

            // Students/Parents picker
            EcareProSelectionBottomSheet(
                title = "Students/Parents",
                isVisible = data.isStudentParentPickerVisible,
                options = data.studentParentContacts.map { it.name  },
                selectedOptions = data.selectedStudentParents.map { it.name },
                isMultiSelection = true,
                onDismiss = { handleIntent(CreateCircularIntent.OnDismissStudentParentPicker) },
                onOptionsSelected = { selected ->
                    val contacts = data.studentParentContacts.filter { it.name in selected }
                    handleIntent(CreateCircularIntent.OnStudentParentsSelected(contacts))
                },
            )

            // File picker
            EcareProFileUploadBottomSheet(
                isVisible = data.isFilePickerVisible,
                onDismiss = { handleIntent(CreateCircularIntent.OnDismissFilePicker) },
                allowMultiple = false,
                onFilesSelected = { files ->
                    files.firstOrNull()?.let { handleIntent(CreateCircularIntent.OnFileSelected(it)) }
                },
            )
        }
    }
}

@Composable
private fun CheckboxRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.appTypography.interRegular14px,
            color = MaterialTheme.appColors.textPrimary,
        )
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.appColors.primary,
                uncheckedColor = MaterialTheme.appColors.textSecondary,
                checkmarkColor = White,
            ),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipientPickerBottomSheet(
    selectedType: RecipientType,
    onTypeSelected: (RecipientType) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = White,
        dragHandle = null,
        tonalElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 26.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Send circular to",
                    style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 20.sp),
                    color = MaterialTheme.appColors.textPrimary,
                )
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                        },
                    tint = MaterialTheme.appColors.textPrimary,
                )
            }

            RecipientType.entries.forEachIndexed { index, type ->
                val isSelected = selectedType == type
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onTypeSelected(type)
                            scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = type.label,
                        style = if (isSelected)
                            MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 16.sp)
                        else
                            MaterialTheme.appTypography.interRegular14px.copy(fontSize = 16.sp),
                        color = if (isSelected) MaterialTheme.appColors.primary else MaterialTheme.appColors.textPrimary,
                        modifier = Modifier.weight(1f),
                    )
                    RadioButton(
                        selected = isSelected,
                        onClick = null,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.appColors.primary,
                            unselectedColor = MaterialTheme.appColors.textSecondary,
                        ),
                    )
                }
                if (index < RecipientType.entries.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = Color(0xFFEEEEEE),
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, name = "Create Circular")
@Composable
private fun CreateCircularPreview() {
    EcareProTheme {
        CreateCircularScreenContent(
            uiState = UiState.Success(
                CreateCircularUiState(
                    title = "",
                    releadOnDisplay = SimpleDateFormat("dd MMMM yyyy", Locale.US).format(Date()),
                    releadOnApi = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
                )
            ),
            handleIntent = {},
            snackbarHostState = remember { SnackbarHostState() },
            snackbarMessage = null,
            onSnackbarDismissed = {},
        )
    }
}

@Preview(showBackground = true, name = "Create Circular - Staff Type Selected")
@Composable
private fun CreateCircularStaffTypePreview() {
    EcareProTheme {
        CreateCircularScreenContent(
            uiState = UiState.Success(
                CreateCircularUiState(
                    title = "Sample Circular",
                    releadOnDisplay = SimpleDateFormat("dd MMMM yyyy", Locale.US).format(Date()),
                    releadOnApi = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
                    selectedRecipientType = RecipientType.STAFF_TYPE,
                )
            ),
            handleIntent = {},
            snackbarHostState = remember { SnackbarHostState() },
            snackbarMessage = null,
            onSnackbarDismissed = {},
        )
    }
}

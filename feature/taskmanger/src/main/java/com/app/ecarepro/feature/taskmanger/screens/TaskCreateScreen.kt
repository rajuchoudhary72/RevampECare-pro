package com.app.ecarepro.feature.taskmanger.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
import com.app.ecarepro.designsystem.core.component.EcareProDateRangeField
import com.app.ecarepro.designsystem.core.component.EcareProFileUploadBottomSheet
import com.app.ecarepro.designsystem.core.component.EcareProInputField
import com.app.ecarepro.designsystem.core.component.EcareProPersonSelectionBottomSheet
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProSelectionBottomSheet
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.PersonSelectionItem
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import androidx.compose.ui.tooling.preview.Preview
import com.app.ecarepro.core.domain.model.TaskAssignee
import com.app.ecarepro.core.domain.model.TaskListItem
import com.app.ecarepro.core.domain.model.TaskWatcherDomain
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.taskmanger.TaskPriority
import com.app.ecarepro.feature.taskmanger.UserAvatar

// ============== MAIN SCREEN ==============

@Composable
fun TaskCreateScreen(
    viewModel: TaskCreateViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                TaskCreateEvent.NavigateBack -> navigateToBack()
                TaskCreateEvent.TaskCreated -> navigateToBack()
                is TaskCreateEvent.ShowMessage -> {
                    snackbarMessage = event.snackbarMessage
                    snackbarHostState.showSnackbar(event.snackbarMessage.text)
                }
            }
        }
    }

    TaskCreateScreenContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null },
    )
}

// ============== SCREEN CONTENT ==============

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskCreateScreenContent(
    uiState: UiState<TaskCreateUiState>,
    handleIntent: (TaskCreateIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit,
) {
    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = "Add new task",
                onNavigationClicked = { handleIntent(TaskCreateIntent.OnBackClicked) },
                navigationIcon = {
                    IconButton(onClick = { handleIntent(TaskCreateIntent.OnBackClicked) }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.appColors.background,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = onSnackbarDismissed,
        isLoading = (uiState as? UiState.Success)?.data?.isSaving == true,
    ) { paddingValues ->
        UiStateHandler(
            state = uiState,
            onRetry = { handleIntent(TaskCreateIntent.OnRetry) },
        ) { data ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                // Scrollable form content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Task name
                    EcareProInputField(
                        label = "Task name",
                        value = data.taskName,
                        placeholder = "What you want to accomplish",
                        onValueChange = { handleIntent(TaskCreateIntent.OnTaskNameChanged(it)) },
                    )

                    // Start date & End date row
                    EcareProDateRangeField(
                        startDateDisplay = data.startDateDisplay,
                        endDateDisplay = data.dueDateDisplay,
                        startDateMillis = data.startDateMillis,
                        onStartDateSelected = { apiDate, displayDate, millis ->
                            handleIntent(TaskCreateIntent.OnStartDateChanged(apiDate, displayDate, millis))
                        },
                        onEndDateSelected = { apiDate, displayDate, millis ->
                            handleIntent(TaskCreateIntent.OnEndDateChanged(apiDate, displayDate, millis))
                        },
                    )

                    // Description
                    EcareProInputField(
                        label = "Add description",
                        value = data.description,
                        placeholder = "Explain what this task is about",
                        singleLine = false,
                        minLines = 4,
                        maxLines = 6,
                        onValueChange = { handleIntent(TaskCreateIntent.OnDescriptionChanged(it)) },
                    )

                    // Priority & Task list row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DropdownField(
                            label = "Priority",
                            value = data.selectedPriority.displayName,
                            modifier = Modifier.weight(1f),
                            onClick = { handleIntent(TaskCreateIntent.OnPriorityClicked) },
                        )
                        DropdownField(
                            label = "Task list",
                            value = data.selectedTaskList?.title ?: "Select",
                            modifier = Modifier.weight(1f),
                            onClick = { handleIntent(TaskCreateIntent.OnTaskListClicked) },
                        )
                    }

                    // Select assignee
                    PersonSelectionSection(
                        label = "Select assignee",
                        placeholder = "Add one or more assignee",
                        selectedCount = data.selectedAssigneeIds.size,
                        selectedPersons = data.availableAssignees.filter {
                            data.selectedAssigneeIds.contains(it.userID.toString())
                        }.map { PersonChipData(it.name, it.designation, it.photo) },
                        isLoading = data.isLoadingAssignees,
                        onDropdownClick = {
                            if (data.selectedTaskList == null) {
                                handleIntent(TaskCreateIntent.OnShowError("Please select a task list first"))
                            } else {
                                handleIntent(TaskCreateIntent.OnAssigneeClicked)
                            }
                        },
                        onClear = { handleIntent(TaskCreateIntent.OnClearAssignees) },
                    )

                    // Add attachment
                    AttachmentSection(
                        selectedFile = data.selectedFile,
                        onAttachmentClick = { handleIntent(TaskCreateIntent.OnAttachmentClicked) },
                        onRemoveClick = { handleIntent(TaskCreateIntent.OnRemoveAttachment) },
                    )

                    // Remind before
                    RemindBeforeSection(
                        hours = data.remindBeforeHours,
                        onDecrement = {
                            handleIntent(TaskCreateIntent.OnRemindBeforeChanged(data.remindBeforeHours - 1))
                        },
                        onIncrement = {
                            handleIntent(TaskCreateIntent.OnRemindBeforeChanged(data.remindBeforeHours + 1))
                        },
                    )

                    // Make it public
                    MakePublicRow(
                        isPublic = data.isPublic,
                        onChanged = { handleIntent(TaskCreateIntent.OnIsPublicChanged(it)) },
                    )

                    // Select watchers (hidden if public)
                    if (!data.isPublic) {
                        PersonSelectionSection(
                            label = "Select watchers",
                            placeholder = "Add one or more watcher",
                            selectedCount = data.selectedWatcherIds.size,
                            selectedPersons = data.allWatchers.filter {
                                data.selectedWatcherIds.contains(it.userID.toString())
                            }.map { PersonChipData(it.name, it.designation, it.photo) },
                            onDropdownClick = { handleIntent(TaskCreateIntent.OnWatcherClicked) },
                            onClear = { handleIntent(TaskCreateIntent.OnClearWatchers) },
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Add task button at bottom
                AddTaskButton(
                    isSaving = data.isSaving,
                    onClick = { handleIntent(TaskCreateIntent.OnSubmitClicked) },
                )
            }

            // ============== BOTTOM SHEETS & DIALOGS ==============

            // Priority Bottom Sheet
            EcareProSelectionBottomSheet(
                title = "Priority",
                isVisible = data.isPrioritySheetVisible,
                options = TaskPriority.entries.map { it.displayName },
                selectedOptions = listOf(data.selectedPriority.displayName),
                isMultiSelection = false,
                onDismiss = { handleIntent(TaskCreateIntent.OnPrioritySheetDismissed) },
                onOptionsSelected = { selectedList ->
                    val selectedName = selectedList.firstOrNull() ?: return@EcareProSelectionBottomSheet
                    val priority = TaskPriority.entries.firstOrNull { it.displayName == selectedName }
                        ?: return@EcareProSelectionBottomSheet
                    handleIntent(TaskCreateIntent.OnPrioritySelected(priority))
                }
            )

            // Task list Bottom Sheet
            EcareProSelectionBottomSheet(
                title = "Task list",
                isVisible = data.isTaskListSheetVisible,
                options = data.taskLists.map { it.title },
                selectedOptions = listOfNotNull(data.selectedTaskList?.title),
                isMultiSelection = false,
                onDismiss = { handleIntent(TaskCreateIntent.OnTaskListSheetDismissed) },
                onOptionsSelected = { selectedList ->
                    val selectedTitle = selectedList.firstOrNull() ?: return@EcareProSelectionBottomSheet
                    val taskList = data.taskLists.firstOrNull { it.title == selectedTitle }
                        ?: return@EcareProSelectionBottomSheet
                    handleIntent(TaskCreateIntent.OnTaskListSelected(taskList))
                }
            )

            // Assignee Bottom Sheet
            EcareProPersonSelectionBottomSheet(
                title = "Select assignee",
                isVisible = data.isAssigneeSheetVisible,
                persons = data.availableAssignees.map { assignee ->
                    PersonSelectionItem(
                        id = assignee.userID.toString(),
                        name = assignee.name,
                        photo = assignee.photo,
                        subtitle = assignee.designation,
                        isSelected = data.selectedAssigneeIds.contains(assignee.userID.toString()),
                    )
                },
                searchPlaceholder = "Search by assignee name",
                onDismiss = { handleIntent(TaskCreateIntent.OnAssigneeSheetDismissed) },
                onSelectionChanged = { handleIntent(TaskCreateIntent.OnAssigneeSelectionChanged(it)) }
            )

            // Watcher Bottom Sheet
            EcareProPersonSelectionBottomSheet(
                title = "Select watchers",
                isVisible = data.isWatcherSheetVisible,
                persons = data.allWatchers.map { watcher ->
                    PersonSelectionItem(
                        id = watcher.userID.toString(),
                        name = watcher.name,
                        photo = watcher.photo,
                        subtitle = watcher.designation,
                        isSelected = data.selectedWatcherIds.contains(watcher.userID.toString()),
                    )
                },
                searchPlaceholder = "Search by name",
                onDismiss = { handleIntent(TaskCreateIntent.OnWatcherSheetDismissed) },
                onSelectionChanged = { handleIntent(TaskCreateIntent.OnWatcherSelectionChanged(it)) }
            )

            // File Upload Bottom Sheet
            EcareProFileUploadBottomSheet(
                isVisible = data.isFileUploadSheetVisible,
                onDismiss = { handleIntent(TaskCreateIntent.OnFileUploadSheetDismissed) },
                maxFileSizeInMb = 10,
                onShowError = { handleIntent(TaskCreateIntent.OnShowError(it)) },
                onFilesSelected = { files ->
                    val file = files.firstOrNull()
                    if (file != null) handleIntent(TaskCreateIntent.OnFileSelected(file))
                }
            )

        }
    }
}

// ============== FORM COMPONENTS ==============


@Composable
private fun DropdownField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isLoading: Boolean = false,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.appTypography.interRegular12px,
            color = MaterialTheme.appColors.textSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.appColors.border, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = value,
                style = MaterialTheme.appTypography.interRegular14px,
                color = if (value == "Select" || value.contains("Add one"))
                    MaterialTheme.appColors.textSecondary
                else MaterialTheme.appColors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.appColors.primary
                )
            } else {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.appColors.textSecondary
                )
            }
        }
    }
}

private data class PersonChipData(
    val name: String,
    val designation: String,
    val photoUrl: String,
)

@Composable
private fun PersonSelectionSection(
    label: String,
    placeholder: String,
    selectedCount: Int,
    selectedPersons: List<PersonChipData>,
    isLoading: Boolean = false,
    onDropdownClick: () -> Unit,
    onClear: () -> Unit,
) {
    Column {
        // Header row: label + "N selected · Clear"
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.appTypography.interRegular12px,
                color = MaterialTheme.appColors.textSecondary
            )
            if (selectedCount > 0) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$selectedCount selected",
                        style = MaterialTheme.appTypography.interRegular12px,
                        color = MaterialTheme.appColors.primary
                    )
                    Text(
                        text = "  ·  ",
                        style = MaterialTheme.appTypography.interRegular12px,
                        color = MaterialTheme.appColors.textSecondary
                    )
                    Text(
                        text = "Clear",
                        style = MaterialTheme.appTypography.interRegular12px,
                        color = MaterialTheme.appColors.textSecondary,
                        modifier = Modifier.clickable(onClick = onClear)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Dropdown field
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.appColors.border, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onDropdownClick)
                .padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = placeholder,
                style = MaterialTheme.appTypography.interRegular14px,
                color = MaterialTheme.appColors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.appColors.primary
                )
            } else {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.appColors.textSecondary
                )
            }
        }

        // Selected person chips
        if (selectedPersons.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                selectedPersons.forEach { person ->
                    PersonChip(
                        name = person.name,
                        designation = person.designation,
                        photoUrl = person.photoUrl,
                    )
                }
            }
        }
    }
}

@Composable
private fun PersonChip(
    name: String,
    designation: String,
    photoUrl: String,
) {
    Row(
        modifier = Modifier
            .background(
                MaterialTheme.appColors.border.copy(alpha = 0.3f),
                RoundedCornerShape(24.dp)
            )
            .padding(start = 4.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        UserAvatar(
            name = name,
            photoUrl = photoUrl,
            size = 36,
        )
        Column {
            Text(
                text = name,
                style = MaterialTheme.appTypography.interMedium12px,
                color = MaterialTheme.appColors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (designation.isNotEmpty()) {
                Text(
                    text = "Designation: $designation",
                    style = MaterialTheme.appTypography.interRegular12px,
                    color = MaterialTheme.appColors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun AttachmentSection(
    selectedFile: com.app.ecarepro.designsystem.core.component.SelectedFileDetails?,
    onAttachmentClick: () -> Unit,
    onRemoveClick: () -> Unit,
) {
    Column {
        Text(
            text = "Add attachment",
            style = MaterialTheme.appTypography.interRegular12px,
            color = MaterialTheme.appColors.textSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (selectedFile != null) {
            // Show selected file
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.appColors.primary, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.appColors.primary.copy(alpha = 0.05f))
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.appColors.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = selectedFile.name,
                        style = MaterialTheme.appTypography.interMedium14px,
                        color = MaterialTheme.appColors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = selectedFile.formattedSize,
                        style = MaterialTheme.appTypography.interRegular12px,
                        color = MaterialTheme.appColors.textSecondary,
                    )
                }
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove",
                    modifier = Modifier
                        .size(20.dp)
                        .clickable(onClick = onRemoveClick),
                    tint = MaterialTheme.appColors.textSecondary,
                )
            }
        } else {
            // Upload placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.appColors.border,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onAttachmentClick),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.appColors.textSecondary
                    )
                    Text(
                        text = "Click here to add file",
                        style = MaterialTheme.appTypography.interMedium14px,
                        color = MaterialTheme.appColors.textPrimary,
                    )
                    Text(
                        text = "Max 10 MB files are allowed",
                        style = MaterialTheme.appTypography.interRegular12px,
                        color = MaterialTheme.appColors.textSecondary,
                    )
                }
            }
        }
    }
}

@Composable
private fun RemindBeforeSection(
    hours: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Remind before (no of hours)",
            style = MaterialTheme.appTypography.interRegular14px,
            color = MaterialTheme.appColors.textPrimary,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .border(1.dp, MaterialTheme.appColors.border, RoundedCornerShape(6.dp))
                    .clip(RoundedCornerShape(6.dp))
                    .clickable(onClick = onDecrement),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.appColors.textPrimary
                )
            }
            Text(
                text = hours.toString(),
                style = MaterialTheme.appTypography.interMedium16px,
                color = MaterialTheme.appColors.textPrimary,
            )
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .border(1.dp, MaterialTheme.appColors.border, RoundedCornerShape(6.dp))
                    .clip(RoundedCornerShape(6.dp))
                    .clickable(onClick = onIncrement),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.appColors.textPrimary
                )
            }
        }
    }
}

@Composable
private fun MakePublicRow(
    isPublic: Boolean,
    onChanged: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Make it public",
            style = MaterialTheme.appTypography.interRegular14px,
            color = MaterialTheme.appColors.textPrimary,
        )
        Checkbox(
            checked = isPublic,
            onCheckedChange = onChanged,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.appColors.primary,
                uncheckedColor = MaterialTheme.appColors.textSecondary,
                checkmarkColor = White,
            )
        )
    }
}

@Composable
private fun AddTaskButton(
    isSaving: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.appColors.primary)
                .clickable(enabled = !isSaving, onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = White,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(
                    text = "Add task",
                    style = MaterialTheme.appTypography.interSemiBold16px,
                    color = White,
                )
            }
        }
    }
}

// ============== PREVIEWS ==============

@Preview(showBackground = true)
@Composable
private fun TaskCreateScreenEmptyPreview() {
    EcareProTheme {
        TaskCreateScreenContent(
            uiState = UiState.Success(
                TaskCreateUiState(
                    taskLists = listOf(
                        TaskListItem(id = 1, title = "Academics"),
                        TaskListItem(id = 2, title = "Sports"),
                        TaskListItem(id = 3, title = "Administration"),
                    ),
                    allWatchers = listOf(
                        TaskWatcherDomain(userID = 1, name = "John Doe", designation = "Teacher"),
                        TaskWatcherDomain(userID = 2, name = "Jane Smith", designation = "Admin"),
                    ),
                )
            ),
            handleIntent = {},
            snackbarHostState = SnackbarHostState(),
            snackbarMessage = null,
            onSnackbarDismissed = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskCreateScreenFilledPreview() {
    EcareProTheme {
        TaskCreateScreenContent(
            uiState = UiState.Success(
                TaskCreateUiState(
                    taskName = "Complete homework assignment",
                    description = "Finish the math homework and submit before the deadline",
                    selectedPriority = TaskPriority.HIGH,
                    selectedTaskList = TaskListItem(id = 1, title = "Academics"),
                    startDateDisplay = "17 Feb 2026",
                    startDateApi = "2026-02-17",
                    dueDateDisplay = "20 Feb 2026",
                    dueDateApi = "2026-02-20",
                    remindBeforeHours = 3,
                    isPublic = false,
                    taskLists = listOf(
                        TaskListItem(id = 1, title = "Academics"),
                        TaskListItem(id = 2, title = "Sports"),
                    ),
                    allWatchers = listOf(
                        TaskWatcherDomain(userID = 1, name = "John Doe", designation = "Teacher"),
                        TaskWatcherDomain(userID = 2, name = "Jane Smith", designation = "Admin"),
                    ),
                    availableAssignees = listOf(
                        TaskAssignee(userID = 1, name = "Alice Brown", designation = "Student"),
                        TaskAssignee(userID = 2, name = "Bob Wilson", designation = "Student"),
                    ),
                    selectedAssigneeIds = listOf("1", "2"),
                    selectedWatcherIds = listOf("1"),
                )
            ),
            handleIntent = {},
            snackbarHostState = SnackbarHostState(),
            snackbarMessage = null,
            onSnackbarDismissed = {},
        )
    }
}


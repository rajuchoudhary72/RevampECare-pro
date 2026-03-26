package com.app.ecarepro.feature.taskmanger.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.component.EcareProFileUploadBottomSheet
import com.app.ecarepro.designsystem.core.component.EcareProInputField
import com.app.ecarepro.designsystem.core.component.EcareProPersonSelectionBottomSheet
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProSelectionBottomSheet
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.HorizontalTabBar
import com.app.ecarepro.designsystem.core.component.HorizontalTabBarConfiguration
import com.app.ecarepro.designsystem.core.component.PersonSelectionItem
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.component.UploadOption
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.taskmanger.PriorityChip
import com.app.ecarepro.feature.taskmanger.StatusBanner
import com.app.ecarepro.feature.taskmanger.TaskAssigneePresentation
import com.app.ecarepro.feature.taskmanger.TaskPriority
import com.app.ecarepro.feature.taskmanger.TaskStatus
import com.app.ecarepro.feature.taskmanger.UserAvatar

// ============== MAIN SCREEN ==============

@Composable
fun TaskMangerDetailsScreen(
    taskId: String,
    viewModel: TaskMangerDetailsViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
    navigateToDocViewer: (title: String, url: String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(taskId) {
        viewModel.loadTaskDetails(taskId)
    }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                TaskMangerDetailsEvent.NavigateBack -> navigateToBack()
                is TaskMangerDetailsEvent.ShowMessage -> {
                    snackbarMessage = event.snackbarMessage
                    snackbarHostState.showSnackbar(event.snackbarMessage.text)
                }
                is TaskMangerDetailsEvent.ShowDatePicker -> {
                    // TODO: Show platform date picker and call:
                    // viewModel.handleIntent(TaskMangerDetailsIntent.OnDateSelected(event.fieldName, selectedDate))
                }
            }
        }
    }

    TaskMangerDetailsScreenContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null },
        navigateToDocViewer = navigateToDocViewer,
    )
}

// ============== SCREEN CONTENT ==============

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskMangerDetailsScreenContent(
    uiState: UiState<TaskMangerDetailsUiState>,
    handleIntent: (TaskMangerDetailsIntent) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit = {},
    navigateToDocViewer: (title: String, url: String) -> Unit,
) {
    val tabs = listOf("Comments", "Activity")

    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = "Task details",
                onNavigationClicked = { handleIntent(TaskMangerDetailsIntent.OnBackClicked) }
            )
        },
        containerColor = MaterialTheme.appColors.background,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = onSnackbarDismissed,
        isLoading = if (uiState is UiState.Success) uiState.data.isActionLoading else false,
    ) { paddingValues ->
        UiStateHandler(
            state = uiState,
            onRetry = { handleIntent(TaskMangerDetailsIntent.OnRefresh) },
        ) { data ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                // 1. Status Banner at top
                StatusBanner(
                    status = data.status,
                    onClick = {
                        if (data.canChangeStatus && data.imOwner) {
                            handleIntent(TaskMangerDetailsIntent.OnStatusClicked)
                        }
                    }
                )

                // 2. Scrollable content
                PullToRefreshBox(
                    modifier = Modifier.weight(1f),
                    isRefreshing = data.isRefreshing,
                    onRefresh = { handleIntent(TaskMangerDetailsIntent.OnRefresh) }
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Task details card
                        item {
                            TaskDetailsCard(
                                data = data,
                                handleIntent = handleIntent,
                                navigateToDocViewer = navigateToDocViewer,
                            )
                        }

                        // Comments/Activity tabs
                        item {
                            HorizontalTabBar(
                                tabs = tabs,
                                selectedTab = tabs[data.selectedTabIndex],
                                onTabSelected = {
                                    handleIntent(TaskMangerDetailsIntent.OnTabSelected(tabs.indexOf(it)))
                                },
                                configuration = HorizontalTabBarConfiguration(
                                    horizontalPadding = 0.dp,
                                    tabSpacing = 32.dp
                                )
                            )
                        }

                        // Comments or Activity list based on selected tab
                        if (data.selectedTabIndex == 0) {
                            if (data.comments.isEmpty()) {
                                item {
                                    EmptyListMessage(message = "No comments yet")
                                }
                            } else {
                                items(data.comments) { comment ->
                                    TimelineItem(
                                        name = comment.name,
                                        photo = comment.photo,
                                        subtitle = comment.commentOn,
                                        text = comment.comment,
                                    )
                                }
                            }
                        } else {
                            if (data.activities.isEmpty()) {
                                item {
                                    EmptyListMessage(message = "No activity yet")
                                }
                            } else {
                                items(data.activities) { activity ->
                                    TimelineItem(
                                        name = activity.name,
                                        photo = activity.photo,
                                        subtitle = activity.actionOn,
                                        text = activity.activity,
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Comment input at bottom
                CommentInputBar(
                    text = data.commentText,
                    isSending = data.isCommentSending,
                    onTextChange = { handleIntent(TaskMangerDetailsIntent.OnCommentTextChanged(it)) },
                    onSendClick = { handleIntent(TaskMangerDetailsIntent.OnSendComment) }
                )
            }

            // ============== DIALOGS & BOTTOM SHEETS ==============

            // Edit Text Dialog
            if (data.isEditDialogVisible) {
                EditFieldDialog(
                    title = data.editDialogTitle,
                    value = data.editDialogCurrentValue,
                    isSingleLine = data.editDialogFieldName == TaskFieldName.TASK_TITLE,
                    onValueChange = { handleIntent(TaskMangerDetailsIntent.OnEditDialogValueChanged(it)) },
                    onConfirm = { handleIntent(TaskMangerDetailsIntent.OnEditDialogConfirmed) },
                    onDismiss = { handleIntent(TaskMangerDetailsIntent.OnEditDialogDismissed) },
                )
            }

            // Priority Bottom Sheet
            EcareProSelectionBottomSheet(
                title = "Priority",
                isVisible = data.isPrioritySheetVisible,
                options = TaskPriority.entries.map { it.displayName },
                selectedOptions = listOf(data.currentPriority.displayName),
                isMultiSelection = false,
                onDismiss = { handleIntent(TaskMangerDetailsIntent.OnPrioritySheetDismissed) },
                onOptionsSelected = { selectedList ->
                    val selectedName = selectedList.firstOrNull() ?: return@EcareProSelectionBottomSheet
                    val newPriority = TaskPriority.entries.firstOrNull { it.displayName == selectedName }
                        ?: return@EcareProSelectionBottomSheet
                    handleIntent(TaskMangerDetailsIntent.OnPrioritySelected(newPriority))
                }
            )

            // Status Bottom Sheet
            EcareProSelectionBottomSheet(
                title = "Status",
                isVisible = data.isStatusSheetVisible,
                options = TaskStatus.entries.map { it.displayName },
                selectedOptions = listOf(data.currentStatus.displayName),
                isMultiSelection = false,
                onDismiss = { handleIntent(TaskMangerDetailsIntent.OnStatusSheetDismissed) },
                onOptionsSelected = { selectedList ->
                    val selectedName = selectedList.firstOrNull() ?: return@EcareProSelectionBottomSheet
                    val newStatus = TaskStatus.entries.firstOrNull { it.displayName == selectedName }
                        ?: return@EcareProSelectionBottomSheet
                    handleIntent(TaskMangerDetailsIntent.OnStatusSelected(newStatus))
                }
            )

            // Assignee Bottom Sheet
            EcareProPersonSelectionBottomSheet(
                title = "Assignees",
                isVisible = data.isAssigneeSheetVisible,
                persons = data.assignees.map { assignee ->
                    PersonSelectionItem(
                        id = assignee.id.toString(),
                        name = assignee.name,
                        photo = assignee.photo,
                        subtitle = assignee.designation,
                        isSelected = true,
                    )
                },
                searchPlaceholder = "Search by assignee name",
                onDismiss = { handleIntent(TaskMangerDetailsIntent.OnAssigneeSheetDismissed) },
                onSelectionChanged = { /* Read-only view of assignees */ }
            )

            // File Upload Bottom Sheet
            EcareProFileUploadBottomSheet(
                isVisible = data.isFileUploadSheetVisible,
                onDismiss = { handleIntent(TaskMangerDetailsIntent.OnDismissFileUploadSheet) },
                allowedOptions = listOf(UploadOption.CAMERA, UploadOption.GALLERY),
                onShowError = { handleIntent(TaskMangerDetailsIntent.OnShowError(it)) },
                onFilesSelected = { files ->
                    val file = files.firstOrNull()
                    if (file != null) handleIntent(TaskMangerDetailsIntent.OnFileSelected(file))
                }
            )
        }
    }
}

// ============== TASK DETAILS CARD ==============

@Composable
private fun TaskDetailsCard(
    data: TaskMangerDetailsUiState,
    handleIntent: (TaskMangerDetailsIntent) -> Unit,
    navigateToDocViewer: (title: String, url: String) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        val canEdit = data.canChangeStatus && data.imOwner

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Chips row (Due time + Priority)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (data.dueDate.isNotEmpty()) {
                    DueTimeChip(text = "Due: ${data.dueDate}")
                }
                PriorityChip(
                    priority = data.priority,
                    onClick = if (canEdit) {
                        { handleIntent(TaskMangerDetailsIntent.OnPriorityClicked) }
                    } else null
                )
            }

            // Title with edit button
            TitleRow(
                title = data.title,
                canEdit = canEdit,
                onEditClick = { handleIntent(TaskMangerDetailsIntent.OnEditTitleClicked) }
            )

            // Description
            if (data.description.isNotEmpty()) {
                Text(
                    text = data.description,
                    style = MaterialTheme.appTypography.interRegular14px,
                    color = MaterialTheme.appColors.textSecondary,
                )
            }

            // Start date
            DateRow(
                label = "Start date",
                value = data.startDate.ifEmpty { "Not set" },
                canEdit = canEdit,
                onEditClick = { handleIntent(TaskMangerDetailsIntent.OnEditStartDateClicked) }
            )

            // End date
            DateRow(
                label = "End date",
                value = data.dueDate.ifEmpty { "Not set" },
                canEdit = canEdit,
                onEditClick = { handleIntent(TaskMangerDetailsIntent.OnEditDueDateClicked) }
            )

            // Assignees section
            if (data.assignees.isNotEmpty()) {
                AssigneesSection(
                    assignees = data.assignees,
                    canEdit = canEdit,
                    onEditClick = { handleIntent(TaskMangerDetailsIntent.OnAssigneeClicked) }
                )
            }

            // Attachment section
            AttachmentSection(
                imageUrl = data.attachmentUrl,
                canEdit = canEdit,
                onUploadClick = { handleIntent(TaskMangerDetailsIntent.OnUploadAttachmentClicked) },
                onAttachmentClick = {
                    if (data.attachmentUrl.isNotEmpty()) {
                        navigateToDocViewer("Attachment", data.attachmentUrl)
                    }
                }
            )
        }
    }
}

// ============== SMALL COMPONENTS ==============

@Composable
private fun DueTimeChip(text: String) {
    val chipColor = Color(0xFF689F38)

    Row(
        modifier = Modifier
            .background(chipColor.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.AccessTime,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = chipColor
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.appTypography.interMedium12px,
            color = chipColor
        )
    }
}

@Composable
private fun TitleRow(title: String, canEdit: Boolean, onEditClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = title,
            style = MaterialTheme.appTypography.interSemiBold16px,
            color = MaterialTheme.appColors.textPrimary,
            modifier = Modifier.weight(1f)
        )
        if (canEdit) {
            EditIcon(onClick = onEditClick)
        }
    }
}

@Composable
private fun DateRow(label: String, value: String, canEdit: Boolean, onEditClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.appColors.textSecondary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.appTypography.interRegular14px,
            color = MaterialTheme.appColors.textSecondary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.appTypography.interMedium14px,
            color = MaterialTheme.appColors.textPrimary
        )
        if (canEdit) {
            Spacer(modifier = Modifier.width(8.dp))
            EditIcon(onClick = onEditClick)
        }
    }
}

@Composable
private fun EditIcon(onClick: () -> Unit) {
    Icon(
        imageVector = Icons.Default.Edit,
        contentDescription = "Edit",
        modifier = Modifier
            .size(18.dp)
            .clickable(onClick = onClick),
        tint = MaterialTheme.appColors.textSecondary
    )
}

// ============== ASSIGNEES SECTION ==============

@Composable
private fun AssigneesSection(
    assignees: List<TaskAssigneePresentation>,
    canEdit: Boolean,
    onEditClick: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.appColors.textSecondary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Assigned (${assignees.size})",
                style = MaterialTheme.appTypography.interRegular14px,
                color = MaterialTheme.appColors.textSecondary,
                modifier = Modifier.weight(1f)
            )
            if (canEdit) {
                EditIcon(onClick = onEditClick)
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(start = 32.dp)
        ) {
            items(assignees) { assignee ->
                AssigneeCard(assignee = assignee)
            }
        }
    }
}

@Composable
private fun AssigneeCard(assignee: TaskAssigneePresentation) {
    Card(
        modifier = Modifier.width(200.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.appColors.background)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserAvatar(name = assignee.name, photoUrl = assignee.photo, size = 40)
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = assignee.name,
                    style = MaterialTheme.appTypography.interMedium12px,
                    color = MaterialTheme.appColors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Designation: ${assignee.designation}",
                    style = MaterialTheme.appTypography.interRegular10px,
                    color = MaterialTheme.appColors.textSecondary,
                    maxLines = 1
                )
            }
        }
    }
}

// ============== ATTACHMENT SECTION ==============

@Composable
private fun AttachmentSection(imageUrl: String?, canEdit: Boolean, onUploadClick: () -> Unit, onAttachmentClick: () -> Unit = {}) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AttachFile,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.appColors.textSecondary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Attachment",
                style = MaterialTheme.appTypography.interRegular14px,
                color = MaterialTheme.appColors.textSecondary,
                modifier = Modifier.weight(1f)
            )
            if (canEdit) {
                Row(
                    modifier = Modifier.clickable(onClick = onUploadClick),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Upload new",
                        style = MaterialTheme.appTypography.interMedium14px,
                        color = MaterialTheme.appColors.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Upload,
                        contentDescription = "Upload",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.appColors.primary
                    )
                }
            }
        }

        if (!imageUrl.isNullOrEmpty()) {
            EcareProAsyncImage(
                imageUrl = imageUrl,
                contentDescription = "Attachment",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp)
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onAttachmentClick),
                contentScale = ContentScale.Crop
            )
        }
    }
}

// ============== TIMELINE ITEM (used for both Comments and Activities) ==============

@Composable
private fun TimelineItem(
    name: String,
    photo: String,
    subtitle: String,
    text: String,
) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserAvatar(name = name, photoUrl = photo, size = 40)
            Column {
                Text(
                    text = name,
                    style = MaterialTheme.appTypography.interSemiBold14px,
                    color = MaterialTheme.appColors.textPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.appTypography.interRegular12px,
                    color = MaterialTheme.appColors.textSecondary
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            style = MaterialTheme.appTypography.interRegular14px,
            color = MaterialTheme.appColors.textPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.appColors.border)
    }
}

// ============== COMMENT INPUT BAR ==============

@Composable
private fun CommentInputBar(
    text: String,
    isSending: Boolean,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.ChatBubbleOutline,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.appColors.textSecondary
        )

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .background(MaterialTheme.appColors.background, RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            if (text.isEmpty()) {
                Text(
                    text = "Write a comment...",
                    style = MaterialTheme.appTypography.interRegular14px,
                    color = MaterialTheme.appColors.textSecondary
                )
            }
            BasicTextField(
                value = text,
                onValueChange = onTextChange,
                textStyle = MaterialTheme.appTypography.interRegular14px.copy(
                    color = MaterialTheme.appColors.textPrimary
                ),
                cursorBrush = SolidColor(MaterialTheme.appColors.primary),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSending,
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        IconButton(
            onClick = onSendClick,
            enabled = !isSending,
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.appColors.primary, CircleShape)
        ) {
            if (isSending) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = White,
                    strokeWidth = 2.dp,
                )
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    modifier = Modifier.size(20.dp),
                    tint = White
                )
            }
        }
    }
}

// ============== EDIT FIELD DIALOG ==============

@Composable
private fun EditFieldDialog(
    title: String,
    value: String,
    isSingleLine: Boolean,
    onValueChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.appTypography.interSemiBold16px,
                color = MaterialTheme.appColors.textPrimary,
            )
        },
        text = {
            EcareProInputField(
                label = title,
                value = value,
                placeholder = "Enter value",
                singleLine = isSingleLine,
                minLines = if (isSingleLine) 1 else 3,
                onValueChange = onValueChange,
            )
        },
        confirmButton = {
            Text(
                text = "Save",
                style = MaterialTheme.appTypography.interSemiBold14px,
                color = MaterialTheme.appColors.primary,
                modifier = Modifier
                    .clickable(onClick = onConfirm)
                    .padding(8.dp)
            )
        },
        dismissButton = {
            Text(
                text = "Cancel",
                style = MaterialTheme.appTypography.interRegular14px,
                color = MaterialTheme.appColors.textSecondary,
                modifier = Modifier
                    .clickable(onClick = onDismiss)
                    .padding(8.dp)
            )
        },
        containerColor = White,
    )
}

// ============== EMPTY LIST MESSAGE ==============

@Composable
private fun EmptyListMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.appTypography.interRegular14px,
            color = MaterialTheme.appColors.textSecondary
        )
    }
}

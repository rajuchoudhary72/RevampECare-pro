package com.app.ecarepro.feature.taskmanger

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
import com.app.ecarepro.designsystem.core.component.BottomSearchBarView
import com.app.ecarepro.designsystem.core.component.EcareProEmptyState
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProPersonSelectionBottomSheet
import com.app.ecarepro.designsystem.core.component.EcareProSelectionBottomSheet
import com.app.ecarepro.designsystem.core.component.PersonSelectionItem
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.HorizontalTabBar
import com.app.ecarepro.designsystem.core.component.HorizontalTabBarConfiguration
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

@Composable
fun TaskMangerScreen(
    viewModel: TaskMangerViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
    navigateToAddTask: () -> Unit = {},
    navigateToTaskDetail: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                TaskMangerEvent.NavigateBack -> navigateToBack()
                is TaskMangerEvent.ShowMessage -> {
                    snackbarMessage = event.snackbarMessage
                    snackbarHostState.showSnackbar(event.snackbarMessage.text)
                }
                is TaskMangerEvent.NavigateToTaskDetail -> navigateToTaskDetail(event.taskId)
                TaskMangerEvent.NavigateToAddTask -> navigateToAddTask()
            }
        }
    }

    TaskMangerScreenContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskMangerScreenContent(
    uiState: UiState<TaskMangerUiState>,
    handleIntent: (TaskMangerIntent) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit = {},
) {
    EcareProScaffold(
        topBar = {
            Column {
                EcareProTopAppBar(
                    title = "Task manager",
                    onNavigationClicked = { handleIntent(TaskMangerIntent.OnBackClicked) },
                    actions = {
                        // Add new task button
                        Text(
                            text = "+ Add new task",
                            style = MaterialTheme.appTypography.interSemiBold14px,
                            color = MaterialTheme.appColors.primary,
                            modifier = Modifier
                                .clickable { handleIntent(TaskMangerIntent.OnAddTaskClicked) }
                                .padding(horizontal = 16.dp)
                        )
                    }
                )

                // Filter Tabs
                if (uiState is UiState.Success) {
                    HorizontalTabBar(
                        tabs = uiState.data.filterTabs,
                        selectedTab = uiState.data.filterTabs.getOrNull(uiState.data.selectedFilterIndex)
                            ?: uiState.data.filterTabs.first(),
                        onTabSelected = { tab ->
                            val index = uiState.data.filterTabs.indexOf(tab)
                            if (index != -1) {
                                handleIntent(TaskMangerIntent.OnFilterSelected(index))
                            }
                        },
                        configuration = HorizontalTabBarConfiguration(
                            tabSpacing = 20.dp,
                            horizontalPadding = 16.dp
                        )
                    )
                }
            }
        },
        containerColor = MaterialTheme.appColors.background,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = onSnackbarDismissed,
        isLoading = if (uiState is UiState.Success) uiState.data.isLoading else false,
        bottomBar = {
            if (uiState is UiState.Success) {
                BottomSearchBarView(
                    searchText = uiState.data.searchQuery,
                    onSearchTextChange = { handleIntent(TaskMangerIntent.OnSearchQueryChanged(it)) },
                    placeholder = "Search by task name",
                    showSortButton = true,
                    onSortClick = { /* TODO: Implement sort */ }
                )
            }
        }
    ) { paddingValues ->
        UiStateHandler(
            state = uiState
        ) { data ->
            PullToRefreshBox(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                isRefreshing = data.isRefreshing,
                onRefresh = { handleIntent(TaskMangerIntent.OnRefresh) }
            ) {
                val hasNoTasks = data.filteredOverdueTasks.isEmpty() &&
                        data.filteredTodaysTasks.isEmpty() &&
                        data.filteredUpcomingTasks.isEmpty() &&
                        data.filteredClosedTasks.isEmpty()

                if (hasNoTasks) {
                    EcareProEmptyState(
                        message = "No tasks found\nTasks will appear here when available",
                        icon = Icons.Default.Task
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Overdue Tasks Section
                        if (data.filteredOverdueTasks.isNotEmpty()) {
                            item {
                                TaskSectionCollapsibleView(
                                    title = "Overdue tasks",
                                    tasks = data.filteredOverdueTasks,
                                    color = StatusColors.OverdueRed,
                                    isExpanded = data.isOverdueExpanded,
                                    onToggle = { handleIntent(TaskMangerIntent.OnToggleOverdueSection) },
                                    onTaskClick = { handleIntent(TaskMangerIntent.OnTaskClicked(it)) },
                                    onStatusClick = { taskId, status -> handleIntent(TaskMangerIntent.OnStatusClicked(taskId, status)) },
                                    onPriorityClick = { taskId, priority -> handleIntent(TaskMangerIntent.OnPriorityClicked(taskId, priority)) },
                                    onAssigneeClick = { taskId, assignees -> handleIntent(TaskMangerIntent.OnAssigneeClicked(taskId, assignees)) }
                                )
                            }
                        }

                        // Today's Tasks Section
                        if (data.filteredTodaysTasks.isNotEmpty()) {
                            item {
                                TaskSectionCollapsibleView(
                                    title = "Due today",
                                    tasks = data.filteredTodaysTasks,
                                    color = StatusColors.TodayGreen,
                                    isExpanded = data.isTodayExpanded,
                                    onToggle = { handleIntent(TaskMangerIntent.OnToggleTodaySection) },
                                    onTaskClick = { handleIntent(TaskMangerIntent.OnTaskClicked(it)) },
                                    onStatusClick = { taskId, status -> handleIntent(TaskMangerIntent.OnStatusClicked(taskId, status)) },
                                    onPriorityClick = { taskId, priority -> handleIntent(TaskMangerIntent.OnPriorityClicked(taskId, priority)) },
                                    onAssigneeClick = { taskId, assignees -> handleIntent(TaskMangerIntent.OnAssigneeClicked(taskId, assignees)) }
                                )
                            }
                        }

                        // Upcoming Tasks Section
                        if (data.filteredUpcomingTasks.isNotEmpty()) {
                            item {
                                TaskSectionCollapsibleView(
                                    title = "Upcoming tasks",
                                    tasks = data.filteredUpcomingTasks,
                                    color = StatusColors.UpcomingBlue,
                                    isExpanded = data.isUpcomingExpanded,
                                    onToggle = { handleIntent(TaskMangerIntent.OnToggleUpcomingSection) },
                                    onTaskClick = { handleIntent(TaskMangerIntent.OnTaskClicked(it)) },
                                    onStatusClick = { taskId, status -> handleIntent(TaskMangerIntent.OnStatusClicked(taskId, status)) },
                                    onPriorityClick = { taskId, priority -> handleIntent(TaskMangerIntent.OnPriorityClicked(taskId, priority)) },
                                    onAssigneeClick = { taskId, assignees -> handleIntent(TaskMangerIntent.OnAssigneeClicked(taskId, assignees)) }
                                )
                            }
                        }

                        // Closed Tasks Section
                        if (data.filteredClosedTasks.isNotEmpty()) {
                            item {
                                TaskSectionCollapsibleView(
                                    title = "Closed tasks",
                                    tasks = data.filteredClosedTasks,
                                    color = StatusColors.ClosedGray,
                                    isExpanded = data.isClosedExpanded,
                                    onToggle = { handleIntent(TaskMangerIntent.OnToggleClosedSection) },
                                    onTaskClick = { handleIntent(TaskMangerIntent.OnTaskClicked(it)) },
                                    onStatusClick = { taskId, status -> handleIntent(TaskMangerIntent.OnStatusClicked(taskId, status)) },
                                    onPriorityClick = { taskId, priority -> handleIntent(TaskMangerIntent.OnPriorityClicked(taskId, priority)) },
                                    onAssigneeClick = { taskId, assignees -> handleIntent(TaskMangerIntent.OnAssigneeClicked(taskId, assignees)) }
                                )
                            }
                        }
                    }
                }
            }

            // Assignee Selection Bottom Sheet
            EcareProPersonSelectionBottomSheet(
                title = "Select assignee",
                isVisible = data.isAssigneeSheetVisible,
                persons = data.assigneeSheetAssignees.map { assignee ->
                    PersonSelectionItem(
                        id = assignee.id.toString(),
                        name = assignee.name,
                        photo = assignee.photo,
                        subtitle = assignee.designation,
                        isSelected = true,
                    )
                },
                searchPlaceholder = "Search by assignee name",
                onDismiss = { handleIntent(TaskMangerIntent.OnAssigneeSheetDismissed) },
                onSelectionChanged = { /* TODO: Handle assignee selection change when API is available */ }
            )

            // Priority Selection Bottom Sheet
            EcareProSelectionBottomSheet(
                title = "Priority",
                isVisible = data.isPrioritySheetVisible,
                options = TaskPriority.entries.map { it.displayName },
                selectedOptions = listOf(data.prioritySheetCurrentPriority.displayName),
                isMultiSelection = false,
                onDismiss = { handleIntent(TaskMangerIntent.OnPrioritySheetDismissed) },
                onOptionsSelected = { selectedList ->
                    val selectedName = selectedList.firstOrNull() ?: return@EcareProSelectionBottomSheet
                    val newPriority = TaskPriority.entries.firstOrNull { it.displayName == selectedName }
                        ?: return@EcareProSelectionBottomSheet
                    handleIntent(TaskMangerIntent.OnPrioritySelected(data.prioritySheetTaskId, newPriority))
                }
            )

            // Status Selection Bottom Sheet
            EcareProSelectionBottomSheet(
                title = "Status",
                isVisible = data.isStatusSheetVisible,
                options = TaskStatus.entries.map { it.displayName },
                selectedOptions = listOf(data.statusSheetCurrentStatus.displayName),
                isMultiSelection = false,
                onDismiss = { handleIntent(TaskMangerIntent.OnStatusSheetDismissed) },
                onOptionsSelected = { selectedList ->
                    val selectedName = selectedList.firstOrNull() ?: return@EcareProSelectionBottomSheet
                    val newStatus = TaskStatus.entries.firstOrNull { it.displayName == selectedName }
                        ?: return@EcareProSelectionBottomSheet
                    handleIntent(TaskMangerIntent.OnStatusSelected(data.statusSheetTaskId, newStatus))
                }
            )
        }
    }
}

/**
 * Collapsible section for tasks grouped by category
 */
@Composable
private fun TaskSectionCollapsibleView(
    title: String,
    tasks: List<TaskPresentation>,
    color: Color,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onTaskClick: (String) -> Unit,
    onStatusClick: (String, TaskStatus) -> Unit,
    onPriorityClick: (String, TaskPriority) -> Unit,
    onAssigneeClick: (String, List<TaskAssigneePresentation>) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            // Section Header
            SectionHeader(
                title = title,
                count = tasks.size,
                color = color,
                isExpanded = isExpanded,
                onToggle = onToggle
            )

            // Section Content
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(animationSpec = tween(300)) +
                        expandVertically(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300)) +
                        shrinkVertically(animationSpec = tween(300))
            ) {
                Column(
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    tasks.forEachIndexed { index, task ->
                        TaskCard(
                            task = task,
                            onClick = { onTaskClick(task.id) },
                            onStatusClick = { onStatusClick(task.id, task.status) },
                            onPriorityClick = { onPriorityClick(task.id, task.priority) },
                            onAssigneeClick = { onAssigneeClick(task.id, task.assignees) }
                        )

                        if (index != tasks.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 56.dp, end = 16.dp),
                                thickness = 0.5.dp,
                                color = MaterialTheme.appColors.border
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    count: Int,
    color: Color,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "chevron_rotation"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Colored indicator bar
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(24.dp)
                .background(color, RoundedCornerShape(2.dp))
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Title with count
        Text(
            text = "$title ($count)",
            style = MaterialTheme.appTypography.interMedium14px,
            color = MaterialTheme.appColors.textPrimary,
            modifier = Modifier.weight(1f)
        )

        // Chevron icon
        Icon(
            imageVector = Icons.Default.ExpandMore,
            contentDescription = if (isExpanded) "Collapse" else "Expand",
            modifier = Modifier
                .size(18.dp)
                .rotate(rotation),
            tint = MaterialTheme.appColors.textSecondary
        )
    }
}

@Composable
private fun TaskCard(
    task: TaskPresentation,
    onClick: () -> Unit,
    onStatusClick: () -> Unit,
    onPriorityClick: () -> Unit,
    onAssigneeClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Status indicator (from common components)
        StatusIndicator(status = task.status, onClick = onStatusClick)

        // Content
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Task title
            Text(
                text = task.taskTitle,
                style = MaterialTheme.appTypography.interRegular14px,
                color = MaterialTheme.appColors.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category badge
                CategoryBadge(type = task.taskList)

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Assignee avatars (from common components)
                    if (task.assignees.isNotEmpty()) {
                        AssigneeAvatars(
                            assignees = task.assignees,
                            onClick = onAssigneeClick
                        )
                    }

                    // Separator dot
                    if (task.assignees.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .background(MaterialTheme.appColors.border, CircleShape)
                        )
                    }

                    // Priority flag (from common components)
                    PriorityFlagIcon(
                        priority = task.priority,
                        onClick = onPriorityClick
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryBadge(type: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(Color(0xFF2096BA), CircleShape)
        )

        Text(
            text = type,
            style = MaterialTheme.appTypography.interRegular12px,
            color = MaterialTheme.appColors.textSecondary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskMangerScreenPreview() {
    EcareProTheme {
        TaskMangerScreenContent(
            uiState = UiState.Success(
                TaskMangerUiState(
                    filterTabs = listOf("My tasks", "All tasks", "Assigned to me", "Assigned by me"),
                    filteredOverdueTasks = listOf(
                        TaskPresentation(
                            id = "1",
                            taskTitle = "Complete and submit today's homework on time",
                            taskList = "Academics",
                            priority = TaskPriority.HIGH,
                            status = TaskStatus.OPEN,
                            assignees = listOf(
                                TaskAssigneePresentation(id = 1, name = "John Doe", photo = ""),
                                TaskAssigneePresentation(id = 2, name = "Jane Smith", photo = ""),
                                TaskAssigneePresentation(id = 3, name = "Bob Wilson", photo = "")
                            )
                        ),
                        TaskPresentation(
                            id = "2",
                            taskTitle = "Attend the morning assembly and stand in proper line",
                            taskList = "Academics",
                            priority = TaskPriority.MEDIUM,
                            status = TaskStatus.OPEN,
                            assignees = listOf(
                                TaskAssigneePresentation(id = 1, name = "Alice Brown", photo = "")
                            )
                        )
                    ),
                    filteredTodaysTasks = listOf(
                        TaskPresentation(
                            id = "3",
                            taskTitle = "Complete and submit today's homework on time",
                            taskList = "Academics",
                            priority = TaskPriority.LOW,
                            status = TaskStatus.OPEN,
                            assignees = listOf(
                                TaskAssigneePresentation(id = 1, name = "Test User", photo = "")
                            )
                        )
                    ),
                    isOverdueExpanded = true,
                    isTodayExpanded = true
                )
            ),
            handleIntent = {},
            snackbarHostState = SnackbarHostState(),
            snackbarMessage = null,
            onSnackbarDismissed = {}
        )
    }
}

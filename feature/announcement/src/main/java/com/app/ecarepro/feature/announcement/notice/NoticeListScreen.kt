package com.app.ecarepro.feature.announcement.notice

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.Notice
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
import com.app.ecarepro.designsystem.core.component.BottomSearchBarView
import com.app.ecarepro.designsystem.core.component.EcareProEmptyState
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProSelectionBottomSheet
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.announcement.common.DropdownSelectorField
import com.app.ecarepro.feature.announcement.common.NoticeFilterBottomSheet
import com.app.ecarepro.feature.announcement.common.NoticeItem

@Composable
fun NoticeListScreen(
    noticeType: NoticeType,
    viewModel: NoticeListViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToDetail: (String, NoticeType) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is NoticeListEvent.NavigateBack -> navigateBack()
                is NoticeListEvent.NavigateToDetail -> navigateToDetail(event.noticeId, event.noticeType)
                is NoticeListEvent.ShowMessage -> {
                    snackbarMessage = event.snackbarMessage
                    snackbarHostState.showSnackbar(event.snackbarMessage.text)
                }
            }
        }
    }

    NoticeListContent(
        noticeType = noticeType,
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null },
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun NoticeListContent(
    noticeType: NoticeType,
    uiState: UiState<NoticeListUiState>,
    handleIntent: (NoticeListIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit,
) {
    val title = when (noticeType) {
        NoticeType.SCHOOL -> "All Notices"
        NoticeType.STAFF -> "Staff Notices"
        NoticeType.CLASS -> "Class Notices"
    }

    EcareProScaffold(
        topBar = {
            Column {
                EcareProTopAppBar(
                    title = title,
                    onNavigationClicked = { handleIntent(NoticeListIntent.OnBackClicked) }
                )
                if (noticeType == NoticeType.CLASS && uiState is UiState.Success) {
                    DropdownSelectorField(
                        selectedLabel = uiState.data.selectedClass?.className ?: "Select Class",
                        onClick = { handleIntent(NoticeListIntent.OnShowClassPicker) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        },
        containerColor = White,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = onSnackbarDismissed,
        bottomBar = {
            BottomSearchBarView(
                searchText = if (uiState is UiState.Success) uiState.data.searchQuery else "",
                onSearchTextChange = { handleIntent(NoticeListIntent.OnSearchQueryChanged(it)) },
                placeholder = "Search by title",
                rightIconVector = Icons.Default.FilterList,
                onRightIconClick = { handleIntent(NoticeListIntent.OnShowFilter) }
            )
        }
    ) { paddingValues ->
        UiStateHandler(
            modifier = Modifier.padding(paddingValues),
            state = uiState
        ) { data ->
            PullToRefreshBox(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                isRefreshing = data.isRefreshing,
                onRefresh = { handleIntent(NoticeListIntent.OnRefresh) }
            ) {
                if (data.filteredNotices.isEmpty() && !data.isNoticesLoading) {
                    EcareProEmptyState(message = "No notices found")
                } else {
                    NoticeGroupedList(
                        notices = data.filteredNotices,
                        onNoticeClicked = { noticeId ->
                            handleIntent(NoticeListIntent.OnNoticeClicked(noticeId))
                        }
                    )
                }
            }

            if (data.isFilterVisible) {
                NoticeFilterBottomSheet(
                    selectedFilter = data.selectedFilter,
                    onFilterSelected = { handleIntent(NoticeListIntent.OnFilterSelected(it)) },
                    onDismiss = { handleIntent(NoticeListIntent.OnDismissFilter) }
                )
            }

            if (data.isClassPickerVisible && noticeType == NoticeType.CLASS) {
                EcareProSelectionBottomSheet(
                    title = "Select Class",
                    isVisible = true,
                    options = data.classes.map { it.className ?: "" },
                    selectedOptions = listOf(data.selectedClass?.className ?: ""),
                    isMultiSelection = false,
                    onDismiss = { handleIntent(NoticeListIntent.OnDismissClassPicker) },
                    onOptionsSelected = { selected ->
                        val classItem = data.classes.find { it.className == selected.firstOrNull() }
                        classItem?.let { handleIntent(NoticeListIntent.OnClassSelected(it)) }
                    }
                )
            }
        }
    }
}

private val previewNotices = listOf(
    Notice("1", 101, "Terms & Conditions", "<p>Please read carefully.</p>", "21-Jan-2025", "2025-01-21T14:34:00", isNew = true, isRead = false, hasAttachment = true, filePath = null, fileSize = null),
    Notice("2", 102, "Holiday Notice for Republic Day", "<p>School will remain closed.</p>", "21-Jan-2025", "2025-01-21T14:34:00", isNew = true, isRead = false, hasAttachment = true, filePath = null, fileSize = null),
    Notice("3", 103, "Parent-Teacher Meeting Schedule", "<p>Meeting on Friday.</p>", "21-Jan-2025", "2025-01-21T14:34:00", isNew = false, isRead = true, hasAttachment = false, filePath = null, fileSize = null),
    Notice("4", 104, "Mandatory Uniform Guidelines Update", "<p>New uniform policy.</p>", "20-Jan-2025", "2025-01-20T14:34:00", isNew = false, isRead = true, hasAttachment = false, filePath = null, fileSize = null),
    Notice("5", 105, "Transport Route Timing Changes", "<p>Bus timings updated.</p>", "20-Jan-2025", "2025-01-20T14:34:00", isNew = false, isRead = false, hasAttachment = false, filePath = null, fileSize = null),
)

@Preview(showBackground = true, name = "School Notice List")
@Composable
private fun SchoolNoticeListPreview() {
    EcareProTheme {
        NoticeListContent(
            noticeType = NoticeType.SCHOOL,
            uiState = UiState.Success(
                NoticeListUiState(
                    allNotices = previewNotices,
                    filteredNotices = previewNotices,
                )
            ),
            handleIntent = {},
            snackbarHostState = remember { SnackbarHostState() },
            snackbarMessage = null,
            onSnackbarDismissed = {},
        )
    }
}

@Preview(showBackground = true, name = "Staff Notice List")
@Composable
private fun StaffNoticeListPreview() {
    EcareProTheme {
        NoticeListContent(
            noticeType = NoticeType.STAFF,
            uiState = UiState.Success(
                NoticeListUiState(
                    allNotices = previewNotices,
                    filteredNotices = previewNotices,
                )
            ),
            handleIntent = {},
            snackbarHostState = remember { SnackbarHostState() },
            snackbarMessage = null,
            onSnackbarDismissed = {},
        )
    }
}

@Preview(showBackground = true, name = "Class Notice List")
@Composable
private fun ClassNoticeListPreview() {
    EcareProTheme {
        NoticeListContent(
            noticeType = NoticeType.CLASS,
            uiState = UiState.Success(
                NoticeListUiState(
                    allNotices = previewNotices,
                    filteredNotices = previewNotices,
                    selectedClass = com.app.ecarepro.core.domain.model.Class(32, "12 A", "Si+8afTPNMJj527AG4g+aQ==", false),
                )
            ),
            handleIntent = {},
            snackbarHostState = remember { SnackbarHostState() },
            snackbarMessage = null,
            onSnackbarDismissed = {},
        )
    }
}

@Preview(showBackground = true, name = "Notice List Empty")
@Composable
private fun NoticeListEmptyPreview() {
    EcareProTheme {
        NoticeListContent(
            noticeType = NoticeType.SCHOOL,
            uiState = UiState.Success(NoticeListUiState()),
            handleIntent = {},
            snackbarHostState = remember { SnackbarHostState() },
            snackbarMessage = null,
            onSnackbarDismissed = {},
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NoticeGroupedList(
    notices: List<com.app.ecarepro.core.domain.model.Notice>,
    onNoticeClicked: (String) -> Unit,
) {
    val grouped = notices.groupBy { it.noticeDate ?: "" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        grouped.forEach { (date, noticesForDate) ->
            stickyHeader {
                Text(
                    text = date.uppercase(),
                    style = MaterialTheme.appTypography.interRegular12px,
                    color = MaterialTheme.appColors.textSecondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
            items(noticesForDate) { notice ->
                NoticeItem(
                    notice = notice,
                    onClick = { onNoticeClicked(notice.id) }
                )
                HorizontalDivider(thickness = 0.5.dp)
            }
        }
    }
}

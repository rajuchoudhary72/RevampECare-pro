package com.app.ecarepro.feature.feed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.Attachment
import com.app.ecarepro.core.domain.model.FeedType
import com.app.ecarepro.core.domain.model.FeedUpdate
import com.app.ecarepro.core.domain.model.FileType
import com.app.ecarepro.core.domain.model.GalleryUpdate
import com.app.ecarepro.designsystem.core.component.EcareProBackground
import com.app.ecarepro.designsystem.core.component.Loader
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.feature.feed.component.FeedItem
import com.app.ecarepro.feature.feed.component.FeedTabBar

@Composable
fun FeedScreen(
    viewModel: FeedViewModel = hiltViewModel(),
    onFeedItemClick: (FeedUpdate) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FeedScreenContent(
        uiState = uiState,
        onRefresh = { viewModel.handleIntent(FeedIntent.RefreshFeed) },
        onLoadMore = { viewModel.handleIntent(FeedIntent.LoadMoreFeed) },
        onTabSelected = { tab -> viewModel.handleIntent(FeedIntent.ChangeTab(tab)) },
        onFilterClick = { viewModel.handleIntent(FeedIntent.ShowFilter) },
        onFeedItemClick = onFeedItemClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FeedScreenContent(
    uiState: FeedUiState,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onTabSelected: (FeedTab) -> Unit,
    onFilterClick: () -> Unit,
    onFeedItemClick: (FeedUpdate) -> Unit
) {
    val listState = rememberLazyListState()
    val pullToRefreshState = rememberPullToRefreshState()

    // Detect when scrolled to bottom for pagination
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null &&
                    lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 3 &&
                    !uiState.isLoadingMore &&
                    uiState.hasMorePages
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            onLoadMore()
        }
    }

    EcareProBackground(
        overlayColor = MaterialTheme.appColors.background
    ) {
        Scaffold(
            containerColor = MaterialTheme.appColors.background,
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Tab Bar
                FeedTabBar(
                    selectedTab = uiState.selectedTab,
                    onTabSelected = onTabSelected,
                    onFilterClick = onFilterClick
                )

                // Feed Content
                PullToRefreshBox(
                    state = pullToRefreshState,
                    isRefreshing = uiState.isLoading && uiState.feeds.isNotEmpty(),
                    onRefresh = onRefresh,
                    modifier = Modifier.fillMaxSize()
                ) {
                when {
                    uiState.isLoading && uiState.feeds.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Loader()
                        }
                    }

                    uiState.errorMessage != null && uiState.feeds.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = uiState.errorMessage,
                                color = MaterialTheme.appColors.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    uiState.feeds.isEmpty() && !uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No updates available",
                                color = MaterialTheme.appColors.textSecondary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    else -> {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = uiState.feeds,
                                key = { it.id }
                            ) { feedUpdate ->
                                FeedItem(
                                    feedUpdate = feedUpdate,
                                    onClick = { onFeedItemClick(feedUpdate) }
                                )
                            }

                            // Loading indicator at the bottom when loading more
                            if (uiState.isLoadingMore) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            color = MaterialTheme.appColors.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                }
            }
        }
    }
}

// ============================================
// Preview Section
// ============================================

private object PreviewMockData {
    val circularWithImage = FeedUpdate(
        menuID = 7,
        chMenuID = 10,
        sbChMenuID = 0,
        module = "Circular",
        id = "circular_1",
        caption = "Annual Sports Day Announcement",
        hasAttachment = true,
        updatedOn = "07 Nov",
        msgDTL = "We are excited to announce the Annual Sports Day. All students are requested to participate.",
        galleryUpdate = null,
        webLink = "/Portal/Circular?ID=abc123",
        feedType = FeedType.CIRCULAR,
        attachments = listOf(
            Attachment(
                fileName = "sports_day.jpg",
                fileUrl = "https://picsum.photos/800/600",
                fileType = FileType.IMAGE
            )
        )
    )

    val noticeWithPdf = FeedUpdate(
        menuID = 7,
        chMenuID = 11,
        sbChMenuID = 0,
        module = "Notice",
        id = "notice_1",
        caption = "Parent-Teacher Meeting Schedule",
        hasAttachment = true,
        updatedOn = "16 Oct",
        msgDTL = "Please find attached the schedule for upcoming parent-teacher meetings.",
        galleryUpdate = null,
        webLink = "/Portal/Notice?ID=xyz789",
        feedType = FeedType.NOTICE,
        attachments = listOf(
            Attachment(
                fileName = "PTM_Schedule.pdf",
                fileUrl = "https://example.com/ptm.pdf",
                fileType = FileType.PDF
            ),
            Attachment(
                fileName = "Guidelines.docx",
                fileUrl = "https://example.com/guidelines.docx",
                fileType = FileType.DOCX
            )
        )
    )

    val photoWithGallery = FeedUpdate(
        menuID = 34,
        chMenuID = 48,
        sbChMenuID = 0,
        module = "Photo",
        id = "gzklUmg3mma+0XPviSQW0w==",
        caption = "Yoga Days",
        hasAttachment = false,
        updatedOn = "19 Nov",
        msgDTL = null,
        galleryUpdate = GalleryUpdate(
            sMdlID = 1,
            subModule = null,
            total = 3,
            fileURL = "https://s3-noi.aces3.ai/franciscan/SchImg/DEMOIN/PhotoAlbum/Thumb/",
            fileNames = listOf("Photo_4443356.jpg", "Photo_4444778.jpg", "Photo_4446294.jpg")
        ),
        webLink = "/Portal/PhotoAlbums",
        feedType = FeedType.PHOTO,
        attachments = emptyList() // galleryUpdate will be converted to attachments in UI
    )

    val circularNoAttachment = FeedUpdate(
        menuID = 7,
        chMenuID = 10,
        sbChMenuID = 0,
        module = "Circular",
        id = "circular_2",
        caption = "Holiday Homework Reminder",
        hasAttachment = false,
        updatedOn = "06 Nov",
        msgDTL = "This is a reminder to complete your holiday homework before the school reopens.",
        galleryUpdate = null,
        webLink = "/Portal/Circular?ID=def456",
        feedType = FeedType.CIRCULAR,
        attachments = emptyList()
    )

    val allFeeds = listOf(
        circularWithImage,
        noticeWithPdf,
        photoWithGallery,
        circularNoAttachment
    )
}

@Preview(showBackground = true, name = "Feed Screen - With Data")
@Composable
private fun FeedScreenWithDataPreview() {
    EcareProTheme {
        FeedScreenContent(
            uiState = FeedUiState(
                feeds = PreviewMockData.allFeeds,
                isLoading = false,
                isLoadingMore = false,
                currentPage = 1,
                totalCount = 47,
                hasMorePages = true,
                errorMessage = null
            ),
            onRefresh = {},
            onLoadMore = {},
            onTabSelected = {},
            onFilterClick = {},
            onFeedItemClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Feed Screen - Loading")
@Composable
private fun FeedScreenLoadingPreview() {
    EcareProTheme {
        FeedScreenContent(
            uiState = FeedUiState(
                feeds = emptyList(),
                isLoading = true,
                isLoadingMore = false,
                currentPage = 1,
                totalCount = 0,
                hasMorePages = false,
                errorMessage = null
            ),
            onRefresh = {},
            onLoadMore = {},
            onTabSelected = {},
            onFilterClick = {},
            onFeedItemClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Feed Screen - Empty")
@Composable
private fun FeedScreenEmptyPreview() {
    EcareProTheme {
        FeedScreenContent(
            uiState = FeedUiState(
                feeds = emptyList(),
                isLoading = false,
                isLoadingMore = false,
                currentPage = 1,
                totalCount = 0,
                hasMorePages = false,
                errorMessage = null
            ),
            onRefresh = {},
            onLoadMore = {},
            onTabSelected = {},
            onFilterClick = {},
            onFeedItemClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Feed Screen - Error")
@Composable
private fun FeedScreenErrorPreview() {
    EcareProTheme {
        FeedScreenContent(
            uiState = FeedUiState(
                feeds = emptyList(),
                isLoading = false,
                isLoadingMore = false,
                currentPage = 1,
                totalCount = 0,
                hasMorePages = false,
                errorMessage = "Failed to load feed. Please try again."
            ),
            onRefresh = {},
            onLoadMore = {},
            onTabSelected = {},
            onFilterClick = {},
            onFeedItemClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Feed Screen - Loading More")
@Composable
private fun FeedScreenLoadingMorePreview() {
    EcareProTheme {
        FeedScreenContent(
            uiState = FeedUiState(
                feeds = PreviewMockData.allFeeds,
                isLoading = false,
                isLoadingMore = true,
                currentPage = 1,
                totalCount = 47,
                hasMorePages = true,
                errorMessage = null
            ),
            onRefresh = {},
            onLoadMore = {},
            onTabSelected = {},
            onFilterClick = {},
            onFeedItemClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Feed Screen - Pinned Tab")
@Composable
private fun FeedScreenPinnedTabPreview() {
    EcareProTheme {
        FeedScreenContent(
            uiState = FeedUiState(
                feeds = PreviewMockData.allFeeds,
                isLoading = false,
                isLoadingMore = false,
                currentPage = 1,
                totalCount = 47,
                hasMorePages = true,
                errorMessage = null,
                selectedTab = FeedTab.PINNED
            ),
            onRefresh = {},
            onLoadMore = {},
            onTabSelected = {},
            onFilterClick = {},
            onFeedItemClick = {}
        )
    }
}

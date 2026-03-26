package com.app.ecarepro.feature.feed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.Attachment
import com.app.ecarepro.core.domain.model.FeedModule
import com.app.ecarepro.core.domain.model.FeedType
import com.app.ecarepro.core.domain.model.FeedUpdate
import com.app.ecarepro.core.domain.model.FileType
import com.app.ecarepro.core.domain.model.GalleryUpdate
import com.app.ecarepro.designsystem.core.component.EcareProBackground
import com.app.ecarepro.designsystem.core.component.Loader
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.feature.feed.component.FeedFilterBottomSheet
import com.app.ecarepro.feature.feed.component.FeedItem
import com.app.ecarepro.feature.feed.component.FeedTabBar

@Composable
fun FeedScreen(
    viewModel: FeedViewModel = hiltViewModel(),
    onFeedItemClick: (FeedUpdate) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FeedScreenContent(
        uiState = uiState,
        onRefresh = { viewModel.handleIntent(FeedIntent.RefreshFeed) },
        onLoadMore = { viewModel.handleIntent(FeedIntent.LoadMoreFeed) },
        onTabSelected = { tab -> viewModel.handleIntent(FeedIntent.ChangeTab(tab)) },
        onFilterClick = { viewModel.handleIntent(FeedIntent.ShowFilter) },
        onFilterDismiss = { viewModel.handleIntent(FeedIntent.DismissFilter) },
        onFilterApply = { ids -> viewModel.handleIntent(FeedIntent.ApplyFilter(ids)) },
        onFeedItemClick = onFeedItemClick,
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
    onFilterDismiss: () -> Unit,
    onFilterApply: (Set<Int>) -> Unit,
    onFeedItemClick: (FeedUpdate) -> Unit,
) {
    val listState = rememberLazyListState()
    val pullToRefreshState = rememberPullToRefreshState()

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
        if (shouldLoadMore) onLoadMore()
    }

    // Filter bottom sheet
    if (uiState.showFilterSheet) {
        FeedFilterBottomSheet(
            modules = uiState.modules,
            activeFilterMenuIDs = uiState.activeFilterMenuIDs,
            onApply = onFilterApply,
            onDismiss = onFilterDismiss,
        )
    }

    EcareProBackground(overlayColor = MaterialTheme.appColors.background) {
        Scaffold(
            containerColor = MaterialTheme.appColors.background,
            contentWindowInsets = WindowInsets(0),
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                FeedTabBar(
                    selectedTab = uiState.selectedTab,
                    onTabSelected = onTabSelected,
                    onFilterClick = onFilterClick,
                )

                PullToRefreshBox(
                    state = pullToRefreshState,
                    isRefreshing = uiState.isLoading && uiState.feeds.isNotEmpty(),
                    onRefresh = onRefresh,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    when {
                        uiState.isLoading && uiState.feeds.isEmpty() -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                Loader()
                            }
                        }

                        uiState.errorMessage != null && uiState.feeds.isEmpty() -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = uiState.errorMessage,
                                    color = MaterialTheme.appColors.error,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        }

                        uiState.feeds.isEmpty() && !uiState.isLoading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "No updates available",
                                    color = MaterialTheme.appColors.textSecondary,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        }

                        else -> {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize(),
                            ) {
                                items(
                                    items = uiState.feeds,
                                    key = { it.uniqueKey },
                                ) { feedUpdate ->
                                    FeedItem(
                                        feedUpdate = feedUpdate,
                                        onClick = { onFeedItemClick(feedUpdate) },
                                    )
                                }

                                if (uiState.isLoadingMore) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            CircularProgressIndicator(
                                                color = MaterialTheme.appColors.primary,
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

private object PreviewData {
    val modules = listOf(
        FeedModule(menuID = 2, moduleName = "Message"),
        FeedModule(menuID = 52, moduleName = "School Notice"),
        FeedModule(menuID = 53, moduleName = "Class Notice"),
        FeedModule(menuID = 55, moduleName = "Circular"),
        FeedModule(menuID = 59, moduleName = "Leave Requests"),
        FeedModule(menuID = 71, moduleName = "Photo Albums"),
        FeedModule(menuID = 72, moduleName = "Video Albums"),
    )

    val feeds = listOf(
        FeedUpdate(
            menuID = 52,
            module = "Notice",
            id = "notice_1",
            caption = "Terms And Conditions",
            hasAttachment = true,
            updatedOn = "10 Mar",
            msgDTL = null,
            galleryUpdate = null,
            webLink = "/Portal/Notice?ID=abc",
            feedType = FeedType.SCHOOL_NOTICE,
            attachments = listOf(
                Attachment("schedule.pdf", "https://example.com/s.pdf", FileType.PDF)
            ),
            userName = "School",
            accentColor = "#FFC107",
        ),
        FeedUpdate(
            menuID = 55,
            module = "Circular",
            id = "circular_1",
            caption = "Annual Sports Day Announcement",
            hasAttachment = false,
            updatedOn = "09 Mar",
            msgDTL = "All students are requested to participate in the Annual Sports Day event.",
            galleryUpdate = null,
            webLink = "/Portal/Circular?ID=def",
            feedType = FeedType.CIRCULAR,
            attachments = emptyList(),
            userName = "School",
            accentColor = "#4CAF50",
        ),
        FeedUpdate(
            menuID = 71,
            module = "Photo",
            id = "photo_1",
            caption = "Yoga Day Celebration",
            hasAttachment = false,
            updatedOn = "21 Feb",
            msgDTL = null,
            galleryUpdate = GalleryUpdate(
                sMdlID = 1,
                subModule = null,
                total = 3,
                fileURL = "https://picsum.photos/800/",
                fileNames = listOf("500", "501", "502"),
            ),
            webLink = "/Portal/PhotoAlbums",
            feedType = FeedType.PHOTO,
            attachments = emptyList(),
            userName = "Gallery",
            accentColor = "#00BCD4",
        ),
        FeedUpdate(
            menuID = 53,
            module = "Notice",
            id = "notice_2",
            caption = "Class Notice: Holiday on Monday",
            hasAttachment = false,
            updatedOn = "15 Jan",
            msgDTL = "School will remain closed on Monday due to a public holiday.",
            galleryUpdate = null,
            webLink = "/Portal/Notice?ID=ghi",
            feedType = FeedType.CLASS_NOTICE,
            attachments = emptyList(),
            userName = "School",
            accentColor = "#FF9800",
        ),
    )
}

@Preview(showBackground = true, name = "Feed Screen - With Data")
@Composable
private fun FeedScreenWithDataPreview() {
    EcareProTheme {
        FeedScreenContent(
            uiState = FeedUiState(
                feeds = PreviewData.feeds,
                allFeeds = PreviewData.feeds,
                modules = PreviewData.modules,
                isLoading = false,
                isLoadingMore = false,
                currentPage = 1,
                totalCount = 98,
                hasMorePages = true,
            ),
            onRefresh = {},
            onLoadMore = {},
            onTabSelected = {},
            onFilterClick = {},
            onFilterDismiss = {},
            onFilterApply = {},
            onFeedItemClick = {},
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
            ),
            onRefresh = {},
            onLoadMore = {},
            onTabSelected = {},
            onFilterClick = {},
            onFilterDismiss = {},
            onFilterApply = {},
            onFeedItemClick = {},
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
                hasMorePages = false,
            ),
            onRefresh = {},
            onLoadMore = {},
            onTabSelected = {},
            onFilterClick = {},
            onFilterDismiss = {},
            onFilterApply = {},
            onFeedItemClick = {},
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
                errorMessage = "Failed to load feed. Please try again.",
            ),
            onRefresh = {},
            onLoadMore = {},
            onTabSelected = {},
            onFilterClick = {},
            onFilterDismiss = {},
            onFilterApply = {},
            onFeedItemClick = {},
        )
    }
}

@Preview(showBackground = true, name = "Feed Screen - Loading More")
@Composable
private fun FeedScreenLoadingMorePreview() {
    EcareProTheme {
        FeedScreenContent(
            uiState = FeedUiState(
                feeds = PreviewData.feeds,
                allFeeds = PreviewData.feeds,
                modules = PreviewData.modules,
                isLoading = false,
                isLoadingMore = true,
                currentPage = 1,
                totalCount = 98,
                hasMorePages = true,
            ),
            onRefresh = {},
            onLoadMore = {},
            onTabSelected = {},
            onFilterClick = {},
            onFilterDismiss = {},
            onFilterApply = {},
            onFeedItemClick = {},
        )
    }
}

@Preview(showBackground = true, name = "Feed Screen - Pinned Tab")
@Composable
private fun FeedScreenPinnedTabPreview() {
    EcareProTheme {
        FeedScreenContent(
            uiState = FeedUiState(
                feeds = PreviewData.feeds.take(2),
                allFeeds = PreviewData.feeds,
                modules = PreviewData.modules,
                isLoading = false,
                currentPage = 1,
                totalCount = 98,
                hasMorePages = true,
                selectedTab = FeedTab.PINNED,
            ),
            onRefresh = {},
            onLoadMore = {},
            onTabSelected = {},
            onFilterClick = {},
            onFilterDismiss = {},
            onFilterApply = {},
            onFeedItemClick = {},
        )
    }
}

@Preview(showBackground = true, name = "Feed Screen - Filter Active")
@Composable
private fun FeedScreenFilterActivePreview() {
    EcareProTheme {
        FeedScreenContent(
            uiState = FeedUiState(
                feeds = PreviewData.feeds.filter { it.menuID == 55 },
                allFeeds = PreviewData.feeds,
                modules = PreviewData.modules,
                activeFilterMenuIDs = setOf(55),
                isLoading = false,
                currentPage = 1,
                totalCount = 98,
                hasMorePages = true,
            ),
            onRefresh = {},
            onLoadMore = {},
            onTabSelected = {},
            onFilterClick = {},
            onFilterDismiss = {},
            onFilterApply = {},
            onFeedItemClick = {},
        )
    }
}

package com.app.ecarepro.feature.feed

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.Attachment
import com.app.ecarepro.core.domain.model.FeedType
import com.app.ecarepro.core.domain.model.FeedUpdate
import com.app.ecarepro.core.domain.model.FileType
import com.app.ecarepro.core.domain.model.GalleryUpdate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    init {
        loadFeed()
    }

    fun handleIntent(intent: FeedIntent) {
        when (intent) {
            is FeedIntent.LoadFeed -> loadFeed(refresh = false)
            is FeedIntent.RefreshFeed -> loadFeed(refresh = true)
            is FeedIntent.LoadMoreFeed -> loadMoreFeed()
            is FeedIntent.OnFeedItemClick -> {
                // Navigate to detail screen based on feed type
                // This will be handled by the screen
            }
            is FeedIntent.ChangeTab -> {
                _uiState.update { it.copy(selectedTab = intent.tab) }
                loadFeed(refresh = true)
            }
            is FeedIntent.ShowFilter -> {
                _uiState.update { it.copy(showFilterDialog = !it.showFilterDialog) }
            }
        }
    }

    private fun loadFeed(refresh: Boolean = false) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Simulate API call delay
            delay(1000)

            try {
                val mockFeeds = generateMockFeeds(1)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        feeds = mockFeeds,
                        currentPage = 1,
                        totalCount = 47,
                        hasMorePages = true,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load feed"
                    )
                }
            }
        }
    }

    private fun loadMoreFeed() {
        if (_uiState.value.isLoading || !_uiState.value.hasMorePages) return

        val nextPage = _uiState.value.currentPage + 1

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }

            // Simulate API call delay
            delay(1000)

            try {
                val moreFeeds = generateMockFeeds(nextPage)
                _uiState.update {
                    val newFeeds = it.feeds + moreFeeds
                    it.copy(
                        isLoadingMore = false,
                        feeds = newFeeds,
                        currentPage = nextPage,
                        hasMorePages = newFeeds.size < 47,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingMore = false,
                        errorMessage = e.message ?: "Failed to load more feed"
                    )
                }
            }
        }
    }

    private fun generateMockFeeds(page: Int): List<FeedUpdate> {
        val feeds = mutableListOf<FeedUpdate>()

        // Circular with single image
        feeds.add(
            FeedUpdate(
                menuID = 7,
                chMenuID = 10,
                sbChMenuID = 0,
                module = "Circular",
                id = "circular_$page",
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
        )

        // Notice with PDF attachment
        feeds.add(
            FeedUpdate(
                menuID = 7,
                chMenuID = 11,
                sbChMenuID = 0,
                module = "Notice",
                id = "notice_$page",
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
        )

        // Photo gallery with multiple images
        feeds.add(
            FeedUpdate(
                menuID = 34,
                chMenuID = 48,
                sbChMenuID = 0,
                module = "Photo",
                id = "photo_$page",
                caption = "Science Exhibition 2024",
                hasAttachment = false,
                updatedOn = "13 Oct",
                msgDTL = "Highlights from our annual science exhibition showcasing amazing projects by students.",
                galleryUpdate = GalleryUpdate(
                    sMdlID = 1,
                    subModule = null,
                    total = 3,
                    fileURL = "https://picsum.photos/",
                    fileNames = listOf("800/500", "800/501", "800/502")
                ),
                webLink = "/Portal/PhotoAlbums",
                feedType = FeedType.PHOTO,
                attachments = listOf(
                    Attachment(
                        fileName = "photo1.jpg",
                        fileUrl = "https://picsum.photos/800/500",
                        fileType = FileType.IMAGE
                    ),
                    Attachment(
                        fileName = "photo2.jpg",
                        fileUrl = "https://picsum.photos/800/501",
                        fileType = FileType.IMAGE
                    ),
                    Attachment(
                        fileName = "photo3.jpg",
                        fileUrl = "https://picsum.photos/800/502",
                        fileType = FileType.IMAGE
                    )
                )
            )
        )

        // Circular without attachments
        feeds.add(
            FeedUpdate(
                menuID = 7,
                chMenuID = 10,
                sbChMenuID = 0,
                module = "Circular",
                id = "circular2_$page",
                caption = "Holiday Homework Reminder",
                hasAttachment = false,
                updatedOn = "06 Nov",
                msgDTL = "This is a reminder to complete your holiday homework before the school reopens.",
                galleryUpdate = null,
                webLink = "/Portal/Circular?ID=def456",
                feedType = FeedType.CIRCULAR,
                attachments = emptyList()
            )
        )

        // Notice with Excel file
        feeds.add(
            FeedUpdate(
                menuID = 7,
                chMenuID = 11,
                sbChMenuID = 0,
                module = "Notice",
                id = "notice2_$page",
                caption = "Exam Time Table Released",
                hasAttachment = true,
                updatedOn = "15 Oct",
                msgDTL = "The examination timetable for final exams has been released. Please download and check.",
                galleryUpdate = null,
                webLink = "/Portal/Notice?ID=pqr123",
                feedType = FeedType.NOTICE,
                attachments = listOf(
                    Attachment(
                        fileName = "Exam_TimeTable.xlsx",
                        fileUrl = "https://example.com/timetable.xlsx",
                        fileType = FileType.XLSX
                    )
                )
            )
        )

        return feeds
    }
}

sealed interface FeedIntent {
    data object LoadFeed : FeedIntent
    data object RefreshFeed : FeedIntent
    data object LoadMoreFeed : FeedIntent
    data class OnFeedItemClick(val feedUpdate: FeedUpdate) : FeedIntent
    data class ChangeTab(val tab: FeedTab) : FeedIntent
    data object ShowFilter : FeedIntent
}

enum class FeedTab {
    LATEST,
    PINNED
}

@Immutable
data class FeedUiState(
    val feeds: List<FeedUpdate> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val currentPage: Int = 1,
    val totalCount: Int = 0,
    val hasMorePages: Boolean = true,
    val errorMessage: String? = null,
    val selectedTab: FeedTab = FeedTab.LATEST,
    val showFilterDialog: Boolean = false
)

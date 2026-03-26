package com.app.ecarepro.feature.announcement.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.core.ui.viewmodel.navKeyViewModel
import com.app.ecarepro.designsystem.core.component.ECAttachment
import com.app.ecarepro.feature.announcement.circular.CircularDetailScreen
import com.app.ecarepro.feature.announcement.circular.CircularDetailViewModel
import com.app.ecarepro.feature.announcement.circular.CircularScreen
import com.app.ecarepro.feature.announcement.circular.CreateCircularScreen
import com.app.ecarepro.feature.announcement.notice.NoticeDetailScreen
import com.app.ecarepro.feature.announcement.notice.NoticeDetailViewModel
import com.app.ecarepro.feature.announcement.notice.NoticeListScreen
import com.app.ecarepro.feature.announcement.notice.NoticeListViewModel
import com.app.ecarepro.feature.announcement.notice.NoticeType
import kotlinx.serialization.Serializable

@Serializable
sealed interface AnnouncementNavGraph : NavKey {
    @Serializable
    data class NoticeList(val noticeTypeOrdinal: Int) : AnnouncementNavGraph

    @Serializable
    data class NoticeDetail(val noticeId: String, val noticeTypeOrdinal: Int) : AnnouncementNavGraph

    @Serializable
    data object Circular : AnnouncementNavGraph

    @Serializable
    data class CircularDetail(val circularId: String) : AnnouncementNavGraph

    @Serializable
    data object CreateCircular : AnnouncementNavGraph
}

@Composable
fun EntryProviderBuilder<NavKey>.EntryAnnouncementNavigation(
    backStack: SnapshotStateList<NavKey>,
    navigateBack: () -> Unit,
    navigateToAttachmentList: (List<ECAttachment>) -> Unit,
) {
    entry<AnnouncementNavGraph.NoticeList> { key ->
        val noticeType = NoticeType.entries[key.noticeTypeOrdinal]
        val viewModel: NoticeListViewModel = navKeyViewModel(key)
        NoticeListScreen(
            noticeType = noticeType,
            viewModel = viewModel,
            navigateBack = navigateBack,
            navigateToDetail = { noticeId, type ->
                backStack.add(AnnouncementNavGraph.NoticeDetail(noticeId, type.ordinal))
            }
        )
    }

    entry<AnnouncementNavGraph.NoticeDetail> { key ->
        val viewModel: NoticeDetailViewModel = navKeyViewModel(key)
        NoticeDetailScreen(
            viewModel = viewModel,
            navigateBack = navigateBack,
            navigateToAttachment = navigateToAttachmentList
        )
    }

    entry<AnnouncementNavGraph.Circular> {
        CircularScreen(
            navigateBack = navigateBack,
            navigateToDetail = { circularId ->
                backStack.add(AnnouncementNavGraph.CircularDetail(circularId))
            },
            navigateToCreate = {
                backStack.add(AnnouncementNavGraph.CreateCircular)
            }
        )
    }

    entry<AnnouncementNavGraph.CreateCircular> {
        CreateCircularScreen(
            navigateBack = navigateBack,
        )
    }

    entry<AnnouncementNavGraph.CircularDetail> { key ->
        val viewModel: CircularDetailViewModel = navKeyViewModel(key)
        CircularDetailScreen(
            viewModel = viewModel,
            navigateBack = navigateBack,
            navigateToAttachment = navigateToAttachmentList
        )
    }
}

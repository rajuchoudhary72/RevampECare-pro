package com.app.ecarepro.feature.conversationreport.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.core.ui.viewmodel.navKeyViewModel
import com.app.ecarepro.feature.conversationreport.conversation_detail.ConversationDetailScreen
import com.app.ecarepro.feature.conversationreport.conversation_detail.ConversationDetailViewModel
import com.app.ecarepro.feature.conversationreport.conversation_list.ConversationListScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface ConversationReportNavGraph : NavKey {
    @Serializable
    data object ConversationList : ConversationReportNavGraph

    @Serializable
    data class ConversationDetail(val msgId: String) : ConversationReportNavGraph
}

@Composable
fun EntryProviderBuilder<NavKey>.entryConversationReportNavigation(
    navigateBack: () -> Unit,
    navigateTo: (NavKey) -> Unit,
    navigateToAttachment: (String, String) -> Unit = { _, _ -> },
) {
    entry<ConversationReportNavGraph.ConversationList> {
        ConversationListScreen(
            navigateBack = navigateBack,
            navigateToDetail = { msgId ->
                navigateTo(ConversationReportNavGraph.ConversationDetail(msgId))
            },
        )
    }
    entry<ConversationReportNavGraph.ConversationDetail> { key ->
        val viewModel: ConversationDetailViewModel = navKeyViewModel(key)
        ConversationDetailScreen(
            viewModel = viewModel,
            navigateBack = navigateBack,
            navigateToAttachment = { url ->
                navigateToAttachment(url.substringAfterLast('/'), url)
            },
        )
    }
}

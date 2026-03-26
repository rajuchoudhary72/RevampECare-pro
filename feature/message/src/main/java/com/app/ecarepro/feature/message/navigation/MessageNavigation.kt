package com.app.ecarepro.feature.message.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.feature.message.MessageScreen
import com.app.ecarepro.feature.message.screens.ChatScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface MessageNavigationGraph : NavKey {
    @Serializable
    data object MessageList : MessageNavigationGraph

    @Serializable
    data class ChatDetail(
        val messageId: String,
        val title: String,
        val participantName: String,
        val participantRole: String,
        val participantPhotoUrl: String,
    ) : MessageNavigationGraph
}

@Composable
fun EntryProviderBuilder<NavKey>.EntryMessageNavigation(
    backStack: SnapshotStateList<NavKey>,
    navigateToBack: () -> Unit,
) {
    entry<MessageNavigationGraph.MessageList> {
        MessageScreen(
            navigateToBack = navigateToBack,
            onChatClick = { detail -> backStack.add(detail) },
        )
    }

    entry<MessageNavigationGraph.ChatDetail> { key ->
        ChatScreen(
            conversationTitle = key.title,
            participantName = key.participantName,
            participantRole = key.participantRole,
            participantPhotoUrl = key.participantPhotoUrl,
            onNavigateBack = { backStack.removeLastOrNull() },
        )
    }
}

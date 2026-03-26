package com.app.ecarepro.feature.notice.navigation

import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.feature.notice.notification_list.NotificationsScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface NotificationsNavGraph : NavKey {
    @Serializable
    data object NotificationList : NotificationsNavGraph
}

fun EntryProviderBuilder<NavKey>.entryNotificationsNavigation(
    navigateBack: () -> Unit,
    navigateTo: (NavKey) -> Unit,
) {
    entry<NotificationsNavGraph.NotificationList> {
        NotificationsScreen(
            navigateBack = navigateBack,
            navigateTo = navigateTo,
        )
    }
}

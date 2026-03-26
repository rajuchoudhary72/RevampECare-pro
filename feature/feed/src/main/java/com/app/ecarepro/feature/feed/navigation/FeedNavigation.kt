package com.app.ecarepro.feature.feed.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.feature.feed.FeedScreen
import kotlinx.serialization.Serializable

const val FEED_ROUTE = "feed_route"

fun NavController.navigateToFeed(navOptions: NavOptions? = null) {
    navigate(FEED_ROUTE, navOptions)
}

fun NavGraphBuilder.feedScreen(
    onFeedItemClick: (String, String) -> Unit  // (feedId, module)
) {
    composable(route = FEED_ROUTE) {
        FeedScreen(
            onFeedItemClick = { feedUpdate ->
                onFeedItemClick(feedUpdate.id, feedUpdate.module)
            }
        )
    }
}

// Navigation3

@Serializable
sealed interface FeedNavGraph : NavKey {
    @Serializable
    data object Feed : FeedNavGraph
}

fun EntryProviderBuilder<NavKey>.entryFeedNavigation(
    navigateBack: () -> Unit,
) {
    entry<FeedNavGraph.Feed> {
        FeedScreen()
    }
}

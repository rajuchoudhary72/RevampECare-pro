package com.app.ecarepro.feature.feed.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.app.ecarepro.feature.feed.FeedScreen

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

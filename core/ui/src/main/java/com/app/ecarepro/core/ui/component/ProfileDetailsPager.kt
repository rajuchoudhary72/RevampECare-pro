package com.app.ecarepro.core.ui.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * A horizontal pager component for profile details screens.
 * Wraps the foundation HorizontalPager with common configuration.
 *
 * @param pagerState The state of the pager
 * @param modifier Modifier to be applied to the pager
 * @param pageContent The content to display for each page, receives the page index
 */
@Composable
fun ProfileDetailsPager(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    pageContent: @Composable (page: Int) -> Unit
) {
    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize()
    ) { page ->
        pageContent(page)
    }
}

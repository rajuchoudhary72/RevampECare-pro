package com.app.ecarepro.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.HorizontalTabBar
import com.app.ecarepro.designsystem.core.component.HorizontalTabBarConfiguration
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import kotlinx.coroutines.launch

/**
 * A scaffold for profile details screens that provides:
 * - Compact profile header with photo, name, subtitle, and close button
 * - Horizontal tab bar for sections
 * - State handling for loading/error/success
 * - Snackbar support
 *
 * Matches the Figma design for Staff/Student profile details screens.
 *
 * @param T The type of the UI state data
 * @param uiState The current UI state
 * @param headerData Lambda to extract ProfileDetailsHeaderData from the UI state data
 * @param visibleTabs Lambda to extract visible tabs from the UI state data
 * @param selectedTabIndex Lambda to extract the selected tab index from the UI state data
 * @param onCloseClicked Callback when close button is clicked
 * @param onTabSelected Callback when a tab is selected
 * @param snackbarHostState The snackbar host state
 * @param snackbarMessage Optional snackbar message to display
 * @param onSnackbarDismissed Callback when snackbar is dismissed
 * @param content The content to display, receives PagerState and UI state data
 */
@Composable
fun <T> ProfileDetailsScaffold(
    uiState: UiState<T>,
    headerData: (T) -> ProfileDetailsHeaderData?,
    visibleTabs: (T) -> List<ProfileTab<*>>,
    selectedTabIndex: (T) -> Int,
    onCloseClicked: () -> Unit,
    onTabSelected: (Int) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    snackbarMessage: SnackbarMessage? = null,
    onSnackbarDismissed: () -> Unit = {},
    content: @Composable (PagerState, T) -> Unit
) {
    val scope = rememberCoroutineScope()

    UiStateHandler(
        state = uiState
    ) { data ->
        val tabs = visibleTabs(data)
        val currentSelectedIndex = selectedTabIndex(data)
        val header = headerData(data)

        val pagerState = rememberPagerState(
            initialPage = currentSelectedIndex,
            pageCount = { tabs.size }
        )

        // Sync pager with tab selection
        LaunchedEffect(currentSelectedIndex) {
            if (pagerState.currentPage != currentSelectedIndex) {
                pagerState.animateScrollToPage(currentSelectedIndex)
            }
        }

        // Sync tab selection with pager swipe
        LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
            if (!pagerState.isScrollInProgress && pagerState.currentPage != currentSelectedIndex) {
                onTabSelected(pagerState.currentPage)
            }
        }

        EcareProScaffold(
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(White)
                ) {
                    // Profile Header - Compact design matching Figma
                    ProfileDetailsHeader(
                        headerData = header,
                        onCloseClick = onCloseClicked,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Tab Bar
                    if (tabs.isNotEmpty()) {
                        HorizontalTabBar(
                            tabs = tabs.map { it.displayName },
                            selectedTab = tabs.getOrNull(currentSelectedIndex)?.displayName ?: "",
                            onTabSelected = { tabName ->
                                val index = tabs.indexOfFirst { it.displayName == tabName }
                                if (index != -1) {
                                    scope.launch {
                                        onTabSelected(index)
                                        pagerState.animateScrollToPage(index)
                                    }
                                }
                            },
                            configuration = HorizontalTabBarConfiguration(
                                tabSpacing = 24.dp
                            )
                        )
                    }
                }
            },
            containerColor = White,
            snackbarHostState = snackbarHostState,
            snackbarMessage = snackbarMessage,
            onSnackbarDismissed = onSnackbarDismissed
        ) { paddingValues ->
            // Content with pager
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.appColors.background)
            ) {
                content(pagerState, data)
            }
        }
    }
}

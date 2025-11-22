/**
 * TimetableScreen.kt
 *
 * This file contains the UI implementation for the Timetable feature screen.
 * It displays a weekly timetable with day-wise tabs and period entries.
 *
 * Architecture:
 * - Uses MVI (Model-View-Intent) pattern with ViewModel
 * - State is managed via StateFlow from TimetableViewModel
 * - User actions are dispatched as TimetableIntent objects
 *
 * Key Components:
 * - TimetableScreen: Entry point composable (handles navigation & ViewModel injection)
 * - TimetableScreenContent: Stateless UI composable (renders the actual content)
 */
package com.app.ecarepro.feature.timetable

// Jetpack Compose Foundation imports for layout and paging
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState

// Material3 components
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme

// Compose runtime for state management
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// Hilt for dependency injection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// Domain models
import com.app.ecarepro.core.domain.model.Timetable
import com.app.ecarepro.core.domain.model.TimetableData

// Core UI utilities for handling loading/error/success states
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler

// Design system components and theming
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors

// Feature-specific UI components
import com.app.ecarepro.feature.timetable.components.AppBar
import com.app.ecarepro.feature.timetable.components.DayTabs
import com.app.ecarepro.feature.timetable.components.EmptyItem
import com.app.ecarepro.feature.timetable.components.RecessItem
import com.app.ecarepro.feature.timetable.components.TimetableItem
import kotlinx.coroutines.launch

/**
 * Entry point composable for the Timetable screen.
 *
 * This is a stateful composable that:
 * 1. Injects the ViewModel using Hilt
 * 2. Collects UI state from ViewModel
 * 3. Handles one-time navigation events
 * 4. Delegates rendering to TimetableScreenContent
 *
 * @param viewModel The ViewModel that manages UI state and business logic.
 *                  Automatically injected by Hilt if not provided.
 * @param navigateToBack Callback invoked when user wants to go back.
 *                       Typically connected to NavController.popBackStack()
 */
@Composable
fun TimetableScreen(
    viewModel: TimetableViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
) {
    // Collect UI state as lifecycle-aware state
    // This ensures the UI updates when state changes and respects lifecycle
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // LaunchedEffect with Unit key runs once when composable enters composition
    // Used to collect one-time events (navigation) from the ViewModel
    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                // Handle back navigation event triggered by ViewModel
                TimetableEvent.NavigateBack -> navigateToBack()
            }
        }
    }

    // Delegate to stateless content composable for easier testing and preview
    TimetableScreenContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent
    )
}

/**
 * Stateless composable that renders the Timetable screen content.
 *
 * This composable is separated from TimetableScreen to:
 * - Enable @Preview support (no ViewModel dependency)
 * - Facilitate unit testing with mock states
 * - Follow unidirectional data flow pattern
 *
 * Screen Layout:
 * ┌─────────────────────────┐
 * │       AppBar            │  <- Navigation back button
 * ├─────────────────────────┤
 * │  Day1 | Day2 | Day3 ... │  <- Horizontal scrollable tabs
 * ├─────────────────────────┤
 * │                         │
 * │   Timetable Entries     │  <- Swipeable pages (HorizontalPager)
 * │   (Period/Recess items) │     with LazyColumn for each day
 * │                         │
 * └─────────────────────────┘
 *
 * @param uiState Sealed class representing Loading/Error/Success states.
 *                Contains TimetableUiState with days and timetable entries.
 * @param handleIntent Lambda to dispatch user actions (intents) to ViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun TimetableScreenContent(
    uiState: UiState<TimetableUiState>,
    handleIntent: (TimetableIntent) -> Unit,
) {

    // PagerState manages the horizontal swipe between days
    // Initializes to the currently selected day from UI state
    val pagerState = rememberPagerState(
        initialPage = if (uiState is UiState.Success) uiState.data.selectedDayIndex else 0,
        pageCount = { if (uiState is UiState.Success) uiState.data.days.size else 0 }
    )
    // Coroutine scope for launching animations (tab click -> page scroll)
    val coroutineScope = rememberCoroutineScope()

    // Sync pager swipe gestures with ViewModel state
    // When user finishes swiping, update the selected day in ViewModel
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            handleIntent(TimetableIntent.OnDaySelected(pagerState.currentPage))
        }
    }

    // Main scaffold providing app structure with top bar
    EcareProScaffold(
        topBar = {
            // Top bar container with shadow for elevation effect
            Column(
                modifier = Modifier
                    .shadow(elevation = 1.dp),
            ) {
                // App bar with back navigation
                AppBar(
                    onClickNavigationIcon = { handleIntent(TimetableIntent.OnBackClicked) }
                )

                // Day tabs shown only when data is loaded successfully
                if (uiState is UiState.Success && uiState.data.days.isNotEmpty()) {
                    DayTabs(
                        selectedDayIndex = uiState.data.selectedDayIndex,
                        days = uiState.data.days,
                        onClickDayTabs = {
                            // When tab is clicked, update state AND animate pager
                            coroutineScope.launch {
                                handleIntent(TimetableIntent.OnDaySelected(it))
                                pagerState.animateScrollToPage(it)
                            }
                        }
                    )
                }
            }
        },
        containerColor = White,
    ) { paddingValues ->

        // UiStateHandler automatically shows loading/error states
        // Only renders success content when data is available
        UiStateHandler(
            modifier = Modifier.padding(paddingValues),
            state = uiState,
        ) { data ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // HorizontalPager enables swipe between days
                // Each page contains the timetable for that specific day
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    // Get timetable entries for the current page/day
                    val timetable: List<Timetable> = data.timetables[page] ?: emptyList()

                    if (timetable.isNotEmpty()) {
                        // LazyColumn for efficient scrolling of timetable entries
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(timetable) { entry ->
                                // Render different item types based on entry.type
                                if (entry.type == "Recess") {
                                    // Special styling for recess/break periods
                                    RecessItem(details = entry.details ?: "")
                                } else {
                                    // Regular period item with subject, class, time info
                                    TimetableItem(entry = entry)
                                    // Divider between entries for visual separation
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        color = MaterialTheme.appColors.divider,
                                        thickness = .5.dp
                                    )
                                }
                            }
                        }
                    } else {
                        // Show placeholder when no classes scheduled for the day
                        EmptyItem()
                    }
                }
            }

        }


    }


}


/**
 * Preview function for Android Studio's Compose Preview.
 *
 * Demonstrates the TimetableScreenContent with mock data including:
 * - Multiple day tabs (Day 1-4)
 * - Regular timetable entries (periods with subject info)
 * - A recess/break entry
 *
 * To see this preview: Open this file in Android Studio and switch to
 * "Split" or "Design" view in the top-right corner.
 */
@Preview(showBackground = true)
@Composable
private fun TimetableScreenPreview() {
    // Mock timetable data for preview demonstration
    val mockTimetable = listOf(
        Timetable(
            period = 3,
            className = "11-A",
            subject = "Business studies",
            time = "09:30 AM - 10:30 AM",
            duration = "60 mins",
            isCurrent = false
        ),
        Timetable(
            period = 3,
            className = "11-A",
            subject = "Business studies",
            time = "09:30 AM - 10:30 AM",
            duration = "60 mins",
            isCurrent = false
        ),
        Timetable(
            period = 3,
            className = "11-A",
            subject = "Business studies",
            time = "09:30 AM - 10:30 AM",
            duration = "60 mins",
            isCurrent = false
        ),
        Timetable(
            period = 3,
            className = "11-A",
            subject = "Business studies",
            time = "09:30 AM - 10:30 AM",
            duration = "60 mins",
            isCurrent = false,
            type = "recess",
            details = "Recess (11:30 PM - 12:30 AM)"
        ),
        Timetable(
            period = 3,
            className = "11-A",
            subject = "Business studies",
            time = "09:30 AM - 10:30 AM",
            duration = "60 mins",
            isCurrent = false
        ),
    )
    // Wrap in app theme to apply proper styling
    EcareProTheme {
        TimetableScreenContent(
            // Simulate successful state with mock data
            uiState = UiState.Success(
                TimetableUiState(
                    // Mock 4 days for the tab display
                    days = listOf(
                        TimetableData(day = "Day 1", dayNo = 1, timeTable = emptyList()),
                        TimetableData(day = "Day 2", dayNo = 1, timeTable = emptyList()),
                        TimetableData(day = "Day 3", dayNo = 1, timeTable = emptyList()),
                        TimetableData(day = "Day 4", dayNo = 1, timeTable = emptyList()),
                    ),
                    // Map page index to timetable entries (only Day 1 has data here)
                    timetables = mapOf(0 to mockTimetable)
                )
            ),
            // Empty lambda since preview doesn't need to handle intents
            handleIntent = {}
        )
    }
}

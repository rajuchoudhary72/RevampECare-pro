package com.app.ecarepro.feature.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.feature.calendar.R
import com.app.ecarepro.core.domain.model.activity_calendar.ActivityMonth
import com.app.ecarepro.designsystem.core.component.EcareProClassTabs
import com.app.ecarepro.designsystem.core.component.EcareProEmptyState
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.feature.calendar.components.ActivityTimelineItem

@Composable
fun CalendarScreen(
    navigateToBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                CalendarEvent.NavigateBack -> navigateToBack()
            }
        }
    }

    CalendarContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarContent(
    uiState: CalendarUiState,
    handleIntent: (CalendarIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(
        initialPage = uiState.selectedMonthIndex,
        pageCount = { uiState.months.size },
    )

    // Sync tab click → pager scroll
    LaunchedEffect(uiState.selectedMonthIndex) {
        if (pagerState.currentPage != uiState.selectedMonthIndex) {
            pagerState.animateScrollToPage(uiState.selectedMonthIndex)
        }
    }

    // Sync pager swipe → tab selection
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage != uiState.selectedMonthIndex) {
            handleIntent(CalendarIntent.OnPageSwiped(pagerState.currentPage))
        }
    }

    EcareProScaffold(
        modifier = modifier,
        topBar = {
            EcareProTopAppBar(
                title = uiState.navTitle,
                onNavigationClicked = { handleIntent(CalendarIntent.OnBackClicked) },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.appColors.primary)
                    }
                }

                uiState.isError -> {
                    EcareProEmptyState(message = uiState.errorMessage)
                }

                else -> {
                    // Month tab bar with bottom shadow
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(elevation = 4.dp)
                            .background(MaterialTheme.appColors.background),
                    ) {
                        EcareProClassTabs(
                            selectedTabIndex = uiState.selectedMonthIndex,
                            tabs = uiState.monthTabLabels,
                            onTabClick = { handleIntent(CalendarIntent.OnMonthTabClicked(it)) },
                            applyOrdinalTransform = false,
                        )
                    }

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                    ) { pageIndex ->
                        MonthPage(month = uiState.months.getOrNull(pageIndex))
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthPage(month: ActivityMonth?) {
    if (month == null || month.activities.isEmpty()) {
        EcareProEmptyState(message = stringResource(R.string.activity_calendar_no_activities))
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 20.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        itemsIndexed(
            items = month.activities,
            key = { index, _ -> index },
        ) { index, activity ->
            ActivityTimelineItem(
                activity = activity,
                isFirst = index == 0,
                isLast = index == month.activities.size - 1,
            )
        }
    }
}

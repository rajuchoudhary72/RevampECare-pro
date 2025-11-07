package com.app.ecarepro.feature.timetable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.feature.timetable.components.AppBar
import com.app.ecarepro.feature.timetable.components.DayTabs
import com.app.ecarepro.feature.timetable.components.EmptyItem
import com.app.ecarepro.feature.timetable.components.RecessItem
import com.app.ecarepro.feature.timetable.components.TimetableItem
import kotlinx.coroutines.launch

@Composable
fun TimetableScreen(
    viewModel: TimetableViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                TimetableEvent.NavigateBack -> navigateToBack()
            }
        }
    }

    TimetableScreenContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun TimetableScreenContent(
    uiState: TimetableUiState,
    handleIntent: (TimetableIntent) -> Unit,
) {
    val pagerState = rememberPagerState(
        initialPage = uiState.selectedDayIndex,
        pageCount = { uiState.days.size }
    )
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            handleIntent(TimetableIntent.OnDaySelected(pagerState.currentPage))
        }
    }


    EcareProScaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .shadow(elevation = 1.dp),
            ) {
                AppBar(
                    onClickNavigationIcon = { handleIntent(TimetableIntent.OnBackClicked) }
                )

                DayTabs(
                    selectedDayIndex = uiState.selectedDayIndex,
                    days = uiState.days,
                    onClickDayTabs = {
                        coroutineScope.launch {
                            handleIntent(TimetableIntent.OnDaySelected(it))
                            pagerState.animateScrollToPage(it)
                        }
                    }
                )
            }
        },
        isLoading = uiState.isLoading,
        containerColor = White,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val timetable = uiState.timetables[page] ?: emptyList()
                if (timetable.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(timetable) { entry ->
                            if (entry.type == "Recess") {
                                RecessItem(details = entry.details ?: "")
                            } else {
                                TimetableItem(entry = entry)
                                if (entry.isCurrent.not())
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        color = MaterialTheme.appColors.divider,
                                        thickness = .5.dp
                                    )
                            }
                        }
                    }
                } else {
                    EmptyItem()
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun TimetableScreenPreview() {
    val mockTimetable = listOf(
        TimetableEntry(
            "1",
            "11-C",
            "Business studies",
            "07:30 AM - 08:30 AM",
            "60 mins",
            isCurrent = false
        ),
        TimetableEntry(
            "2",
            "12-A",
            "Business studies",
            "08:30 AM - 09:30 AM",
            "60 mins",
            isCurrent = false
        ),
        TimetableEntry(
            "3",
            "11-A",
            "Business studies",
            "09:30 AM - 10:30 AM",
            "60 mins",
            isCurrent = true
        ),
        TimetableEntry(type = "Recess", details = "Recess (11:30 PM - 12:30 AM)"),
        TimetableEntry(
            "5",
            "2-A",
            "Physics",
            "12:30 AM - 01:30 PM",
            "60 mins",
            isCurrent = false
        ),
    )
    EcareProTheme {
        TimetableScreenContent(
            uiState = TimetableUiState(
                days = listOf("Day 1", "Day 2", "Day 3", "Day 4", "Day 5"),
                timetables = mapOf(0 to mockTimetable)
            ),
            handleIntent = {}
        )
    }
}
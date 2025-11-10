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
import com.app.ecarepro.core.domain.model.Timetable
import com.app.ecarepro.core.domain.model.TimetableData
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
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
    uiState: UiState<TimetableUiState>,
    handleIntent: (TimetableIntent) -> Unit,
) {

    val pagerState = rememberPagerState(
        initialPage = if (uiState is UiState.Success) uiState.data.selectedDayIndex else 0,
        pageCount = { if (uiState is UiState.Success) uiState.data.days.size else 0 }
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

                if (uiState is UiState.Success && uiState.data.days.isNotEmpty()) {
                    DayTabs(
                        selectedDayIndex = uiState.data.selectedDayIndex,
                        days = uiState.data.days,
                        onClickDayTabs = {
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

        UiStateHandler(
            modifier = Modifier.padding(paddingValues),
            state = uiState,
        ) { data ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val timetable: List<Timetable> = data.timetables[page] ?: emptyList()
                    if (timetable.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(timetable) { entry ->
                                if (entry.type == "Recess") {
                                    RecessItem(details = entry.details ?: "")
                                } else {
                                    TimetableItem(entry = entry)
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


}


@Preview(showBackground = true)
@Composable
private fun TimetableScreenPreview() {
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
    EcareProTheme {
        TimetableScreenContent(
            uiState = UiState.Success(
                TimetableUiState(
                    days = listOf(
                        TimetableData(day = "Day 1", dayNo = 1, timeTable = emptyList()),
                        TimetableData(day = "Day 2", dayNo = 1, timeTable = emptyList()),
                        TimetableData(day = "Day 3", dayNo = 1, timeTable = emptyList()),
                        TimetableData(day = "Day 4", dayNo = 1, timeTable = emptyList()),
                    ),
                    timetables = mapOf(0 to mockTimetable)
                )
            ),
            handleIntent = {}
        )
    }
}
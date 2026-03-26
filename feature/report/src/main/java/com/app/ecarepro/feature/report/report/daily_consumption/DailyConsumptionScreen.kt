package com.app.ecarepro.feature.report.report.daily_consumption

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.sms.DailyConsumptionItem
import com.app.ecarepro.designsystem.core.component.EcareProEmptyState
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.report.report.daily_consumption.component.TimelineConnector
import com.app.ecarepro.feature.report.report.daily_consumption.component.TimelineItem
import com.app.ecarepro.feature.report.report.component.DateRangeSection

@Composable
fun DailyConsumptionScreen(
    navigateBack: () -> Unit,
    viewModel: DailyConsumptionViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage: SnackbarMessage? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is DailyConsumptionEvent.NavigateBack -> navigateBack()
                is DailyConsumptionEvent.ShowMessage -> {
                    snackbarMessage = event.message
                    snackbarHostState.showSnackbar(event.message.text)
                }
            }
        }
    }

    DailyConsumptionContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DailyConsumptionContent(
    uiState: DailyConsumptionUiState,
    handleIntent: (DailyConsumptionIntent) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    snackbarMessage: SnackbarMessage? = null,
    onSnackbarDismissed: () -> Unit = {},
) {
    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = "Daily Consumption",
                onNavigationClicked = { handleIntent(DailyConsumptionIntent.OnBackClicked) },
            )
        },
        bottomBar = {
            TotalSmsFooter(total = uiState.grandTotal)
        },
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = onSnackbarDismissed,
        containerColor = Color(0xFFF5F5F5),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            DateRangeSection(
                startDate = uiState.startDate,
                endDate = uiState.endDate,
                onDateRangeSelected = { start, end ->
                    handleIntent(DailyConsumptionIntent.SelectDateRange(start, end))
                },
            )

            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.appColors.primary)
                    }
                }
                uiState.isError -> {
                    EcareProEmptyState(message = "Something went wrong")
                }
                uiState.items.isEmpty() -> {
                    EcareProEmptyState(message = "No data found")
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp),
                    ) {
                        itemsIndexed(
                            items = uiState.items,
                            key = { index, _ -> index },
                        ) { index, item ->
                            val isFirst = index == 0
                            val isLast = index == uiState.items.lastIndex
                            TimelineItem(
                                item = item,
                                isFirst = isFirst,
                                isLast = isLast,
                            )
                            if (!isLast) {
                                TimelineConnector()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TotalSmsFooter(total: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.appColors.primary)
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Text(
            text = "Total SMS:",
            style = MaterialTheme.appTypography.interSemiBold16px,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterStart),
        )
        Text(
            text = total,
            style = MaterialTheme.appTypography.interSemiBold16px,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterEnd),
        )
    }
}

@Preview(showBackground = true, name = "Daily Consumption - Loading")
@Composable
private fun PreviewDailyConsumptionLoading() {
    EcareProTheme {
        DailyConsumptionContent(
            uiState = DailyConsumptionUiState(isLoading = true),
            handleIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "Daily Consumption - Loaded")
@Composable
private fun PreviewDailyConsumptionLoaded() {
    EcareProTheme {
        DailyConsumptionContent(
            uiState = DailyConsumptionUiState(
                isLoading = false,
                items = listOf(
                    DailyConsumptionItem("21-Mar-2026", "21 March", "Saturday", 1),
                    DailyConsumptionItem("20-Mar-2026", "20 March", "Friday", 6),
                    DailyConsumptionItem("19-Mar-2026", "19 March", "Thursday", 11),
                ),
                grandTotal = "18",
            ),
            handleIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "Daily Consumption - Empty")
@Composable
private fun PreviewDailyConsumptionEmpty() {
    EcareProTheme {
        DailyConsumptionContent(
            uiState = DailyConsumptionUiState(isLoading = false, items = emptyList()),
            handleIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "Daily Consumption - Error")
@Composable
private fun PreviewDailyConsumptionError() {
    EcareProTheme {
        DailyConsumptionContent(
            uiState = DailyConsumptionUiState(isLoading = false, isError = true),
            handleIntent = {},
        )
    }
}

package com.app.ecarepro.feature.smsdailyconsumption

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.designsystem.core.component.EcareProDateRangeField
import com.app.ecarepro.designsystem.core.component.EcareProEmptyState
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.smsdailyconsumption.component.SmsTimelineConnector
import com.app.ecarepro.feature.smsdailyconsumption.component.SmsTimelineItem
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun SmsDailyConsumptionScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SmsDailyConsumptionViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                SmsDailyConsumptionEvent.NavigateBack -> navigateBack()
            }
        }
    }

    SmsDailyConsumptionContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SmsDailyConsumptionContent(
    uiState: SmsDailyConsumptionUiState,
    handleIntent: (SmsDailyConsumptionIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val apiFmt = remember { SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH) }
    val displayFmt = remember { SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH) }

    var fromDateDisplay by rememberSaveable {
        mutableStateOf(
            runCatching { displayFmt.format(apiFmt.parse(uiState.fromDate)!!) }.getOrElse { uiState.fromDate }
        )
    }
    var toDateDisplay by rememberSaveable {
        mutableStateOf(
            runCatching { displayFmt.format(apiFmt.parse(uiState.toDate)!!) }.getOrElse { uiState.toDate }
        )
    }
    var fromDateMillis by rememberSaveable {
        mutableLongStateOf(
            runCatching { apiFmt.parse(uiState.fromDate)!!.time }.getOrElse { System.currentTimeMillis() }
        )
    }

    EcareProScaffold(
        modifier = modifier,
        topBar = {
            EcareProTopAppBar(
                title = stringResource(R.string.sms_dailyConsumption_title),
                onNavigationClicked = { handleIntent(SmsDailyConsumptionIntent.OnBackClicked) },
            )
        },
        bottomBar = {
            TotalSmsFooter(
                total = uiState.totalSms,
                primaryColor = MaterialTheme.appColors.primary,
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            EcareProDateRangeField(
                startDateDisplay = fromDateDisplay,
                endDateDisplay = toDateDisplay,
                startDateLabel = stringResource(R.string.sms_dailyConsumption_startDate),
                endDateLabel = stringResource(R.string.sms_dailyConsumption_endDate),
                startDateMillis = fromDateMillis,
                minStartDateMillis = Long.MIN_VALUE,
                onStartDateSelected = { apiDate, displayDate, millis ->
                    fromDateDisplay = displayDate
                    fromDateMillis = millis
                    handleIntent(
                        SmsDailyConsumptionIntent.OnDateRangeSelected(
                            fromDate = apiDate,
                            toDate = uiState.toDate,
                        )
                    )
                },
                onEndDateSelected = { apiDate, displayDate, _ ->
                    toDateDisplay = displayDate
                    handleIntent(
                        SmsDailyConsumptionIntent.OnDateRangeSelected(
                            fromDate = uiState.fromDate,
                            toDate = apiDate,
                        )
                    )
                },
                apiDateFormat = "yyyy-MM-dd",
                displayDateFormat = "MMM dd, yyyy",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            )

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

                uiState.items.isEmpty() -> {
                    EcareProEmptyState(message = stringResource(R.string.sms_dailyConsumption_noData))
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
                            SmsTimelineItem(
                                item = item,
                                isFirst = isFirst,
                                isLast = isLast,
                            )
                            if (!isLast) {
                                SmsTimelineConnector()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TotalSmsFooter(
    total: Int,
    primaryColor: Color,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(primaryColor)
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Text(
            text = stringResource(R.string.sms_dailyConsumption_totalSMS),
            style = MaterialTheme.appTypography.interSemiBold16px,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterStart),
        )
        Text(
            text = "$total",
            style = MaterialTheme.appTypography.interSemiBold16px,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterEnd),
        )
    }
}

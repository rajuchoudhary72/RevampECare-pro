package com.app.ecarepro.feature.report.birthday_report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.designsystem.core.component.EcareProEmptyState
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProSelectionBottomSheet
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.report.birthday_report.component.BirthdayCard
import com.app.ecarepro.feature.report.birthday_report.component.BirthdayFilterBar
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BirthdayListScreen(
    navigateBack: () -> Unit,
    viewModel: BirthdayListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is BirthdayListEvent.NavigateBack -> navigateBack()
            }
        }
    }

    BirthdayListContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BirthdayListContent(
    uiState: BirthdayListUiState,
    handleIntent: (BirthdayListIntent) -> Unit,
) {
    val monthNames = DateFormatSymbols(Locale.ENGLISH).months.take(12)
    val dateDisplayFormat = SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH)

    val filterValueLabel = when (uiState.filterMode) {
        FilterMode.MONTH -> monthNames[uiState.selectedMonthIndex]
        FilterMode.DATE -> dateDisplayFormat.format(uiState.selectedDate)
    }

    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                modifier = Modifier.shadow(elevation = 1.dp),
                title = "Birthday report",
                onNavigationClicked = { handleIntent(BirthdayListIntent.OnBackClicked) },
            )
        },
        bottomBar = {
            BirthdayFilterBar(
                filterMode = uiState.filterMode,
                filterValueLabel = filterValueLabel,
                onFilterModeClick = { handleIntent(BirthdayListIntent.ShowFilterModeSheet) },
                onFilterValueClick = {
                    when (uiState.filterMode) {
                        FilterMode.MONTH -> handleIntent(BirthdayListIntent.ShowMonthSheet)
                        FilterMode.DATE -> handleIntent(BirthdayListIntent.ShowDatePicker)
                    }
                },
            )
        },
        containerColor = androidx.compose.ui.graphics.Color.White,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            // Tab row
            TabRow(
                selectedTabIndex = uiState.selectedTab.ordinal,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White),
                containerColor = Color.White,
                contentColor = MaterialTheme.appColors.primary,
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTab.ordinal]),
                        color = MaterialTheme.appColors.primary,
                    )
                },
            ) {
                BirthdayTab.entries.forEach { tab ->
                    val isSelected = tab == uiState.selectedTab
                    Tab(
                        selected = isSelected,
                        onClick = { handleIntent(BirthdayListIntent.SelectTab(tab)) },
                        text = {
                            Text(
                                text = tab.label,
                                style = MaterialTheme.appTypography.interMedium14px,
                                color = if (isSelected) MaterialTheme.appColors.primary
                                        else MaterialTheme.appColors.textSecondary,
                            )
                        },
                    )
                }
            }

            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.appColors.primary)
                    }
                }
                uiState.error != null -> {
                    EcareProEmptyState(message = uiState.error)
                }
                uiState.birthdays.isEmpty() -> {
                    EcareProEmptyState(message = "No birthday's today!")
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        itemsIndexed(uiState.birthdays) { index, item ->
                            BirthdayCard(presentation = item)
                            if (index < uiState.birthdays.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    thickness = 0.5.dp,
                                    color = Color(0xFFEEEEEE),
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Filter mode bottom sheet
    if (uiState.showFilterModeSheet) {
        EcareProSelectionBottomSheet(
            isVisible = true,
            title = "Select filter",
            options = FilterMode.entries.map { it.label },
            selectedOptions = listOf(uiState.filterMode.label),
            isMultiSelection = false,
            onDismiss = { handleIntent(BirthdayListIntent.DismissFilterModeSheet) },
            onOptionsSelected = { selected ->
                val mode = FilterMode.entries.find { it.label == selected.firstOrNull() } ?: return@EcareProSelectionBottomSheet
                handleIntent(BirthdayListIntent.SelectFilterMode(mode))
                handleIntent(BirthdayListIntent.DismissFilterModeSheet)
            },
        )
    }

    // Month bottom sheet
    if (uiState.showMonthSheet) {
        EcareProSelectionBottomSheet(
            isVisible = true,
            title = "Select month",
            options = monthNames,
            selectedOptions = listOf(monthNames[uiState.selectedMonthIndex]),
            isMultiSelection = false,
            onDismiss = { handleIntent(BirthdayListIntent.DismissMonthSheet) },
            onOptionsSelected = { selected ->
                val index = monthNames.indexOf(selected.firstOrNull())
                if (index >= 0) handleIntent(BirthdayListIntent.SelectMonth(index))
                handleIntent(BirthdayListIntent.DismissMonthSheet)
            },
        )
    }

    // Date picker
    if (uiState.showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.selectedDate.time,
        )
        DatePickerDialog(
            onDismissRequest = { handleIntent(BirthdayListIntent.DismissDatePicker) },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis ?: uiState.selectedDate.time
                    handleIntent(BirthdayListIntent.SelectDate(Date(millis)))
                    handleIntent(BirthdayListIntent.DismissDatePicker)
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { handleIntent(BirthdayListIntent.DismissDatePicker) }) {
                    Text("Cancel")
                }
            },
        ) {
            androidx.compose.material3.DatePicker(state = datePickerState)
        }
    }
}

@Preview(showBackground = true, name = "Birthday List - Loaded Students")
@Composable
private fun PreviewBirthdayListLoaded() {
    EcareProTheme {
        BirthdayListContent(
            uiState = BirthdayListUiState(
                isLoading = false,
                birthdays = listOf(
                    BirthdayCardPresentation(1, "Aaditi Rawat, LKG A", "9487145788", "", "18th March", null),
                    BirthdayCardPresentation(2, "Aastha Saini, LKG A", "9487145788", "", "18th March", null),
                ),
            ),
            handleIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "Birthday List - Loading")
@Composable
private fun PreviewBirthdayListLoading() {
    EcareProTheme {
        BirthdayListContent(
            uiState = BirthdayListUiState(isLoading = true),
            handleIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "Birthday List - Empty")
@Composable
private fun PreviewBirthdayListEmpty() {
    EcareProTheme {
        BirthdayListContent(
            uiState = BirthdayListUiState(isLoading = false, birthdays = emptyList()),
            handleIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "Birthday List - Staff tab")
@Composable
private fun PreviewBirthdayListStaff() {
    EcareProTheme {
        BirthdayListContent(
            uiState = BirthdayListUiState(
                isLoading = false,
                selectedTab = BirthdayTab.STAFF,
                birthdays = listOf(
                    BirthdayCardPresentation(1, "PIYUSH CHILWAL, TEACHER1", "9876543210", "Father/Spouse: LAL SINGH CHILWAL", "1 Mar", null),
                    BirthdayCardPresentation(2, "AMIT VERMA, TEACHER2", "9876543211", "Father/Spouse: RAMESH VERMA", "1 Mar", null),
                ),
            ),
            handleIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "Birthday List - Parents tab")
@Composable
private fun PreviewBirthdayListParents() {
    EcareProTheme {
        BirthdayListContent(
            uiState = BirthdayListUiState(
                isLoading = false,
                selectedTab = BirthdayTab.PARENTS,
                birthdays = listOf(
                    BirthdayCardPresentation(1, "Sunita Devi", "9487145788", "F/O: Rajesh Kumar, 10th-Section-A", "15 Mar", null),
                    BirthdayCardPresentation(2, "Meena Sharma", null, "M/O: Suresh Sharma, 9th-Section-B", "15 Mar", null),
                ),
            ),
            handleIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "Birthday List - Error")
@Composable
private fun PreviewBirthdayListError() {
    EcareProTheme {
        BirthdayListContent(
            uiState = BirthdayListUiState(
                isLoading = false,
                error = "Something went wrong. Please try again.",
            ),
            handleIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "Birthday List - Date filter")
@Composable
private fun PreviewBirthdayListDateFilter() {
    EcareProTheme {
        BirthdayListContent(
            uiState = BirthdayListUiState(
                isLoading = false,
                filterMode = FilterMode.DATE,
                birthdays = listOf(
                    BirthdayCardPresentation(1, "Aaditi Rawat, LKG A", "9487145788", "", "22 Mar", null),
                ),
            ),
            handleIntent = {},
        )
    }
}

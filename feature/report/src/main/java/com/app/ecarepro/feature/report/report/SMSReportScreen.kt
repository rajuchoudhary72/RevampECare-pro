package com.app.ecarepro.feature.report.report

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.smsreport.SMSReportItem
import com.app.ecarepro.core.domain.model.smsreport.SMSTypeItem
import com.app.ecarepro.designsystem.core.component.EcareProEmptyState
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProSelectionBottomSheet
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.report.report.component.DateRangeSection
import com.app.ecarepro.feature.report.report.component.SMSReportCard

@Composable
fun SMSReportScreen(
    navigateBack: () -> Unit,
    viewModel: SMSReportViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is SMSReportEvent.NavigateBack -> navigateBack()
                is SMSReportEvent.ShowMessage -> {
                    snackbarMessage = event.message
                    snackbarHostState.showSnackbar(event.message.text)
                }
            }
        }
    }
    Content(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    uiState: SMSReportUiState,
    handleIntent: (SMSReportIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit,
) {
    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                modifier = Modifier.shadow(elevation = 1.dp),
                title = "SMS report",
                onNavigationClicked = { handleIntent(SMSReportIntent.OnBackClicked) },
            )
        },
        bottomBar = {
            SMSTypeDropdown(
                selectedTypeName = uiState.selectedTypeName,
                onClick = { handleIntent(SMSReportIntent.OpenTypePicker) },
            )
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
                    handleIntent(SMSReportIntent.SelectDateRange(start, end))
                },
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
                    EcareProEmptyState(message = "Something went wrong")
                }
                uiState.items.isEmpty() -> {
                    EcareProEmptyState(message = "No SMS found")
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(uiState.items) { item ->
                            SMSReportCard(item = item)
                        }
                    }
                }
            }
        }
    }

    if (uiState.showTypePicker) {
        EcareProSelectionBottomSheet(
            isVisible = true,
            title = "SMS type",
            options = uiState.typePickerOptions,
            selectedOptions = listOf(uiState.selectedTypeOption),
            isMultiSelection = false,
            onDismiss = { handleIntent(SMSReportIntent.DismissTypePicker) },
            onOptionsSelected = { selected ->
                val subject = selected.firstOrNull() ?: "All"
                if (subject == "All") {
                    handleIntent(SMSReportIntent.SelectSMSType(0))
                } else {
                    val typeID = uiState.smsTypes.find { it.subject == subject }?.typeID ?: 0
                    handleIntent(SMSReportIntent.SelectSMSType(typeID))
                }
            },
        )
    }
}

@Composable
private fun SMSTypeDropdown(
    selectedTypeName: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = selectedTypeName,
                style = MaterialTheme.appTypography.interRegular14px,
                color = MaterialTheme.appColors.textPrimary,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Select SMS type",
                tint = MaterialTheme.appColors.textSecondary,
            )
        }
    }
}

@Preview(showBackground = true, name = "SMS Report - Loaded")
@Composable
private fun PreviewSMSReportLoaded() {
    val mockItems = listOf(
        SMSReportItem(
            receiverName = "Mrs. Aastha Saini",
            receiverDesignation = "Officer",
            receiverPhotoUrl = null,
            messageText = "Dear staff, Your login details for e-care pro is as follows:\nSchool code: DEMOIN",
            smsTypeName = "Credentials SMS",
            status = "Sent",
            isSent = true,
            senderName = "e-care",
            senderPhotoUrl = null,
            sentOnDate = "12 March 2026",
        ),
        SMSReportItem(
            receiverName = "Mr. Rahul Sharma",
            receiverDesignation = "Teacher",
            receiverPhotoUrl = null,
            messageText = "Your attendance has been marked absent today.",
            smsTypeName = "Absentee SMS",
            status = "Failed",
            isSent = false,
            senderName = "e-care",
            senderPhotoUrl = null,
            sentOnDate = "11 March 2026",
        ),
    )
    EcareProTheme {
        Content(
            uiState = SMSReportUiState(
                isLoading = false,
                items = mockItems,
                smsTypes = listOf(
                    SMSTypeItem(101, "Absentee SMS"),
                    SMSTypeItem(9, "Birthday"),
                ),
            ),
            handleIntent = {},
            snackbarHostState = remember { SnackbarHostState() },
            snackbarMessage = null,
            onSnackbarDismissed = {},
        )
    }
}

@Preview(showBackground = true, name = "SMS Report - Loading")
@Composable
private fun PreviewSMSReportLoading() {
    EcareProTheme {
        Content(
            uiState = SMSReportUiState(isLoading = true),
            handleIntent = {},
            snackbarHostState = remember { SnackbarHostState() },
            snackbarMessage = null,
            onSnackbarDismissed = {},
        )
    }
}

@Preview(showBackground = true, name = "SMS Report - Empty")
@Composable
private fun PreviewSMSReportEmpty() {
    EcareProTheme {
        Content(
            uiState = SMSReportUiState(isLoading = false, items = emptyList()),
            handleIntent = {},
            snackbarHostState = remember { SnackbarHostState() },
            snackbarMessage = null,
            onSnackbarDismissed = {},
        )
    }
}

@Preview(showBackground = true, name = "SMS Report - Error")
@Composable
private fun PreviewSMSReportError() {
    EcareProTheme {
        Content(
            uiState = SMSReportUiState(isLoading = false, isError = true),
            handleIntent = {},
            snackbarHostState = remember { SnackbarHostState() },
            snackbarMessage = null,
            onSnackbarDismissed = {},
        )
    }
}

@Preview(showBackground = true, name = "SMS Report - Type Selected")
@Composable
private fun PreviewSMSReportTypeSelected() {
    val smsTypes = listOf(
        SMSTypeItem(101, "Absentee SMS"),
        SMSTypeItem(9, "Birthday"),
        SMSTypeItem(6, "Complaint"),
    )
    val mockItems = listOf(
        SMSReportItem(
            receiverName = "Mrs. Aastha Saini",
            receiverDesignation = "Officer",
            receiverPhotoUrl = null,
            messageText = "Dear staff, Your login details for e-care pro.",
            smsTypeName = "Absentee SMS",
            status = "Sent",
            isSent = true,
            senderName = "e-care",
            senderPhotoUrl = null,
            sentOnDate = "12 March 2026",
        ),
    )
    EcareProTheme {
        Content(
            uiState = SMSReportUiState(
                isLoading = false,
                items = mockItems,
                smsTypes = smsTypes,
                selectedTypeID = 101,
            ),
            handleIntent = {},
            snackbarHostState = remember { SnackbarHostState() },
            snackbarMessage = null,
            onSnackbarDismissed = {},
        )
    }
}

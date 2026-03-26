package com.app.ecarepro.feature.fee.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.designsystem.core.component.EcareProEmptyState
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.fee.common.component.DateRangeField
import com.app.ecarepro.feature.fee.common.component.FeeFilterCard
import com.app.ecarepro.feature.fee.filter.component.MultiSelectBottomSheet
import com.app.ecarepro.feature.fee.navigation.FeeNavGraph
import androidx.compose.ui.tooling.preview.Preview
import com.app.ecarepro.core.domain.model.fee.FilterItemDomain
import com.app.ecarepro.designsystem.core.theme.EcareProTheme

@Composable
fun ReportFilterScreen(
    viewModel: ReportFilterViewModel,
    navigateBack: () -> Unit,
    navigateTo: (NavKey) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                is ReportFilterEvent.NavigateBack -> navigateBack()
                is ReportFilterEvent.ShowMessage -> {
                    snackbarMessage = event.message
                    snackbarHostState.showSnackbar(event.message.text)
                }
                is ReportFilterEvent.NavigateToDefaulter -> {
                    navigateTo(
                        FeeNavGraph.DefaulterList(
                            dateFrom = event.dateFrom,
                            dateTo = event.dateTo,
                            classId = event.classId,
                            feeTypeId = event.feeTypeId,
                            schoolId = event.schoolId,
                            sectionId = event.sectionId,
                            installId = event.installId,
                            installmentNames = event.installmentNames,
                        )
                    )
                }
                is ReportFilterEvent.NavigateToEstimate -> {
                    navigateTo(
                        FeeNavGraph.EstimateList(
                            dateFrom = event.dateFrom,
                            dateTo = event.dateTo,
                            classId = event.classId,
                            feeTypeId = event.feeTypeId,
                            schoolId = event.schoolId,
                            sectionId = event.sectionId,
                            installId = event.installId,
                        )
                    )
                }
            }
        }
    }

    Content(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null },
        handleIntent = viewModel::handleIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    uiState: ReportFilterUiState,
    snackbarHostState: SnackbarHostState,
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit,
    handleIntent: (ReportFilterIntent) -> Unit,
) {
    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                modifier = Modifier.shadow(elevation = 1.dp),
                title = uiState.title,
                onNavigationClicked = { handleIntent(ReportFilterIntent.OnBackClicked) },
            )
        },
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = onSnackbarDismissed,
        containerColor = Color(0xFFF5F5F5),
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = MaterialTheme.appColors.primary)
            }
        } else if (uiState.isError) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                EcareProEmptyState(message = "Failed to load filters")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item {
                        DateRangeField(
                            startMillis = uiState.startMillis,
                            endMillis = uiState.endMillis,
                            onRangeSelected = { start, end ->
                                handleIntent(ReportFilterIntent.SelectDateRange(start, end))
                            },
                        )
                    }
                    item {
                        FeeFilterCard(
                            icon = Icons.Default.Class,
                            iconColor = Color(0xFF4CAF50),
                            title = "Class",
                            subtitle = if (uiState.selectedClassIds.isEmpty()) "All classes"
                            else "${uiState.selectedClassIds.size} selected",
                            onClick = { handleIntent(ReportFilterIntent.OpenSheet(FilterSheetType.CLASSES)) },
                        )
                    }
                    item {
                        FeeFilterCard(
                            icon = Icons.Default.AccountBalance,
                            iconColor = Color(0xFF2196F3),
                            title = "School",
                            subtitle = if (uiState.selectedSchoolIds.isEmpty()) "All schools"
                            else "${uiState.selectedSchoolIds.size} selected",
                            onClick = { handleIntent(ReportFilterIntent.OpenSheet(FilterSheetType.SCHOOLS)) },
                        )
                    }
                    item {
                        FeeFilterCard(
                            icon = Icons.Default.Category,
                            iconColor = Color(0xFFFF9800),
                            title = "Fee Type",
                            subtitle = if (uiState.selectedFeeTypeIds.isEmpty()) "All fee types"
                            else "${uiState.selectedFeeTypeIds.size} selected",
                            onClick = { handleIntent(ReportFilterIntent.OpenSheet(FilterSheetType.FEE_TYPES)) },
                        )
                    }
                    item {
                        FeeFilterCard(
                            icon = Icons.Default.DateRange,
                            iconColor = Color(0xFFE91E63),
                            title = "Installment",
                            subtitle = if (uiState.selectedInstallmentIds.isEmpty()) "Select installment *"
                            else "${uiState.selectedInstallmentIds.size} selected",
                            onClick = { handleIntent(ReportFilterIntent.OpenSheet(FilterSheetType.INSTALLMENTS)) },
                        )
                    }
                    item {
                        FeeFilterCard(
                            icon = Icons.Default.Layers,
                            iconColor = Color(0xFF9C27B0),
                            title = "Section",
                            subtitle = if (uiState.selectedSectionIds.isEmpty()) "All sections"
                            else "${uiState.selectedSectionIds.size} selected",
                            onClick = { handleIntent(ReportFilterIntent.OpenSheet(FilterSheetType.SECTIONS)) },
                        )
                    }
                }

                Button(
                    onClick = { handleIntent(ReportFilterIntent.Submit) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = uiState.isSubmitEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.appColors.primary,
                        disabledContainerColor = Color.Gray.copy(alpha = 0.5f),
                    ),
                ) {
                    Text(
                        text = "Submit",
                        style = MaterialTheme.appTypography.interSemiBold16px,
                        color = Color.White,
                    )
                }
            }
        }
    }

    // Multi-select bottom sheets
    when (uiState.activeSheet) {
        FilterSheetType.CLASSES -> {
            MultiSelectBottomSheet(
                title = "Select Class",
                items = uiState.classes.map { it.id to it.name },
                selectedIds = uiState.selectedClassIds,
                onConfirm = { handleIntent(ReportFilterIntent.ConfirmClasses(it)) },
                onDismiss = { handleIntent(ReportFilterIntent.DismissSheet) },
            )
        }
        FilterSheetType.SCHOOLS -> {
            MultiSelectBottomSheet(
                title = "Select School",
                items = uiState.schools.map { it.id to it.name },
                selectedIds = uiState.selectedSchoolIds,
                onConfirm = { handleIntent(ReportFilterIntent.ConfirmSchools(it)) },
                onDismiss = { handleIntent(ReportFilterIntent.DismissSheet) },
            )
        }
        FilterSheetType.FEE_TYPES -> {
            MultiSelectBottomSheet(
                title = "Select Fee Type",
                items = uiState.feeTypes.map { it.id to it.name },
                selectedIds = uiState.selectedFeeTypeIds,
                onConfirm = { handleIntent(ReportFilterIntent.ConfirmFeeTypes(it)) },
                onDismiss = { handleIntent(ReportFilterIntent.DismissSheet) },
            )
        }
        FilterSheetType.INSTALLMENTS -> {
            MultiSelectBottomSheet(
                title = "Select Installment",
                items = uiState.installments.map { it.id to it.name },
                selectedIds = uiState.selectedInstallmentIds,
                onConfirm = { handleIntent(ReportFilterIntent.ConfirmInstallments(it)) },
                onDismiss = { handleIntent(ReportFilterIntent.DismissSheet) },
            )
        }
        FilterSheetType.SECTIONS -> {
            MultiSelectBottomSheet(
                title = "Select Section",
                items = uiState.sections.map { it.id to it.name },
                selectedIds = uiState.selectedSectionIds,
                onConfirm = { handleIntent(ReportFilterIntent.ConfirmSections(it)) },
                onDismiss = { handleIntent(ReportFilterIntent.DismissSheet) },
            )
        }
        null -> Unit
    }
}

@Preview(showBackground = true, name = "Defaulter Filter – Loaded")
@Composable
private fun PreviewDefaulterFilter() {
    val classes = listOf(
        FilterItemDomain("1", "Class 1"),
        FilterItemDomain("2", "Class 2"),
        FilterItemDomain("3", "Class 3"),
    )
    val installments = listOf(
        FilterItemDomain("i1", "1st Installment"),
        FilterItemDomain("i2", "2nd Installment"),
    )
    EcareProTheme {
        Content(
            uiState = ReportFilterUiState(
                isLoading = false,
                title = "Defaulter Report",
                startMillis = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000,
                endMillis = System.currentTimeMillis(),
                startDateApi = "2026-02-21",
                endDateApi = "2026-03-21",
                classes = classes,
                installments = installments,
                selectedInstallmentIds = setOf("i1"),
            ),
            snackbarHostState = remember { SnackbarHostState() },
            snackbarMessage = null,
            onSnackbarDismissed = {},
            handleIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "Estimate Filter – Loading")
@Composable
private fun PreviewEstimateFilterLoading() {
    EcareProTheme {
        Content(
            uiState = ReportFilterUiState(
                isLoading = true,
                title = "Estimate Report",
            ),
            snackbarHostState = remember { SnackbarHostState() },
            snackbarMessage = null,
            onSnackbarDismissed = {},
            handleIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "Filter – Nothing Selected")
@Composable
private fun PreviewFilterNothingSelected() {
    val items = listOf(
        FilterItemDomain("s1", "Section A"),
        FilterItemDomain("s2", "Section B"),
    )
    EcareProTheme {
        Content(
            uiState = ReportFilterUiState(
                isLoading = false,
                title = "Defaulter Report",
                startMillis = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000,
                endMillis = System.currentTimeMillis(),
                startDateApi = "2026-02-21",
                endDateApi = "2026-03-21",
                sections = items,
            ),
            snackbarHostState = remember { SnackbarHostState() },
            snackbarMessage = null,
            onSnackbarDismissed = {},
            handleIntent = {},
        )
    }
}

package com.app.ecarepro.feature.transport_att.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.transport.TransportStudent
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
import com.app.ecarepro.designsystem.core.component.EcareProDatePicker
import com.app.ecarepro.designsystem.core.component.EcareProEmptyState
import com.app.ecarepro.designsystem.core.component.EcareProScaffold
import com.app.ecarepro.designsystem.core.component.EcareProTopAppBar
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.transport_att.components.DropConfirmDialog
import com.app.ecarepro.feature.transport_att.components.OutPassStudentItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutPassScreen(
    viewModel: OutPassViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<SnackbarMessage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collect { event ->
            when (event) {
                OutPassEvent.NavigateBack -> navigateToBack()
                is OutPassEvent.ShowMessage -> {
                    snackbarMessage = event.snackbarMessage
                    snackbarHostState.showSnackbar(event.snackbarMessage.text)
                }
            }
        }
    }

    OutPassScreenContent(
        uiState = uiState,
        handleIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = { snackbarMessage = null },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OutPassScreenContent(
    uiState: UiState<OutPassUiState>,
    handleIntent: (OutPassIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    snackbarMessage: SnackbarMessage?,
    onSnackbarDismissed: () -> Unit,
) {
    EcareProScaffold(
        topBar = {
            EcareProTopAppBar(
                title = "Out Passes",
                onNavigationClicked = { handleIntent(OutPassIntent.OnBackClicked) },
            )
        },
        containerColor = White,
        snackbarHostState = snackbarHostState,
        snackbarMessage = snackbarMessage,
        onSnackbarDismissed = onSnackbarDismissed,
        isLoading = if (uiState is UiState.Success) (uiState as UiState.Success).data.isLoading else false,
    ) { paddingValues ->
        UiStateHandler(
            modifier = Modifier.padding(paddingValues),
            state = uiState,
        ) { data ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Date selector with calendar icon
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { handleIntent(OutPassIntent.ShowDatePicker) },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = MaterialTheme.appColors.primary,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text(
                        text = data.selectedDate.ifEmpty { "Select date" },
                        style = MaterialTheme.appTypography.interSemiBold14px,
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.appColors.textSecondary,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (data.showReport && data.studentList.isNotEmpty()) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        itemsIndexed(data.studentList) { index, student ->
                            OutPassStudentItem(student = student)
                        }
                    }
                } else if (data.selectedDate.isNotEmpty() && !data.isLoading) {
                    EcareProEmptyState(message = "No out pass records found")
                }
            }

            // Date Picker
            EcareProDatePicker(
                isVisible = data.isDatePickerVisible,
                onDismiss = { handleIntent(OutPassIntent.DismissDatePicker) },
                onDateSelected = { dateStr ->
                    try {
                        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                        val date = sdf.parse(dateStr)
                        date?.let {
                            handleIntent(OutPassIntent.OnDateSelected(it.time))
                        }
                    } catch (_: Exception) {}
                },
            )

            // Drop confirm dialog
            if (data.isDropConfirmVisible && data.droppingStudentIndex != null) {
                val student = data.studentList.getOrNull(data.droppingStudentIndex)
                if (student != null) {
                    DropConfirmDialog(
                        studentName = student.stName,
                        date = data.selectedDate,
                        onDismiss = { handleIntent(OutPassIntent.OnDismissDropConfirm) },
                        onConfirm = {
                            handleIntent(
                                OutPassIntent.OnConfirmDropStudent(data.droppingStudentIndex)
                            )
                        },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun OutPassScreenPreview() {
    EcareProTheme {
        OutPassScreenContent(
            uiState = UiState.Success(
                OutPassUiState(
                    selectedDate = "09 Feb 2026",
                    showReport = true,
                    studentList = listOf(
                        TransportStudent(
                            admissionNo = "ADM001", className = "Class 5A",
                            dropAtt = "", dropStatus = 0, dropTime = "",
                            isConstant = false, isDropped = false, photo = "",
                            pickupAtt = "", pickupStatus = 0, pickupTime = "",
                            rollNo = "1", route = "Route A",
                            stID = 1, stName = "John Doe", stop = "Stop 1", stopID = 1,
                        ),
                    ),
                )
            ),
            handleIntent = {},
            snackbarHostState = SnackbarHostState(),
            snackbarMessage = null,
            onSnackbarDismissed = {},
        )
    }
}

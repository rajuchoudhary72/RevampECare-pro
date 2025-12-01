package com.app.ecarepro.feature.leave

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.LeaveType
import com.app.ecarepro.designsystem.core.component.EcareProBackground
import com.app.ecarepro.designsystem.core.component.Loader
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors

@Composable
fun ApplyLeaveScreen(
    viewModel: ApplyLeaveViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onSuccess()
        }
    }

    ApplyLeaveScreenContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onLeaveTypeSelected = { viewModel.handleIntent(ApplyLeaveIntent.OnLeaveTypeSelected(it)) },
        onStartDateSelected = { viewModel.handleIntent(ApplyLeaveIntent.OnStartDateSelected(it)) },
        onEndDateSelected = { viewModel.handleIntent(ApplyLeaveIntent.OnEndDateSelected(it)) },
        onReasonChanged = { viewModel.handleIntent(ApplyLeaveIntent.OnReasonChanged(it)) },
        onHalfDayChanged = { viewModel.handleIntent(ApplyLeaveIntent.OnHalfDayChanged(it)) },
        onSubmit = { viewModel.handleIntent(ApplyLeaveIntent.OnSubmit) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ApplyLeaveScreenContent(
    uiState: ApplyLeaveUiState,
    onBackClick: () -> Unit,
    onLeaveTypeSelected: (LeaveType) -> Unit,
    onStartDateSelected: (String) -> Unit,
    onEndDateSelected: (String) -> Unit,
    onReasonChanged: (String) -> Unit,
    onHalfDayChanged: (Boolean) -> Unit,
    onSubmit: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        EcareProBackground(
            overlayColor = MaterialTheme.appColors.background
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                contentWindowInsets = WindowInsets.systemBars,
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Apply Leave",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = onBackClick) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.appColors.surface,
                            titleContentColor = MaterialTheme.appColors.textPrimary
                        )
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Leave Type Dropdown
                    LeaveTypeDropdown(
                        leaveTypes = uiState.leaveTypes,
                        selectedLeaveType = uiState.selectedLeaveType,
                        onLeaveTypeSelected = onLeaveTypeSelected
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Date Selection
                    Row(modifier = Modifier.fillMaxWidth()) {
                        DateField(
                            label = "Start Date",
                            date = uiState.startDate,
                            onDateSelected = onStartDateSelected,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        DateField(
                            label = "End Date",
                            date = uiState.endDate,
                            onDateSelected = onEndDateSelected,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Duration Display
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.appColors.background
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Duration",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${uiState.duration} day(s)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Half Day Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Half Day",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Switch(
                            checked = uiState.isHalfDay,
                            onCheckedChange = onHalfDayChanged
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Reason TextField
                    OutlinedTextField(
                        value = uiState.reason,
                        onValueChange = onReasonChanged,
                        label = { Text("Reason") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        maxLines = 5
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Submit Button
                    Button(
                        onClick = onSubmit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !uiState.isSubmitting && uiState.selectedLeaveType != null,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (uiState.isSubmitting) "Submitting..." else "Submit",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (uiState.isLoading) {
            Loader()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LeaveTypeDropdown(
    leaveTypes: List<LeaveType>,
    selectedLeaveType: LeaveType?,
    onLeaveTypeSelected: (LeaveType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedLeaveType?.leaveType ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Leave Type") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            leaveTypes.forEach { leaveType ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(leaveType.leaveType)
                            Text(
                                text = "Available: ${leaveType.available}/${leaveType.total}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.appColors.textSecondary
                            )
                        }
                    },
                    onClick = {
                        onLeaveTypeSelected(leaveType)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun DateField(
    label: String,
    date: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = date,
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = "Select Date"
            )
        },
        modifier = modifier
            .clickable {
                // TODO: Open date picker
                onDateSelected("2024-12-01")
            }
    )
}

@Preview(showBackground = true)
@Composable
fun ApplyLeaveScreenPreview() {
    EcareProTheme {
        ApplyLeaveScreenContent(
            uiState = ApplyLeaveUiState(),
            onBackClick = {},
            onLeaveTypeSelected = {},
            onStartDateSelected = {},
            onEndDateSelected = {},
            onReasonChanged = {},
            onHalfDayChanged = {},
            onSubmit = {}
        )
    }
}

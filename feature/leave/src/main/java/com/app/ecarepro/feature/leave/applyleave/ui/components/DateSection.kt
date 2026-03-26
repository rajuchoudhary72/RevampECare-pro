package com.app.ecarepro.feature.leave.applyleave.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.leave.R
import com.app.ecarepro.feature.leave.applyleave.data.SessionType
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DateSection(
    startDate: LocalDate,
    endDate: LocalDate,
    startSession: SessionType,
    endSession: SessionType,
    minDate: LocalDate,
    maxDate: LocalDate,
    showSessionSelector: Boolean,
    calculatedDuration: Double,
    error: String?,
    onStartDateChange: (LocalDate) -> Unit,
    onEndDateChange: (LocalDate) -> Unit,
    onStartSessionChange: (SessionType) -> Unit,
    onEndSessionChange: (SessionType) -> Unit
) {
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.feature_leave_duration_label),
            style = MaterialTheme.appTypography.interMedium16px,
            color = MaterialTheme.appColors.textPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))

        // From Date
        OutlinedTextField(
            value = startDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.feature_leave_from_date)) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = stringResource(R.string.feature_leave_select_date),
                    tint = MaterialTheme.appColors.primary,
                    modifier = Modifier.clickable { showStartDatePicker = true }
                )
            },
            modifier = Modifier.fillMaxWidth(),
            isError = error != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.appColors.primary,
                unfocusedBorderColor = MaterialTheme.appColors.divider
            )
        )

        // Start Session Selector (Staff Only)
        if (showSessionSelector) {
            Spacer(modifier = Modifier.height(8.dp))
            SessionSelector(
                label = "Start Session",
                selectedSession = startSession,
                onSessionChange = onStartSessionChange
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // To Date
        OutlinedTextField(
            value = endDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.feature_leave_to_date)) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = stringResource(R.string.feature_leave_select_date),
                    tint = MaterialTheme.appColors.primary,
                    modifier = Modifier.clickable { showEndDatePicker = true }
                )
            },
            modifier = Modifier.fillMaxWidth(),
            isError = error != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.appColors.primary,
                unfocusedBorderColor = MaterialTheme.appColors.divider
            )
        )

        // End Session Selector (Staff Only)
        if (showSessionSelector) {
            Spacer(modifier = Modifier.height(8.dp))
            SessionSelector(
                label = "End Session",
                selectedSession = endSession,
                onSessionChange = onEndSessionChange
            )
        }

        // Duration Display
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.appColors.primary.copy(alpha = 0.05f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.feature_leave_total_duration),
                    style = MaterialTheme.appTypography.interMedium16px,
                    color = MaterialTheme.appColors.textPrimary
                )
                Text(
                    text = "${calculatedDuration} day${if (calculatedDuration != 1.0) "s" else ""}",
                    style = MaterialTheme.appTypography.interSemiBold14px,
                    color = MaterialTheme.appColors.primary
                )
            }
        }

        if (error != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = error,
                style = MaterialTheme.appTypography.interRegular12px,
                color = MaterialTheme.appColors.error
            )
        }
    }

    // Date Pickers
    if (showStartDatePicker) {
        DatePickerDialog(
            initialDate = startDate,
            minDate = minDate,
            maxDate = maxDate,
            onDateSelected = {
                onStartDateChange(it)
                showStartDatePicker = false
            },
            onDismiss = { showStartDatePicker = false }
        )
    }

    if (showEndDatePicker) {
        DatePickerDialog(
            initialDate = endDate,
            minDate = startDate,
            maxDate = maxDate,
            onDateSelected = {
                onEndDateChange(it)
                showEndDatePicker = false
            },
            onDismiss = { showEndDatePicker = false }
        )
    }
}

@Composable
private fun SessionSelector(
    label: String,
    selectedSession: SessionType,
    onSessionChange: (SessionType) -> Unit
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.appTypography.interRegular12px,
            color = MaterialTheme.appColors.textSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SessionType.values().forEach { session ->
                FilterChip(
                    selected = selectedSession == session,
                    onClick = { onSessionChange(session) },
                    label = {
                        Text(
                            text = session.displayName,
                            style = MaterialTheme.appTypography.interMedium16px
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.appColors.primary,
                        selectedLabelColor = androidx.compose.ui.graphics.Color.White,
                        containerColor = MaterialTheme.appColors.divider
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialog(
    initialDate: LocalDate,
    minDate: LocalDate,
    maxDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.toEpochDay() * 86400000L
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    val date = LocalDate.ofEpochDay(millis / 86400000L)
                    onDateSelected(date)
                }
            }) {
                Text(stringResource(R.string.feature_leave_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.feature_leave_cancel))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

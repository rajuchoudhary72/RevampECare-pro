package com.app.ecarepro.designsystem.core.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.app.ecarepro.core.designsystem.R
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * A reusable date-range compound component with Start date & End date side by side.
 *
 * Built-in validations:
 *  - Start date cannot be before [minStartDateMillis] (defaults to today).
 *  - End date cannot be before the selected start date.
 *
 * @param startDateDisplay  Formatted start date shown in the field (empty = placeholder).
 * @param endDateDisplay    Formatted end date shown in the field (empty = placeholder).
 * @param startDateLabel    Label above start date field.
 * @param endDateLabel      Label above end date field.
 * @param placeholder       Placeholder text when no date is selected.
 * @param minStartDateMillis Earliest selectable start date in millis (defaults to today at 00:00).
 * @param startDateMillis   Currently selected start date in millis (used to constrain end date).
 * @param onStartDateSelected Called with (apiDate, displayDate) when a start date is confirmed.
 * @param onEndDateSelected   Called with (apiDate, displayDate) when an end date is confirmed.
 * @param apiDateFormat     Date format for the API string (default "yyyy-MM-dd").
 * @param displayDateFormat Date format for display string (default "dd MMM yyyy").
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcareProDateRangeField(
    startDateDisplay: String,
    endDateDisplay: String,
    modifier: Modifier = Modifier,
    startDateLabel: String = "Start date",
    endDateLabel: String = "End date",
    placeholder: String = "Select date",
    minStartDateMillis: Long = remember { todayStartMillis() },
    startDateMillis: Long? = null,
    onStartDateSelected: (apiDate: String, displayDate: String, millis: Long) -> Unit,
    onEndDateSelected: (apiDate: String, displayDate: String, millis: Long) -> Unit,
    apiDateFormat: String = "yyyy-MM-dd",
    displayDateFormat: String = "dd MMM yyyy",
) {
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DateField(
            label = startDateLabel,
            value = startDateDisplay.ifEmpty { placeholder },
            isPlaceholder = startDateDisplay.isEmpty(),
            modifier = Modifier.weight(1f),
            onClick = { showStartPicker = true },
        )
        DateField(
            label = endDateLabel,
            value = endDateDisplay.ifEmpty { placeholder },
            isPlaceholder = endDateDisplay.isEmpty(),
            modifier = Modifier.weight(1f),
            onClick = { showEndPicker = true },
        )
    }

    // Start Date Picker
    if (showStartPicker) {
        DatePickerWithMinDate(
            minDateMillis = minStartDateMillis,
            onDateSelected = { millis ->
                val date = Date(millis)
                val api = SimpleDateFormat(apiDateFormat, Locale.US).format(date)
                val display = SimpleDateFormat(displayDateFormat, Locale.US).format(date)
                onStartDateSelected(api, display, millis)
                showStartPicker = false
            },
            onDismiss = { showStartPicker = false },
        )
    }

    // End Date Picker
    if (showEndPicker) {
        DatePickerWithMinDate(
            minDateMillis = startDateMillis ?: minStartDateMillis,
            onDateSelected = { millis ->
                val date = Date(millis)
                val api = SimpleDateFormat(apiDateFormat, Locale.US).format(date)
                val display = SimpleDateFormat(displayDateFormat, Locale.US).format(date)
                onEndDateSelected(api, display, millis)
                showEndPicker = false
            },
            onDismiss = { showEndPicker = false },
        )
    }
}

// ============== PRIVATE HELPERS ==============

@Composable
private fun DateField(
    label: String,
    value: String,
    isPlaceholder: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.appTypography.interRegular12px,
            color = MaterialTheme.appColors.textSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        EcareProOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            value = if (isPlaceholder) "" else value,
            onValueChange = {},
            placeholder = {
                Text(
                    text = value,
                    style = MaterialTheme.appTypography.interRegular14px,
                    color = if (isPlaceholder) MaterialTheme.appColors.textSecondary
                    else MaterialTheme.appColors.textPrimary
                )
            },
            enabled = false,
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.icon_arrow_down),
                    contentDescription = "Select",
                    tint = MaterialTheme.appColors.textSecondary
                )
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerWithMinDate(
    minDateMillis: Long,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= minDateMillis
            }
        }
    )
    val confirmEnabled by remember { derivedStateOf { datePickerState.selectedDateMillis != null } }

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                title = "OK",
                onClick = {
                    datePickerState.selectedDateMillis?.let { onDateSelected(it) }
                },
                enabled = confirmEnabled,
                titleColor = MaterialTheme.appColors.primary,
            )
        },
        dismissButton = {
            TextButton(
                title = "Cancel",
                onClick = onDismiss,
                titleColor = MaterialTheme.appColors.primary,
            )
        },
    ) {
        DatePicker(state = datePickerState)
    }
}

private fun todayStartMillis(): Long {
    return Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

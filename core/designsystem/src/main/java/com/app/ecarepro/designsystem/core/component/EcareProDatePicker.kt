package com.app.ecarepro.designsystem.core.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import java.text.SimpleDateFormat
import java.util.Date

/**
 * A reusable Date Picker that combines a read-only text field (looking like a dropdown)
 * and a Material3 DatePickerDialog.
 * @param isVisible Controls visibility of the DatePickerDialog.
 * @param onDismiss Called when the dialog should be closed without selection.
 * @param onDateSelected Called when a date is confirmed. Returns the formatted string.
 * @param dateFormat The format pattern (default: "dd MMM yyyy").
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcareProDatePicker(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onDateSelected: (String) -> Unit,
    dateFormat: String = "yyyy-MM-dd",
) {
    if (isVisible.not()) return

    val datePickerState = rememberDatePickerState()
    val confirmEnabled by remember { derivedStateOf { datePickerState.selectedDateMillis != null } }

    DatePickerDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                title = "OK",
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formattedDate = convertMillisToDate(millis, dateFormat)
                        onDateSelected(formattedDate)
                    }
                },
                enabled = confirmEnabled,
                titleColor = MaterialTheme.appColors.primary
            )
        },
        dismissButton = {
            TextButton(
                title = "Cancel",
                onClick = onDismiss,
                titleColor = MaterialTheme.appColors.primary
            )
        },
    ) {
        DatePicker(state = datePickerState)
    }

}

// Helper function
private fun convertMillisToDate(millis: Long, format: String): String {
    val formatter = SimpleDateFormat(format, java.util.Locale.getDefault())
    return formatter.format(Date(millis))
}

@Preview(showBackground = true)
@Composable
private fun EcareProDatePickerPreview() {
    EcareProTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ){
            EcareProDatePicker(
                isVisible = true,
                onDismiss = {},
                onDateSelected = {}
            )
        }

    }
}
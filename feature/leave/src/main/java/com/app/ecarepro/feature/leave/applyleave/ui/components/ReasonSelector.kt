package com.app.ecarepro.feature.leave.applyleave.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.leave.R
import com.app.ecarepro.feature.leave.applyleave.data.LeaveReason

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReasonSelector(
    selectedReason: LeaveReason?,
    error: String?,
    onSelect: (LeaveReason?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.feature_leave_reason_label),
            style = MaterialTheme.appTypography.interMedium16px,
            color = MaterialTheme.appColors.textPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = selectedReason?.displayName ?: "",
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown",
                        tint = MaterialTheme.appColors.textSecondary
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                placeholder = {
                    Text(
                        text = stringResource(R.string.feature_leave_select_reason),
                        style = MaterialTheme.appTypography.interRegular14px,
                        color = MaterialTheme.appColors.textSecondary
                    )
                },
                isError = error != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.appColors.primary,
                    unfocusedBorderColor = MaterialTheme.appColors.divider
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                LeaveReason.values().forEach { reason ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = reason.displayName,
                                style = MaterialTheme.appTypography.interMedium16px
                            )
                        },
                        onClick = {
                            onSelect(reason)
                            expanded = false
                        }
                    )
                }
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
}

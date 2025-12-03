package com.app.ecarepro.designsystem.core.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcareProSelectionBottomSheet(
    title: String,
    isVisible: Boolean,
    options: List<String>,
    selectedOptions: List<String>,
    isMultiSelection: Boolean = false,
    onDismiss: () -> Unit,
    onOptionsSelected: (List<String>) -> Unit,
) {
    if (!isVisible) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = White,
        dragHandle = null,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 20.sp),
                    color = MaterialTheme.appColors.textPrimary
                )
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                        },
                    tint = MaterialTheme.appColors.textPrimary
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(options) { index, option ->
                    val isSelected = selectedOptions.contains(option)

                    val textColor =
                        if (isSelected) MaterialTheme.appColors.primary else MaterialTheme.appColors.textPrimary

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (isMultiSelection) {
                                    val newList = selectedOptions.toMutableList()
                                    if (isSelected) newList.remove(option) else newList.add(option)
                                    onOptionsSelected(newList)
                                } else {
                                    onOptionsSelected(listOf(option))
                                    scope.launch { sheetState.hide() }
                                        .invokeOnCompletion { onDismiss() }
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = option,
                            style = if (isSelected)
                                MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 16.sp)
                            else
                                MaterialTheme.appTypography.interRegular14px.copy(fontSize = 16.sp),
                            color = textColor,
                            modifier = Modifier.weight(1f)
                        )

                        if (isMultiSelection) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = null,
                                colors = CheckboxDefaults.colors(
                                    checkedColor = MaterialTheme.appColors.primary,
                                    uncheckedColor = MaterialTheme.appColors.textSecondary,
                                    checkmarkColor = White
                                )
                            )
                        } else {
                            RadioButton(
                                selected = isSelected,
                                onClick = null,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.appColors.primary,
                                    unselectedColor = MaterialTheme.appColors.textSecondary
                                )
                            )
                        }
                    }

                    if (index < options.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = Color(0xFFEEEEEE)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(name = "Single Selection - Class", showBackground = true)
@Composable
private fun PreviewSingleSelectionBottomSheet() {
    EcareProTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            EcareProSelectionBottomSheet(
                title = "Select class",
                isVisible = true,
                options = listOf(
                    "Class IV",
                    "Class V",
                    "Class VI",
                    "Class VII",
                    "Class VIII"
                ),
                selectedOptions = listOf("Class IV"),
                isMultiSelection = false,
                onDismiss = {},
                onOptionsSelected = {}
            )
        }
    }
}

@Preview(name = "Multi Selection - Sections", showBackground = true)
@Composable
private fun PreviewMultiSelectionBottomSheet() {
    EcareProTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            EcareProSelectionBottomSheet(
                title = "Select section",
                isVisible = true,
                options = listOf(
                    "Section A",
                    "Section B",
                    "Section C",
                    "Section D"
                ),
                selectedOptions = listOf(
                    "Section A",
                    "Section C"
                ),
                isMultiSelection = true,
                onDismiss = {},
                onOptionsSelected = {}
            )
        }
    }
}
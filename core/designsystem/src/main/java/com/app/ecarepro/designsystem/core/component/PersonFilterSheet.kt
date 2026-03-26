package com.app.ecarepro.designsystem.core.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

/**
 * Filter bottom sheet for person lists (staff, students)
 *
 * @param filterSections List of filter sections to display
 * @param currentFilters Currently selected filters (map of sectionId to selected options)
 * @param onApplyFilters Callback when filters are applied
 * @param onDismiss Callback when bottom sheet is dismissed
 * @param sheetState State of the bottom sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonFilterSheet(
    filterSections: List<FilterSection>,
    currentFilters: Map<String, Set<String>>,
    onApplyFilters: (Map<String, Set<String>>) -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    // Local state for filter selection
    var selectedFilters by remember(currentFilters) {
        mutableStateOf(currentFilters.toMutableMap())
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filters",
                    style = MaterialTheme.appTypography.interSemiBold14px.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.appColors.textPrimary
                )

                // Close button
                Icon(
                    painter = painterResource(id = com.app.ecarepro.core.designsystem.R.drawable.ic_close),
                    contentDescription = "Close",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onDismiss() },
                    tint = MaterialTheme.appColors.textPrimary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Filter sections
            filterSections.forEach { section ->
                FilterSectionView(
                    section = section,
                    selectedOptions = selectedFilters[section.id] ?: emptySet(),
                    onOptionToggled = { option ->
                        val currentOptions = selectedFilters[section.id]?.toMutableSet() ?: mutableSetOf()
                        if (currentOptions.contains(option)) {
                            currentOptions.remove(option)
                        } else {
                            currentOptions.add(option)
                        }
                        selectedFilters = selectedFilters.toMutableMap().apply {
                            if (currentOptions.isEmpty()) {
                                remove(section.id)
                            } else {
                                put(section.id, currentOptions)
                            }
                        }
                    }
                )

                if (section != filterSections.last()) {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Reset button
                Button(
                    onClick = {
                        selectedFilters = mutableMapOf()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF2F2F7),
                        contentColor = MaterialTheme.appColors.textPrimary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Reset",
                        style = MaterialTheme.appTypography.interMedium16px.copy(
                            fontSize = 16.sp
                        )
                    )
                }

                // Apply button
                Button(
                    onClick = {
                        onApplyFilters(selectedFilters)
                        onDismiss()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.appColors.primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Apply Filters",
                        style = MaterialTheme.appTypography.interSemiBold14px.copy(
                            fontSize = 16.sp
                        ),
                        color = White
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterSectionView(
    section: FilterSection,
    selectedOptions: Set<String>,
    onOptionToggled: (String) -> Unit
) {
    Column {
        Text(
            text = section.title,
            style = MaterialTheme.appTypography.interSemiBold14px.copy(
                fontSize = 16.sp
            ),
            color = MaterialTheme.appColors.textPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            section.options.forEach { option ->
                FilterChip(
                    text = option,
                    isSelected = selectedOptions.contains(option),
                    onClick = { onOptionToggled(option) }
                )
            }
        }
    }
}

@Composable
private fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.appColors.primary
    } else {
        Color.Transparent
    }

    val borderColor = if (isSelected) {
        MaterialTheme.appColors.primary
    } else {
        Color(0xFFD1D1D6)
    }

    val textColor = if (isSelected) {
        White
    } else {
        MaterialTheme.appColors.textPrimary
    }

    Row(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(20.dp))
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.appTypography.interMedium16px,
            color = textColor
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun PersonFilterSheetPreview() {
    EcareProTheme {
        PersonFilterSheet(
            filterSections = listOf(
                FilterSection(
                    id = "staffType",
                    title = "Staff Type",
                    options = listOf("Teaching", "Non-Teaching", "Admin")
                ),
                FilterSection(
                    id = "designation",
                    title = "Designation",
                    options = listOf("TGT", "PGT", "PRT", "Principal")
                )
            ),
            currentFilters = mapOf("staffType" to setOf("Teaching")),
            onApplyFilters = {},
            onDismiss = {}
        )
    }
}

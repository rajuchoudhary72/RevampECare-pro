package com.app.ecarepro.designsystem.core.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
 * Sort options for lists (student, staff, etc.)
 */
enum class SortOption(val displayName: String) {
    ROLL_NUMBER("Roll number"),
    ADMISSION_NUMBER("Admission number"),
    NAME("Name"),
    DATE_OF_JOINING("Date of joining")
}

/**
 * Sort direction
 */
enum class SortDirection {
    ASCENDING,
    DESCENDING
}

/**
 * Sort configuration combining option and direction
 */
data class SortConfig(
    val option: SortOption = SortOption.ROLL_NUMBER,
    val direction: SortDirection = SortDirection.ASCENDING
)

/**
 * Reusable bottom sheet for sorting lists
 *
 * Features:
 * - Multiple sort options (configurable)
 * - Toggle between ascending and descending order
 * - Radio button selection with visual feedback
 * - Sort direction indicators
 *
 * @param currentSortConfig Current sort configuration
 * @param onSortSelected Callback when a sort option is selected
 * @param onDismiss Callback when bottom sheet is dismissed
 * @param sheetState State of the bottom sheet
 * @param availableOptions List of sort options to show (default: all)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortBottomSheet(
    currentSortConfig: SortConfig,
    onSortSelected: (SortConfig) -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(),
    availableOptions: List<SortOption> = SortOption.entries
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sort by",
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

            // Sort options
            availableOptions.forEach { option ->
                val isSelected = currentSortConfig.option == option
                val currentDirection = if (isSelected) currentSortConfig.direction else SortDirection.ASCENDING

                SortOptionItem(
                    option = option,
                    isSelected = isSelected,
                    currentDirection = currentDirection,
                    onOptionClick = {
                        val newDirection = if (isSelected) {
                            // Toggle direction if already selected
                            if (currentSortConfig.direction == SortDirection.ASCENDING) {
                                SortDirection.DESCENDING
                            } else {
                                SortDirection.ASCENDING
                            }
                        } else {
                            SortDirection.ASCENDING
                        }
                        onSortSelected(SortConfig(option, newDirection))
                    }
                )

                if (option != availableOptions.last()) {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun SortOptionItem(
    option: SortOption,
    isSelected: Boolean,
    currentDirection: SortDirection,
    onOptionClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isSelected) Color(0xFFF2F2F7) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onOptionClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Option icon and name
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Radio button icon
            Icon(
                painter = painterResource(
                    id = if (isSelected) {
                        com.app.ecarepro.core.designsystem.R.drawable.ic_radio_selected
                    } else {
                        com.app.ecarepro.core.designsystem.R.drawable.ic_radio_unselected
                    }
                ),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = if (isSelected) {
                    MaterialTheme.appColors.primary
                } else {
                    MaterialTheme.appColors.textSecondary
                }
            )

            Text(
                text = option.displayName,
                style = MaterialTheme.appTypography.interRegular14px.copy(
                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                ),
                color = MaterialTheme.appColors.textPrimary
            )
        }

        // Sort direction icon
        if (isSelected) {
            Icon(
                painter = painterResource(
                    id = if (currentDirection == SortDirection.ASCENDING) {
                        com.app.ecarepro.core.designsystem.R.drawable.ic_sort_ascending
                    } else {
                        com.app.ecarepro.core.designsystem.R.drawable.ic_sort_descending
                    }
                ),
                contentDescription = if (currentDirection == SortDirection.ASCENDING) {
                    "Ascending"
                } else {
                    "Descending"
                },
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.appColors.primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun SortBottomSheetPreview() {
    EcareProTheme {
        SortBottomSheet(
            currentSortConfig = SortConfig(SortOption.ROLL_NUMBER, SortDirection.ASCENDING),
            onSortSelected = {},
            onDismiss = {}
        )
    }
}

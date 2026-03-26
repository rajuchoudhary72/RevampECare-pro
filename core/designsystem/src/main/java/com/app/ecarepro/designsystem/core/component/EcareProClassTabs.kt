package com.app.ecarepro.designsystem.core.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

/**
 * EcareProClassTabs - A reusable scrollable tab row component
 *
 * A scrollable horizontal tab row component that displays class/category tabs with
 * custom styling. Supports automatic ordinal conversion (1 -> 1st, 2 -> 2nd, etc.)
 * and custom text transformation.
 *
 * Features:
 * - Scrollable tabs for long lists
 * - Selected/unselected state styling
 * - Automatic ordinal conversion for numeric strings
 * - Custom text transformation support
 * - Customizable colors, spacing, and typography
 *
 * @param selectedTabIndex Index of the currently selected tab
 * @param tabs List of tab labels (strings)
 * @param onTabClick Callback when a tab is clicked, receives the tab index
 * @param modifier Modifier for the tab row
 * @param containerColor Background color of the tab row
 * @param selectedContentColor Color of the selected tab text
 * @param unselectedContentColor Color of unselected tab text
 * @param edgePadding Padding at the start and end of the tab row
 * @param minTabWidth Minimum width for each tab
 * @param applyOrdinalTransform If true, converts numeric strings to ordinal (1 -> 1st)
 * @param textTransform Optional custom text transformation function
 */
@Composable
fun EcareProClassTabs(
    selectedTabIndex: Int,
    tabs: List<String>,
    onTabClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = White,
    selectedContentColor: Color = MaterialTheme.appColors.primary,
    unselectedContentColor: Color = MaterialTheme.appColors.textPrimary,
    edgePadding: Dp = 0.dp,
    minTabWidth: Dp = 70.dp,
    applyOrdinalTransform: Boolean = true,
    textTransform: ((String) -> String)? = null
) {
    PrimaryScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = containerColor,
        edgePadding = edgePadding,
        minTabWidth = minTabWidth,
        modifier = modifier
    ) {
        tabs.forEachIndexed { index, tabLabel ->
            val isSelected = index == selectedTabIndex

            // Apply text transformation
            val displayText = when {
                textTransform != null -> textTransform(tabLabel)
                applyOrdinalTransform -> tabLabel.toOrdinalOrOriginal()
                else -> tabLabel
            }

            // Dynamic text style based on selection state
            val textStyle = if (isSelected) {
                MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 16.sp)
            } else {
                MaterialTheme.appTypography.interMedium16px.copy(fontSize = 14.sp)
            }

            Tab(
                selected = isSelected,
                onClick = { onTabClick(index) },
                text = {
                    Text(
                        text = displayText,
                        style = textStyle
                    )
                },
                selectedContentColor = selectedContentColor,
                unselectedContentColor = unselectedContentColor
            )
        }
    }
}

/**
 * Helper extension to convert string to ordinal or return original
 * Examples: "1" -> "1st", "2" -> "2nd", "UKG" -> "UKG"
 */
private fun String.toOrdinalOrOriginal(): String {
    val number = this.toIntOrNull() ?: return this
    return number.toOrdinal()
}

/**
 * Converts a number to its ordinal form
 * Examples: 1 -> "1st", 2 -> "2nd", 3 -> "3rd", 4 -> "4th"
 */
private fun Int.toOrdinal(): String {
    val suffix = when {
        this % 100 in 11..13 -> "th"
        this % 10 == 1 -> "st"
        this % 10 == 2 -> "nd"
        this % 10 == 3 -> "rd"
        else -> "th"
    }
    return "$this$suffix"
}

// Preview Functions

@Preview(name = "Class Tabs with Numbers", showBackground = true)
@Composable
private fun EcareProClassTabsPreview_Numbers() {
    EcareProTheme {
        var selectedIndex by remember { mutableIntStateOf(2) }
        EcareProClassTabs(
            selectedTabIndex = selectedIndex,
            tabs = listOf("All", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"),
            onTabClick = { selectedIndex = it }
        )
    }
}

@Preview(name = "Class Tabs with Mixed", showBackground = true)
@Composable
private fun EcareProClassTabsPreview_Mixed() {
    EcareProTheme {
        var selectedIndex by remember { mutableIntStateOf(0) }
        EcareProClassTabs(
            selectedTabIndex = selectedIndex,
            tabs = listOf("All", "UKG", "LKG", "1", "2", "3", "4", "5"),
            onTabClick = { selectedIndex = it }
        )
    }
}

@Preview(name = "Class Tabs - Text Only", showBackground = true)
@Composable
private fun EcareProClassTabsPreview_TextOnly() {
    EcareProTheme {
        var selectedIndex by remember { mutableIntStateOf(1) }
        EcareProClassTabs(
            selectedTabIndex = selectedIndex,
            tabs = listOf("All", "Mathematics", "Science", "English", "History"),
            onTabClick = { selectedIndex = it },
            applyOrdinalTransform = false
        )
    }
}

@Preview(name = "Class Tabs - Custom Transform", showBackground = true)
@Composable
private fun EcareProClassTabsPreview_CustomTransform() {
    EcareProTheme {
        var selectedIndex by remember { mutableIntStateOf(0) }
        EcareProClassTabs(
            selectedTabIndex = selectedIndex,
            tabs = listOf("math", "science", "english", "history"),
            onTabClick = { selectedIndex = it },
            applyOrdinalTransform = false,
            textTransform = { it.uppercase() }
        )
    }
}

@Preview(name = "All Preview Variants", showBackground = true)
@Composable
private fun EcareProClassTabsPreview_AllVariants() {
    EcareProTheme {
        Column {
            var selectedIndex1 by remember { mutableIntStateOf(0) }
            Text("Numbers with ordinals:", style = MaterialTheme.appTypography.interSemiBold14px)
            EcareProClassTabs(
                selectedTabIndex = selectedIndex1,
                tabs = listOf("All", "1", "2", "3", "4", "5"),
                onTabClick = { selectedIndex1 = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            var selectedIndex2 by remember { mutableIntStateOf(1) }
            Text("Mixed content:", style = MaterialTheme.appTypography.interSemiBold14px)
            EcareProClassTabs(
                selectedTabIndex = selectedIndex2,
                tabs = listOf("All", "UKG", "LKG", "1", "2"),
                onTabClick = { selectedIndex2 = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            var selectedIndex3 by remember { mutableIntStateOf(2) }
            Text("Text only:", style = MaterialTheme.appTypography.interSemiBold14px)
            EcareProClassTabs(
                selectedTabIndex = selectedIndex3,
                tabs = listOf("All", "Active", "Pending", "Completed"),
                onTabClick = { selectedIndex3 = it },
                applyOrdinalTransform = false
            )
        }
    }
}

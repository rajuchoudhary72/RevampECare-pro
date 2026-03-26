package com.app.ecarepro.designsystem.core.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.core.domain.ext.toOrdinal
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

/**
 * Configuration for HorizontalTabBar
 */
data class HorizontalTabBarConfiguration(
    val selectedColor: Color? = null,           // Color for selected tab text (defaults to app primary)
    val unselectedColor: Color? = null,         // Color for unselected tab text (defaults to text primary)
    val indicatorColor: Color? = null,          // Color for bottom indicator (defaults to app primary)
    val indicatorHeight: Dp = 3.dp,            // Height of the indicator bar
    val fontSize: TextUnit = 15.sp,            // Tab text size
    val tabSpacing: Dp = 24.dp,                // Spacing between tabs
    val horizontalPadding: Dp = 16.dp,         // Horizontal padding of the bar
    val showDivider: Boolean = true,           // Show bottom divider line
)

/**
 * HorizontalTabBar - A horizontally scrollable tab bar with animated indicator
 *
 * Design specs:
 * - Tab spacing: 24dp (configurable)
 * - Selected tab: Green underline indicator
 * - Unselected: Gray text
 * - Selected: SemiBold font weight
 * - Unselected: Regular font weight
 * - Indicator animates smoothly between tabs
 */
@Composable
fun HorizontalTabBar(
    tabs: List<String>,
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    configuration: HorizontalTabBarConfiguration = HorizontalTabBarConfiguration()
) {
    val scrollState = rememberLazyListState()
    val density = LocalDensity.current

    // Tab positions for indicator animation
    var selectedTabOffset by remember { mutableStateOf(0.dp) }
    var selectedTabWidth by remember { mutableStateOf(0.dp) }

    val selectedColor = configuration.selectedColor ?: MaterialTheme.appColors.primary
    val unselectedColor = configuration.unselectedColor ?: MaterialTheme.appColors.textPrimary
    val indicatorColor = configuration.indicatorColor ?: MaterialTheme.appColors.primary

    Column(modifier = modifier.fillMaxWidth()) {
        Box {
            LazyRow(
                state = scrollState,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = configuration.horizontalPadding
                )
            ) {
                items(tabs) { tab ->
                    val isSelected = tab == selectedTab

                    val displayText = tab.toOrdinal()
                    Text(
                        text = displayText,
                        style = MaterialTheme.appTypography.interMedium16px.copy(
                            fontSize = configuration.fontSize,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        ),
                        color = if (isSelected) selectedColor else unselectedColor,
                        modifier = Modifier
                            .clickable { onTabSelected(tab) }
                            .padding(vertical = 12.dp)
                            .padding(end = configuration.tabSpacing)
                            .onGloballyPositioned { coordinates ->
                                if (isSelected) {
                                    with(density) {
                                        selectedTabOffset = coordinates.positionInParent().x.toDp()
                                        selectedTabWidth = coordinates.size.width.toDp()
                                    }
                                }
                            }
                    )
                }
            }

            // Animated Indicator
            val animatedOffset by animateDpAsState(targetValue = selectedTabOffset, label = "tab_indicator")
            val animatedWidth by animateDpAsState(targetValue = selectedTabWidth, label = "tab_width")

            Box(
                modifier = Modifier
                    .offset(x = animatedOffset)
                    .width(animatedWidth)
                    .height(configuration.indicatorHeight)
                    .align(Alignment.BottomStart)
                    .background(indicatorColor)
            )
        }

        // Divider
        if (configuration.showDivider) {
            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.appColors.border
            )
        }
    }

    // Auto-scroll to selected tab
    LaunchedEffect(selectedTab) {
        val index = tabs.indexOf(selectedTab)
        if (index != -1) {
            scrollState.animateScrollToItem(index)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HorizontalTabBarPreview() {
    EcareProTheme {
        HorizontalTabBar(
            tabs = listOf("All", "LKG", "HKG", "UKG", "1", "2","3","4"),
            selectedTab = "All",
            onTabSelected = {}
        )
    }
}

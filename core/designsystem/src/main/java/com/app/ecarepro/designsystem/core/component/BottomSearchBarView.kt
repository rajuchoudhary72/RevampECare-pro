package com.app.ecarepro.designsystem.core.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
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
 * BottomSearchBarView - A reusable bottom-positioned search bar component
 * with an optional right icon button, sort icon button, and filter button.
 *
 * Features:
 * - Bottom positioned with shadow effect
 * - Integrated search bar with clear functionality
 * - Optional sort icon button
 * - Optional filter icon button with badge count
 * - Optional right icon button with custom action
 *
 * @param searchText Current search text
 * @param onSearchTextChange Callback when search text changes
 * @param placeholder Placeholder text for search field (default: "Search here...")
 * @param modifier Modifier for the component
 * @param showSortButton Whether to show the sort button (default: false)
 * @param onSortClick Optional callback when sort button is clicked
 * @param showFilterButton Whether to show the filter button (default: false)
 * @param onFilterClick Optional callback when filter button is clicked
 * @param filterBadgeCount Number to display on filter badge (0 = no badge)
 * @param rightIconPainter Optional painter for right icon button
 * @param rightIconVector Optional vector for right icon button (alternative to painter)
 * @param rightIconRes Optional drawable resource ID for right icon
 * @param onRightIconClick Optional callback when right icon is clicked
 * @param onSearchSubmit Optional callback when search is submitted
 */
@Composable
fun BottomSearchBarView(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search here...",
    showSortButton: Boolean = false,
    onSortClick: (() -> Unit)? = null,
    showFilterButton: Boolean = false,
    onFilterClick: (() -> Unit)? = null,
    filterBadgeCount: Int = 0,
    rightIconPainter: Painter? = null,
    rightIconVector: ImageVector? = null,
    rightIconRes: Int? = null,
    onRightIconClick: (() -> Unit)? = null,
    onSearchSubmit: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .imePadding()
            .shadow(
                elevation = 6.dp,
                spotColor = Color.Black.copy(alpha = 0.06f),
                ambientColor = Color.Black.copy(alpha = 0.06f)
            )
            .background(White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // SearchBar component
        SearchBar(
            searchText = searchText,
            onSearchTextChange = onSearchTextChange,
            placeholder = placeholder,
            modifier = Modifier.weight(1f),
            onSearchSubmit = onSearchSubmit
        )

        // Sort button
        if (showSortButton) {
            IconButton(
                onClick = { onSortClick?.invoke() },
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = MaterialTheme.appColors.background,
                        shape = RoundedCornerShape(10.dp)
                    )
            ) {
                Icon(
                    painter = painterResource(id = com.app.ecarepro.core.designsystem.R.drawable.ic_sort),
                    contentDescription = "Sort",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.appColors.textPrimary
                )
            }
        }

        // Filter button with badge
        if (showFilterButton) {
            Box(
                contentAlignment = Alignment.TopEnd
            ) {
                IconButton(
                    onClick = { onFilterClick?.invoke() },
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            color = MaterialTheme.appColors.background,
                            shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    Icon(
                        painter = painterResource(id = com.app.ecarepro.core.designsystem.R.drawable.ic_filter),
                        contentDescription = "Filter",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.appColors.textPrimary
                    )
                }

                // Badge
                if (filterBadgeCount > 0) {
                    Box(
                        modifier = Modifier
                            .offset(x = 2.dp, y = (-2).dp)
                            .size(16.dp)
                            .background(
                                color = MaterialTheme.appColors.primary,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (filterBadgeCount > 9) "9+" else filterBadgeCount.toString(),
                            style = MaterialTheme.appTypography.interSemiBold14px.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = White
                        )
                    }
                }
            }
        }

        // Optional right icon button
        when {
            rightIconPainter != null || rightIconVector != null || rightIconRes != null -> {
                IconButton(
                    onClick = { onRightIconClick?.invoke() },
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            color = MaterialTheme.appColors.background,
                            shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    when {
                        rightIconPainter != null -> {
                            Icon(
                                painter = rightIconPainter,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.appColors.textPrimary
                            )
                        }
                        rightIconVector != null -> {
                            Icon(
                                imageVector = rightIconVector,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.appColors.textPrimary
                            )
                        }
                        rightIconRes != null -> {
                            Icon(
                                painter = painterResource(id = rightIconRes),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.appColors.textPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

// Preview Functions

@Preview(name = "Empty Search Bar", showBackground = true)
@Composable
private fun BottomSearchBarViewPreview_Empty() {
    EcareProTheme {
        var searchText by remember { mutableStateOf("") }
        BottomSearchBarView(
            searchText = searchText,
            onSearchTextChange = { searchText = it },
            placeholder = "Search here..."
        )
    }
}

@Preview(name = "Search Bar with Text", showBackground = true)
@Composable
private fun BottomSearchBarViewPreview_WithText() {
    EcareProTheme {
        var searchText by remember { mutableStateOf("Sample search query") }
        BottomSearchBarView(
            searchText = searchText,
            onSearchTextChange = { searchText = it },
            placeholder = "Search here..."
        )
    }
}

@Preview(name = "With Download Icon", showBackground = true)
@Composable
private fun BottomSearchBarViewPreview_WithIcon() {
    EcareProTheme {
        var searchText by remember { mutableStateOf("") }
        BottomSearchBarView(
            searchText = searchText,
            onSearchTextChange = { searchText = it },
            placeholder = "Search syllabus...",
            rightIconRes = com.app.ecarepro.core.designsystem.R.drawable.ic_download,
            onRightIconClick = { }
        )
    }
}

@Preview(name = "With Text and Icon", showBackground = true)
@Composable
private fun BottomSearchBarViewPreview_Complete() {
    EcareProTheme {
        var searchText by remember { mutableStateOf("Mathematics") }
        BottomSearchBarView(
            searchText = searchText,
            onSearchTextChange = { searchText = it },
            placeholder = "Search students...",
            rightIconRes = com.app.ecarepro.core.designsystem.R.drawable.ic_download,
            onRightIconClick = { }
        )
    }
}

@Preview(name = "All Variants", showBackground = true)
@Composable
private fun BottomSearchBarViewPreview_AllVariants() {
    EcareProTheme {
        Column {
            var searchText1 by remember { mutableStateOf("") }
            BottomSearchBarView(
                searchText = searchText1,
                onSearchTextChange = { searchText1 = it },
                placeholder = "Empty search bar"
            )

            Spacer(modifier = Modifier.height(16.dp))

            var searchText2 by remember { mutableStateOf("With text") }
            BottomSearchBarView(
                searchText = searchText2,
                onSearchTextChange = { searchText2 = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            var searchText3 by remember { mutableStateOf("") }
            BottomSearchBarView(
                searchText = searchText3,
                onSearchTextChange = { searchText3 = it },
                placeholder = "With icon",
                rightIconRes = com.app.ecarepro.core.designsystem.R.drawable.ic_download,
                onRightIconClick = { }
            )
        }
    }
}

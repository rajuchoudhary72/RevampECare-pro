package com.app.ecarepro.feature.syllabus.componets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.syllabus.R

/**
 * Bottom positioned search bar with optional right icon
 * Matches the UI from the screenshot - positioned at the bottom of the screen
 *
 * @param searchQuery Current search text
 * @param onSearchQueryChanged Callback when search text changes
 * @param placeholder Placeholder text for search field
 * @param rightIcon Optional icon to display on the right (e.g., filter/sort icon)
 * @param onRightIconClick Callback when right icon is clicked
 * @param modifier Modifier for the container
 */
@Composable
fun BottomSearchBar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    placeholder: String = "Search by title or subject",
    rightIcon: (@Composable () -> Unit)? = null,
    onRightIconClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                spotColor = Color.Black.copy(alpha = 0.06f),
                ambientColor = Color.Black.copy(alpha = 0.06f)
            )
            .background(White)
            .padding(horizontal = 21.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Search Bar
        SearchBarInternal(
            searchText = searchQuery,
            onSearchTextChange = onSearchQueryChanged,
            placeholder = placeholder,
            modifier = Modifier.weight(1f)
        )

        // Optional right icon (filter/sort button)
        if (rightIcon != null && onRightIconClick != null) {
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.appColors.background,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { onRightIconClick() }
                    .padding(horizontal = 18.dp, vertical = 14.5.dp),
                contentAlignment = Alignment.Center
            ) {
                rightIcon()
            }
        }
    }
}

/**
 * Internal search bar component with magnifying glass and clear button
 */
@Composable
private fun SearchBarInternal(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .border(
                width = 0.5.dp,
                color = MaterialTheme.appColors.divider,
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                color = MaterialTheme.appColors.background,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 14.dp, vertical = 15.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Search Icon
        Icon(
            painter = painterResource(R.drawable.ic_search),
            contentDescription = "Search",
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.appColors.textSecondary
        )

        // Text Field
        BasicTextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            modifier = Modifier.weight(1f),
            textStyle = MaterialTheme.appTypography.interRegular14px.copy(
                color = MaterialTheme.appColors.textPrimary
            ),
            decorationBox = { innerTextField ->
                if (searchText.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.appTypography.interRegular14px,
                        color = MaterialTheme.appColors.textSecondary
                    )
                }
                innerTextField()
            },
            singleLine = true
        )

        // Clear Button (only show when text is not empty)
        if (searchText.isNotEmpty()) {
            IconButton(
                onClick = { onSearchTextChange("") },
                modifier = Modifier.size(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear search",
                    modifier = Modifier.size(13.5.dp),
                    tint = MaterialTheme.appColors.textPrimary
                )
            }
        }
    }
}

/**
 * Legacy SearchBar component for backward compatibility
 * Now wraps BottomSearchBar without the optional icon
 */
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
) {
    BottomSearchBar(
        searchQuery = searchQuery,
        onSearchQueryChanged = onSearchQueryChanged,
        modifier = modifier
    )
}

// Previews
@Preview(showBackground = true)
@Composable
private fun BottomSearchBarPreview() {
    EcareProTheme {
        BottomSearchBar(
            searchQuery = "",
            onSearchQueryChanged = {},
            rightIcon = {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.appColors.textPrimary
                )
            },
            onRightIconClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomSearchBarWithTextPreview() {
    EcareProTheme {
        BottomSearchBar(
            searchQuery = "Physics",
            onSearchQueryChanged = {},
            rightIcon = {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.appColors.textPrimary
                )
            },
            onRightIconClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchBarPreview() {
    EcareProTheme {
        SearchBar(
            searchQuery = "",
            onSearchQueryChanged = {}
        )
    }
}

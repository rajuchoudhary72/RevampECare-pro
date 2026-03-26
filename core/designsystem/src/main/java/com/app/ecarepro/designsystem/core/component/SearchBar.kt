package com.app.ecarepro.designsystem.core.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

/**
 * SearchBar component with search icon, text field, and clear button
 *
 * @param searchText Current search text
 * @param onSearchTextChange Callback when search text changes
 * @param placeholder Placeholder text to show when empty
 * @param modifier Modifier for the search bar
 * @param onSearchSubmit Optional callback when user submits the search
 */
@Composable
fun SearchBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search...",
    onSearchSubmit: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.appColors.background,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 0.5.dp,
                color = MaterialTheme.appColors.border,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 14.dp, vertical = 15.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Magnifying glass icon
        Icon(
            painter = painterResource(id = com.app.ecarepro.core.designsystem.R.drawable.ic_search),
            contentDescription = "Search",
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.appColors.textSecondary
        )

        // Text field
        BasicTextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            modifier = Modifier.weight(1f),
            textStyle = MaterialTheme.appTypography.interRegular14px.copy(
                color = MaterialTheme.appColors.textPrimary
            ),
            singleLine = true,
            cursorBrush = SolidColor(MaterialTheme.appColors.textPrimary),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearchSubmit?.invoke()
                }
            ),
            decorationBox = { innerTextField ->
                if (searchText.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.appTypography.interRegular14px.copy(
                            color = MaterialTheme.appColors.textSecondary,
                            fontSize = 14.sp
                        )
                    )
                }
                innerTextField()
            }
        )

        // Clear button (X mark)
        if (searchText.isNotEmpty()) {
            Icon(
                painter = painterResource(id = com.app.ecarepro.core.designsystem.R.drawable.ic_close),
                contentDescription = "Clear",
                modifier = Modifier
                    .size(13.5.dp, 14.dp)
                    .clickable { onSearchTextChange("") },
                tint = MaterialTheme.appColors.textPrimary
            )
        }
    }
}

// Preview Functions

@Preview(name = "Empty SearchBar", showBackground = true)
@Composable
private fun SearchBarPreview_Empty() {
    EcareProTheme {
        var searchText by remember { mutableStateOf("") }
        SearchBar(
            searchText = searchText,
            onSearchTextChange = { searchText = it },
            placeholder = "Search...",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Preview(name = "SearchBar with Text", showBackground = true)
@Composable
private fun SearchBarPreview_WithText() {
    EcareProTheme {
        var searchText by remember { mutableStateOf("Sample query") }
        SearchBar(
            searchText = searchText,
            onSearchTextChange = { searchText = it },
            placeholder = "Search...",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Preview(name = "Custom Placeholder", showBackground = true)
@Composable
private fun SearchBarPreview_CustomPlaceholder() {
    EcareProTheme {
        var searchText by remember { mutableStateOf("") }
        SearchBar(
            searchText = searchText,
            onSearchTextChange = { searchText = it },
            placeholder = "Search students, classes, subjects...",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Preview(name = "All States", showBackground = true)
@Composable
private fun SearchBarPreview_AllStates() {
    EcareProTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            var searchText1 by remember { mutableStateOf("") }
            Text("Empty state:")
            SearchBar(
                searchText = searchText1,
                onSearchTextChange = { searchText1 = it },
                placeholder = "Search here...",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            var searchText2 by remember { mutableStateOf("Active search") }
            Text("With text (shows clear button):")
            SearchBar(
                searchText = searchText2,
                onSearchTextChange = { searchText2 = it },
                placeholder = "Search...",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

package com.app.ecarepro.feature.syllabus.componets

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.app.ecarepro.designsystem.core.component.EcareProOutlinedTextField
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.syllabus.R

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
) {

    EcareProOutlinedTextField(
        modifier = modifier,
        containerColor = MaterialTheme.appColors.background,
        value = searchQuery,
        onValueChange = onSearchQueryChanged,
        placeholder = {
            Text(
                text = "Search by title or subject",
                style = MaterialTheme.appTypography.interRegular14px,
                color = MaterialTheme.appColors.textSecondary
            )
        },
        leadingIcon = {
            Icon(
                painterResource(R.drawable.ic_search),
                contentDescription = "Search Icon"
            )
        },
    )
}


@Preview(showBackground = true)
@Composable
private fun AppBarPreview() {
    EcareProTheme {
        SearchBar(searchQuery = "", onSearchQueryChanged = {})
    }
}
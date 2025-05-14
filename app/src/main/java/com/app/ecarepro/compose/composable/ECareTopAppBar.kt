package com.app.ecarepro.compose.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.ecarepro.R
import com.app.ecarepro.compose.theme.ECareProTheme
import com.app.ecarepro.compose.theme.white


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ECareTopAppBar(
    title: String,
    searchQuery: String,
    onQueryChange: (String) -> Unit = {},
    onClickBack: () -> Unit = {}
) {
    var isSearchActive by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            if (isSearchActive) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                        .clip(RoundedCornerShape(120.dp))
                        .background(Color.White),
                    value = searchQuery,
                    onValueChange = { newText -> onQueryChange(newText) },
                    placeholder = { Text(stringResource(R.string.search)) },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (searchQuery.isEmpty()) {
                                    isSearchActive = false
                                } else {
                                    onQueryChange("")
                                }
                            }
                        ) {
                            Icon(
                                Icons.Filled.Clear,
                                contentDescription = stringResource(R.string.clear_search)
                            )
                        }
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors().copy(
                        focusedContainerColor = white,
                        unfocusedContainerColor = white,
                        focusedIndicatorColor = white,
                        unfocusedIndicatorColor = white,
                        disabledIndicatorColor = white,
                        cursorColor = Color.Black

                    ),
                    textStyle = MaterialTheme.typography.bodyMedium,
                )

            } else {
                Text(title)
            }
        },
        navigationIcon = {
            IconButton(onClick = onClickBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
            }
        },
        actions = {
            if (isSearchActive) {
                Spacer(modifier = Modifier.width(16.dp))
            } else {
                IconButton(onClick = { isSearchActive = true }) {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = stringResource(R.string.co_search)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF4CAF50),
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}

@Preview(showBackground = true)
@Composable
fun DefineSkillTopAppBarPreview() {
    ECareProTheme {
        ECareTopAppBar(
            title = "Define Skill",
            searchQuery = ""
        )
    }
}
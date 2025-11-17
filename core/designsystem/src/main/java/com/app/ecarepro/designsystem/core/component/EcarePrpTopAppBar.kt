package com.app.ecarepro.designsystem.core.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcareProTopAppBar(
    modifier: Modifier = Modifier,
    title: String,
    onNavigationClicked: () -> Unit,
    navigationIcon: @Composable () -> Unit = { TopAppBarNavigationIcon(onNavigationClicked) },
    actions: @Composable RowScope.() -> Unit = {},
    expandedHeight: Dp = TopAppBarDefaults.TopAppBarExpandedHeight,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(containerColor = White),
    scrollBehavior: TopAppBarScrollBehavior? = null,
    contentPadding: PaddingValues = TopAppBarDefaults.ContentPadding,
) {

    TopAppBar(
        title = {
            TopAppBarTitle(title)
        },
        navigationIcon = navigationIcon,
        actions = actions,
        expandedHeight = expandedHeight,
        windowInsets = windowInsets,
        colors = colors,
        scrollBehavior = scrollBehavior,
        contentPadding = contentPadding,
        modifier = modifier
    )
}

@Composable
private fun TopAppBarNavigationIcon(onNavigationClicked: () -> Unit) {
    IconButton(onClick = onNavigationClicked) {
        Icon(
            Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back"
        )
    }
}

@Composable
private fun TopAppBarTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.appTypography.interMedium16px,
        color = MaterialTheme.appColors.textPrimary
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun AppBarPreview() {
    EcareProTheme {
        EcareProTopAppBar(
            title = "Syllabus",
            onNavigationClicked = {},
        )
    }
}
package com.app.ecarepro.designsystem.core.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.InterFontFamily
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors

/**
 * EcarePro App Bar using Material3 TopAppBar
 *
 * A navigation bar component that wraps Material3's TopAppBar with EcarePro styling.
 * This version provides better integration with Material Design components and
 * automatic WindowInsets handling.
 *
 * @param title The title text to display in the app bar
 * @param onBackClick Callback invoked when the back button is clicked (null hides back button)
 * @param modifier Optional modifier for the app bar
 * @param backgroundColor Background color of the app bar (default: White)
 * @param contentColor Color for the title and navigation icon (default: theme's textPrimary)
 * @param elevation Elevation of the shadow (default: 4.dp)
 * @param actions Optional composable actions to display on the right side
 *
 * Example usage:
 * ```
 * EcareProAppBar(
 *     title = "Leave Report",
 *     onBackClick = { navController.popBackStack() }
 * )
 * ```
 *
 * Example with actions:
 * ```
 * EcareProAppBar(
 *     title = "Messages",
 *     onBackClick = { navController.popBackStack() },
 *     actions = {
 *         IconButton(onClick = { /* Search */ }) {
 *             Icon(Icons.Default.Search, contentDescription = "Search")
 *         }
 *         IconButton(onClick = { /* More */ }) {
 *             Icon(Icons.Default.MoreVert, contentDescription = "More")
 *         }
 *     }
 * )
 * ```
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcareProAppBar(
    title: String,
    onBackClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    backgroundColor: Color = White,
    contentColor: Color = MaterialTheme.appColors.textPrimary,
    elevation: androidx.compose.ui.unit.Dp = 4.dp,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = elevation,
                spotColor = Color.Black.copy(alpha = 0.06f),
                ambientColor = Color.Black.copy(alpha = 0.06f)
            ),
        color = backgroundColor
    ) {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = contentColor,
                    maxLines = 1
                )
            },
            navigationIcon = {
                if (onBackClick != null) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back",
                            modifier = Modifier.size(20.dp),
                            tint = contentColor
                        )
                    }
                }
            },
            actions = actions,
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                navigationIconContentColor = contentColor,
                titleContentColor = contentColor,
                actionIconContentColor = contentColor
            ),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

/**
 * EcarePro Centered Title App Bar
 *
 * A variation with centered title, commonly used for modal screens or bottom sheets.
 *
 * @param title The title text to display (centered)
 * @param onBackClick Callback invoked when the back button is clicked (null hides back button)
 * @param modifier Optional modifier for the app bar
 * @param backgroundColor Background color of the app bar (default: White)
 * @param contentColor Color for the title and navigation icon (default: theme's textPrimary)
 * @param elevation Elevation of the shadow (default: 4.dp)
 * @param actions Optional composable actions to display on the right side
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcareProCenteredAppBar(
    title: String,
    onBackClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    backgroundColor: Color = White,
    contentColor: Color = MaterialTheme.appColors.textPrimary,
    elevation: androidx.compose.ui.unit.Dp = 4.dp,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = elevation,
                spotColor = Color.Black.copy(alpha = 0.06f),
                ambientColor = Color.Black.copy(alpha = 0.06f)
            ),
        color = backgroundColor
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = title,
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = contentColor,
                    maxLines = 1
                )
            },
            navigationIcon = {
                if (onBackClick != null) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back",
                            modifier = Modifier.size(20.dp),
                            tint = contentColor
                        )
                    }
                }
            },
            actions = actions,
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent,
                navigationIconContentColor = contentColor,
                titleContentColor = contentColor,
                actionIconContentColor = contentColor
            ),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

/**
 * EcarePro Large Title App Bar
 *
 * A larger app bar with prominent title, suitable for main screens.
 *
 * @param title The title text to display in large format
 * @param modifier Optional modifier for the app bar
 * @param backgroundColor Background color of the app bar (default: White)
 * @param contentColor Color for the title (default: theme's textPrimary)
 * @param elevation Elevation of the shadow (default: 4.dp)
 * @param actions Optional composable actions to display on the right side
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcareProLargeAppBar(
    title: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = White,
    contentColor: Color = MaterialTheme.appColors.textPrimary,
    elevation: androidx.compose.ui.unit.Dp = 4.dp,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = elevation,
                spotColor = Color.Black.copy(alpha = 0.06f),
                ambientColor = Color.Black.copy(alpha = 0.06f)
            ),
        color = backgroundColor
    ) {
        LargeTopAppBar(
            title = {
                Text(
                    text = title,
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            },
            actions = actions,
            colors = TopAppBarDefaults.largeTopAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = contentColor,
                actionIconContentColor = contentColor
            )
        )
    }
}

// Preview Composables

@Preview(name = "Standard App Bar", showBackground = true)
@Composable
private fun EcareProAppBarPreview() {
    EcareProTheme {
        Column {
            EcareProAppBar(
                title = "Leave Report",
                onBackClick = {}
            )
        }
    }
}

@Preview(name = "App Bar with Actions", showBackground = true)
@Composable
private fun EcareProAppBarWithActionsPreview() {
    EcareProTheme {
        Column {
            EcareProAppBar(
                title = "Messages",
                onBackClick = {},
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Replace with Search
                            contentDescription = "Search",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Replace with MoreVert
                            contentDescription = "More",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )
        }
    }
}

@Preview(name = "App Bar with Text Action", showBackground = true)
@Composable
private fun EcareProAppBarWithTextActionPreview() {
    EcareProTheme {
        Column {
            EcareProAppBar(
                title = "Edit Profile",
                onBackClick = {},
                actions = {
                    TextButton(onClick = { }) {
                        Text(
                            text = "Save",
                            color = MaterialTheme.appColors.primary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            )
        }
    }
}

@Preview(name = "Centered App Bar", showBackground = true)
@Composable
private fun EcareProCenteredAppBarPreview() {
    EcareProTheme {
        Column {
            EcareProCenteredAppBar(
                title = "Settings",
                onBackClick = {},
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Replace with Check
                            contentDescription = "Done"
                        )
                    }
                }
            )
        }
    }
}

@Preview(name = "Large App Bar", showBackground = true)
@Composable
private fun EcareProLargeAppBarPreview() {
    EcareProTheme {
        Column {
            EcareProLargeAppBar(
                title = "Dashboard",
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Replace with Settings
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        }
    }
}

@Preview(name = "App Bar without Back Button", showBackground = true)
@Composable
private fun EcareProAppBarNoBackPreview() {
    EcareProTheme {
        Column {
            EcareProAppBar(
                title = "Notifications",
                onBackClick = null,
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Replace with MoreVert
                            contentDescription = "More options"
                        )
                    }
                }
            )
        }
    }
}

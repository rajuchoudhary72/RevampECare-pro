package com.app.ecarepro.designsystem.core.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
 * EcarePro Navigation Top Bar Component
 *
 * A reusable navigation bar with back button, title, and optional trailing content.
 * Based on iOS NavigationBarView pattern with Material Design 3 implementation.
 *
 * @param title The title text to display in the navigation bar
 * @param onBackClick Callback invoked when the back button is clicked
 * @param modifier Optional modifier for the navigation bar
 * @param showBackButton Whether to show the back button (default: true)
 * @param backgroundColor Background color of the navigation bar (default: White)
 * @param contentColor Color for the title and back button (default: theme's textPrimary)
 * @param elevation Elevation of the shadow (default: 4.dp)
 * @param trailingContent Optional composable content to display on the right side
 *
 * Example usage:
 * ```
 * EcareProTopBar(
 *     title = "Leave Report",
 *     onBackClick = { navController.popBackStack() }
 * )
 * ```
 *
 * Example with trailing content:
 * ```
 * EcareProTopBar(
 *     title = "Messages",
 *     onBackClick = { navController.popBackStack() }
 * ) {
 *     IconButton(onClick = { /* Search */ }) {
 *         Icon(Icons.Default.Search, contentDescription = "Search")
 *     }
 * }
 * ```
 */
@Composable
fun EcareProTopBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = true,
    backgroundColor: Color = White,
    contentColor: Color = MaterialTheme.appColors.textPrimary,
    elevation: androidx.compose.ui.unit.Dp = 4.dp,
    trailingContent: @Composable RowScope.() -> Unit = {}
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, top = 20.dp, end = 20.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
            if (showBackButton) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate back",
                    modifier = Modifier
                        .size(20.dp)
                        .clickable(
                            onClick = onBackClick,
                            role = Role.Button,
                            interactionSource = remember { MutableInteractionSource() },
                           // indication = rememberRipple(bounded = false, radius = 20.dp)
                        ),
                    tint = contentColor
                )
            }

            // Title
            Text(
                text = title,
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = contentColor,
                maxLines = 1
            )

            // Spacer to push trailing content to the right
            Spacer(modifier = Modifier.weight(1f))

            // Optional trailing content
            trailingContent()
        }
    }
}

/**
 * Simplified version of EcareProTopBar with no back button
 *
 * @param title The title text to display
 * @param modifier Optional modifier for the navigation bar
 * @param backgroundColor Background color of the navigation bar (default: White)
 * @param contentColor Color for the title (default: theme's textPrimary)
 * @param elevation Elevation of the shadow (default: 4.dp)
 * @param trailingContent Optional composable content to display on the right side
 */
@Composable
fun EcareProTopBarNoBack(
    title: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = White,
    contentColor: Color = MaterialTheme.appColors.textPrimary,
    elevation: androidx.compose.ui.unit.Dp = 4.dp,
    trailingContent: @Composable RowScope.() -> Unit = {}
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Title
            Text(
                text = title,
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = contentColor,
                maxLines = 1
            )

            // Spacer to push trailing content to the right
            Spacer(modifier = Modifier.weight(1f))

            // Optional trailing content
            trailingContent()
        }
    }
}

// Preview Composables

@Preview(name = "Basic Navigation Bar", showBackground = true)
@Composable
private fun EcareProTopBarPreview() {
    EcareProTheme {
        Column {
            EcareProTopBar(
                title = "Leave Report",
                onBackClick = {}
            )
        }
    }
}

@Preview(name = "Navigation Bar with Icon Button", showBackground = true)
@Composable
private fun EcareProTopBarWithIconPreview() {
    EcareProTheme {
        Column {
            EcareProTopBar(
                title = "Documents",
                onBackClick = {}
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Replace with Download icon
                    contentDescription = "Download",
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { }
                )
            }
        }
    }
}

@Preview(name = "Navigation Bar with Multiple Actions", showBackground = true)
@Composable
private fun EcareProTopBarWithMultipleActionsPreview() {
    EcareProTheme {
        Column {
            EcareProTopBar(
                title = "Messages",
                onBackClick = {}
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Replace with Search icon
                        contentDescription = "Search",
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { }
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Replace with MoreVert icon
                        contentDescription = "More options",
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { }
                    )
                }
            }
        }
    }
}

@Preview(name = "Navigation Bar with Text Button", showBackground = true)
@Composable
private fun EcareProTopBarWithTextButtonPreview() {
    EcareProTheme {
        Column {
            EcareProTopBar(
                title = "Edit Profile",
                onBackClick = {}
            ) {
                Text(
                    text = "Save",
                    color = MaterialTheme.appColors.primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable { }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Preview(name = "Navigation Bar without Back Button", showBackground = true)
@Composable
private fun EcareProTopBarNoBackPreview() {
    EcareProTheme {
        Column {
            EcareProTopBarNoBack(
                title = "Dashboard"
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Replace with Settings icon
                    contentDescription = "Settings",
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { }
                )
            }
        }
    }
}

@Preview(name = "Navigation Bar - No Trailing Content", showBackground = true)
@Composable
private fun EcareProTopBarMinimalPreview() {
    EcareProTheme {
        Column {
            EcareProTopBar(
                title = "Questionnaire",
                onBackClick = {},
                showBackButton = false
            )
        }
    }
}

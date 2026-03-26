package com.app.ecarepro.feature.menu.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.app.ecarepro.core.domain.model.menu.MenuItem
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

@Composable
fun MenuItemRow(
    menuItem: MenuItem,
    level: Int,
    expandedMenuItemIds: Set<Int>,
    onMenuItemClicked: (MenuItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val startPadding = (16 + (level - 1) * 20).dp
    val isExpanded = expandedMenuItemIds.contains(menuItem.id)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onMenuItemClicked(menuItem) }
                .padding(start = startPadding, end = 16.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Icon
            if (!menuItem.iconUrl.isNullOrEmpty()) {
                EcareProAsyncImage(
                    imageUrl = menuItem.iconUrl,
                    contentDescription = menuItem.title,
                    modifier = Modifier.size(22.dp),
                )
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Article,
                    contentDescription = menuItem.title,
                    tint = MaterialTheme.appColors.textSecondary,
                    modifier = Modifier.size(16.dp),
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = menuItem.title,
                style = MaterialTheme.appTypography.interRegular14px,
                color = MaterialTheme.appColors.textPrimary,
                modifier = Modifier.weight(1f),
            )

            if (menuItem.hasChildren) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.appColors.textSecondary,
                    modifier = Modifier
                        .size(20.dp)
                        .graphicsLayer { rotationZ = if (isExpanded) 90f else 0f },
                )
            }
        }

        // Children
        if (menuItem.hasChildren) {
            AnimatedVisibility(visible = isExpanded) {
                Column {
                    menuItem.children.forEach { child ->
                        MenuItemRow(
                            menuItem = child,
                            level = level + 1,
                            expandedMenuItemIds = expandedMenuItemIds,
                            onMenuItemClicked = onMenuItemClicked,
                        )
                    }
                }
            }
        }
    }
}

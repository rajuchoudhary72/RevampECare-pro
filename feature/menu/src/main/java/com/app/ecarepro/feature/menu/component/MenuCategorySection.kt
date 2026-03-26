package com.app.ecarepro.feature.menu.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.ecarepro.core.domain.model.menu.MenuCategory
import com.app.ecarepro.core.domain.model.menu.MenuItem
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.menu.R

@Composable
fun MenuCategorySection(
    category: MenuCategory,
    isExpanded: Boolean,
    expandedMenuItemIds: Set<Int>,
    onToggleCategory: (Int) -> Unit,
    onMenuItemClicked: (MenuItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Category header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleCategory(category.id) }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = category.title.uppercase(),
                style = MaterialTheme.appTypography.interSemiBold14px.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
                color = MaterialTheme.appColors.textSecondary,
                modifier = Modifier.weight(1f),
            )
            Icon(
                painter = painterResource(
                    id = if (isExpanded) R.drawable.ic_minus_circle else R.drawable.ic_add_circle
                ),
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                tint = MaterialTheme.appColors.textSecondary,
                modifier = Modifier.size(20.dp),
            )
        }

        // Category items
        AnimatedVisibility(visible = isExpanded) {
            Column {
                category.menuItems.forEach { menuItem ->
                    MenuItemRow(
                        menuItem = menuItem,
                        level = 1,
                        expandedMenuItemIds = expandedMenuItemIds,
                        onMenuItemClicked = onMenuItemClicked,
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        HorizontalDivider(
            color = Color(0xFFEEEEEE),
            thickness = 0.5.dp,
        )
    }
}

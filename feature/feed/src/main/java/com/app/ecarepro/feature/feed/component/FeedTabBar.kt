package com.app.ecarepro.feature.feed.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.feature.feed.FeedTab

@Composable
fun FeedTabBar(
    selectedTab: FeedTab,
    onTabSelected: (FeedTab) -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF4CAF50))

    ) {
        // Left side - Tabs
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TabItem(
                icon = Icons.Default.Schedule,
                label = "Latest",
                isSelected = selectedTab == FeedTab.LATEST,
                onClick = { onTabSelected(FeedTab.LATEST) }
            )

            TabItem(
                icon = Icons.Default.Bookmark,
                label = "Pinned",
                isSelected = selectedTab == FeedTab.PINNED,
                onClick = { onTabSelected(FeedTab.PINNED) }
            )
        }

        // Right side - Filter button
        FilterButton(onClick = onFilterClick)
    }
}

@Composable
private fun TabItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentColor = Color.White

    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(top = 8.dp, start = 12.dp, end = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icon
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )

        // Label
        Text(
            text = label,
            color = contentColor,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.padding(top = 4.dp, bottom = 6.dp)
        )

        // Bottom indicator
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(3.dp)
                .background(
                    color = if (isSelected) Color.White else Color.Transparent,
                    shape = RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp)
                )
        )
    }
}

@Composable
private fun FilterButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF66BB6A))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Filter",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Filter",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FeedTabBarPreview() {
    EcareProTheme {
        FeedTabBar(
            selectedTab = FeedTab.LATEST,
            onTabSelected = {},
            onFilterClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FeedTabBarPinnedPreview() {
    EcareProTheme {
        FeedTabBar(
            selectedTab = FeedTab.PINNED,
            onTabSelected = {},
            onFilterClick = {}
        )
    }
}

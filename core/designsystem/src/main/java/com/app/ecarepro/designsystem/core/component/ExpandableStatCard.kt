package com.app.ecarepro.designsystem.core.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

/**
 * Expandable card component for displaying statistics in rows with collapse/expand functionality.
 * Combines the header style of ExpandableCardSection with StatCard content.
 *
 * @param title The card title
 * @param icon The icon to display in the header
 * @param iconColor The tint color for the icon
 * @param iconBackgroundColor The background color for the icon container
 * @param stats List of stat items to display
 * @param isExpanded Whether the card content is expanded
 * @param onToggle Callback when the card header is clicked
 * @param modifier Optional modifier for the card
 * @param onViewDetails Optional callback for view details action
 */
@Composable
fun ExpandableStatCard(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    iconBackgroundColor: Color,
    stats: List<StatItem>,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    onViewDetails: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column {
            // Header (always visible, clickable to toggle)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CardIconContainer(
                    icon = icon,
                    iconColor = iconColor,
                    iconBackgroundColor = iconBackgroundColor
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = title,
                    style = MaterialTheme.appTypography.interMedium16px,
                    color = MaterialTheme.appColors.textPrimary,
                    modifier = Modifier.weight(1f)
                )

                // Chevron rotation animation
                val rotation by animateFloatAsState(
                    targetValue = if (isExpanded) 180f else 0f,
                    animationSpec = tween(durationMillis = 300),
                    label = "chevron_rotation"
                )

                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(rotation),
                    tint = MaterialTheme.appColors.textSecondary
                )
            }

            // Expandable content
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(animationSpec = tween(300)) +
                        expandVertically(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300)) +
                        shrinkVertically(animationSpec = tween(300))
            ) {
                Column {
                    // Stats rows
                    stats.forEachIndexed { index, stat ->
                        ExpandableStatRow(
                            label = stat.label,
                            value = stat.value,
                            labelColor = stat.labelColor ?: MaterialTheme.appColors.textSecondary,
                            valueColor = stat.valueColor ?: MaterialTheme.appColors.textPrimary,
                            showDivider = index < stats.size - 1 || onViewDetails != null
                        )
                    }

                    // View details link
                    if (onViewDetails != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onViewDetails() }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "View details",
                                style = MaterialTheme.appTypography.interMedium14px,
                                color = Color(0xFF00C7BE)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun ExpandableStatRow(
    label: String,
    value: String,
    labelColor: Color,
    valueColor: Color,
    showDivider: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.appTypography.interRegular16px,
                color = labelColor
            )
            Text(
                text = value,
                style = MaterialTheme.appTypography.interMedium16px,
                color = valueColor
            )
        }
        if (showDivider) {
            Divider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.appColors.textSecondary.copy(alpha = 0.1f)
            )
        }
    }
}

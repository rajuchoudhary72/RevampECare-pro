package com.app.ecarepro.feature.globalsearch.search.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.app.ecarepro.core.domain.model.globalsearch.SearchResultPresentation
import com.app.ecarepro.core.domain.model.globalsearch.SearchResultType
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

@Composable
fun SearchResultRow(
    result: SearchResultPresentation,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Photo / Module icon
        when (result.type) {
            SearchResultType.STUDENT, SearchResultType.STAFF -> {
                EcareProAsyncImage(
                    imageUrl = result.photoUrl,
                    contentDescription = result.name,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                )
            }
            SearchResultType.MODULE -> {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(10.dp))
                        .clip(RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    if (!result.moduleIconUrl.isNullOrEmpty()) {
                        EcareProAsyncImage(
                            imageUrl = result.moduleIconUrl,
                            contentDescription = result.name,
                            modifier = Modifier.size(30.dp),
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.GridView,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.appColors.textSecondary,
                        )
                    }
                }
            }
        }

        // Name + subtitle
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = result.name,
                style = MaterialTheme.appTypography.interSemiBold14px,
                color = MaterialTheme.appColors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (result.subtitle.isNotEmpty()) {
                Text(
                    text = result.subtitle,
                    style = MaterialTheme.appTypography.interRegular12px,
                    color = MaterialTheme.appColors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        // Badge
        val badgeColor = when (result.type) {
            SearchResultType.STUDENT -> Color(0xFFF5A623)
            SearchResultType.STAFF -> Color(0xFF4A90D9)
            SearchResultType.MODULE -> MaterialTheme.appColors.textSecondary
        }
        val badgeText = when (result.type) {
            SearchResultType.STUDENT -> "Student"
            SearchResultType.STAFF -> "Staff"
            SearchResultType.MODULE -> "Module"
        }

        Box(
            modifier = Modifier
                .then(
                    when (result.type) {
                        SearchResultType.STUDENT, SearchResultType.STAFF ->
                            Modifier.background(badgeColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                        SearchResultType.MODULE ->
                            Modifier.border(
                                width = 1.dp,
                                color = MaterialTheme.appColors.textSecondary.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp),
                            )
                    }
                )
                .padding(horizontal = 10.dp, vertical = 4.dp),
        ) {
            Text(
                text = badgeText,
                style = MaterialTheme.appTypography.interMedium12px,
                color = badgeColor,
            )
        }
    }
}

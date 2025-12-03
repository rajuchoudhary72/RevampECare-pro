package com.app.ecarepro.feature.assignment.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.app.ecarepro.core.designsystem.R
import com.app.ecarepro.designsystem.core.component.EcareProOutlinedTextField
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography


@Composable
fun SearchAndFilterBottomBar(
    modifier: Modifier = Modifier,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onClickFilter: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(White)
            .shadow(1.dp)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EcareProOutlinedTextField(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                containerColor = MaterialTheme.appColors.background,
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                placeholder = {
                    Text(
                        text = "Search by class, teacher or subject",
                        style = MaterialTheme.appTypography.interRegular14px,
                        color = MaterialTheme.appColors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                leadingIcon = {
                    Icon(
                        painterResource(R.drawable.ic_search),
                        contentDescription = "Search Icon"
                    )
                },
                unfocusedBorderColor = MaterialTheme.appColors.border
            )

            Box(
                modifier = Modifier
                    .background(MaterialTheme.appColors.background)
                    .size(48.dp)
                    .border(
                        1.dp,
                        MaterialTheme.appColors.border,
                        RoundedCornerShape(8.dp)
                    )
                    .clickable { onClickFilter() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(com.app.ecarepro.feature.assignment.R.drawable.icon_calendar), // Ensure icon exists
                    contentDescription = "Filter Date",
                    tint = MaterialTheme.appColors.textSecondary
                )
            }
        }
    }
}
package com.app.ecarepro.feature.homeselection.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.core.domain.model.HomeSelection
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography


@Composable
fun HomeSelectionCard(
    option: HomeSelection,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val border =
        if (isSelected) BorderStroke(1.dp, MaterialTheme.appColors.primary) else BorderStroke(
            1.dp,
            MaterialTheme.appColors.border
        )
    val cardColors = if (isSelected) {
        CardDefaults.cardColors(containerColor = MaterialTheme.appColors.primary.copy(alpha = 0.14f))
    } else {
        CardDefaults.cardColors(containerColor = Color.Transparent)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        border = border,
        colors = cardColors
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = option.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .background(White, MaterialTheme.shapes.medium)
                    .border(
                        width = 0.5.dp,
                        color = if (isSelected) White else MaterialTheme.appColors.border,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(12.dp),
                contentScale = ContentScale.Fit
            )
            Column {
                Text(
                    text = option.title,
                    style = MaterialTheme.appTypography.nunitoBold12px.copy(fontSize = 18.sp),
                )
                Text(
                    text = option.description,
                    style = MaterialTheme.appTypography.interRegular12px
                )
            }
        }
    }
}

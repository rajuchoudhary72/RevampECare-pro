package com.app.ecarepro.feature.login.component

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.component.AppAsyncImage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.login.screens.forgotpassword.Ward

// Mock Ward data for preview
val previewWards = listOf(
    Ward(id = 1, name = "Nimish G", photoUrl = null, isSelected = true),
    Ward(id = 2, name = "Shally", photoUrl = null, isSelected = false),
)

@Composable
fun WardList(
    modifier: Modifier = Modifier,
    wards: List<Ward>,
    onWardSelected: (Ward) -> Unit,
    selectedWard: Ward?,
) {
    Column(modifier = modifier) {
        wards.forEach { ward ->
            WardItem(
                ward = ward,
                isSelected = ward == selectedWard,
                onSelected = { onWardSelected(ward) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun WardItem(
    modifier: Modifier = Modifier,
    ward: Ward,
    isSelected: Boolean,
    onSelected: () -> Unit,
) {
    val backgroundColor = if (isSelected) MaterialTheme.appColors.primary else Color.White
    val contentColor = if (isSelected) Color.White else MaterialTheme.appColors.textPrimary
    val border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.appColors.border)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onSelected),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = border
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.width(100.dp))
            AppAsyncImage(
                imageUrl = ward.photoUrl,
                contentDescription = ward.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
            )
            Text(
                text = ward.name,
                style = MaterialTheme.appTypography.interMedium16px,
                color = contentColor,
                modifier = Modifier
                    .padding(start = 10.dp),
                textAlign = TextAlign.Start
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F7F7)
@Composable
private fun WardListPreview() {
    EcareProTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            WardList(
                wards = previewWards,
                onWardSelected = {},
                selectedWard = null
            )
        }
    }
}
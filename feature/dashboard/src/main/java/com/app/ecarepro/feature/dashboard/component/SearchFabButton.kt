package com.app.ecarepro.feature.dashboard.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.dashboard.R

@Composable
fun SearchFabButton(
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier

            .background(
                brush = Brush.linearGradient(
                    listOf(
                        Color(0xFF00FFFF),
                        Color(0xFDFF63FF),
                    )
                ),
                shape = RoundedCornerShape(50)
            )
            .clickable {
                onClick()
            }
            .padding(1.dp)
    ) {
        Row(
            modifier = Modifier
                .background(
                    color = MaterialTheme.appColors.textPrimary,
                    shape = RoundedCornerShape(50)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = null,
                tint = White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Search",
                style = MaterialTheme.appTypography.nunitoMedium12px.copy(
                    fontSize = 16.sp,
                    color = White
                )
            )
        }
    }
}

@Preview
@Composable
fun SearchFabButtonPreview() {
    EcareProTheme {
        SearchFabButton(onClick = {})
    }

}

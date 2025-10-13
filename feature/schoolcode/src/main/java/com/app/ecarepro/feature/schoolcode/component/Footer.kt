package com.app.ecarepro.feature.schoolcode.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.schoolcode.R

@Composable
fun Footer() {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Powered by",
            style = MaterialTheme.appTypography.interMedium16px.copy(fontSize = 14.sp),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 16.dp),
            color = White
        )
        Image(
            painter = painterResource(id = R.drawable.franciscan_logo),
            contentDescription = "Franciscan e-care Logo",
            modifier = Modifier.size(width = 96.dp, height = 16.dp),
        )
    }

}
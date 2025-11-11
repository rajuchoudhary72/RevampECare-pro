package com.app.ecarepro.designsystem.core.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.core.designsystem.R
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

@Composable
fun EcareProErrorState(
    modifier: Modifier = Modifier,
    title: String = "Ooops",
    message: String,
    onRetry: () -> Unit,
) {

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_error),
                contentDescription = "Error Icon",
                modifier = Modifier.size(100.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.appColors.textSecondary.copy(alpha = 0.5f))
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = title,
                style = MaterialTheme.appTypography.nunitoBlack34px.copy(fontSize = 24.sp),
                color = MaterialTheme.appColors.textPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.appTypography.interRegular14px,
                color = MaterialTheme.appColors.textSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                modifier = Modifier.width(150.dp),
                title = stringResource(id = R.string.btn_retry),
                onClick = onRetry
            )
        }
    }
}

@Preview
@Composable
fun EcareProErrorStatePreview() {
    EcareProTheme {
        EcareProErrorState(
            message = "We couldn't load the timetable. Please check your connection and try again.",
            onRetry = {}
        )
    }
}

package com.app.ecarepro.feature.timetable.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.timetable.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    onClickNavigationIcon: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.feature_timetable_my_timetable),
                style = MaterialTheme.appTypography.interMedium16px,
                color = MaterialTheme.appColors.textPrimary
            )
        },
        navigationIcon = {
            IconButton(onClick = onClickNavigationIcon) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }
    )
}

@Preview
@Composable
private fun AppBarPreview() {
    EcareProTheme {
        AppBar(onClickNavigationIcon = {})
    }

}
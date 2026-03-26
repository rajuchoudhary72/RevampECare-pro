package com.app.ecarepro.feature.homeselection.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.core.domain.model.SchoolDetail
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.homeselection.R


@Composable
fun HeaderSection(schoolDetails: SchoolDetail?) {
    EcareProAsyncImage(
        imageUrl = schoolDetails?.logo,
        contentDescription = "School Logo",
        modifier = Modifier
            .size(60.dp)
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = stringResource(R.string.feature_homeselection_how_do_you_want_to_start),
        style = MaterialTheme.appTypography.nunitoBlack34px.copy(fontSize = 28.sp),
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = stringResource(R.string.feature_homeselection_begin_with_dashboard_feeds_or_favorites_whichever_suits_you_best_you_can_update_this_later_in_settings),
        style = MaterialTheme.appTypography.interRegular14px
    )
}

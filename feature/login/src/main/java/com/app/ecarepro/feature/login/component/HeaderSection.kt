package com.app.ecarepro.feature.login.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.core.domain.model.SchoolDetail
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.login.R


@Composable
fun HeaderSection(
    modifier: Modifier = Modifier,
    schoolDetails: SchoolDetail?,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        EcareProAsyncImage(
            imageUrl = schoolDetails?.logo,
            contentDescription = "School Logo",
            modifier = Modifier.size(80.dp),
        )
        Text(
            text = stringResource(R.string.feature_login_hello_again),
            style = MaterialTheme.appTypography.nunitoBlack34px.copy(
                fontSize = 28.sp
            ),
            color = MaterialTheme.appColors.textPrimary,
        )
        Text(
            text = stringResource(R.string.feature_login_welcome_to_school, schoolDetails?.schoolName.orEmpty(), schoolDetails?.schAdd1?.capitalize().orEmpty()),
            style = MaterialTheme.appTypography.interRegular14px,
            color = MaterialTheme.appColors.textSecondary
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun HeaderSectionPreview() {
    HeaderSection(
        schoolDetails = null
    )
}
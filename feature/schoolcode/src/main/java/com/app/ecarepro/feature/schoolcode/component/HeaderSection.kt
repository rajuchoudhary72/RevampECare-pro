package com.app.ecarepro.feature.schoolcode.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.designsystem.core.component.EcareProBackground
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.schoolcode.R

@Composable
fun HeaderSection(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ecare_pro_white_logo),
            contentDescription = "Franciscan e-care Logo",
            modifier = Modifier.size(width = 128.dp, height = 41.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(32.dp))
        /* fontWeight = FontWeight.Bold,font*/
        Text(
            text = stringResource(R.string.unlock_your_franciscan_experience_title),
            style = MaterialTheme.appTypography.nunitoBlack34px.copy(fontSize = 28.sp),
            color = White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.unlock_your_franciscan_experience_subtitle),
            style = MaterialTheme.appTypography.interRegular14px,
            color = White,
        )
    }

}

@Preview
@Composable
fun HeaderSectionPreview() {
    EcareProTheme {
        EcareProBackground {
            HeaderSection()
        }
    }
}

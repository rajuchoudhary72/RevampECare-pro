package com.app.ecarepro.feature.login.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.designsystem.core.component.TextButton
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.login.R


@Composable
fun FooterSection(
    modifier: Modifier = Modifier,
    onChangeSchoolClicked: () -> Unit = {},
    onHelpClicked: () -> Unit = {},
) {

    val needHelpText = stringResource(R.string.feature_login_need_help)
    val clickHereText = stringResource(R.string.feature_login_click_here)

    val annotatedString = buildAnnotatedString {
        withStyle(
            style = MaterialTheme.appTypography.interRegular14px.toSpanStyle().copy(
                color = MaterialTheme.appColors.textSecondary
            ),
        ) {
            append(needHelpText)
        }

        pushLink(
            LinkAnnotation.Clickable(
                tag = "FIND_CODE_TAG",
                linkInteractionListener = { onHelpClicked() }
            )
        )

        withStyle(
            style = MaterialTheme.appTypography.interRegular14px.toSpanStyle().copy(
                textDecoration = TextDecoration.Underline,
                color = MaterialTheme.appColors.primary,
            )
        ) {
            append(clickHereText)
        }

        pop()
    }
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            TextButton(
                onClick = onChangeSchoolClicked,
                title = stringResource(R.string.feature_login_change_school),
                leadingIcon = R.drawable.ic_back_arrow,
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
            )

            Text(
                annotatedString
            )

        }

        Spacer(modifier = Modifier.weight(1f))

        Footer()
    }
}

@Composable
fun Footer(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.appColors.textPrimary
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.feature_login_powered_by),
            style = MaterialTheme.appTypography.interMedium16px.copy(fontSize = 14.sp),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 16.dp),
            color = color
        )
        Image(
            painter = painterResource(id = R.drawable.franciscan_logo),
            contentDescription = stringResource(R.string.feature_login_franciscan_logo_desc),
            modifier = Modifier.size(width = 96.dp, height = 16.dp),
            colorFilter = ColorFilter.tint(color)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FooterSectionPreview() {
    EcareProTheme {
        FooterSection()
    }
}
package com.app.ecarepro.feature.schoolcode.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appTypography


@Composable
fun FindCodeLink(onFindCodeClicked: () -> Unit) {
    val annotatedString = buildAnnotatedString {
        withStyle(
            style = MaterialTheme.appTypography.interRegular14px.toSpanStyle()
        ) {
            append("Didn't know your code? ") // stringResource(R.string.didnt_know_code)
        }


        // Use LinkAnnotation to make a portion of the text clickable
        pushLink(
            LinkAnnotation.Clickable(
                tag = "FIND_CODE_TAG",
                linkInteractionListener = { onFindCodeClicked() }
            )
        )



        withStyle(
            style = MaterialTheme.appTypography.interMedium16px.toSpanStyle().copy(
                textDecoration = TextDecoration.Underline,
                color = White,
                fontStyle = FontStyle.Italic
            )
        ) {
            append("Find here") // stringResource(R.string.find_here_link)
        }

        pop()
    }

    Text(
        text = annotatedString,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.fillMaxWidth(),
        color = White
    )
}
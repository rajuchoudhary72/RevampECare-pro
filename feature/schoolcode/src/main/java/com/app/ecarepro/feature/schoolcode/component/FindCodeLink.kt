package com.app.ecarepro.feature.schoolcode.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import com.app.ecarepro.feature.schoolcode.LinkColor

@Composable
fun FindCodeLink(onFindCodeClicked: () -> Unit) {
    val annotatedString = buildAnnotatedString {
        append("Didn't know your code? ") // stringResource(R.string.didnt_know_code)
        pushStringAnnotation(tag = "FIND_CODE", annotation = "find_code_link")
        withStyle(
            style = SpanStyle(
                color = LinkColor, fontWeight = FontWeight.Bold
            )
        ) { // Use MaterialTheme.colorScheme.primary or a custom link color
            append("Find here") // stringResource(R.string.find_here_link)
        }
        pop()
    }

    ClickableText(
        text = annotatedString,
        style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "FIND_CODE", start = offset, end = offset)
                .firstOrNull()?.let {
                    onFindCodeClicked()
                }
        },
        modifier = Modifier.fillMaxWidth(),
    )
}
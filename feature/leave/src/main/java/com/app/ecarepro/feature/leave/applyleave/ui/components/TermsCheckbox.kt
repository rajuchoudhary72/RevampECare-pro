package com.app.ecarepro.feature.leave.applyleave.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.leave.R

@Composable
fun TermsCheckbox(
    isAccepted: Boolean,
    error: String?,
    onToggle: () -> Unit,
    onTermsClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = isAccepted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.appColors.primary
                )
            )
            Spacer(modifier = Modifier.width(8.dp))

            val agreeTo = stringResource(R.string.feature_leave_agree_to)
            val termsLink = stringResource(R.string.feature_leave_terms_link)
            val annotatedText = buildAnnotatedString {
                append(agreeTo)
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.appColors.primary
                    )
                ) {
                    append(termsLink)
                }
            }

            Text(
                text = annotatedText,
                style = MaterialTheme.appTypography.interRegular14px,
                color = MaterialTheme.appColors.textPrimary,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .clickable { onTermsClick() }
            )
        }

        if (error != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = error,
                style = MaterialTheme.appTypography.interRegular12px,
                color = MaterialTheme.appColors.error,
                modifier = Modifier.padding(start = 48.dp)
            )
        }
    }
}

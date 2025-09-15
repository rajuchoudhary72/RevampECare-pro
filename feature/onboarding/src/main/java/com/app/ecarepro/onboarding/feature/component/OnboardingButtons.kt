package com.app.ecarepro.onboarding.feature.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.component.Button
import com.app.ecarepro.designsystem.core.component.TextButton
import com.app.ecarepro.designsystem.core.theme.EcareProTheme

@Composable
fun OnboardingButtons(
    modifier: Modifier = Modifier,
    isLastPage: Boolean,
    onSkipClicked: () -> Unit,
    onNextClicked: () -> Unit,
    onAddSchoolClicked: () -> Unit // For the last page
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        AnimatedVisibility(
            visible = isLastPage.not()
        ) {
            TextButton(
                title = "Skip",
                onClick = onSkipClicked
            )
        }

        Spacer(Modifier.weight(1f))

        Button(
            modifier = Modifier.width(148.dp),
            title = if (isLastPage) "Add School" else "Next",
            onClick = if (isLastPage) onAddSchoolClicked else onNextClicked,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingButtonsPreview() {
    EcareProTheme {
        OnboardingButtons(
            isLastPage = false,
            onSkipClicked = {},
            onNextClicked = {},
            onAddSchoolClicked = {}
        )
    }

}

@Preview(showBackground = true)
@Composable
fun OnboardingButtonsLastPagePreview() {
    EcareProTheme {
        OnboardingButtons(
            isLastPage = true,
            onSkipClicked = {},
            onNextClicked = {},
            onAddSchoolClicked = {}
        )
    }

}

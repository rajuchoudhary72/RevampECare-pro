package com.app.ecarepro.onboarding.feature.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.onboarding.feature.OnboardingView
import kotlinx.serialization.Serializable

@Serializable
sealed interface OnboardingNavigationGraph : NavKey {
    @Serializable
    data object Onboarding : OnboardingNavigationGraph
}


@Composable
fun EntryProviderBuilder<NavKey>.Onboarding(
    navigateToAddSchool: () -> Unit
) {
    entry<OnboardingNavigationGraph.Onboarding> {
        OnboardingView(
            navigateToAddSchool = navigateToAddSchool
        )
    }
}

package v2.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.app.ecarepro.onboarding.feature.navigation.Onboarding
import com.app.ecarepro.onboarding.feature.navigation.OnboardingNavigationGraph

@Composable
fun EcareProNavDisplay(
    navigateToLegacyFlow: (LegacyNavigationDestination) -> Unit
) {
    val backStack = remember { mutableStateListOf<NavKey>(OnboardingNavigationGraph.Onboarding) }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            Onboarding(navigateToAddSchool = {
                navigateToLegacyFlow(LegacyNavigationDestination.AddSchool)
            })
        })
}
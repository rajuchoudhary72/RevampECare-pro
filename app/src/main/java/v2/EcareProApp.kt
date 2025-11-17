package v2

import androidx.compose.runtime.Composable
import v2.navigation.ComposeNavigationDestination
import v2.navigation.EcareProNavDisplay
import v2.navigation.LegacyNavigationDestination

@Composable
fun ECateProApp(
    navigateToLegacyFlow: (LegacyNavigationDestination) -> Unit,
    startDestination: ComposeNavigationDestination?
) {
    EcareProNavDisplay(
        startDestination = startDestination,
        navigateToLegacyFlow = navigateToLegacyFlow
    )
}
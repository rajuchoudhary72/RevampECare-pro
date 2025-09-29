package v2

import androidx.compose.runtime.Composable
import v2.navigation.EcareProNavDisplay
import v2.navigation.LegacyNavigationDestination

@Composable
fun ECateProApp(
    navigateToLegacyFlow: (LegacyNavigationDestination) -> Unit
) {
    EcareProNavDisplay(
        navigateToLegacyFlow = navigateToLegacyFlow
    )
}
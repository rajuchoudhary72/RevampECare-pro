package v2.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.scene.rememberSceneSetupNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.app.ecarepro.feature.dashboard.navigation.DashboardNavigationGraph
import com.app.ecarepro.feature.dashboard.navigation.EntryDashboardNavigation
import com.app.ecarepro.feature.login.navigation.EntryLoginNavigation
import com.app.ecarepro.feature.login.navigation.LoginNavigationGraph
import com.app.ecarepro.feature.schoolcode.navigation.EntrySchoolCodeNavigation
import com.app.ecarepro.feature.schoolcode.navigation.SchoolCodeNavigationGraph
import com.app.ecarepro.onboarding.feature.navigation.EntryOnboardingNavigation
import com.app.ecarepro.onboarding.feature.navigation.OnboardingNavigationGraph

@Composable
fun EcareProNavDisplay(
    navigateToLegacyFlow: (LegacyNavigationDestination) -> Unit,
) {
    val backStack = remember { mutableStateListOf<NavKey>(OnboardingNavigationGraph.Onboarding) }

    NavDisplay(
        entryDecorators = listOf(
            // Add the default decorators for managing scenes and saving state
            rememberSceneSetupNavEntryDecorator(),
            rememberSavedStateNavEntryDecorator(),
            // Then add the view model store decorator
            rememberViewModelStoreNavEntryDecorator()
        ),
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {

            EntryOnboardingNavigation(navigateToAddSchool = {
                backStack.removeLastOrNull()
                backStack.add(SchoolCodeNavigationGraph.SchoolCode)
            })

            EntrySchoolCodeNavigation(
                backStack = backStack,
                navigateToLogin = { schoolCode ->
                    backStack.add(LoginNavigationGraph.Login(schoolCode = schoolCode))
                }
            )

            EntryLoginNavigation(
                backStack = backStack,
                backToSchoolCode = {
                    backStack.removeLastOrNull()
                },
                navigateToMain = { user ->
                    navigateToLegacyFlow(LegacyNavigationDestination.Main(user))
                }
            )

            EntryDashboardNavigation()
        }
    )
}
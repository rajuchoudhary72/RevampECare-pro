package v2.navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.scene.rememberSceneSetupNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.app.ecarepro.core.domain.model.User
import com.app.ecarepro.feature.dashboard.navigation.EntryDashboardNavigation
import com.app.ecarepro.feature.login.navigation.EntryLoginNavigation
import com.app.ecarepro.feature.login.navigation.LoginNavigationGraph
import com.app.ecarepro.feature.schoolcode.navigation.EntrySchoolCodeNavigation
import com.app.ecarepro.feature.schoolcode.navigation.SchoolCodeNavigationGraph
import com.app.ecarepro.feature.splash.navigation.EntrySplashNavigation
import com.app.ecarepro.feature.splash.navigation.SplashNavigationGraph
import com.app.ecarepro.feature.timetable.navigation.EntryTimetableNavigation
import com.app.ecarepro.onboarding.feature.navigation.EntryOnboardingNavigation
import com.app.ecarepro.onboarding.feature.navigation.OnboardingNavigationGraph

@Composable
fun EcareProNavDisplay(
    navigateToLegacyFlow: (LegacyNavigationDestination) -> Unit,
    startDestination: ComposeNavigationDestination?,
) {
    val backStack = remember {
        val initialKey = startDestination?.startKey ?: SplashNavigationGraph.Splash
        mutableStateListOf(initialKey)
    }

    val context = LocalContext.current as Activity

    NavDisplay(
        entryDecorators = listOf(
            rememberSceneSetupNavEntryDecorator(),
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        backStack = backStack,
        onBack = {
            if(backStack.size == 1){
                backStack.removeLastOrNull()
            }else{
                context.finish()
            }
        },
        entryProvider = entryProvider {

            EntrySplashNavigation(
                navigateToLogin = {
                    backStack.clear()
                    backStack.add(OnboardingNavigationGraph.Onboarding)
                },
                navigateToDashboard = { user ->
                    navigateToLegacyFlow(navigateToLegacyFlow, user)
                }
            )


            EntryOnboardingNavigation(
                navigateToAddSchool = {
                    backStack.clear()
                    backStack.add(SchoolCodeNavigationGraph.SchoolCode)
                }
            )


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
                    navigateToLegacyFlow(navigateToLegacyFlow, user)
                }
            )

            EntryDashboardNavigation()

            EntryTimetableNavigation(
                navigateToBack = {
                    backStack.removeLastOrNull()
                }
            )
        }
    )
}


private fun navigateToLegacyFlow(
    navigateToLegacyFlow: (LegacyNavigationDestination) -> Unit,
    user: User,
) {
    navigateToLegacyFlow(LegacyNavigationDestination.Main(user))
}
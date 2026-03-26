package com.app.ecarepro.feature.splash.navigation


import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.core.domain.model.User
import com.app.ecarepro.feature.splash.SplashScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface SplashNavigationGraph : NavKey {
    @Serializable
    data object Splash : SplashNavigationGraph
}


@Composable
fun EntryProviderBuilder<NavKey>.EntrySplashNavigation(
    navigateToLogin: () -> Unit,
    navigateToDashboard: (User) -> Unit,
) {
    entry<SplashNavigationGraph.Splash> {
        SplashScreen(
            navigateToLogin = navigateToLogin,
            navigateToDashboard = navigateToDashboard
        )
    }
}

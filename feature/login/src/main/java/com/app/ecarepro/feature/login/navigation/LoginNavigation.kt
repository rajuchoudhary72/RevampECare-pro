package com.app.ecarepro.feature.login.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.core.domain.model.User
import com.app.ecarepro.core.ui.viewmodel.navKeyViewModel
import com.app.ecarepro.feature.homeselection.navigation.EntryHomeSelectionNavigation
import com.app.ecarepro.feature.homeselection.navigation.HomeSelectionNavigationGraph
import com.app.ecarepro.feature.login.LoginScreen
import com.app.ecarepro.feature.login.LoginViewModel
import kotlinx.serialization.Serializable

@Serializable
sealed interface LoginNavigationGraph : NavKey {
    @Serializable
    data class Login(val schoolCode: String) : LoginNavigationGraph
}


@Composable
fun EntryProviderBuilder<NavKey>.EntryLoginNavigation(
    backStack: SnapshotStateList<NavKey>,
    backToSchoolCode: () -> Unit = {},
    navigateToForgotPassword: () -> Unit = {},
    navigateToHelp: () -> Unit = {},
    navigateToMain: (User) -> Unit = {},
) {
    var activeUser: User? = null
    entry<LoginNavigationGraph.Login> { navKey ->
        val viewModel: LoginViewModel = navKeyViewModel(navKey)

        LoginScreen(
            viewModel = viewModel,
            backToSchoolCode = backToSchoolCode,
            navigateToForgotPassword = navigateToForgotPassword,
            navigateToHelp = navigateToHelp,
            selectHomeScreenType = { user ->
                activeUser = user
                backStack.add(HomeSelectionNavigationGraph.HomeSelection)
            },
            navigateToBack = {
                backStack.removeLastOrNull()
            }
        )
    }

    EntryHomeSelectionNavigation(
        onComplete = {
            activeUser?.let { navigateToMain(it) }
            backStack.removeLastOrNull()
        }
    )

}
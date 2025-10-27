package com.app.ecarepro.feature.login.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.core.domain.model.User
import com.app.ecarepro.core.ui.viewmodel.navKeyViewModel
import com.app.ecarepro.feature.login.LoginScreen
import com.app.ecarepro.feature.login.LoginViewModel
import kotlinx.serialization.Serializable
import com.app.ecarepro.feature.homeselection.navigation.EntryHomeSelectionNavigation
import com.app.ecarepro.feature.homeselection.navigation.HomeSelectionNavigationGraph
import androidx.compose.runtime.snapshots.SnapshotStateList


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
    entry<LoginNavigationGraph.Login> { navKey ->
        val viewModel: LoginViewModel = navKeyViewModel(navKey)
        var activeUser: User? = null


        LoginScreen(
            viewModel = viewModel,
            backToSchoolCode = backToSchoolCode,
            navigateToForgotPassword = navigateToForgotPassword,
            navigateToHelp = navigateToHelp,
            selectHomeScreenType = { user ->
                activeUser = user
                backStack.add(HomeSelectionNavigationGraph.HomeSelection)
            },
        )
    }
    EntryHomeSelectionNavigation(
        onComplete = {
            activeUser?.let { navigateToMain(it) }
            backStack.removeLastOrNull()
        }
    )
}
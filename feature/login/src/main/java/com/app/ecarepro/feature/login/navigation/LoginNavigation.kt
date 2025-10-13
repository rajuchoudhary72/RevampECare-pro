package com.app.ecarepro.feature.login.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.core.domain.model.User
import com.app.ecarepro.core.ui.viewmodel.navKeyViewModel
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
    backToSchoolCode: () -> Unit = {},
    navigateToForgotPassword: () -> Unit = {},
    navigateToHelp: () -> Unit = {},
    navigateToMain: (User) -> Unit = {},
) {
    entry<LoginNavigationGraph.Login> { navKey ->
        val viewModel: LoginViewModel = navKeyViewModel(navKey)
        LoginScreen(
            viewModel = viewModel,
            backToSchoolCode = backToSchoolCode,
            navigateToForgotPassword = navigateToForgotPassword,
            navigateToHelp = navigateToHelp,
            navigateToMain = navigateToMain,
        )
    }
}
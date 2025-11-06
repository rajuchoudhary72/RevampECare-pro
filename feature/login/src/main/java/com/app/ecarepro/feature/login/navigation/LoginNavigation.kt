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
import com.app.ecarepro.feature.login.screens.forgotpassword.ForgotPasswordScreen
import com.app.ecarepro.feature.login.screens.forgotpassword.ForgotPasswordViewModel
import com.app.ecarepro.feature.login.screens.help.HelpScreen
import com.app.ecarepro.feature.login.screens.help.HelpViewModel
import com.app.ecarepro.feature.login.screens.otp.OtpScreen
import com.app.ecarepro.feature.login.screens.otp.OtpViewModel
import kotlinx.serialization.Serializable

@Serializable
sealed interface LoginNavigationGraph : NavKey {
    @Serializable
    data class Login(val schoolCode: String) : LoginNavigationGraph

    @Serializable
    data class OtpVerification(
        val schoolCode: String,
        val username: String,
        val otpAuthKey: String,
        val message: String,
    ) : LoginNavigationGraph


    @Serializable
    data class ForgotPassword(
        val schoolCode: String,
        val isStudentLoginBlocked: Boolean,
    ) : LoginNavigationGraph

    @Serializable
    data class Help(
        val schoolCode: String,
    ) : LoginNavigationGraph

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
            navigateToForgotPassword = { schoolCode, isStudentLoginBlocked ->
                backStack.add(
                    LoginNavigationGraph.ForgotPassword(schoolCode, isStudentLoginBlocked)
                )
            },
            navigateToHelp = { schoolCode ->
                backStack.add(LoginNavigationGraph.Help(schoolCode))
            },
            selectHomeScreenType = { user ->
                activeUser = user
                backStack.add(HomeSelectionNavigationGraph.HomeSelection)
            },
            navigateToBack = {
                backStack.removeLastOrNull()
            },
            navigateToOtpVerification = { schoolCode, userName, loginResult ->
                backStack.add(
                    LoginNavigationGraph.OtpVerification(
                        schoolCode = schoolCode,
                        username = userName,
                        otpAuthKey = loginResult.otpAuthKey.orEmpty(),
                        message = loginResult.message
                    )
                )
            }
        )
    }


    entry<LoginNavigationGraph.OtpVerification> { navKey ->
        val viewModel: OtpViewModel = navKeyViewModel(navKey)
        OtpScreen(
            viewModel = viewModel,
            navigateToBack = {
                backStack.removeLastOrNull()
            },
            onOtpVerificationComplete = { user ->
                activeUser = user
                backStack.add(HomeSelectionNavigationGraph.HomeSelection)
            }
        )
    }

    entry<LoginNavigationGraph.ForgotPassword> { navKey ->
        val viewModel: ForgotPasswordViewModel = navKeyViewModel(navKey)
        ForgotPasswordScreen(
            viewModel = viewModel,
            navigateToBack = {
                backStack.removeLastOrNull()
            }
        )
    }

    entry<LoginNavigationGraph.Help> { navKey ->
        val viewModel: HelpViewModel = navKeyViewModel(navKey)
        HelpScreen(
            viewModel = viewModel,
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
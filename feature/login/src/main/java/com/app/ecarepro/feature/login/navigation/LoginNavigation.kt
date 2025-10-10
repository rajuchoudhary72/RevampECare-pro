package com.app.ecarepro.feature.login.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.feature.login.LoginScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface LoginNavigationGraph : NavKey {
    @Serializable
    data class Login(val schoolCode: String) : LoginNavigationGraph
}


@Composable
fun EntryProviderBuilder<NavKey>.Login(
    backToSchoolCode: () -> Unit = {},
) {
    entry<LoginNavigationGraph.Login> {
        LoginScreen(
            schoolCode = it.schoolCode,
            backToSchoolCode = backToSchoolCode
        )
    }
}
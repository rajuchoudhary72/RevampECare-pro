package com.app.ecarepro.feature.profile.navigation

import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.feature.profile.edit_profile.EditProfileScreen
import com.app.ecarepro.feature.profile.profile.MyProfileScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface MyProfileNavGraph : NavKey {
    @Serializable data object Profile : MyProfileNavGraph
    @Serializable data object EditProfile : MyProfileNavGraph
}

fun EntryProviderBuilder<NavKey>.entryMyProfileNavigation(
    navigateBack: () -> Unit,
    navigateTo: (NavKey) -> Unit,
    navigateToLogin: () -> Unit,
    navigateToAddAccount: () -> Unit,
    onRestartApp: () -> Unit,
) {
    entry<MyProfileNavGraph.Profile> {
        MyProfileScreen(
            navigateBack = navigateBack,
            navigateToEdit = { navigateTo(MyProfileNavGraph.EditProfile) },
            navigateToLogin = navigateToLogin,
            navigateToAddAccount = navigateToAddAccount,
            onRestartApp = onRestartApp,
        )
    }
    entry<MyProfileNavGraph.EditProfile> {
        EditProfileScreen(navigateBack = navigateBack)
    }
}

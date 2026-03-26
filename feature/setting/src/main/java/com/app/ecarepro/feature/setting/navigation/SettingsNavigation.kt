package com.app.ecarepro.feature.setting.navigation

import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.feature.setting.change_language.ChangeLanguageScreen
import com.app.ecarepro.feature.setting.change_password.ChangePasswordScreen
import com.app.ecarepro.feature.setting.change_username.ChangeUsernameScreen
import com.app.ecarepro.feature.setting.settings_main.SettingsScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface SettingsNavGraph : NavKey {
    @Serializable data object SettingsMain : SettingsNavGraph
    @Serializable data object ChangeUsername : SettingsNavGraph
    @Serializable data object ChangePassword : SettingsNavGraph
    @Serializable data object ChangeLanguage : SettingsNavGraph
}

fun EntryProviderBuilder<NavKey>.entrySettingsNavigation(
    navigateBack: () -> Unit,
    navigateTo: (NavKey) -> Unit,
    onLogout: () -> Unit,
    onRestartActivity: () -> Unit,
) {
    entry<SettingsNavGraph.SettingsMain> {
        SettingsScreen(
            navigateBack = navigateBack,
            navigateTo = navigateTo,
        )
    }
    entry<SettingsNavGraph.ChangeUsername> {
        ChangeUsernameScreen(
            navigateBack = navigateBack,
            onLogout = onLogout,
        )
    }
    entry<SettingsNavGraph.ChangePassword> {
        ChangePasswordScreen(
            navigateBack = navigateBack,
            onLogout = onLogout,
        )
    }
    entry<SettingsNavGraph.ChangeLanguage> {
        ChangeLanguageScreen(
            navigateBack = navigateBack,
            onRestartActivity = onRestartActivity,
        )
    }
}

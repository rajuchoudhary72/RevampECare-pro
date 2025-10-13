package com.app.ecarepro.designsystem.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import com.app.ecarepro.designsystem.core.model.AppColors
import com.app.ecarepro.designsystem.core.model.AppTypography

val EcareProLightColor = AppColors(
    primary = EmeraldGreen,
    accent = SoftGreen,
    background = OffWhite,
    surface = Charcoal,
    divider = LightGrey,
    border = LightGrey,
    textPrimary = CharcoalText,
    textSecondary = LightGreyText,
    complementary = CoralOrange,
    success = FreshGreen,
    info = SkyBlue,
    warning = AmberOrange,
    error = TomatoRed
)

val EcareProDarkColor = AppColors(
    primary = LimeGreen,
    accent = SoftGreen,
    background = JetBlack,
    surface = SlateBlack,
    divider = DividerGray,
    border = DividerGray,
    textPrimary = LightGrey,
    textSecondary = MutedGray,
    complementary = CoralOrange,
    success = BrightFreshGreen,
    info = SkyCyan,
    warning = AmberOrange,
    error = LightRed
)

val LocalAppColors = staticCompositionLocalOf<AppColors> {
    error("No AppColors provided")
}
val LocalAppTypography = staticCompositionLocalOf<AppTypography> {
    error("No AppColors provided")
}

val MaterialTheme.appColors: AppColors
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current

val MaterialTheme.appTypography: AppTypography
    @Composable
    @ReadOnlyComposable
    get() = LocalAppTypography.current

@Composable
fun EcareProTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val customColorScheme = if (darkTheme) EcareProDarkColor else EcareProLightColor

    val materialColorScheme = if (darkTheme) {
        darkColorScheme(
            primary = customColorScheme.primary,
            secondary = customColorScheme.accent,
            background = customColorScheme.background,
            surface = customColorScheme.surface,
            onPrimary = customColorScheme.textPrimary,
            onSecondary = customColorScheme.textPrimary,
            onBackground = customColorScheme.textPrimary,
            onSurface = customColorScheme.textPrimary,
            error = customColorScheme.error,
            onError = customColorScheme.textPrimary,
        )
    } else {
        lightColorScheme(
            primary = customColorScheme.primary,
            secondary = customColorScheme.accent,
            background = customColorScheme.background,
            surface = customColorScheme.surface,
            onPrimary = customColorScheme.textPrimary,
            onSecondary = customColorScheme.textPrimary,
            onBackground = customColorScheme.textPrimary,
            onSurface = customColorScheme.textPrimary,
            error = customColorScheme.error,
            onError = customColorScheme.textPrimary,
        )
    }

    CompositionLocalProvider(
        LocalAppColors provides customColorScheme,
        LocalAppTypography provides EcareProTypography
    ) {
        MaterialTheme(
            colorScheme = materialColorScheme,
            typography = MaterialTheme.typography,
            content = content
        )
    }
}

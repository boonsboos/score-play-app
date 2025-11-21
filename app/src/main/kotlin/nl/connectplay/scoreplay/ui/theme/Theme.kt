package nl.connectplay.scoreplay.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * Define the real color scheme for light mode.
 */
private val LightColorScheme = lightColorScheme(
    primary = LightColors.Primary,
    onPrimary = LightColors.OnPrimary,
    primaryContainer = LightColors.PrimaryContainer,
    onPrimaryContainer = LightColors.OnPrimaryContainer,
    secondary = LightColors.Secondary,
    onSecondary = LightColors.OnSecondary,
    secondaryContainer = LightColors.SecondaryContainer,
    onSecondaryContainer = LightColors.OnSecondaryContainer,
    tertiary = LightColors.Tertiary,
    onTertiary = LightColors.OnTertiary,
    tertiaryContainer = LightColors.TertiaryContainer,
    onTertiaryContainer = LightColors.OnTertiaryContainer,
    background = LightColors.Background,
    onBackground = LightColors.OnBackground,
    surface = LightColors.Surface,
    onSurface = LightColors.OnSurface,
    surfaceDim = LightColors.SurfaceDim,
    surfaceBright = LightColors.SurfaceBright,
    surfaceContainerLowest = LightColors.SurfaceContainerLowest,
    surfaceContainerLow = LightColors.SurfaceContainerLow,
    surfaceContainer = LightColors.SurfaceContainer,
    surfaceContainerHigh = LightColors.SurfaceContainerHigh,
    surfaceContainerHighest = LightColors.SurfaceContainerHighest,
    outline = LightColors.Outline,
    outlineVariant = LightColors.OutlineVariant,
    error = LightColors.Error,
    onError = LightColors.OnError,
    errorContainer = LightColors.ErrorContainer,
    onErrorContainer = LightColors.OnErrorContainer,
)

/**
 * Define the real color scheme for dark mode.
 */
private val DarkColorScheme = darkColorScheme(
    primary = DarkColors.Primary,
    onPrimary = DarkColors.OnPrimary,
    primaryContainer = DarkColors.PrimaryContainer,
    onPrimaryContainer = DarkColors.OnPrimaryContainer,
    secondary = DarkColors.Secondary,
    onSecondary = DarkColors.OnSecondary,
    secondaryContainer = DarkColors.SecondaryContainer,
    onSecondaryContainer = DarkColors.OnSecondaryContainer,
    tertiary = DarkColors.Tertiary,
    onTertiary = DarkColors.OnTertiary,
    tertiaryContainer = DarkColors.TertiaryContainer,
    onTertiaryContainer = DarkColors.OnTertiaryContainer,
    background = DarkColors.Background,
    onBackground = DarkColors.OnBackground,
    surface = DarkColors.Surface,
    onSurface = DarkColors.OnSurface,
    surfaceDim = DarkColors.SurfaceDim,
    surfaceBright = DarkColors.SurfaceBright,
    surfaceContainerLowest = DarkColors.SurfaceContainerLowest,
    surfaceContainerLow = DarkColors.SurfaceContainerLow,
    surfaceContainer = DarkColors.SurfaceContainer,
    surfaceContainerHigh = DarkColors.SurfaceContainerHigh,
    surfaceContainerHighest = DarkColors.SurfaceContainerHighest,
    outline = DarkColors.Outline,
    outlineVariant = DarkColors.OutlineVariant,
    error = DarkColors.Error,
    onError = DarkColors.OnError,
    errorContainer = DarkColors.ErrorContainer,
    onErrorContainer = DarkColors.OnErrorContainer,
)

@Composable
/**
 * Define an extra color that is not in the default MaterialTheme.colorScheme object,
 * based on the current theme (light or dark).
 */
fun defineExtraColor(light: Color, dark: Color): Color = if (isSystemInDarkTheme()) dark else light

/**
 * Example of defining an extra color in the ColorScheme.
 */
val ColorScheme.exampleColor: Color
    @Composable get() = defineExtraColor(
        light = Color(0xFF69F420),
        dark = Color(0xFF690420),
    )

@Composable
/**
 * With this function, we set the theme for the app, choosing between light and dark color schemes.
 * To use our custom colors you don't have to do anything special, just do for example: `MaterialTheme.colorScheme.primary`
 */
fun ScorePlayTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
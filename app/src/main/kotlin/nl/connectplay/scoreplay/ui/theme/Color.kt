package nl.connectplay.scoreplay.ui.theme

import androidx.compose.ui.graphics.Color


/**
 * The light color scheme for the app.
 */
object LightColors {
    // Primary — brand purple
    val Primary = Color.hsv(281f, 0.80f, 0.45f)
    val OnPrimary = Color.White
    val PrimaryContainer = Color.hsv(281f, 0.35f, 0.92f)
    val OnPrimaryContainer = Color(0xFF2B0034)

    // Secondary — supporting lavender
    val Secondary = Color.hsv(281f, 0.35f, 0.65f)
    val OnSecondary = Color(0xFF2A1A2E)
    val SecondaryContainer = Color.hsv(281f, 0.20f, 0.90f)
    val OnSecondaryContainer = Color(0xFF241428)

    // Tertiary — gold accent
    val Tertiary = Color.hsv(54f, 0.70f, 0.85f)
    val OnTertiary = Color(0xFF211C00)
    val TertiaryContainer = Color.hsv(54f, 0.30f, 0.93f)
    val OnTertiaryContainer = Color(0xFF211C00)

    val Background = Color(0xFFFFFBFF)
    val OnBackground = Color(0xFF1C1B1F)

    val Surface = Color(0xFFFFFBFF)
    val OnSurface = Color(0xFF1C1B1F)
    val OnSurfaceVariant = Color(0xFF49454F)

    val SurfaceDim = Color(0xFFE6E1E8)
    val SurfaceBright = Color(0xFFFFFBFF)

    val SurfaceContainerLowest = Color(0xFFFFFFFF)
    val SurfaceContainerLow = Color(0xFFF7F2FA)
    val SurfaceContainer = Color(0xFFF2EDF6)
    val SurfaceContainerHigh = Color(0xFFECE6F0)
    val SurfaceContainerHighest = Color(0xFFE6E0EA)

    val Outline = Color(0xFF7C757E)
    val OutlineVariant = Color(0xFFC8C2CE)
}

/**
 * The dark color scheme for the app.
 */
object DarkColors {
    // Primary — lifted for dark theme
    val Primary = Color.hsv(281f, 0.60f, 0.80f)
    val OnPrimary = Color(0xFF3A0046)
    val PrimaryContainer = Color.hsv(281f, 0.40f, 0.30f)
    val OnPrimaryContainer = Color.hsv(281f, 0.25f, 0.90f)

    // Secondary
    val Secondary = Color.hsv(281f, 0.30f, 0.75f)
    val OnSecondary = Color(0xFF2A1A2E)
    val SecondaryContainer = Color.hsv(281f, 0.20f, 0.25f)
    val OnSecondaryContainer = Color.hsv(281f, 0.15f, 0.90f)

    // Tertiary
    val Tertiary = Color.hsv(54f, 0.55f, 0.80f)
    val OnTertiary = Color(0xFF2F2600)
    val TertiaryContainer = Color.hsv(54f, 0.30f, 0.28f)
    val OnTertiaryContainer = Color.hsv(54f, 0.25f, 0.90f)

    val Background = Color(0xFF1C1B1F)
    val OnBackground = Color(0xFFE6E1E5)

    val Surface = Color(0xFF1C1B1F)
    val OnSurface = Color(0xFFE6E1E5)
    val OnSurfaceVariant = Color(0xFFCAC4D0)

    val SurfaceDim = Color(0xFF141318)
    val SurfaceBright = Color(0xFF3A383E)

    val SurfaceContainerLowest = Color(0xFF0F0E13)
    val SurfaceContainerLow = Color(0xFF1D1B22)
    val SurfaceContainer = Color(0xFF211F26)
    val SurfaceContainerHigh = Color(0xFF2B2931)
    val SurfaceContainerHighest = Color(0xFF36343C)

    val Outline = Color(0xFF938F99)
    val OutlineVariant = Color(0xFF49454F)
}
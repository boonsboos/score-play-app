package nl.connectplay.scoreplay.ui.theme

import androidx.compose.ui.graphics.Color


/**
 * The light color scheme for the app.
 */
object LightColors {
    val Primary = Color.hsv(281f, 1f, 0.43f)
    val OnPrimary = Color.White
    val PrimaryContainer = Color.hsv(281f, 0.35f, 0.90f)
    val OnPrimaryContainer = Color(0xFF2B0034)

    val Secondary = Color.hsv(281f, 0.50f, 0.80f)
    val OnSecondary = Color.White
    val SecondaryContainer = Color.hsv(281f, 0.25f, 0.95f)
    val OnSecondaryContainer = Color(0xFF1A0020)

    val Tertiary = Color.hsv(54f, 0.79f, 0.84f)
    val OnTertiary = Color.Black
    val TertiaryContainer = Color.hsv(54f, 0.30f, 0.95f)
    val OnTertiaryContainer = Color(0xFF211C00)

    val Background = Color(0xFFFFFBFF)
    val OnBackground = Color(0xFF1C1B1F)

    val Surface = Color(0xFFFFFBFF)
    val OnSurface = Color(0xFF1C1B1F)

    val SurfaceDim = Color(0xFFE6E1E5)
    val SurfaceBright = Color(0xFFFFFBFF)

    val SurfaceContainerLowest = Color(0xFFFFFFFF)
    val SurfaceContainerLow = Color(0xFFF7F2F7)
    val SurfaceContainer = Color(0xFFF3EDF4)
    val SurfaceContainerHigh = Color(0xFFECE6EC)
    val SurfaceContainerHighest = Color(0xFFE6E0E6)

    val Outline = Color(0xFF7C757E)
    val OutlineVariant = Color(0xFFC7C2CA)

    val Error = Color(0xFFBA1A1A)
    val OnError = Color.White
    val ErrorContainer = Color(0xFFFFDAD6)
    val OnErrorContainer = Color(0xFF410002)
}

/**
 * The dark color scheme for the app.
 */
object DarkColors {
    val Primary = Color.hsv(281f, 1f, 0.43f)
    val OnPrimary = Color.White
    val PrimaryContainer = Color.hsv(281f, 0.35f, 0.30f)
    val OnPrimaryContainer = Color.White

    val Secondary = Color.hsv(281f, 0.50f, 0.80f)
    val OnSecondary = Color.Black
    val SecondaryContainer = Color.hsv(281f, 0.25f, 0.25f)
    val OnSecondaryContainer = Color.White

    val Tertiary = Color.hsv(54f, 0.79f, 0.84f)
    val OnTertiary = Color.Black
    val TertiaryContainer = Color.hsv(54f, 0.30f, 0.25f)
    val OnTertiaryContainer = Color.White

    val Background = Color(0xFF1C1B1F)
    val OnBackground = Color(0xFFE6E1E5)

    val Surface = Color(0xFF1C1B1F)
    val OnSurface = Color(0xFFE6E1E5)

    val SurfaceDim = Color(0xFF141316)
    val SurfaceBright = Color(0xFF3A383E)

    val SurfaceContainerLowest = Color(0xFF0F0E11)
    val SurfaceContainerLow = Color(0xFF1D1B20)
    val SurfaceContainer = Color(0xFF211F24)
    val SurfaceContainerHigh = Color(0xFF2B292E)
    val SurfaceContainerHighest = Color(0xFF36343A)

    val Outline = Color(0xFF938F99)
    val OutlineVariant = Color(0xFF49454F)

    val Error = Color(0xFFFFB4AB)
    val OnError = Color(0xFF690005)
    val ErrorContainer = Color(0xFF93000A)
    val OnErrorContainer = Color(0xFFFFDAD6)
}
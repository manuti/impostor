package com.impostor.game.ui.theme

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8B5CF6),
    secondary = Color(0xFFEC4899),
    background = Color(0xFF0F0F16),
    surface = Color(0xFF17171F),
    surfaceVariant = Color(0xFF26262F),
    onPrimary = Color.White,
    onBackground = Color(0xFFF3F4F6),
    onSurface = Color(0xFFF3F4F6),
    onSurfaceVariant = Color(0xFFC6C9D0),
    primaryContainer = Color(0xFF4C1D95),
    onPrimaryContainer = Color(0xFFEDE9FE),
    error = Color(0xFFFF8A80),
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF6D28D9),
    secondary = Color(0xFFBE185D),
    background = Color(0xFFFDFDFD),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFEDEDF2),
    onPrimary = Color.White,
    onBackground = Color(0xFF111111),
    onSurface = Color(0xFF111111),
    onSurfaceVariant = Color(0xFF3F3F46),
    primaryContainer = Color(0xFFEDE9FE),
    onPrimaryContainer = Color(0xFF2E1065),
    error = Color(0xFFB91C1C),
)

/**
 * Colores semánticos del juego (alto contraste y aptos para daltonismo:
 * el color nunca es el único canal, siempre con icono + texto + forma).
 */
data class GameColors(
    val impostor: Color,      // acento del impostor
    val impostorBg: Color,    // fondo de la tarjeta de rol del impostor
    val onImpostorBg: Color,
    val civil: Color,         // acento de los civiles
    val civilBg: Color,       // fondo de la tarjeta de rol del civil
    val onCivilBg: Color,
    val success: Color,
    val warn: Color,
    val cardFrontBg: Color,   // anverso del naipe
    val cardFrontBorder: Color,
    val eye: Color,           // forma del ojo (anverso del naipe)
    val eyePupil: Color,      // pupila del ojo (contraste garantizado sobre eye)
)

private val DarkGameColors = GameColors(
    impostor = Color(0xFFFF8A80),
    impostorBg = Color(0xFF7F1D1D),
    onImpostorBg = Color.White,
    civil = Color(0xFF90CAF9),
    civilBg = Color(0xFF1E3A8A),
    onCivilBg = Color.White,
    success = Color(0xFF66E09A),
    warn = Color(0xFFF5C94A),
    cardFrontBg = Color(0xFF17171F),
    cardFrontBorder = Color(0xFF8B5CF6),
    eye = Color(0xFFF3F4F6),
    eyePupil = Color(0xFF312E81),
)

private val LightGameColors = GameColors(
    impostor = Color(0xFFB91C1C),
    impostorBg = Color(0xFFFDE8E8),
    onImpostorBg = Color(0xFF7F1D1D),
    civil = Color(0xFF1D4ED8),
    civilBg = Color(0xFFE8F0FE),
    onCivilBg = Color(0xFF1E3A8A),
    success = Color(0xFF15803D),
    warn = Color(0xFFB45309),
    cardFrontBg = Color(0xFFFFFFFF),
    cardFrontBorder = Color(0xFF6D28D9),
    eye = Color(0xFF312E81),
    eyePupil = Color(0xFFF59E0B),
)

val LocalGameColors = staticCompositionLocalOf { DarkGameColors }

/** Colores semánticos del juego según el tema activo. */
val MaterialTheme.gameColors: GameColors
    @Composable
    @ReadOnlyComposable
    get() = LocalGameColors.current

/** Tema actual (true = oscuro) y callback para cambiarlo, expuestos a las pantallas. */
val LocalDarkTheme = staticCompositionLocalOf { true }
val LocalOnToggleTheme = staticCompositionLocalOf<() -> Unit> { {} }

// Tipografía grande y legible: contenido >= 16sp (fase 2, usabilidad).
private val AppTypography = Typography(
    headlineLarge = TextStyle(fontSize = 36.sp, fontWeight = FontWeight.Bold, lineHeight = 40.sp),
    headlineMedium = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold, lineHeight = 36.sp),
    headlineSmall = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, lineHeight = 32.sp),
    titleLarge = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.SemiBold, lineHeight = 28.sp),
    titleMedium = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.SemiBold, lineHeight = 26.sp),
    titleSmall = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold, lineHeight = 24.sp),
    bodyLarge = TextStyle(fontSize = 20.sp, lineHeight = 26.sp),
    bodyMedium = TextStyle(fontSize = 18.sp, lineHeight = 24.sp),
    bodySmall = TextStyle(fontSize = 16.sp, lineHeight = 22.sp),
    labelLarge = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
    labelMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
)

@Composable
fun ImpostorTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val gameColors = if (darkTheme) DarkGameColors else LightGameColors
    CompositionLocalProvider(LocalGameColors provides gameColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
        ) {
            // BUG-2: contentColorFor() usa el tema del SISTEMA, no el de la app;
            // con la app en oscuro y el sistema en claro los textos sin color
            // explícito salían negros. Fijamos el color de contenido por defecto.
            CompositionLocalProvider(LocalContentColor provides colorScheme.onBackground) {
                content()
            }
        }
    }
}

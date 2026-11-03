package com.rexon.common.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = CharcoalDark,
    onPrimary = Color.White,
    primaryContainer = BorderGray,
    onPrimaryContainer = CharcoalDark,
    secondary = RoyalViolet,
    onSecondary = Color.White,
    secondaryContainer = RoyalVioletLight,
    onSecondaryContainer = RoyalVioletDark,
    tertiary = EmeraldGreen,
    onTertiary = Color.White,
    tertiaryContainer = EmeraldGreenLight,
    onTertiaryContainer = EmeraldGreen,
    error = CrimsonRed,
    onError = Color.White,
    errorContainer = CrimsonRedLight,
    onErrorContainer = CrimsonRed,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFF3F4F6),
    onSurfaceVariant = TextSecondary,
    outline = BorderGray
)

private val DarkColorScheme = darkColorScheme(
    primary = Color.White,
    onPrimary = CharcoalDark,
    primaryContainer = CharcoalSurface,
    onPrimaryContainer = Color.White,
    secondary = RoyalViolet,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF2E234E),
    onSecondaryContainer = RoyalVioletLight,
    tertiary = EmeraldGreen,
    onTertiary = Color.White,
    error = CrimsonRed,
    onError = Color.White,
    background = CharcoalDark,
    onBackground = Color.White,
    surface = CharcoalSurface,
    onSurface = Color.White,
    outline = Color(0xFF27272A)
)

val RexonShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp), // 16px rounded corners per design specs
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun RexonTheme(
    darkTheme: Boolean = false, // Default to crisp high-contrast light theme as required
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = RexonShapes,
        content = content
    )
}

package com.airops.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Military HUD color palette
val HudGreen = Color(0xFF00FF41)
val HudAmber = Color(0xFFFFB300)
val HudRed = Color(0xFFFF4444)
val HudBlue = Color(0xFF4444FF)
val HudBackground = Color(0xFF0A0E0F)
val HudSurface = Color(0xFF111518)
val HudBorder = Color(0xFF1E2A2E)
val HudText = Color(0xFF8FA9AF)
val TeamAlpha = Color(0xFFFF4444)
val TeamBravo = Color(0xFF4444FF)

private val DarkColorScheme = darkColorScheme(
    primary = HudGreen,
    secondary = HudAmber,
    error = HudRed,
    background = HudBackground,
    surface = HudSurface,
    onBackground = Color(0xFFCDD6D9),
    onSurface = Color(0xFFCDD6D9),
    outline = HudBorder,
)

val AiropsTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        color = HudGreen,
        letterSpacing = 0.1.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        letterSpacing = 0.05.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        color = HudText,
        letterSpacing = 0.08.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        letterSpacing = 0.1.sp
    )
)

@Composable
fun AiropsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = AiropsTypography,
        content = content
    )
}

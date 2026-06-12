package com.codelegger.golfperformancetracker.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Neutral chrome + brand red accent. Color is otherwise reserved for club badges.
private val LightColorScheme = lightColorScheme(
    primary = BrandRed,
    onPrimary = Color.White,
    secondary = Ink700,
    onSecondary = Color.White,
    background = Surface50,
    onBackground = Ink900,
    surface = Surface0,
    onSurface = Ink900,
    surfaceVariant = Surface50,
    onSurfaceVariant = Ink500,
    outline = DividerLine,
    error = BrandRed,
    onError = Color.White,
    errorContainer = Color(0xFFFBE3E6),
    onErrorContainer = Ink900,
)

private val DarkColorScheme = darkColorScheme(
    primary = BrandRedLight,
    onPrimary = Ink900,
    secondary = Ink300,
    onSecondary = Ink900,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    error = BrandRedLight,
    onError = Ink900,
    errorContainer = Color(0xFF5A1A22),
    onErrorContainer = Color(0xFFFAD9DD),
)

/**
 * App theme. Light/dark are driven by the system. Dynamic color defaults to `false` so the
 * brand red/neutral identity is shown consistently; opt in with `dynamicColor = true`.
 */
@Composable
fun GolfTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

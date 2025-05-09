package com.example.lottery.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Colores personalizados en tema pastel
val PastelPurple = Color(0xFFD0BFFF) // Morado pastel principal
val LightPastelPurple = Color(0xFFE8E0FF) // Morado pastel más claro
val DarkPastelPurple = Color(0xFFB09CFF) // Morado pastel más oscuro
val LightGray = Color(0xFFF1F1F1) // Gris claro
val MediumGray = Color(0xFFE0E0E0) // Gris medio
val DarkText = Color(0xFF333333) // Texto casi negro
val White = Color(0xFFFFFFFF) // Blanco puro
val BlackOverlay = Color(0x99000000) // Negro con transparencia para overlays

// Esquema de colores para el tema claro
private val LightColorScheme = lightColorScheme(
    primary = PastelPurple,
    onPrimary = DarkText,
    primaryContainer = LightPastelPurple,
    onPrimaryContainer = DarkText,
    secondary = DarkPastelPurple,
    onSecondary = White,
    secondaryContainer = LightPastelPurple,
    onSecondaryContainer = DarkText,
    tertiary = MediumGray,
    onTertiary = DarkText,
    tertiaryContainer = LightGray,
    onTertiaryContainer = DarkText,
    background = White,
    onBackground = DarkText,
    surface = White,
    onSurface = DarkText,
    surfaceVariant = LightGray,
    onSurfaceVariant = DarkText,
    outline = MediumGray
)

// Esquema de colores para el tema oscuro
private val DarkColorScheme = darkColorScheme(
    primary = LightPastelPurple,
    onPrimary = DarkText,
    primaryContainer = PastelPurple,
    onPrimaryContainer = DarkText,
    secondary = DarkPastelPurple,
    onSecondary = White,
    secondaryContainer = PastelPurple,
    onSecondaryContainer = DarkText,
    tertiary = MediumGray,
    onTertiary = DarkText,
    tertiaryContainer = MediumGray,
    onTertiaryContainer = White,
    background = Color(0xFF121212),
    onBackground = White,
    surface = Color(0xFF1E1E1E),
    onSurface = White,
    surfaceVariant = Color(0xFF303030),
    onSurfaceVariant = LightGray,
    outline = LightGray
)

@Composable
fun LotteryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}